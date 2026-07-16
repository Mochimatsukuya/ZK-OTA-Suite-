package com.wtwd.hfit.ble;

import A6.d;
import D2.p;
import H0.c;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import com.bluetrum.fota.bluetooth.b;
import com.bluetrum.fota.bluetooth.g;
import com.mbridge.msdk.playercommon.exoplayer2.extractor.ts.PsExtractor;
import com.wtwd.hfit.constant.BleConstant;
import com.wtwd.hfit.constant.Constant;
import com.wtwd.hfit.constant.SharedPrefsKey;
import com.wtwd.hfit.jzlib.GZIPHeader;
import com.wtwd.hfit.manager.DeviceManager;
import com.wtwd.hfit.ui.module.application.Application;
import com.wtwd.hfit.ui.module.main.camera.CameraPresenter;
import com.wtwd.hfit.utils.DataConvertUtils;
import com.wtwd.hfit.utils.PermissionsUtil;
import com.wtwd.hfit.utils.PrefUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import p090j2.U0;
import p245z3.a;

/* JADX INFO: loaded from: C:\Users\M0306\Downloads\ApWatch\classes4.dex */
public class BleConnectService implements g.a, SameScreenBleManager.SameScreenSendListener {
    private int bleConnectState;
    private BleConnectStateListener bleConnectStateListener;
    private BleReadDataListener bleReadDataListener;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    private Context context;
    private boolean isSendData;
    private boolean isZK;
    private int reconnectFrequency;
    private SameScreenBleManager sameScreenBleManager;
    private SameScreenReceiveListener sameScreenReceiveListener;
    private SendDataRunnable sendDataRunnable;
    private BluetoothGattCharacteristic ssWriteCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private ZKBleOtaDataListener zkBleOtaDataListener;
    private ZKBleOtaManager zkOtaManager;
    private BluetoothGattCharacteristic zkWriteCharacteristic;
    private List<byte[]> listReadData = new ArrayList();
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() { // from class: com.wtwd.hfit.ble.BleConnectService.5
        @Override // android.bluetooth.BluetoothGattCallback
        public final void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            byte[] value = bluetoothGattCharacteristic.getValue();
            if (BleConnectService.this.zkOtaManager != null && BleConnectService.this.zkOtaManager.p()) {
                boolean zU = BleConnectService.this.zkOtaManager.u(value);
                StringBuilder sb = new StringBuilder("消息");
                sb.append(zU ? "已" : "未");
                sb.append("处理");
                d.a(sb.toString());
            } else if (BleConnectService.this.bleReadDataListener != null) {
                UUID uuid = bluetoothGattCharacteristic.getUuid();
                if (uuid.toString().equals(BleConstant.UUID_READ)) {
                    if (BleConnectService.this.isSendData) {
                        synchronized (BleConnectService.this.listReadData) {
                            BleConnectService.this.listReadData.add(value);
                        }
                        return;
                    }
                    if (!BleConnectService.this.listReadData.isEmpty()) {
                        synchronized (BleConnectService.this.listReadData) {
                            try {
                                Iterator it = BleConnectService.this.listReadData.iterator();
                                while (it.hasNext()) {
                                    BleConnectService.this.bleReadDataListener.j(bluetoothGatt.getDevice(), (byte[]) it.next());
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                        BleConnectService.this.listReadData.clear();
                    }
                    BluetoothDevice device = bluetoothGatt.getDevice();
                    if (value.length >= 10) {
                        BleConnectService.this.bleReadDataListener.j(device, value);
                    }
                } else if (uuid.toString().equals(BleConstant.UUID_READ_AUDIO)) {
                    BleConnectService.this.bleReadDataListener.m(value);
                } else if (uuid.toString().equals(BleConstant.UUID_READ_SAME_SCREEN)) {
                    d.e("收到同屏数据 = " + a.a(value) + "大小=" + value.length, new Object[0]);
                    BleConnectService.this.sameScreenReceiveListener.G0(value);
                }
            }
            super.onCharacteristicChanged(bluetoothGatt, bluetoothGattCharacteristic);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public final void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i4) {
            d.a("onCharacteristicRead---" + i4);
            super.onCharacteristicRead(bluetoothGatt, bluetoothGattCharacteristic, i4);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public final void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i4) {
            d.a("write---" + Arrays.toString(bluetoothGattCharacteristic.getValue()));
            super.onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i4);
            if (i4 == 0 && BleConnectService.this.isSendData && BleConnectService.this.sendDataRunnable != null) {
                BleConnectService.this.handler.post(BleConnectService.this.sendDataRunnable);
            }
            if (i4 == 0 && BleConnectService.this.zkOtaManager != null && BleConnectService.this.zkOtaManager.p()) {
                BleConnectService.this.zkOtaManager.x();
            }
            if (i4 != 0 || BleConnectService.this.sameScreenBleManager == null) {
                return;
            }
            BleConnectService.this.sameScreenBleManager.b();
        }

        /* JADX WARN: Code duplicated, block: B:30:0x00e7  */
        @Override // android.bluetooth.BluetoothGattCallback
        public final void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i4, int i7) {
            if (i4 == 0) {
                Log.d("BleConnectService", "Bluetooth Ble gatt success---status:" + i4);
                Log.d("BleConnectService", "Bluetooth Ble gatt success---newState:" + i7);
                if (i7 == 0) {
                    BleConnectService.this.isSendData = false;
                    BleConnectService.this.bleConnectState = 5;
                    BleConnectService.this.writeCharacteristic = null;
                    BleConnectService.this.zkWriteCharacteristic = null;
                    BleConnectService.this.ssWriteCharacteristic = null;
                    if (BleConnectService.this.bleConnectStateListener != null) {
                        BleConnectService.this.bleConnectStateListener.b(bluetoothGatt.getDevice(), BleConnectService.this.bleConnectState);
                    }
                    if (BleConnectService.this.bluetoothGatt != null) {
                        BleConnectService.this.bluetoothGatt.close();
                        BleConnectService.this.bluetoothGatt = null;
                    }
                    if (BleConnectService.this.zkOtaManager != null && BleConnectService.this.zkOtaManager.p()) {
                        BleConnectService.this.zkOtaManager.A(false);
                        BleConnectService.this.zkOtaManager.C();
                        if (BleConnectService.this.zkOtaManager.dataProvider != null) {
                            b bVar = BleConnectService.this.zkOtaManager.dataProvider;
                            if (bVar.f18563e == bVar.f18559a.length) {
                                BleConnectService.this.zkOtaManager.s();
                            } else {
                                b bVar2 = BleConnectService.this.zkOtaManager.dataProvider;
                                bVar2.f18563e = 0;
                                bVar2.f18562d = 20;
                                bVar2.f18560b = -1;
                            }
                        } else {
                            BleConnectService.this.zkOtaManager.s();
                        }
                    }
                } else if (i7 == 2) {
                    if (Build.VERSION.SDK_INT >= 31 && p233y0.a.checkSelfPermission(BleConnectService.this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) != 0) {
                        return;
                    } else {
                        bluetoothGatt.discoverServices();
                    }
                }
            } else {
                Log.e("BleConnectService", "Bluetooth Ble gatt failure---status:" + i4);
                BleConnectService.this.isSendData = false;
                if (BleConnectService.this.bleConnectState == 2) {
                    BleConnectService.this.bleConnectState = 5;
                } else {
                    if (i4 == 133) {
                        if (BleConnectService.this.bluetoothGatt != null) {
                            BleConnectService.this.bluetoothGatt.disconnect();
                            BleConnectService.this.handler.postDelayed(new Runnable() { // from class: com.wtwd.hfit.ble.BleConnectService.5.1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    BleConnectService.B(BleConnectService.this);
                                }
                            }, 100L);
                        }
                        BleConnectService.C(BleConnectService.this);
                        return;
                    }
                    BleConnectService.this.handler.removeCallbacksAndMessages(null);
                    BleConnectService.this.bleConnectState = 3;
                }
                if (BleConnectService.this.bleConnectStateListener != null) {
                    BleConnectService.this.bleConnectStateListener.b(bluetoothGatt.getDevice(), BleConnectService.this.bleConnectState);
                }
                BleConnectService.this.bluetoothGatt.disconnect();
                BleConnectService.this.handler.postDelayed(new Runnable() { // from class: com.wtwd.hfit.ble.BleConnectService.5.2
                    @Override // java.lang.Runnable
                    public final void run() {
                        BleConnectService.B(BleConnectService.this);
                    }
                }, 100L);
            }
            super.onConnectionStateChange(bluetoothGatt, i4, i7);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public final void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i4) {
            if (i4 == 0) {
                Log.d("BleConnectService", "onDescriptorRead gatt success");
            } else {
                Log.d("BleConnectService", "onDescriptorRead gatt failure");
            }
            super.onDescriptorRead(bluetoothGatt, bluetoothGattDescriptor, i4);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public final void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i4) {
            if (i4 == 0) {
                Log.d("BleConnectService", "onDescriptorWrite gatt success");
                if (Build.VERSION.SDK_INT >= 31 && p233y0.a.checkSelfPermission(BleConnectService.this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) != 0) {
                    return;
                }
                BleConnectService.this.handler.removeCallbacks(BleConnectService.this.runnableStopConnect);
                BleConnectService.this.bleConnectState = 2;
                if (BleConnectService.this.bleConnectStateListener != null) {
                    BleConnectService.this.bleConnectStateListener.b(bluetoothGatt.getDevice(), BleConnectService.this.bleConnectState);
                }
            } else {
                BleConnectService.this.bleConnectState = 3;
                if (BleConnectService.this.bleConnectStateListener != null) {
                    BleConnectService.this.bleConnectStateListener.b(bluetoothGatt.getDevice(), BleConnectService.this.bleConnectState);
                }
                if (Build.VERSION.SDK_INT >= 31 && p233y0.a.checkSelfPermission(BleConnectService.this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) != 0) {
                    return;
                }
                BleConnectService.this.bluetoothGatt.disconnect();
                BleConnectService.this.handler.postDelayed(new Runnable() { // from class: com.wtwd.hfit.ble.BleConnectService.5.4
                    @Override // java.lang.Runnable
                    public final void run() {
                        BleConnectService.B(BleConnectService.this);
                    }
                }, 100L);
                Log.d("BleConnectService", "onDescriptorWrite gatt failure");
            }
            super.onDescriptorWrite(bluetoothGatt, bluetoothGattDescriptor, i4);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public final void onMtuChanged(BluetoothGatt bluetoothGatt, int i4, int i7) {
            super.onMtuChanged(bluetoothGatt, i4, i7);
            d.c(c.l(i4, "mtu"), new Object[0]);
            if (i7 == 0) {
                d.c(c.l(i4, "onMtuChanged mtu="), new Object[0]);
                d.a("发送数据mtu更新");
            } else {
                d.c(c.l(i4, "onMtuChanged fail"), new Object[0]);
            }
            if (i4 > 245) {
                PrefUtil.f(DeviceManager.g().b(), PsExtractor.VIDEO_STREAM_MASK, SharedPrefsKey.PREF_KEY_RATE);
            } else {
                PrefUtil.f(DeviceManager.g().b(), i4 - 5, SharedPrefsKey.PREF_KEY_RATE);
            }
            d.a("是否支持2M" + BleConnectService.this.bluetoothAdapter.isLe2MPhySupported() + "是否支持PHY" + BleConnectService.this.bluetoothAdapter.isLeCodedPhySupported() + "是否支持LE扩展" + BleConnectService.this.bluetoothAdapter.isLeExtendedAdvertisingSupported());
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public final void onPhyRead(BluetoothGatt bluetoothGatt, int i4, int i7, int i10) {
            super.onPhyRead(bluetoothGatt, i4, i7, i10);
            StringBuilder sbC = U0.c(i4, i7, "onPhyRead发送速率", "接收速率", "更新操作的状态");
            sbC.append(i10);
            d.a(sbC.toString());
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public final void onPhyUpdate(BluetoothGatt bluetoothGatt, int i4, int i7, int i10) {
            super.onPhyUpdate(bluetoothGatt, i4, i7, i10);
            StringBuilder sbC = U0.c(i4, i7, "onPhyUpdate发送速率", "接收速率", "更新操作的状态");
            sbC.append(i10);
            d.a(sbC.toString());
        }

        @Override // android.bluetooth.BluetoothGattCallback
        @SuppressLint({"NewApi"})
        public final void onServicesDiscovered(final BluetoothGatt bluetoothGatt, int i4) {
            if (i4 == 0) {
                BluetoothGattService service = bluetoothGatt.getService(UUID.fromString("0000e91a-0000-1000-8000-00805f9b34fb"));
                if (Build.VERSION.SDK_INT >= 31 && p233y0.a.checkSelfPermission(BleConnectService.this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) != 0) {
                    return;
                }
                d.a("服务的UUID" + service.getUuid().toString());
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : service.getCharacteristics()) {
                    if (bluetoothGattCharacteristic.getUuid().equals(UUID.fromString(BleConstant.UUID_READ_AUDIO))) {
                        bluetoothGatt.setCharacteristicNotification(service.getCharacteristic(UUID.fromString(BleConstant.UUID_READ_AUDIO)), true);
                        d.a("音频数据读通道状态");
                    } else if (bluetoothGattCharacteristic.getUuid().equals(UUID.fromString(BleConstant.UUID_READ))) {
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(BleConstant.UUID_READ));
                        bluetoothGatt.setCharacteristicNotification(characteristic, true);
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BleConstant.UUID_SYSTEM));
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        d.a("主读通道状态" + bluetoothGatt.writeDescriptor(descriptor));
                    } else if (bluetoothGattCharacteristic.getUuid().equals(UUID.fromString(BleConstant.UUID_WRITE))) {
                        BleConnectService.this.writeCharacteristic = service.getCharacteristic(UUID.fromString(BleConstant.UUID_WRITE));
                        d.a("主写通道状态无");
                    } else if (bluetoothGattCharacteristic.getUuid().equals(UUID.fromString(BleConstant.UUID_WRITE_ZK))) {
                        BleConnectService.this.zkWriteCharacteristic = service.getCharacteristic(UUID.fromString(BleConstant.UUID_WRITE_ZK));
                        d.a("中科OTA写通道状态无");
                    } else if (bluetoothGattCharacteristic.getUuid().equals(UUID.fromString(BleConstant.UUID_READ_SAME_SCREEN))) {
                        bluetoothGatt.setCharacteristicNotification(service.getCharacteristic(UUID.fromString(BleConstant.UUID_READ_SAME_SCREEN)), true);
                        d.a("同屏数据读通道状态");
                    } else if (bluetoothGattCharacteristic.getUuid().equals(UUID.fromString(BleConstant.UUID_WRITE_SAME_SCREEN))) {
                        BleConnectService.this.ssWriteCharacteristic = service.getCharacteristic(UUID.fromString(BleConstant.UUID_WRITE_SAME_SCREEN));
                        BleConnectService.this.sameScreenBleManager = new SameScreenBleManager();
                        BleConnectService.D(BleConnectService.this);
                        d.a("同屏写通道状态");
                    }
                    d.a("服务的UUID的读写UUID" + bluetoothGattCharacteristic.getUuid().toString());
                }
                BleConnectService.this.handler.postDelayed(new Runnable() { // from class: com.wtwd.hfit.ble.BleConnectService.5.3
                    @Override // java.lang.Runnable
                    public final void run() {
                        if (Build.VERSION.SDK_INT < 31 || p233y0.a.checkSelfPermission(BleConnectService.this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) == 0) {
                            bluetoothGatt.requestMtu(245);
                        }
                    }
                }, 100L);
            } else if (BleConnectService.this.zkOtaManager != null && BleConnectService.this.zkOtaManager.p()) {
                BleConnectService.this.zkOtaManager.A(false);
                BleConnectService.this.zkOtaManager.C();
                BleConnectService.this.zkOtaManager.s();
                BleConnectService.this.zkOtaManager.w();
            }
            super.onServicesDiscovered(bluetoothGatt, i4);
        }
    };
    private Runnable runnableStopConnect = new Runnable() { // from class: com.wtwd.hfit.ble.BleConnectService.6
        @Override // java.lang.Runnable
        public final void run() {
            if (BleConnectService.this.bluetoothGatt != null) {
                BleConnectService.this.bleConnectState = 3;
                if (BleConnectService.this.bleConnectStateListener != null) {
                    BleConnectService.this.bleConnectStateListener.b(BleConnectService.this.bluetoothDevice, BleConnectService.this.bleConnectState);
                }
                if (BleConnectService.this.bluetoothGatt != null) {
                    if (Build.VERSION.SDK_INT >= 31 && p233y0.a.checkSelfPermission(BleConnectService.this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) != 0) {
                        return;
                    }
                    BleConnectService.this.bluetoothGatt.disconnect();
                    BleConnectService.this.handler.postDelayed(new Runnable() { // from class: com.wtwd.hfit.ble.BleConnectService.6.1
                        @Override // java.lang.Runnable
                        public final void run() {
                            BleConnectService.B(BleConnectService.this);
                        }
                    }, 100L);
                }
                BleConnectService.this.handler.removeCallbacks(BleConnectService.this.runnableStopConnect);
            }
        }
    };
    private Handler handler = new Handler();

    /* JADX INFO: renamed from: com.wtwd.hfit.ble.BleConnectService$1, reason: invalid class name */
    class AnonymousClass1 extends Thread {
        public AnonymousClass1() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public final void run() {
            if (Build.VERSION.SDK_INT < 31 || p233y0.a.checkSelfPermission(BleConnectService.this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) == 0) {
                BleConnectService bleConnectService = BleConnectService.this;
                bleConnectService.bluetoothGatt = bleConnectService.bluetoothDevice.connectGatt(BleConnectService.this.context, false, BleConnectService.this.bluetoothGattCallback, 2);
            }
        }
    }

    public interface BleConnectStateListener {
        void b(BluetoothDevice bluetoothDevice, int i4);
    }

    public interface BleReadDataListener {
        void j(BluetoothDevice bluetoothDevice, byte[] bArr);

        void m(byte[] bArr);
    }

    public interface SameScreenReceiveListener {
        void G0(byte[] bArr);
    }

    public class SendDataRunnable implements Runnable {
        private List<byte[]> listData = new ArrayList();
        private int currentSendFrequency = 0;

        public SendDataRunnable(byte[] bArr) {
            int iH = (DeviceManager.g().h() / 20) * 20;
            for (int i4 = 0; i4 < bArr.length / iH; i4++) {
                byte[] bArr2 = new byte[iH];
                System.arraycopy(bArr, i4 * iH, bArr2, 0, iH);
                this.listData.add(bArr2);
            }
            if (bArr.length % iH != 0) {
                int length = bArr.length % iH;
                byte[] bArr3 = new byte[length];
                System.arraycopy(bArr, (bArr.length / iH) * iH, bArr3, 0, length);
                this.listData.add(bArr3);
            }
        }

        public final void a(byte[] bArr) {
            int iH = (DeviceManager.g().h() / 20) * 20;
            for (int i4 = 0; i4 < bArr.length / iH; i4++) {
                byte[] bArr2 = new byte[iH];
                System.arraycopy(bArr, i4 * iH, bArr2, 0, iH);
                this.listData.add(bArr2);
            }
            if (bArr.length % iH != 0) {
                int length = bArr.length % iH;
                byte[] bArr3 = new byte[length];
                System.arraycopy(bArr, (bArr.length / iH) * iH, bArr3, 0, length);
                this.listData.add(bArr3);
            }
        }

        @Override // java.lang.Runnable
        public final void run() {
            if (this.currentSendFrequency >= this.listData.size()) {
                return;
            }
            byte[] bArr = this.listData.get(this.currentSendFrequency);
            if (BleConnectService.this.writeCharacteristic != null && BleConnectService.this.bluetoothGatt != null) {
                BleConnectService.this.writeCharacteristic.setValue(bArr);
                if (Build.VERSION.SDK_INT >= 31 && p233y0.a.checkSelfPermission(BleConnectService.this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) != 0) {
                    return;
                }
                d.a("发送原始数据" + DataConvertUtils.a(bArr));
                boolean zWriteCharacteristic = BleConnectService.this.bluetoothGatt.writeCharacteristic(BleConnectService.this.writeCharacteristic);
                d.a("发送数据状态" + zWriteCharacteristic);
                if (!zWriteCharacteristic) {
                    return;
                }
            }
            int i4 = this.currentSendFrequency + 1;
            this.currentSendFrequency = i4;
            if (i4 >= this.listData.size()) {
                BleConnectService.this.handler.removeCallbacks(BleConnectService.this.sendDataRunnable);
                BleConnectService.this.sendDataRunnable = null;
                BleConnectService.this.isSendData = false;
            }
        }
    }

    public interface ZKBleOtaDataListener {
        void a(int i4);

        void e();

        void f();

        void g();

        void h();

        void i(int i4);

        void k(int i4);

        void l();

        void p();
    }

    public BleConnectService(Context context, BluetoothAdapter bluetoothAdapter) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public static void B(BleConnectService bleConnectService) {
        synchronized (bleConnectService) {
            if (bleConnectService.bluetoothGatt != null) {
                try {
                    try {
                        if (p233y0.a.checkSelfPermission(bleConnectService.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) != 0) {
                            bleConnectService.bluetoothGatt = null;
                        } else {
                            bleConnectService.bluetoothGatt.close();
                            bleConnectService.bluetoothGatt = null;
                        }
                    } catch (Exception e7) {
                        d.c("Error closing bluetoothGatt: " + e7.getMessage(), new Object[0]);
                    }
                } catch (Throwable th) {
                    bleConnectService.bluetoothGatt = null;
                    throw th;
                }
            }
        }
    }

    public static void C(BleConnectService bleConnectService) {
        int i4 = bleConnectService.reconnectFrequency;
        if (i4 != 5) {
            bleConnectService.reconnectFrequency = i4 + 1;
            BluetoothGatt bluetoothGatt = bleConnectService.bluetoothGatt;
            if (bluetoothGatt != null && bluetoothGatt.getConnectedDevices() != null) {
                Iterator<BluetoothDevice> it = bleConnectService.bluetoothGatt.getConnectedDevices().iterator();
                while (it.hasNext()) {
                    if (it.next().equals(bleConnectService.bluetoothDevice)) {
                        bleConnectService.bluetoothGatt.disconnect();
                    }
                }
                bleConnectService.handler.postDelayed(new Runnable() { // from class: com.wtwd.hfit.ble.BleConnectService.3
                    @Override // java.lang.Runnable
                    public final void run() {
                        BleConnectService.B(BleConnectService.this);
                    }
                }, 100L);
            }
            bleConnectService.isSendData = false;
            bleConnectService.new AnonymousClass1().start();
            return;
        }
        bleConnectService.reconnectFrequency = 0;
        bleConnectService.handler.removeCallbacksAndMessages(null);
        bleConnectService.bleConnectState = 3;
        BleConnectStateListener bleConnectStateListener = bleConnectService.bleConnectStateListener;
        if (bleConnectStateListener != null) {
            bleConnectStateListener.b(bleConnectService.bluetoothDevice, 3);
        }
        if (bleConnectService.bluetoothGatt != null) {
            if (Build.VERSION.SDK_INT < 31 || p233y0.a.checkSelfPermission(bleConnectService.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) == 0) {
                bleConnectService.bluetoothGatt.disconnect();
                bleConnectService.handler.postDelayed(new Runnable() { // from class: com.wtwd.hfit.ble.BleConnectService.2
                    @Override // java.lang.Runnable
                    public final void run() {
                        BleConnectService.B(BleConnectService.this);
                    }
                }, 100L);
            }
        }
    }

    public static void D(BleConnectService bleConnectService) {
        bleConnectService.sameScreenBleManager.c(bleConnectService);
    }

    public final void E(BlePresenter blePresenter) {
        this.bleConnectStateListener = blePresenter;
    }

    public final void F(BlePresenter blePresenter) {
        this.bleReadDataListener = blePresenter;
    }

    public final void G(CameraPresenter cameraPresenter) {
        this.sameScreenReceiveListener = cameraPresenter;
    }

    public final void H(BlePresenter blePresenter) {
        this.zkBleOtaDataListener = blePresenter;
    }

    public final void I() {
        this.bleConnectStateListener = null;
        this.bleReadDataListener = null;
        K();
    }

    public final void J(int i4, String str) {
        boolean zA = PrefUtil.a(Application.k().getApplicationContext(), SharedPrefsKey.PREF_KEY_ZK_OTA);
        this.isZK = zA;
        if (zA) {
            this.zkOtaManager = new ZKBleOtaManager(this.context, this.bluetoothDevice, this);
        } else {
            this.zkOtaManager = null;
        }
        int i7 = this.bleConnectState;
        if (i7 == 2 || i7 == 1) {
            return;
        }
        BluetoothDevice remoteDevice = this.bluetoothAdapter.getRemoteDevice(str);
        this.bluetoothDevice = remoteDevice;
        this.bleConnectState = 1;
        BleConnectStateListener bleConnectStateListener = this.bleConnectStateListener;
        if (bleConnectStateListener != null) {
            bleConnectStateListener.b(remoteDevice, 1);
        }
        this.handler.postDelayed(this.runnableStopConnect, i4);
        this.isSendData = false;
        new AnonymousClass1().start();
    }

    public final void K() {
        this.isSendData = false;
        this.bleConnectState = 5;
        BleConnectStateListener bleConnectStateListener = this.bleConnectStateListener;
        if (bleConnectStateListener != null) {
            bleConnectStateListener.b(this.bluetoothDevice, 5);
        }
        if (this.bluetoothGatt != null) {
            if (Build.VERSION.SDK_INT < 31 || p233y0.a.checkSelfPermission(this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) == 0) {
                this.bluetoothGatt.disconnect();
                this.handler.postDelayed(new Runnable() { // from class: com.wtwd.hfit.ble.BleConnectService.4
                    @Override // java.lang.Runnable
                    public final void run() {
                        BleConnectService.B(BleConnectService.this);
                    }
                }, 100L);
            }
        }
    }

    public final int L() {
        return this.bleConnectState;
    }

    public final void M() {
        d.a("升级完成");
        this.zkBleOtaDataListener.h();
    }

    public final void N() {
        d.a("正在升级固件...");
        this.zkBleOtaDataListener.l();
    }

    public final void O(int i4) {
        d.a("升级错误" + p.c(i4));
        this.zkBleOtaDataListener.k(i4);
        f0();
    }

    public final void P() {
        d.a("主机升级完成，等待连接副机");
        this.zkBleOtaDataListener.getClass();
    }

    public final void Q() {
        d.a("升级已暂停");
        this.zkBleOtaDataListener.p();
    }

    public final void R(int i4) {
        d.a("OTA进度：" + i4);
        this.zkBleOtaDataListener.a(i4);
    }

    public final void S() {
        d.a("准备就绪");
        this.zkBleOtaDataListener.e();
    }

    public final void T() {
        d.a("正在升级固件...");
        this.zkBleOtaDataListener.f();
    }

    public final void U() {
        d.a("蓝牙断开");
        this.zkBleOtaDataListener.g();
    }

    public final void V(boolean z10) {
        d.a("升级耳机：".concat(z10 ? "左耳" : "右耳"));
        this.zkBleOtaDataListener.getClass();
    }

    public final void W(boolean z10) {
        d.a("耳机连接状态：" + z10);
        this.zkBleOtaDataListener.getClass();
    }

    public final void X(boolean z10) {
        d.a("耳机连接状态：" + z10);
        this.zkBleOtaDataListener.getClass();
    }

    public final void Y(int i4) {
        d.a("获取到固件版本号：".concat(String.format(Locale.getDefault(), "版本号：%d.%d.%d.%d", Integer.valueOf((i4 >> 12) & 15), Integer.valueOf((i4 >> 8) & 15), Integer.valueOf((i4 >> 4) & 15), Integer.valueOf(i4 & 15))));
        this.zkBleOtaDataListener.i(i4);
    }

    public final void Z() {
        d.a("TWS已断开，停止升级...");
        this.zkBleOtaDataListener.getClass();
    }

    @Override // com.wtwd.hfit.ble.SameScreenBleManager.SameScreenSendListener
    public final void a(byte[] bArr) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = this.ssWriteCharacteristic;
        if (bluetoothGattCharacteristic == null || this.bluetoothGatt == null) {
            return;
        }
        bluetoothGattCharacteristic.setValue(bArr);
        this.ssWriteCharacteristic.setWriteType(1);
        if (Build.VERSION.SDK_INT < 31 || p233y0.a.checkSelfPermission(this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) == 0) {
            d.e("同屏分包写入数据 = " + a.a(bArr) + "大小=" + bArr.length, new Object[0]);
            this.bluetoothGatt.writeCharacteristic(this.ssWriteCharacteristic);
        }
    }

    public final void a0(byte[] bArr) {
        if (this.zkWriteCharacteristic == null || this.bluetoothGatt == null) {
            return;
        }
        d.e("writeZKData = ".concat(a.a(bArr)), new Object[0]);
        this.zkWriteCharacteristic.setValue(bArr);
        this.zkWriteCharacteristic.setWriteType(1);
        if (Build.VERSION.SDK_INT < 31 || p233y0.a.checkSelfPermission(this.context, PermissionsUtil.PERMISSION_BLUETOOTH_CONNECT) == 0) {
            this.bluetoothGatt.writeCharacteristic(this.zkWriteCharacteristic);
        }
    }

    public final void b0() {
        this.sameScreenReceiveListener = null;
    }

    public final void c0() {
        this.zkBleOtaDataListener = null;
    }

    public final void d0() {
        this.bleConnectState = 0;
    }

    public final void e0(@NonNull byte[] bArr) {
        this.zkOtaManager.A(true);
        if (this.zkOtaManager.q()) {
            this.zkOtaManager.y();
        } else {
            this.zkOtaManager.l();
        }
        this.zkOtaManager.B(bArr);
    }

    public final void f0() {
        if (this.bluetoothGatt == null) {
            this.zkOtaManager.r(2);
            return;
        }
        if (this.zkWriteCharacteristic == null) {
            this.zkOtaManager.r(5);
        }
        this.zkOtaManager.D();
    }

    public final void g0(byte[] bArr) {
        int i4 = bArr[5] & GZIPHeader.OS_UNKNOWN;
        if (Constant.IS_START_BIG_SEND && i4 != 131) {
            d.a("添加大发送数据=" + i4 + "DATA_TYPE_DIAL_SYNC=131");
            return;
        }
        d.a("添加大发送数据=" + i4);
        if (this.isSendData) {
            SendDataRunnable sendDataRunnable = this.sendDataRunnable;
            if (sendDataRunnable != null) {
                sendDataRunnable.a(bArr);
            }
            d.a("添加发送数据");
            return;
        }
        this.isSendData = true;
        this.listReadData.clear();
        SendDataRunnable sendDataRunnable2 = new SendDataRunnable(bArr);
        this.sendDataRunnable = sendDataRunnable2;
        this.handler.post(sendDataRunnable2);
        d.a("创建发送数据");
    }

    public final void h0(byte[] bArr) {
        this.sameScreenBleManager.a(bArr);
    }

    @Override // com.wtwd.hfit.ble.SameScreenBleManager.SameScreenSendListener
    public final void b() {
    }
}
