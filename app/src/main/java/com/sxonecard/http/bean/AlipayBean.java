package com.sxonecard.http.bean;

/**
 * Created by pc on 2017-04-27.
 */

public class AlipayBean
{
    /**
     * 二维码字符串.
     */
    private String QrCodeString;
    /**
     * 订单号.
     */
    private String OrderId;

    public String getQrCodeString() {
        return QrCodeString;
    }

    public void setQrCodeString(String qrCodeString) {
        QrCodeString = qrCodeString;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }
}
