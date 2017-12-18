package com.sxonecard.http.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/12/6.
 * 补充值数据的数据库
 */

public class ReChangeSQL extends DataSupport {
    private String card;//卡号
    private String money;//补充金额
    private String OrderId;//当前订单编号
    private long time;//充值失败的时间戳
    private int ChangeTime;//补充值次数


    public void setValue(String card, String money, String OrderId, long time, int changeTime) {
        this.card = card;
        this.money = money;
        this.OrderId = OrderId;
        this.time = time;
        this.ChangeTime = changeTime;
    }

    public int getChangeTime() {
        return ChangeTime;
    }

    public void setChangeTime(int changeTime) {
        ChangeTime = changeTime;
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
