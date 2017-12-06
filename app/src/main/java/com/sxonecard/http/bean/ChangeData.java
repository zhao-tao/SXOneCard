package com.sxonecard.http.bean;

/**
 * Created by pc on 2017-04-28.
 * 充值所需的信息
 * 生成二维码并充值的信息
 */

public class ChangeData {
    private String qrCodeString;
    private String rechangeFee;
    private String orderId;
    private String imeiId;
    //    是否为补充值
    private boolean isRechange;

    public boolean isRechange() {
        return isRechange;
    }

    public void setRechange(boolean rechange) {
        isRechange = rechange;
    }

    public ChangeData() {
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
}
