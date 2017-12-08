package com.sxonecard.http.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/12/6.
 * 补充值数据的数据库
 */

public class ReChangeSQL extends DataSupport {
    private String card;
    private String money;
    //当前订单编号
    private String OrderId;
    private long time;

    public void setValue(String card, String money, String OrderId, long time) {
        this.card = card;
        this.money = money;
        this.OrderId = OrderId;
        this.time = time;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
