package com.sxonecard.http.bean;

/**
 * Created by pc on 2017-06-19.
 */

public class RechargeCardBean {
    private String cardNumber;//卡号（串口获取，需由16进制转换为10进制）
    private int amount;//卡内金额(单位：分)
    private String expireDate; //过期日期
    private String type;    //卡类型
    private String orderNo;//订单号.
//    private String reChangeTime;// 充值日期.
//    private String imeiId;//设备IMEI号.
    private String status; //运行状态  0运行异常  1运行正常.
    private String note; //异常备注,Status为0时备注异常问题.
    public RechargeCardBean(){

    }
    public  RechargeCardBean(String cardNumber,int amount,
                             String expireDate,String type,String status){
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.expireDate =expireDate;
        this.type = type;
        this.status = status;
    }
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

//    public String getReChangeTime() {
//        return reChangeTime;
//    }
//
//    public void setReChangeTime(String reChangeTime) {
//        this.reChangeTime = reChangeTime;
//    }

//    public String getImeiId() {
//        return imeiId;
//    }
//
//    public void setImeiId(String imeiId) {
//        this.imeiId = imeiId;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
