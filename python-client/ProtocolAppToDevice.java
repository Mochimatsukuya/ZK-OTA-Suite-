package com.wtwd.hfit.protocol;

import android.text.TextUtils;
import com.wtwd.hfit.entity.device.AlarmInfo;
import com.wtwd.hfit.entity.device.MedicineInfo;
import com.wtwd.hfit.entity.device.NoticeContent;
import com.wtwd.hfit.entity.device.WeatherInfo;
import com.wtwd.hfit.utils.DataConvertUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/* JADX INFO: loaded from: C:\Users\M0306\Downloads\ApWatch\classes4.dex */
public class ProtocolAppToDevice {
    public static int pid;

    public static ArrayList a(int i4, String str) {
        int i7;
        int i10 = 300;
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(str)) {
            arrayList.add(g(0, 1, ProtocolEnum.DataType.DATA_TYPE_AI_TEXT, new byte[]{(byte) i4}));
        } else {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            int length = bytes.length;
            int i11 = length / 300;
            if (length % 300 != 0) {
                i11++;
            }
            for (int i12 = 0; i12 < i11; i12++) {
                int i13 = i12 * i10;
                if (i12 == i11 - 1 && (i7 = length % i10) != 0) {
                    i10 = i7;
                }
                byte[] bArr = new byte[i10 + 9];
                bArr[0] = (byte) i4;
                System.arraycopy(DataConvertUtils.d(length), 0, bArr, 1, 4);
                System.arraycopy(DataConvertUtils.d(i13), 0, bArr, 5, 4);
                System.arraycopy(bytes, i13, bArr, 9, i10);
                arrayList.add(g(0, 1, ProtocolEnum.DataType.DATA_TYPE_AI_TEXT, bArr));
            }
        }
        return arrayList;
    }

    public static byte[] b(List<AlarmInfo> list) {
        byte[] bArr = new byte[(list.size() * 5) + 1];
        bArr[0] = (byte) list.size();
        for (int i4 = 0; i4 < list.size(); i4++) {
            AlarmInfo alarmInfo = list.get(i4);
            int i7 = i4 * 5;
            bArr[i7 + 1] = (byte) alarmInfo.t();
            bArr[i7 + 2] = (byte) alarmInfo.u();
            bArr[i7 + 3] = (byte) alarmInfo.b();
            bArr[i7 + 4] = (byte) alarmInfo.d();
            bArr[i7 + 5] = (byte) alarmInfo.h();
        }
        return g(0, 1, 106, bArr);
    }

    public static byte[] c(int i4, byte[] bArr) {
        int length = bArr.length + 3;
        byte[] bArr2 = new byte[length];
        byte[] bArrD = DataConvertUtils.d(length);
        bArr2[0] = bArrD[0];
        bArr2[1] = bArrD[1];
        bArr2[2] = (byte) i4;
        System.arraycopy(bArr, 0, bArr2, 3, bArr.length);
        return bArr2;
    }

    public static byte[] d(int i4) {
        return g(0, 1, 117, new byte[]{(byte) i4});
    }

    public static byte[] e(int i4, long j10) {
        byte[] bArr = new byte[9];
        System.arraycopy(DataConvertUtils.d((int) (j10 / 1000)), 0, bArr, 0, 4);
        System.arraycopy(DataConvertUtils.d(TimeZone.getDefault().getOffset(j10) / 1000), 0, bArr, 4, 4);
        bArr[8] = (byte) (i4 & 255);
        return bArr;
    }

    public static byte[] f(int i4, int i7, int i10, int i11, int i12) {
        byte[] bArr = {0, 0, 0, 0, (byte) i7, (byte) i10, (byte) i11, (byte) i12, (byte) 0};
        System.arraycopy(DataConvertUtils.d(i4), 0, bArr, 0, 4);
        return bArr;
    }

    public static byte[] g(int i4, int i7, int i10, byte[] bArr) {
        int length = bArr.length;
        byte[] bArrD = DataConvertUtils.d(length);
        int i11 = length - 10;
        int i12 = i11 / 19;
        int i13 = i11 % 19;
        if (i13 > 0) {
            i12++;
        }
        byte[] bArr2 = new byte[(i12 * 20) + 20];
        int i14 = 0;
        bArr2[0] = 0;
        bArr2[1] = (byte) pid;
        bArr2[2] = (byte) i12;
        bArr2[3] = (byte) i4;
        bArr2[4] = (byte) i7;
        bArr2[5] = (byte) i10;
        bArr2[6] = 0;
        bArr2[7] = 0;
        System.arraycopy(bArrD, 0, bArr2, 8, 2);
        if (bArr.length < 10) {
            System.arraycopy(bArr, 0, bArr2, 10, bArr.length);
            return bArr2;
        }
        System.arraycopy(bArr, 0, bArr2, 10, 10);
        while (i14 < i12) {
            int i15 = i14 * 20;
            int i16 = i14 + 1;
            bArr2[i15 + 20] = (byte) i16;
            System.arraycopy(bArr, (i14 * 19) + 10, bArr2, i15 + 21, (i14 != i12 + (-1) || i13 == 0) ? 19 : i13);
            i14 = i16;
        }
        return bArr2;
    }

    public static byte[] h(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        int length = 0;
        while (it.hasNext()) {
            length += ((MedicineInfo) it.next()).i().getBytes(StandardCharsets.UTF_8).length + 15;
        }
        byte[] bArr = new byte[length + 1];
        bArr[0] = (byte) arrayList.size();
        int length2 = 1;
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            MedicineInfo medicineInfo = (MedicineInfo) arrayList.get(i4);
            byte[] bArr2 = new byte[15];
            byte[] bArrE = DataConvertUtils.e((int) (medicineInfo.t() / 1000), 4);
            System.arraycopy(bArrE, 0, bArr2, 0, bArrE.length);
            byte[] bArr3 = {(byte) ((medicineInfo.e() / 3600) & 255), (byte) ((medicineInfo.e() % 3600) & 255)};
            System.arraycopy(bArr3, 0, bArr2, 4, 2);
            bArr3[0] = (byte) ((medicineInfo.h() / 3600) & 255);
            bArr3[1] = (byte) ((medicineInfo.h() % 3600) & 255);
            System.arraycopy(bArr3, 0, bArr2, 6, 2);
            bArr3[0] = (byte) ((medicineInfo.f() / 3600) & 255);
            bArr3[1] = (byte) ((medicineInfo.f() % 3600) & 255);
            System.arraycopy(bArr3, 0, bArr2, 8, 2);
            bArr3[0] = (byte) ((medicineInfo.d() / 3600) & 255);
            bArr3[1] = (byte) ((medicineInfo.d() % 3600) & 255);
            System.arraycopy(bArr3, 0, bArr2, 10, 2);
            bArr2[12] = (byte) (medicineInfo.c() & 255);
            bArr2[13] = (byte) medicineInfo.u();
            byte[] bytes = medicineInfo.i().getBytes(StandardCharsets.UTF_8);
            bArr2[14] = (byte) bytes.length;
            System.arraycopy(bArr2, 0, bArr, length2, 15);
            System.arraycopy(bytes, 0, bArr, length2 + 15, bytes.length);
            length2 += bytes.length + 15;
        }
        return g(0, 1, ProtocolEnum.DataType.DATA_TYPE_MEDICINE, bArr);
    }

    public static byte[] i(int i4, int i7, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17) {
        byte[] bArr = new byte[22];
        bArr[0] = (byte) 1;
        System.arraycopy(DataConvertUtils.d(i4), 0, bArr, 1, 4);
        bArr[5] = (byte) i7;
        bArr[6] = (byte) i10;
        bArr[7] = (byte) i13;
        bArr[8] = (byte) i12;
        bArr[9] = (byte) i11;
        bArr[10] = (byte) i14;
        bArr[11] = (byte) i15;
        bArr[12] = (byte) i16;
        bArr[13] = (byte) i17;
        return g(0, 1, 133, bArr);
    }

    public static byte[] j(int i4, String str) {
        if (str.getBytes().length > 62) {
            while (str.getBytes().length > 24) {
                str = str.substring(0, str.length() - 1);
            }
        }
        byte[] bArr = new byte[66];
        bArr[0] = (byte) i4;
        if (i4 == 8 || i4 == 9 || i4 == 10) {
            bArr[1] = 1;
            bArr[2] = (byte) Integer.parseInt(str);
        } else {
            byte[] bytes = str.getBytes();
            bArr[1] = (byte) bytes.length;
            System.arraycopy(bytes, 0, bArr, 2, bytes.length);
        }
        return g(0, 1, 113, bArr);
    }

    public static byte[] k(ArrayList arrayList, int i4, int i7) {
        byte[] bArr = new byte[6];
        System.arraycopy(DataConvertUtils.d(i4), 0, bArr, 0, 4);
        bArr[4] = (byte) i7;
        bArr[5] = (byte) arrayList.size();
        int i10 = 0;
        while (i10 < arrayList.size()) {
            NoticeContent noticeContent = (NoticeContent) arrayList.get(i10);
            int iA = noticeContent.a();
            String strB = noticeContent.b();
            byte[] bArrF = iA == 1 ? DataConvertUtils.f(strB) : strB.getBytes();
            while (bArrF.length > 228 - bArr.length) {
                strB = strB.substring(0, strB.length() - 1);
                bArrF = iA == 1 ? DataConvertUtils.f(strB) : strB.getBytes();
            }
            byte[] bArr2 = new byte[bArr.length + 2 + bArrF.length];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            bArr2[bArr.length] = (byte) iA;
            bArr2[bArr.length + 1] = (byte) bArrF.length;
            System.arraycopy(bArrF, 0, bArr2, bArr.length + 2, bArrF.length);
            i10++;
            bArr = bArr2;
        }
        return g(0, 1, 107, bArr);
    }

    public static byte[] l(int i4, byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length + 3];
        bArr2[0] = (byte) i4;
        byte[] bArrD = DataConvertUtils.d(bArr.length);
        bArr2[1] = bArrD[0];
        bArr2[2] = bArrD[1];
        System.arraycopy(bArr, 0, bArr2, 3, bArr.length);
        return g(0, 1, 138, bArr2);
    }

    public static byte[] m(double d10) {
        byte[] bArrE = DataConvertUtils.e((int) ((101.32d - (0.011d * d10)) * 100.0d), 2);
        byte[] bArrE2 = DataConvertUtils.e((int) d10, 2);
        return g(0, 1, ProtocolEnum.DataType.DATA_TYPE_AIR_PRESSURE_ALTITUDE, new byte[]{bArrE[0], bArrE[1], bArrE2[0], bArrE2[1], 0, 0, 0, 0});
    }

    public static ArrayList n(int i4, int i7, int i10, int i11, int i12, int i13, byte[] bArr) {
        int i14;
        if (bArr == null) {
            return null;
        }
        int length = bArr.length + 10;
        byte[] bArr2 = new byte[length];
        bArr2[0] = (byte) i4;
        bArr2[1] = (byte) i7;
        bArr2[2] = (byte) i10;
        byte[] bArrD = DataConvertUtils.d(ProtocolUtils.i(i11));
        bArr2[3] = bArrD[0];
        bArr2[4] = bArrD[1];
        bArr2[5] = 2;
        byte[] bArrD2 = DataConvertUtils.d(i12);
        bArr2[6] = bArrD2[0];
        bArr2[7] = bArrD2[1];
        byte[] bArrD3 = DataConvertUtils.d(i13);
        bArr2[8] = bArrD3[0];
        bArr2[9] = bArrD3[1];
        System.arraycopy(bArr, 0, bArr2, 10, bArr.length);
        ArrayList arrayList = new ArrayList();
        int i15 = length / 420;
        if (length % 420 != 0) {
            i15++;
        }
        int i16 = 420;
        int i17 = 0;
        while (i17 < i15) {
            if (i17 != i15 - 1 || (i14 = length % i16) == 0) {
                i14 = i16;
            }
            byte[] bArr3 = new byte[i14 + 9];
            bArr3[0] = 2;
            System.arraycopy(DataConvertUtils.d(length), 0, bArr3, 1, 4);
            int i18 = i16 * i17;
            System.arraycopy(DataConvertUtils.d(i18), 0, bArr3, 5, 4);
            System.arraycopy(bArr2, i18, bArr3, 9, i14);
            arrayList.add(g(0, 1, 131, bArr3));
            i17++;
            i16 = i14;
        }
        return arrayList;
    }

    public static byte[] o(int i4, ArrayList arrayList) {
        byte[] bArr = arrayList.size() > 3 ? new byte[19] : new byte[(arrayList.size() * 5) + 4];
        System.arraycopy(DataConvertUtils.d(i4), 0, bArr, 0, 4);
        for (int i7 = 0; i7 < arrayList.size() && arrayList.size() <= 3; i7++) {
            WeatherInfo weatherInfo = (WeatherInfo) arrayList.get(i7);
            int i10 = i7 * 5;
            bArr[i10 + 4] = (byte) weatherInfo.c();
            bArr[i10 + 5] = (byte) weatherInfo.b();
            bArr[i10 + 6] = (byte) weatherInfo.a();
        }
        return g(0, 1, 105, bArr);
    }
}
