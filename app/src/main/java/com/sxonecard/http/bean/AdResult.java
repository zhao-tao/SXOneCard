package com.sxonecard.http.bean;

import java.util.List;

/**
 * Created by liukun on 16/3/5.
 */
public class AdResult {

    private int Ret;
    private String Des;
    //用来模仿Data
    private List<AdBean> Data;
    private String RetTime;
    private String Nexttime;

    public int getRet() {
        return Ret;
    }

    public void setRet(int ret) {
        Ret = ret;
    }

    public String getDes() {
        return Des;
    }

    public void setDes(String des) {
        Des = des;
    }

    public List<AdBean> getData() {
        return Data;
    }

    public void setData(List<AdBean> data) {
        Data = data;
    }

    public String getRetTime() {
        return RetTime;
    }

    public void setRetTime(String retTime) {
        RetTime = retTime;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Ret=" + Ret + " Des=" + Des + " RetTime=" + RetTime);
        if (null != Data) {
            sb.append(" Data:" + Data.toString());
        }
        return sb.toString();
    }

    public String getNexttime() {
        return Nexttime;
    }

    public void setNexttime(String nexttime) {
        Nexttime = nexttime;
    }
}
