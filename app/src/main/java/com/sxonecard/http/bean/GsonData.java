package com.sxonecard.http.bean;

/**
 * Created by pc on 2017-04-28.
 */

public class GsonData
{
    private String qrCodeString;
    private String rechangeFee;

    public GsonData()
    {
    }

    public GsonData(String qrCodeString, String rechangeFee) {
        this.qrCodeString = qrCodeString;
        this.rechangeFee = rechangeFee;
    }

    public String getQrCodeString() {
        return qrCodeString;
    }

    public void setQrCodeString(String qrCodeString) {
        this.qrCodeString = qrCodeString;
    }

    public String getRechangeFee() {
        return rechangeFee;
    }

    public void setRechangeFee(String rechangeFee) {
        this.rechangeFee = rechangeFee;
    }


    private String ret;
    private String des;
    private String status;
    private String orderId;
    private String imeiId;
    private String retTime;

    public GsonData(String ret, String des, String status, String orderId, String imeiId, String retTime) {
        this.ret = ret;
        this.des = des;
        this.status = status;
        this.orderId = orderId;
        this.imeiId = imeiId;
        this.retTime = retTime;
    }

    public GsonData(String status, String ret,String orderId)
    {
        this.status = status;
        this.ret = ret;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getImeiId() {
        return imeiId;
    }

    public void setImeiId(String imeiId) {
        this.imeiId = imeiId;
    }

    public String getRetTime() {
        return retTime;
    }

    public void setRetTime(String retTime) {
        this.retTime = retTime;
    }
}
