package com.sxonecard.http.bean;

/**
 * Created by Administrator on 2017-6-19.
 */

public class SetBean {

    private String StartTime;

    private String EndTime;

    private String FirmwareVersion;

    private String Firmware;

    private int AndroidVersion;

    private String Android;

    private String RetTime;

    private int RunRate;

    public String getRetTime() {
        return RetTime;
    }

    public void setRetTime(String retTime) {
        RetTime = retTime;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getFirmwareVersion() {
        return FirmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        FirmwareVersion = firmwareVersion;
    }

    public String getFirmware() {
        return Firmware;
    }

    public void setFirmware(String firmware) {
        Firmware = firmware;
    }

    public int getAndroidVersion() {
        return AndroidVersion;
    }

    public void setAndroidVersion(int androidVersion) {
        AndroidVersion = androidVersion;
    }

    public String getAndroid() {
        return Android;
    }

    public void setAndroid(String android) {
        Android = android;
    }

    public int getRunRate() {
        return RunRate;
    }

    public void setRunRate(int runRate) {
        RunRate = runRate;
    }
}
