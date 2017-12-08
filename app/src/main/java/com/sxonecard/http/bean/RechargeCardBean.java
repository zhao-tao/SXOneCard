package com.sxonecard.http.bean;

import com.sxonecard.util.ByteUtil;

/**
 * Created by pc on 2017-06-19.
 */

public class RechargeCardBean {
    private String cardNO;//10进制卡号
    private int amount;//卡内金额(单位：分)
    private String expireDate; //过期日期
    private String type;    //卡类型
    private String orderNo;//订单号.

    private String status; //运行状态  0运行异常  1运行正常.


    //当前未使用的参数
    private String note; //异常备注,Status为0时备注异常问题.
    //    private String reChangeTime;// 充值日期.
//    private String imeiId;//设备IMEI号.

    public RechargeCardBean() {

    }

    public RechargeCardBean(String cardNumber, int amount,
                            String expireDate, String type, String status) {
        this.amount = amount;
        this.expireDate = expireDate;
        this.type = type;
        this.status = status;
        cardNO = ByteUtil.autoGenericCode(Integer.parseInt(cardNumber, 16), 8);
    }


    public String getCardNO() {
        return cardNO;
    }

    public void setCardNumber(String cardNumber) {
        cardNO = ByteUtil.autoGenericCode(Integer.parseInt(cardNumber, 16), 8);
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
