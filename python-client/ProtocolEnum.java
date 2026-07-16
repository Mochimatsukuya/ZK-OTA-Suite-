package com.wtwd.hfit.protocol;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: C:\Users\M0306\Downloads\ApWatch\classes4.dex */
public class ProtocolEnum {

    @Retention(RetentionPolicy.SOURCE)
    public @interface AnswerType {
        public static final int ANSWER_TYPE_CRC16_WRONG = 3;
        public static final int ANSWER_TYPE_NULL = 0;
        public static final int ANSWER_TYPE_OVER = 4;
        public static final int ANSWER_TYPE_SUCCESS = 1;
        public static final int ANSWER_TYPE_WRONG = 2;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface CmdType {
        public static final int CMD_TYPE_ANSWER = 4;
        public static final int CMD_TYPE_NULL = 0;
        public static final int CMD_TYPE_REQUEST = 3;
        public static final int CMD_TYPE_SEND = 1;
        public static final int CMD_TYPE_SEND_NO_ACK = 2;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface DataType {
        public static final int DATA_TYPE_ADDRESS_BOOK = 135;
        public static final int DATA_TYPE_AIR_PRESSURE_ALTITUDE = 149;
        public static final int DATA_TYPE_AI_TEXT = 154;
        public static final int DATA_TYPE_ALARM_CLOCK = 106;
        public static final int DATA_TYPE_APP_SPORT = 130;
        public static final int DATA_TYPE_APP_SYNC = 110;
        public static final int DATA_TYPE_APP_TEST = 205;
        public static final int DATA_TYPE_AUDIO_BLE_MAC = 23;
        public static final int DATA_TYPE_BATTERY_INFO = 3;
        public static final int DATA_TYPE_BLOOD_OXYGEN = 20;
        public static final int DATA_TYPE_BLOOD_PRESSURE = 18;
        public static final int DATA_TYPE_BLOOD_SUGAR = 27;
        public static final int DATA_TYPE_CALL_CONTROL_TO_APP = 15;
        public static final int DATA_TYPE_CALL_CONTROL_TO_DEV = 117;
        public static final int DATA_TYPE_CALL_REMIND = 122;
        public static final int DATA_TYPE_DEVICE_AUDIO_STATE = 52;
        public static final int DATA_TYPE_DEVICE_INFO = 2;
        public static final int DATA_TYPE_DEV_SYNC = 9;
        public static final int DATA_TYPE_DEV_TYPE = 51;
        public static final int DATA_TYPE_DIAL_INFO = 132;
        public static final int DATA_TYPE_DIAL_SYNC = 131;
        public static final int DATA_TYPE_DRINK_REMIND = 126;
        public static final int DATA_TYPE_ECG = 19;
        public static final int DATA_TYPE_EXERCISE_HEART_RATE = 17;
        public static final int DATA_TYPE_EXT_PID = 31;
        public static final int DATA_TYPE_FIND_PHONE_OR_DEVICE = 11;
        public static final int DATA_TYPE_FUNCTION_CONTROL = 22;
        public static final int DATA_TYPE_GPS = 140;
        public static final int DATA_TYPE_HAND_RISE_SWITCH = 127;
        public static final int DATA_TYPE_HEART_AUTO_SWITCH = 128;
        public static final int DATA_TYPE_HISTORY_HEART_RATE = 8;
        public static final int DATA_TYPE_HISTORY_SPORT = 5;
        public static final int DATA_TYPE_HISTORY_TEMP = 26;
        public static final int DATA_TYPE_LANGUAGE_SETTING = 103;
        public static final int DATA_TYPE_MEDICINE = 150;
        public static final int DATA_TYPE_MENSTRUAL_PERIOD_INFO = 133;
        public static final int DATA_TYPE_MESSAGE_NOTICE = 107;
        public static final int DATA_TYPE_MESSAGE_SWITCH = 124;
        public static final int DATA_TYPE_MUSIC_CONTENT = 113;
        public static final int DATA_TYPE_MUSIC_CONTROL = 14;
        public static final int DATA_TYPE_NOT_DISTURB_MODE = 115;
        public static final int DATA_TYPE_OTA_DATA = 202;
        public static final int DATA_TYPE_OTA_STATUS = 201;
        public static final int DATA_TYPE_PAIR_FINISH = 120;
        public static final int DATA_TYPE_PHONE_AUDIO_STATE = 152;
        public static final int DATA_TYPE_PHONE_AUDIO_WAKE = 153;
        public static final int DATA_TYPE_PHOTOGRAPH = 116;
        public static final int DATA_TYPE_QR_CODE_DOWNLOAD = 138;
        public static final int DATA_TYPE_REAL_HEART_RATE = 7;
        public static final int DATA_TYPE_REAL_HRV = 35;
        public static final int DATA_TYPE_REAL_SPORT = 4;
        public static final int DATA_TYPE_REAL_TEMP = 24;
        public static final int DATA_TYPE_RESET = 118;
        public static final int DATA_TYPE_RESTORE_FACTORY_SETTING = 25;
        public static final int DATA_TYPE_SEDENTARY_REMIND = 114;
        public static final int DATA_TYPE_SENSOR_DATA_CONTROL = 21;
        public static final int DATA_TYPE_SENSOR_DATA_SWITCH = 109;
        public static final int DATA_TYPE_SET_TARGET = 111;
        public static final int DATA_TYPE_SHUTDOWN = 119;
        public static final int DATA_TYPE_SLEEP = 6;
        public static final int DATA_TYPE_SMS_REMIND = 123;
        public static final int DATA_TYPE_SOS_NUMBER_SETTING = 137;
        public static final int DATA_TYPE_SPORT_MODE = 10;
        public static final int DATA_TYPE_SUP_BLE_50 = 29;
        public static final int DATA_TYPE_TARGET_REMIND = 125;
        public static final int DATA_TYPE_TEMP_SETTING = 136;
        public static final int DATA_TYPE_TEST_DEBUG = 203;
        public static final int DATA_TYPE_TIME = 104;
        public static final int DATA_TYPE_UNIT_SETTING = 121;
        public static final int DATA_TYPE_USER_INFO = 102;
        public static final int DATA_TYPE_WEATHER = 105;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ZKOtaError {
        public static final int ZK_OTA_CAN_NOT_SUBSCRIBE_DATA_IN = 1008;
        public static final int ZK_OTA_CRC_ERROR = 11;
        public static final int ZK_OTA_KEY_MISMATCH = 2;
        public static final int ZK_OTA_NOT_CONNECTED = 1000;
        public static final int ZK_OTA_NOT_FOUND_CLIENT_CHARACTERISTIC_CONFIG = 1007;
        public static final int ZK_OTA_NOT_FOUND_NON_PRIMARY_DEVICE = 1009;
        public static final int ZK_OTA_NOT_FOUND_OTA_CHARACTERISTIC = 1006;
        public static final int ZK_OTA_NOT_FOUND_OTA_DATA_IN = 1004;
        public static final int ZK_OTA_NOT_FOUND_OTA_DATA_OUT = 1005;
        public static final int ZK_OTA_NOT_FOUND_OTA_SERVICE = 1003;
        public static final int ZK_OTA_NOT_INIT = 1001;
        public static final int ZK_OTA_REFUSED_BY_DEVICE = 2001;
        public static final int ZK_OTA_REPORT_FROM_DEVICE = 2000;
        public static final int ZK_OTA_SAME_FIRMWARE = 1;
        public static final int ZK_OTA_TIMEOUT_RECEIVE_RESPONSE = 2002;
        public static final int ZK_OTA_TIMEOUT_SCAN_NON_PRIMARY_DEVICE = 1010;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ZKOtaState {
        public static final int ZK_OTA_ALL_FINISH = 5;
        public static final int ZK_OTA_CONTINUE = 7;
        public static final int ZK_OTA_ERROR = 8;
        public static final int ZK_OTA_PAUSE = 6;
        public static final int ZK_OTA_PROGRESS = 3;
        public static final int ZK_OTA_READY = 1;
        public static final int ZK_OTA_START = 2;
        public static final int ZK_OTA_STOP = 4;
        public static final int ZK_OTA_VERSION = 9;
    }
}
