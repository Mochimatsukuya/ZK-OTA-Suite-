package com.wtwd.hfit.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import androidx.annotation.NonNull;
import com.bluetrum.fota.bluetooth.b;
import com.bluetrum.fota.bluetooth.g;
import com.wtwd.hfit.constant.BleConstant;
import java.util.UUID;

/* JADX INFO: loaded from: C:\Users\M0306\Downloads\ApWatch\classes4.dex */
public final class ZKBleOtaManager extends g {
    private static UUID OTA_SERVICE_UUID = null;
    private static final String TAG = "ZKBleOtaManager";
    private int mMtuSize;
    private static final UUID OTA_DATA_IN_UUID = UUID.fromString(BleConstant.UUID_READ);
    public static final UUID UUID_WRITE = UUID.fromString(BleConstant.UUID_WRITE);
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString(BleConstant.UUID_SYSTEM);
    private static final UUID OTA_DATA_OUT_UUID = UUID.fromString(BleConstant.UUID_WRITE_ZK);

    public ZKBleOtaManager(@NonNull Context context, @NonNull BluetoothDevice bluetoothDevice, @NonNull BleConnectService bleConnectService) {
        super(context, bluetoothDevice, bleConnectService);
        this.mMtuSize = 23;
    }

    /* JADX WARN: Code duplicated, block: B:14:0x001f A[RETURN, SYNTHETIC] */
    @Override // com.bluetrum.fota.bluetooth.g
    public final boolean i() {
        if (this.allowedUpdate) {
            b bVar = this.dataProvider;
            int i4 = bVar.f18560b;
            byte[] bArr = bVar.f18559a;
            if (i4 != -1) {
                int i7 = bVar.f18563e;
                if (i7 - bVar.f18561c != i4 && i7 != bArr.length) {
                    return true;
                }
            } else if (bVar.f18563e != bArr.length) {
                return true;
            }
        }
        return false;
    }

    @Override // com.bluetrum.fota.bluetooth.g
    public final int n() {
        return this.mMtuSize - 3;
    }

    @Override // com.bluetrum.fota.bluetooth.g
    public final void t() {
        super.w();
    }
}
