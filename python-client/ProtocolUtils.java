package com.wtwd.hfit.protocol;

import A6.d;
import android.os.ParcelUuid;
import android.util.SparseArray;
import com.wtwd.hfit.constant.BleConstant;
import com.wtwd.hfit.entity.DataEnum;
import com.wtwd.hfit.utils.DataConvertUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import p090j2.U0;

/* JADX INFO: loaded from: C:\Users\M0306\Downloads\ApWatch\classes4.dex */
public class ProtocolUtils {
    public static int a(byte[] bArr) {
        int i4 = 0;
        for (byte b9 : bArr) {
            i4 ^= b9 << 8;
            int i7 = 0;
            do {
                int i10 = i4 & DataEnum.FunctionType.GPS_CTRL;
                i4 <<= 1;
                if (i10 == 32768) {
                    i4 ^= 32773;
                }
                i7++;
            } while (i7 < 8);
        }
        return 65535 & i4;
    }

    public static String b(List<ParcelUuid> list, SparseArray<byte[]> sparseArray) {
        if (list == null) {
            return null;
        }
        Iterator<ParcelUuid> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().toString().equals("0000e91a-0000-1000-8000-00805f9b34fb")) {
                if (sparseArray.size() != 1) {
                    return null;
                }
                for (int i4 = 0; i4 < sparseArray.size(); i4++) {
                    StringBuilder sbE = U0.e(i4, "蓝牙广播数据i=", "key=");
                    sbE.append(sparseArray.keyAt(i4));
                    sbE.append("value=");
                    sbE.append(DataConvertUtils.a(sparseArray.valueAt(i4)));
                    d.a(sbE.toString());
                }
                int iKeyAt = sparseArray.keyAt(0);
                DataConvertUtils.d(iKeyAt);
                byte[] bArr = sparseArray.get(iKeyAt);
                int length = bArr.length;
                String hexString = Integer.toHexString(DataConvertUtils.c(0, new byte[]{bArr[0], bArr[1], 0, 0}));
                if (hexString.length() < 4) {
                    StringBuilder sb = new StringBuilder(hexString);
                    for (int length2 = hexString.length(); length2 < 4; length2++) {
                        sb.insert(0, "0");
                    }
                    hexString = sb.toString();
                }
                return hexString.toUpperCase();
            }
        }
        return null;
    }

    public static String c(double d10, int i4) {
        double d11 = i4 == 16 ? d10 * 6.21371192237E-4d : d10 / 1000.0d;
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.CHINA);
        decimalFormat.applyPattern("#.##");
        return decimalFormat.format(d11);
    }

    public static String d(double d10) {
        return (((int) d10) / 60) + "'" + ((int) (d10 % 60.0d)) + "''";
    }

    public static int e(List<ParcelUuid> list, SparseArray<byte[]> sparseArray) {
        if (sparseArray.size() != 1) {
            return 0;
        }
        Iterator<ParcelUuid> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().toString().equals(BleConstant.UUID_ZK)) {
                return 9;
            }
        }
        int iKeyAt = sparseArray.keyAt(0);
        DataConvertUtils.d(iKeyAt);
        int length = sparseArray.get(iKeyAt).length;
        return 8;
    }

    public static String f(double d10, int i4) {
        if (i4 == 1) {
            d10 = (d10 * 1.8d) + 32.0d;
        }
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.CHINA);
        decimalFormat.applyPattern("#");
        return decimalFormat.format(d10);
    }

    public static double g(double d10, int i4) {
        if (d10 == 0.0d) {
            return 0.0d;
        }
        return i4 == 1 ? (d10 * 1.8d) + 32.0d : d10;
    }

    public static boolean h(int i4, int i7) {
        String strG = DataConvertUtils.g(i4, 32);
        int iIndexOf = DataConvertUtils.g(i7, 32).indexOf("1");
        return (iIndexOf != -1 ? Integer.parseInt(strG.substring(iIndexOf, iIndexOf + 1)) : 0) == 1;
    }

    public static int i(int i4) {
        return ((i4 & 248) << 8) | (((i4 >> 8) & 252) << 3) | (((i4 >> 16) & 255) >> 3);
    }
}
