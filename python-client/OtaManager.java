package com.example.zkota;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

/**
 * Core OTA engine for ZK devices.
 *
 * Handles:
 *  - TLV info exchange
 *  - firmware MD5/version handshake
 *  - block transfer
 *  - status notifications
 *  - TWS logic
 */
public abstract class OtaManager {

    protected final Context context;
    protected final BluetoothDevice device;
    protected final OtaEventListener listener;

    protected OtaDataProvider dataProvider;

    protected boolean allowedUpdate = false;
    protected boolean isUpdating = false;
    protected boolean isPaused = false;
    protected boolean needIdentification = true;
    protected boolean sentIdentification = false;

    protected int otaFirmwareVersion = -1;
    protected int deviceFirmwareVersion = -1;

    protected boolean isTwsDevice = false;
    protected boolean isTwsConnected = false;
    protected boolean receivedChannelInfo = false;
    protected boolean primaryUpdated = false;

    protected byte seq = 0;

    private final Handler mainHandler = new Handler();
    private final Handler timeoutHandler = new Handler();

    public OtaManager(Context ctx, BluetoothDevice dev, OtaEventListener listener) {
        this.context = ctx;
        this.device = dev;
        this.listener = listener;
    }

    // ------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------

    public void loadFirmware(byte[] firmware) {
        this.dataProvider = new OtaDataProvider(firmware);
        checkReady();
    }

    public void startOta() {
        isUpdating = true;
        listener.onOtaStart();
        requestOtaInfoVersion();
    }

    public void stopOta() {
        isUpdating = false;
        listener.onOtaStopped();
        reset();
    }

    public abstract boolean hasMoreData();
    public abstract int getPacketPayloadSize();
    public abstract void onPrimaryUpdated();

    // ------------------------------------------------------------
    // Incoming notifications from BLE
    // ------------------------------------------------------------

    public void handleNotification(byte[] data) {
        if (data.length < 3) return;

        byte opcode = data[0];
        byte seqNum = data[1];
        byte[] payload = new byte[data.length - 2];
        System.arraycopy(data, 2, payload, 0, payload.length);

        switch (opcode) {

            case (byte)0x90: // -112 status
                handleStatus(payload);
                break;

            case (byte)0x91: // -111 info
                handleInfo(payload);
                break;

            case (byte)0x92: // -110 TLV info
                handleTlvInfo(payload);
                break;

            case (byte)0x93: // -109 version string
                handleVersionString(payload);
                break;
        }
    }

    // ------------------------------------------------------------
    // Status handling
    // ------------------------------------------------------------

    private void handleStatus(byte[] payload) {
        byte status = payload[0];

        switch (status) {

            case (byte)0x00: // OK, continue
                if (!isPaused && hasMoreData()) {
                    sendNextBlock();
                }
                break;

            case (byte)0xFF: // -1 done
                listener.onOtaFinished();
                reset();
                break;

            case (byte)0xFD: // -3 forbidden/pause
                isPaused = true;
                isUpdating = false;
                listener.onOtaPaused();
                break;

            case (byte)0xFE: // -2 resume
                isPaused = false;
                isUpdating = true;
                requestOtaInfoVersion();
                break;

            default:
                listener.onOtaError("Status error: " + status);
                reset();
                break;
        }
    }

    // ------------------------------------------------------------
    // Info handling
    // ------------------------------------------------------------

    private void handleInfo(byte[] payload) {
        byte type = payload[0];
        byte[] body = new byte[payload.length - 1];
        System.arraycopy(payload, 1, body, 0, body.length);

        if (type == 1) {
            // Device FW version
            deviceFirmwareVersion = ByteBuffer.wrap(body)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .getShort();

            // Send MD5 + version
            sendFirmwareMd5(deviceFirmwareVersion + 1);

        } else if (type == 2) {
            // Allowed update?
            allowedUpdate = (body[10] == 1);

            if (allowedUpdate) {
                sendNextBlock();
            } else {
                listener.onOtaError("Device does not allow update");
            }
        }
    }

    private void handleTlvInfo(byte[] payload) {
        ByteBuffer buf = ByteBuffer.wrap(payload);

        while (buf.remaining() > 2) {
            byte type = buf.get();
            int len = buf.get() & 0xFF;
            byte[] value = new byte[len];
            buf.get(value);

            parseTlv(type, value);
        }

        checkReady();
    }

    private void parseTlv(byte type, byte[] value) {
        switch (type) {

            case 1: // device FW version
                deviceFirmwareVersion = ByteBuffer.wrap(value)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .getShort();
                break;

            case 2: // block/packet sizes
                ByteBuffer b = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
                dataProvider.offset = b.getInt();
                dataProvider.blockSize = b.getInt();
                dataProvider.packetSize = b.getShort();
                allowedUpdate = (b.get() == 1);
                break;

            case 3: // TWS device
                isTwsDevice = (ByteBuffer.wrap(value)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .getShort() & 1) != 0;
                break;

            case 4: // TWS connected
                isTwsConnected = (ByteBuffer.wrap(value)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .getShort() & 1) != 0;
                break;

            case 5: // BLE address
                // optional
                break;

            case 6: // channel
                receivedChannelInfo = true;
                break;
        }
    }

    private void handleVersionString(byte[] payload) {
        listener.onVersionString(new String(payload));
    }

    // ------------------------------------------------------------
    // Outgoing commands
    // ------------------------------------------------------------

    private void requestOtaInfoVersion() {
        byte[] cmd = new byte[] {
                (byte)0x91,
                nextSeq(),
                0x01
        };
        listener.send(cmd);
    }

    private void sendFirmwareMd5(int version) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(dataProvider.firmware);

            ByteBuffer buf = ByteBuffer.allocate(9)
                    .order(ByteOrder.LITTLE_ENDIAN);

            buf.put((byte)0x91);
            buf.put(nextSeq());
            buf.put((byte)0x02);
            buf.putShort((short)version);
            buf.put(digest, 0, 4);

            listener.send(buf.array());

        } catch (Exception e) {
            listener.onOtaError("MD5 error: " + e.getMessage());
        }
    }

    private void sendNextBlock() {
        byte[] packet = dataProvider.buildBlockPacket(nextSeq());
        listener.send(packet);
        listener.onProgress(dataProvider.getProgress());
    }

    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------

    private void checkReady() {
        if (dataProvider != null) {
            listener.onReadyToUpdate();
        }
    }

    protected void reset() {
        isUpdating = false;
        isPaused = false;
        sentIdentification = false;
        receivedChannelInfo = false;
        needIdentification = true;
        seq = 0;
    }

    private byte nextSeq() {
        return seq++;
    }
}
