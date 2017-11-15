package com.sxonecard.http;

/**
 * Created by liukun on 16/3/5.
 */
public class HttpResult<T> {

    private int Ret;
    private String Des;
    //用来模仿Data
    private T Data;
    private String RetTime;

    public int getRet() {  return Ret; }

    public void setRet(int ret) {  Ret = ret;  }

    public String getDes() {  return Des;   }

    public void setDes(String des) {    Des = des; }

    public T getData() {    return Data; }

    public void setData(T data) {    Data = data;  }

    public String getRetTime() {    return RetTime;   }

    public void setRetTime(String retTime) {  RetTime = retTime; }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Ret=" + Ret + " Des=" + Des+" RetTime="+RetTime);
        if (null != Data) {
            sb.append(" Data:" + Data.toString());
        }
        return sb.toString();
    }
}
