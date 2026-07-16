package com.example.zkota;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.annotation.NonNull;

/**
 * ZkOtaManager
 *
 * Thin wrapper around OtaManager providing ZK‑specific MTU and UUIDs.
 */
public final class ZkOtaManager extends OtaManager {

    // OTA characteristics
    public static final String UUID_OTA_READ  = "0000e91b-0000-1000-8000-00805f9b34fb";
    public static final String UUID_OTA_WRITE = "0000e91d-0000-1000-8000-00805f9b34fb";

    private int mtuSize = 23;

    public ZkOtaManager(
            @NonNull Context context,
            @NonNull BluetoothDevice device,
            @NonNull OtaEventListener listener
    ) {
        super(context, device, listener);
    }

    @Override
    public boolean hasMoreData() {
        return dataProvider != null && dataProvider.hasMore();
    }

    @Override
    public int getPacketPayloadSize() {
        return mtuSize - 3;
    }

    @Override
    public void onPrimaryUpdated() {
        // ZK devices finalize OTA by calling super.reset()
        super.reset();
    }
}
