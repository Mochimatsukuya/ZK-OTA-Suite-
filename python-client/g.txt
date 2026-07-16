package com.bluetrum.fota.bluetooth;

import A9.I;
import B.RunnableC0851p;
import D2.n;
import H.r;
import S.h;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import com.applovin.impl.D0;
import com.applovin.impl.adview.p;
import com.applovin.impl.sdk.w;
import com.google.android.gms.location.DeviceOrientationRequest;
import com.mbridge.msdk.foundation.tools.SameMD5;
import com.mbridge.msdk.playercommon.exoplayer2.extractor.ts.PsExtractor;
import com.wtwd.hfit.ble.BleConnectService;
import com.wtwd.hfit.ble.ZKBleOtaManager;
import com.wtwd.hfit.jzlib.GZIPHeader;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import p090j2.U0;
import p108l1.v;

/* JADX INFO: compiled from: OtaManager.java */
/* JADX INFO: loaded from: C:\Users\M0306\Downloads\python scripts\ApWatch\classes.dex */
public abstract class g {
    private static final int DEFAULT_TIMEOUT = 10000;
    protected static final int DELAY_AFTER_SEND_IDENTIFICATION = 200;
    private static final String TAG = "g";
    private static final int UNDEFINED_FIRMWARE_VERSION = -1;
    protected boolean allowedUpdate;
    protected byte[] bluetoothAddress;
    protected com.bluetrum.fota.bluetooth.a commandGenerator;
    protected final Context context;
    public b dataProvider;
    protected BluetoothDevice device;
    private final a eventListener;
    private boolean isDeviceReady;
    private boolean receivedChannelInfo = false;
    protected int otaFirmwareVersion = -1;
    protected int deviceFirmwareVersion = -1;
    private int mBlockSize = 4096;
    private int mPacketSize = PsExtractor.VIDEO_STREAM_MASK;
    protected boolean isZKUpdating = false;
    protected boolean isUpdatePause = false;
    protected boolean isPrimaryUpdated = false;
    protected Boolean isTwsDevice = null;
    protected Boolean isTwsConnected = null;
    public boolean disconnectedDueToDeviceError = false;
    private boolean _needIdentification = true;
    protected boolean sentIdentification = false;
    private final Handler notifyHandler = new Handler(Looper.getMainLooper());
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());

    /* JADX INFO: compiled from: OtaManager.java */
    public interface a {
    }

    public g(@NonNull Context context, @NonNull BluetoothDevice bluetoothDevice, @NonNull BleConnectService bleConnectService) {
        this.context = context;
        this.device = bluetoothDevice;
        this.eventListener = bleConnectService;
        com.bluetrum.fota.bluetooth.a aVar = new com.bluetrum.fota.bluetooth.a();
        aVar.f18558b = (byte) 0;
        this.commandGenerator = aVar;
    }

    public static String h(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b9 : bArr) {
            String hexString = Integer.toHexString(b9 & GZIPHeader.OS_UNKNOWN);
            if (hexString.length() == 1) {
                hexString = "0".concat(hexString);
            }
            sb.append(hexString.toUpperCase());
        }
        return sb.toString();
    }

    public final void A(boolean z10) {
        this.isDeviceReady = z10;
    }

    public final void B(@NonNull byte[] bArr) {
        b bVar = new b(bArr);
        this.dataProvider = bVar;
        bVar.f18560b = this.mBlockSize;
        bVar.f18562d = n();
        com.bluetrum.fota.bluetooth.a aVar = this.commandGenerator;
        if (aVar != null) {
            aVar.f18557a = this.dataProvider;
        } else {
            b bVar2 = this.dataProvider;
            com.bluetrum.fota.bluetooth.a aVar2 = new com.bluetrum.fota.bluetooth.a();
            aVar2.f18558b = (byte) 0;
            aVar2.f18557a = bVar2;
            this.commandGenerator = aVar2;
        }
        k();
    }

    public final void C() {
        this.isZKUpdating = false;
    }

    public void D() {
        this.isZKUpdating = true;
        Handler handler = this.notifyHandler;
        a aVar = this.eventListener;
        Objects.requireNonNull(aVar);
        handler.post(new r(aVar, 19));
        m();
    }

    public abstract boolean i();

    public final void j() {
        this.timeoutHandler.removeCallbacksAndMessages(null);
    }

    public final void k() {
        boolean zO = o();
        Log.d(TAG, "checkIfReadyToUpdate: " + zO);
        if (zO) {
            Handler handler = this.notifyHandler;
            a aVar = this.eventListener;
            Objects.requireNonNull(aVar);
            handler.post(new RunnableC0851p(aVar, 22));
        }
    }

    public final void l() {
        com.bluetrum.fota.bluetooth.a aVar = this.commandGenerator;
        aVar.getClass();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(-110);
        byte b9 = aVar.f18558b;
        aVar.f18558b = (byte) (b9 + 1);
        byteArrayOutputStream.write(b9);
        try {
            byteArrayOutputStream.write(com.bluetrum.fota.bluetooth.a.a((byte) 1));
            byteArrayOutputStream.write(com.bluetrum.fota.bluetooth.a.a((byte) 3));
            byteArrayOutputStream.write(com.bluetrum.fota.bluetooth.a.a((byte) 4));
            byteArrayOutputStream.write(com.bluetrum.fota.bluetooth.a.a((byte) 5));
            byteArrayOutputStream.write(com.bluetrum.fota.bluetooth.a.a((byte) 6));
        } catch (Exception e7) {
            Log.e("a", "Failed to generate Command", e7);
            e7.printStackTrace();
        }
        ((BleConnectService) this.eventListener).a0(byteArrayOutputStream.toByteArray());
    }

    public final void m() {
        com.bluetrum.fota.bluetooth.a aVar = this.commandGenerator;
        aVar.getClass();
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(3);
        byteBufferAllocate.put((byte) -111);
        byte b9 = aVar.f18558b;
        aVar.f18558b = (byte) (b9 + 1);
        byteBufferAllocate.put(b9);
        byteBufferAllocate.put((byte) 1);
        ((BleConnectService) this.eventListener).a0(byteBufferAllocate.array());
        Log.d(TAG, "getOtaInfoVersion");
    }

    public abstract int n();

    public final boolean o() {
        String str = TAG;
        StringBuilder sb = new StringBuilder("isReady: ");
        sb.append(this.isTwsDevice);
        sb.append(", ");
        sb.append(this.isTwsConnected);
        sb.append(", ");
        sb.append(this.dataProvider != null);
        sb.append(", ");
        sb.append(this.isDeviceReady);
        Log.d(str, sb.toString());
        return this.dataProvider != null && this.isDeviceReady;
    }

    public final boolean p() {
        return this.isZKUpdating;
    }

    public final boolean q() {
        return this._needIdentification;
    }

    public final void r(int i4) {
        this.disconnectedDueToDeviceError = true;
        this.notifyHandler.post(new w(i4, 1, this));
        w();
    }

    public final void s() {
        if (!this.disconnectedDueToDeviceError) {
            Handler handler = this.notifyHandler;
            a aVar = this.eventListener;
            Objects.requireNonNull(aVar);
            handler.post(new c(aVar, 0));
        }
        this.disconnectedDueToDeviceError = false;
    }

    public abstract void t();

    /* JADX WARN: Code duplicated, block: B:28:0x00ec  */
    public final boolean u(byte[] bArr) {
        byte[] bArrDigest;
        Boolean bool;
        if (bArr.length < 3) {
            Log.w(TAG, "接收到数据长度小于3");
            return false;
        }
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr);
        byte b9 = byteBufferWrap.get();
        byteBufferWrap.get();
        String str = TAG;
        v.f(b9, "processData", str);
        switch (b9) {
            case -112:
                byte b10 = byteBufferWrap.get();
                Log.w(str, "processNotifyStatus = " + h(bArr));
                if (b10 == -128) {
                    j();
                    this.isZKUpdating = false;
                    this.isTwsConnected = Boolean.FALSE;
                    Handler handler = this.notifyHandler;
                    a aVar = this.eventListener;
                    Objects.requireNonNull(aVar);
                    handler.post(new h(aVar, 12));
                    this.notifyHandler.post(new D0(1, this, this.isTwsConnected.booleanValue()));
                } else if (b10 == 64) {
                    Log.w(str, "STATE_SEQNUM = " + ((int) b10));
                    j();
                    r(11);
                } else if (b10 == -3) {
                    j();
                    this.allowedUpdate = false;
                    this.isZKUpdating = false;
                    this.isUpdatePause = true;
                    Handler handler2 = this.notifyHandler;
                    a aVar2 = this.eventListener;
                    Objects.requireNonNull(aVar2);
                    handler2.post(new c(aVar2, 2));
                } else if (b10 == -2) {
                    Handler handler3 = this.notifyHandler;
                    a aVar3 = this.eventListener;
                    Objects.requireNonNull(aVar3);
                    handler3.post(new f(aVar3, 1));
                    this.isZKUpdating = true;
                    this.isUpdatePause = false;
                    m();
                } else if (b10 == -1) {
                    j();
                    Boolean bool2 = this.isTwsDevice;
                    if (bool2 == null || !bool2.booleanValue() || !this.receivedChannelInfo || this.isPrimaryUpdated) {
                        Handler handler4 = this.notifyHandler;
                        a aVar4 = this.eventListener;
                        Objects.requireNonNull(aVar4);
                        handler4.post(new c(aVar4, 1));
                        w();
                    } else {
                        this.isPrimaryUpdated = true;
                        Handler handler5 = this.notifyHandler;
                        a aVar5 = this.eventListener;
                        Objects.requireNonNull(aVar5);
                        handler5.post(new E7.b(aVar5, 24));
                        t();
                    }
                } else if (b10 != 0) {
                    j();
                    r(11);
                } else {
                    j();
                    if (!this.isUpdatePause) {
                        b bVar = this.dataProvider;
                        if (bVar.f18563e != bVar.f18559a.length) {
                            z();
                        }
                    }
                }
                return true;
            case -111:
                byte b11 = byteBufferWrap.get();
                byte[] bArr2 = new byte[byteBufferWrap.remaining()];
                byteBufferWrap.get(bArr2, 0, byteBufferWrap.remaining());
                Log.d(str, "processGetInfo" + ((int) b11));
                v(bArr2, b11);
                if (b11 == 1) {
                    int i4 = this.otaFirmwareVersion;
                    b bVar2 = this.dataProvider;
                    if (bVar2 != null) {
                        byte[] bArr3 = bVar2.f18559a;
                        try {
                            MessageDigest messageDigest = MessageDigest.getInstance(SameMD5.TAG);
                            messageDigest.update(bArr3);
                            bArrDigest = messageDigest.digest();
                        } catch (NoSuchAlgorithmException e7) {
                            e7.printStackTrace();
                            bArrDigest = null;
                        }
                        com.bluetrum.fota.bluetooth.a aVar6 = this.commandGenerator;
                        aVar6.getClass();
                        ByteBuffer byteBufferOrder = ByteBuffer.allocate(9).order(ByteOrder.LITTLE_ENDIAN);
                        byteBufferOrder.put((byte) -111);
                        byte b12 = aVar6.f18558b;
                        aVar6.f18558b = (byte) (b12 + 1);
                        byteBufferOrder.put(b12);
                        byteBufferOrder.put((byte) 2);
                        byteBufferOrder.putShort((short) i4);
                        if (bArrDigest != null) {
                            byteBufferOrder.put(bArrDigest, 0, 4);
                        } else {
                            byteBufferOrder.putInt(-1);
                        }
                        ((BleConnectService) this.eventListener).a0(byteBufferOrder.array());
                    }
                    break;
                } else if (b11 == 2) {
                    if (this.allowedUpdate) {
                        z();
                    } else {
                        j();
                        r(12);
                    }
                }
                return true;
            case -110:
                byte[] bArr4 = new byte[byteBufferWrap.remaining()];
                byteBufferWrap.get(bArr4, 0, byteBufferWrap.remaining());
                Log.d(str, "processGetInfoTLV");
                while (bArr4.length > 2) {
                    ByteBuffer byteBufferWrap2 = ByteBuffer.wrap(bArr4);
                    byte b13 = byteBufferWrap2.get();
                    byte[] bArr5 = new byte[byteBufferWrap2.get()];
                    byteBufferWrap2.get(bArr5);
                    v(bArr5, b13);
                    if (!byteBufferWrap2.hasRemaining()) {
                        if (!o() && (bool = this.isTwsDevice) != null && bool.booleanValue() && this.receivedChannelInfo && this.isPrimaryUpdated) {
                            D();
                        } else {
                            k();
                        }
                        return true;
                    }
                    byte[] bArr6 = new byte[byteBufferWrap2.remaining()];
                    byteBufferWrap2.get(bArr6);
                    bArr4 = bArr6;
                }
                if (!o()) {
                    k();
                } else {
                    k();
                }
                return true;
            case -109:
                Log.w(str, "版本号" + h(bArr));
                int i7 = ((bArr[3] & GZIPHeader.OS_UNKNOWN) << 8) | (bArr[4] & GZIPHeader.OS_UNKNOWN);
                int i10 = bArr[5] & GZIPHeader.OS_UNKNOWN;
                int i11 = bArr[6] & GZIPHeader.OS_UNKNOWN;
                int i12 = bArr[7] & GZIPHeader.OS_UNKNOWN;
                int i13 = bArr[8] & GZIPHeader.OS_UNKNOWN;
                StringBuilder sb = new StringBuilder();
                sb.append(i7);
                sb.append(".");
                sb.append(i10);
                sb.append(".");
                sb.append(i11);
                String strI = n.i(i12, i13, ".", ".", sb);
                StringBuilder sbC = U0.c(i7, i10, "版本号V", ".", ".");
                I.o(i11, i12, ".", ".", sbC);
                sbC.append(i13);
                Log.w(str, sbC.toString());
                this.notifyHandler.post(new E7.b((ZKBleOtaManager) this, strI));
                return true;
            default:
                return false;
        }
    }

    public final void v(byte[] bArr, byte b9) {
        String str = TAG;
        StringBuilder sbE = U0.e(b9, "processInfo: ", " -> ");
        sbE.append(p245z3.a.a(bArr));
        Log.d(str, sbE.toString());
        final boolean z10 = false;
        final boolean z11 = true;
        switch (b9) {
            case 1:
                if (bArr.length == 2) {
                    short s6 = ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
                    this.notifyHandler.post(new com.applovin.mediation.nativeAds.adPlacer.a(s6, 1, this));
                    this.deviceFirmwareVersion = s6;
                }
                break;
            case 2:
                if (bArr.length == 11) {
                    ByteBuffer byteBufferOrder = ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN);
                    int i4 = byteBufferOrder.getInt();
                    this.dataProvider.f18563e = i4;
                    Log.d(str, "startAddress = " + i4);
                    int i7 = byteBufferOrder.getInt();
                    this.mBlockSize = i7;
                    this.dataProvider.f18560b = i7;
                    Log.d(str, "blockSize = " + i7);
                    short s10 = byteBufferOrder.getShort();
                    this.mPacketSize = s10;
                    this.dataProvider.f18562d = s10;
                    Log.d(str, "packetSize = " + ((int) s10));
                    this.allowedUpdate = byteBufferOrder.get() == 1;
                    Log.d(str, "allowedUpdate = " + this.allowedUpdate);
                }
                break;
            case 3:
                if (bArr.length == 2) {
                    this.isTwsDevice = Boolean.valueOf((ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getShort() & 1) != 0);
                    Log.d(str, "isTWS: " + this.isTwsDevice);
                    final boolean zBooleanValue = this.isTwsDevice.booleanValue();
                    this.notifyHandler.post(new Runnable() { // from class: com.bluetrum.fota.bluetooth.e
                        @Override // java.lang.Runnable
                        public final void run() {
                            ((BleConnectService) this.f18568a.eventListener).W(zBooleanValue);
                        }
                    });
                }
                break;
            case 4:
                if (bArr.length == 2) {
                    this.isTwsConnected = Boolean.valueOf((ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getShort() & 1) != 0);
                    Log.d(str, "isTwsConnected: " + this.isTwsConnected);
                    this.notifyHandler.post(new D0(1, this, this.isTwsConnected.booleanValue()));
                }
                break;
            case 5:
                if (bArr.length == 6) {
                    Log.d(str, "Received BLE Address");
                    this.bluetoothAddress = bArr;
                }
                break;
            case 6:
                if (bArr.length == 1) {
                    byte b10 = bArr[0];
                    if (b10 == 1) {
                        Log.d(str, "Channel: Left");
                        this.notifyHandler.post(new Runnable() { // from class: com.bluetrum.fota.bluetooth.d
                            @Override // java.lang.Runnable
                            public final void run() {
                                ((BleConnectService) this.f18566a.eventListener).V(z11);
                            }
                        });
                    } else if (b10 == 0) {
                        Log.d(str, "Channel: Right");
                        this.notifyHandler.post(new Runnable() { // from class: com.bluetrum.fota.bluetooth.d
                            @Override // java.lang.Runnable
                            public final void run() {
                                ((BleConnectService) this.f18566a.eventListener).V(z10);
                            }
                        });
                    }
                    this.receivedChannelInfo = true;
                }
                break;
        }
    }

    public void w() {
        this.otaFirmwareVersion = -1;
        this.deviceFirmwareVersion = -1;
        this.allowedUpdate = false;
        this.isZKUpdating = false;
        this.isUpdatePause = false;
        this.sentIdentification = false;
        this.receivedChannelInfo = false;
        j();
        this.commandGenerator.f18558b = (byte) 0;
    }

    public final void x() {
        if (this._needIdentification && !this.sentIdentification) {
            HandlerThread handlerThread = new HandlerThread("Get All Info");
            handlerThread.start();
            new Handler(handlerThread.getLooper()).postDelayed(new f((ZKBleOtaManager) this, 0), 200L);
            return;
        }
        if (!i()) {
            b bVar = this.dataProvider;
            if (bVar != null) {
                int i4 = bVar.f18560b;
                byte[] bArr = bVar.f18559a;
                if (i4 != -1) {
                    int i7 = bVar.f18563e;
                    if (i7 - bVar.f18561c != i4 && i7 != bArr.length) {
                        return;
                    }
                } else if (bVar.f18563e != bArr.length) {
                    return;
                }
                this.timeoutHandler.postDelayed(new r((ZKBleOtaManager) this, 20), DeviceOrientationRequest.OUTPUT_PERIOD_MEDIUM);
                return;
            }
            return;
        }
        com.bluetrum.fota.bluetooth.a aVar = this.commandGenerator;
        aVar.getClass();
        ByteBuffer byteBufferOrder = ByteBuffer.allocate(517).order(ByteOrder.LITTLE_ENDIAN);
        byteBufferOrder.put((byte) 32);
        byte b9 = aVar.f18558b;
        aVar.f18558b = (byte) (b9 + 1);
        byteBufferOrder.put(b9);
        b bVar2 = aVar.f18557a;
        int iPosition = byteBufferOrder.position();
        int length = bVar2.f18560b;
        byte[] bArr2 = bVar2.f18559a;
        if (length == -1) {
            length = bArr2.length;
        }
        int iMin = Math.min(length, bVar2.f18562d - iPosition);
        int i10 = bVar2.f18560b;
        if (i10 != -1) {
            int i11 = bVar2.f18563e - bVar2.f18561c;
            if (i11 + iMin > i10) {
                iMin = i10 - i11;
            }
        }
        int i12 = bVar2.f18563e;
        if (i12 + iMin > bArr2.length) {
            iMin = bArr2.length - i12;
        }
        byte[] bArr3 = new byte[iMin];
        System.arraycopy(bArr2, i12, bArr3, 0, iMin);
        bVar2.f18563e += iMin;
        byteBufferOrder.put(bArr3);
        byte[] bArr4 = new byte[byteBufferOrder.position()];
        byteBufferOrder.rewind();
        byteBufferOrder.get(bArr4);
        ((BleConnectService) this.eventListener).a0(bArr4);
        b bVar3 = this.dataProvider;
        this.notifyHandler.post(new p((bVar3.f18563e * 100) / bVar3.f18559a.length, 1, this));
    }

    public final void y() {
        this.commandGenerator.getClass();
        ((BleConnectService) this.eventListener).a0(com.bluetrum.fota.bluetooth.a.f18556c);
    }

    public final void z() {
        int iMin;
        com.bluetrum.fota.bluetooth.a aVar = this.commandGenerator;
        aVar.getClass();
        ByteBuffer byteBufferOrder = ByteBuffer.allocate(517).order(ByteOrder.LITTLE_ENDIAN);
        byteBufferOrder.put((byte) -96);
        byte b9 = aVar.f18558b;
        aVar.f18558b = (byte) (b9 + 1);
        byteBufferOrder.put(b9);
        byteBufferOrder.putInt(aVar.f18557a.f18563e);
        b bVar = aVar.f18557a;
        int i4 = bVar.f18560b;
        byte[] bArr = bVar.f18559a;
        if (i4 == -1) {
            iMin = bArr.length - bVar.f18563e;
        } else {
            iMin = Math.min(bArr.length - bVar.f18563e, i4);
            bVar.f18561c = bVar.f18563e;
        }
        byteBufferOrder.putInt(iMin);
        b bVar2 = aVar.f18557a;
        int iPosition = byteBufferOrder.position();
        int length = bVar2.f18560b;
        byte[] bArr2 = bVar2.f18559a;
        if (length == -1) {
            length = bArr2.length;
        }
        int iMin2 = Math.min(length, bVar2.f18562d - iPosition);
        int i7 = bVar2.f18563e;
        if (i7 + iMin2 > bArr2.length) {
            iMin2 = bArr2.length - i7;
        }
        byte[] bArr3 = new byte[iMin2];
        System.arraycopy(bArr2, i7, bArr3, 0, iMin2);
        bVar2.f18563e += iMin2;
        byteBufferOrder.put(bArr3);
        byte[] bArr4 = new byte[byteBufferOrder.position()];
        byteBufferOrder.rewind();
        byteBufferOrder.get(bArr4);
        ((BleConnectService) this.eventListener).a0(bArr4);
        b bVar3 = this.dataProvider;
        this.notifyHandler.post(new p((bVar3.f18563e * 100) / bVar3.f18559a.length, 1, this));
    }
}
