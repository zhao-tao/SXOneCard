package com.sxonecard.http.bean;

/**
 * Created by Administrator on 2017/4/29 0029.
 * 支付状态
 * status：0 未成功 1 成功
 */

public class TradeStatusBean
{
    private int Status;
    private String OrderId;
    private String ImeiId;
    private int Price;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getImeiId() {
        return ImeiId;
    }

    public void setImeiId(String imeiId) {
        ImeiId = imeiId;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }
}
