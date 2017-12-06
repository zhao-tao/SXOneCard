package com.sxonecard.http.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/12/6.
 */

public class ReChangeBean extends DataSupport {
    private String card;
    private int money;
    private long time;

    public void setValue(String card, int money, long time) {
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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
