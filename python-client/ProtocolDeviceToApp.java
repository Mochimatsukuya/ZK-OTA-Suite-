package com.wtwd.hfit.protocol;

import A6.d;
import com.wtwd.hfit.entity.device.AlarmInfo;
import com.wtwd.hfit.entity.device.DeviceInfo;
import com.wtwd.hfit.entity.health.heart.HeartRateInfo;
import com.wtwd.hfit.entity.health.sport.SportInfo;
import com.wtwd.hfit.entity.health.temp.TempInfo;
import com.wtwd.hfit.jzlib.GZIPHeader;
import com.wtwd.hfit.utils.DataConvertUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: C:\Users\M0306\Downloads\ApWatch\classes4.dex */
public class ProtocolDeviceToApp {
    public static ArrayList a(byte[] bArr) {
        int i4 = bArr[0] & GZIPHeader.OS_UNKNOWN;
        ArrayList arrayList = new ArrayList();
        for (int i7 = 0; i7 < i4; i7++) {
            AlarmInfo alarmInfo = new AlarmInfo();
            int i10 = i7 * 5;
            alarmInfo.F(bArr[i10 + 1] & GZIPHeader.OS_UNKNOWN);
            alarmInfo.A(bArr[i10 + 2] & GZIPHeader.OS_UNKNOWN);
            alarmInfo.w(bArr[i10 + 3] & GZIPHeader.OS_UNKNOWN);
            alarmInfo.y(bArr[i10 + 4] & GZIPHeader.OS_UNKNOWN);
            alarmInfo.C(bArr[i10 + 5] & GZIPHeader.OS_UNKNOWN);
            arrayList.add(alarmInfo);
        }
        return arrayList;
    }

    public static DeviceInfo b(byte[] bArr) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.t(bArr[0] & GZIPHeader.OS_UNKNOWN);
        deviceInfo.h(bArr[1] & GZIPHeader.OS_UNKNOWN);
        deviceInfo.j(bArr[2] & GZIPHeader.OS_UNKNOWN);
        deviceInfo.f(bArr[3] & GZIPHeader.OS_UNKNOWN);
        deviceInfo.u(bArr[4] & GZIPHeader.OS_UNKNOWN);
        deviceInfo.i(bArr[5] & GZIPHeader.OS_UNKNOWN);
        return deviceInfo;
    }

    public static ArrayList c(byte[] bArr) {
        int i4 = bArr[2] & GZIPHeader.OS_UNKNOWN;
        if (i4 > (bArr.length - 3) / 5) {
            d.a("心率数据错误");
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList();
        for (int i7 = 0; i7 < i4; i7++) {
            int i10 = i7 * 5;
            arrayList.add(new HeartRateInfo(DataConvertUtils.c(i10 + 3, bArr), bArr[i10 + 7]));
        }
        return arrayList;
    }

    public static int d(byte[] bArr) {
        return bArr[0];
    }

    public static ArrayList e(byte[] bArr) {
        int i4 = bArr[2] & GZIPHeader.OS_UNKNOWN;
        if (i4 != (bArr.length - 3) / 20) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i7 = 0; i7 < i4; i7++) {
            int i10 = i7 * 20;
            arrayList.add(new SportInfo(DataConvertUtils.c(i10 + 3, bArr), DataConvertUtils.c(i10 + 7, bArr), DataConvertUtils.c(i10 + 11, bArr), DataConvertUtils.c(i10 + 15, bArr), DataConvertUtils.c(i10 + 19, bArr)));
        }
        return arrayList;
    }

    public static ArrayList f(byte[] bArr, boolean z10) {
        int i4 = bArr[2] & GZIPHeader.OS_UNKNOWN;
        int i7 = z10 ? 8 : 6;
        ArrayList arrayList = new ArrayList();
        for (int i10 = 0; i10 < i4; i10++) {
            int i11 = i10 * i7;
            int iC = DataConvertUtils.c(i11 + 3, bArr);
            int iB = DataConvertUtils.b(i11 + 7, 2, bArr);
            TempInfo tempInfo = new TempInfo();
            tempInfo.l(iC);
            tempInfo.g(iB);
            if (z10) {
                tempInfo.k(DataConvertUtils.b(i11 + 9, 2, bArr));
            }
            arrayList.add(tempInfo);
        }
        return arrayList;
    }
}
