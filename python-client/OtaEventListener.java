package com.example.zkota;

public interface OtaEventListener {

    void send(byte[] data);

    void onReadyToUpdate();
    void onOtaStart();
    void onOtaPaused();
    void onOtaFinished();
    void onOtaStopped();
    void onOtaError(String msg);

    void onProgress(int percent);
    void onVersionString(String version);
}
