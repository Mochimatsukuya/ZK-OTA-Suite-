package com.example.zkota;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Handles firmware chunking for OTA.
 */
public class OtaDataProvider {

    public final byte[] firmware;

    public int offset = 0;
    public int blockSize = 4096;
    public int packetSize = 512;

    public OtaDataProvider(byte[] fw) {
        this.firmware = fw;
    }

    public boolean hasMore() {
        return offset < firmware.length;
    }

    public int getProgress() {
        return (offset * 100) / firmware.length;
    }

    public byte[] buildBlockPacket(byte seq) {

        int remaining = firmware.length - offset;
        int len = Math.min(remaining, blockSize);

        ByteBuffer buf = ByteBuffer.allocate(1 + 1 + 4 + 4 + len)
                .order(ByteOrder.LITTLE_ENDIAN);

        buf.put((byte)0xA0); // block opcode
        buf.put(seq);
        buf.putInt(offset);
        buf.putInt(len);

        buf.put(firmware, offset, len);

        offset += len;

        return buf.array();
    }
}
