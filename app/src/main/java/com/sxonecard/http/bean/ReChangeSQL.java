package com.sxonecard.http.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/12/6.
 * 补充值数据的数据库
 */

public class ReChangeSQL extends DataSupport {
    private String card;
    private String money;
    private long time;

    public void setValue(String card, String money, long time) {
        this.card = card;
        this.money = money;
        this.time = time;
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
