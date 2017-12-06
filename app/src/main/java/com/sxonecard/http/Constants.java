package com.sxonecard.http;


/**
 * Created by HeQiang on 2016/10/23.
 */

public class Constants {
    public static boolean isDebug = true;
    public static String BASEURL = "http://pay.thecitypass.cn/";
    // public static String BASEURL = "http://www.bjilvy.cn/";

    //    页面常量
    public static final int PAGE_CHECK_CARD = 0;
    public static final int PAGE_CHOOSE_SERVICE = 1;
    public static final int PAGE_CHOOSE_MONEY = 2;
    public static final int PAGE_PAY_METHOD = 4;
    public static final int PAGE_QR_CODE = 5;
    public static final int PAGE_PAY_SUCCESS = 6;
    public static final int PAGE_RECHANGE = 7;
    public static final int PAGE_RECHANGE_ERROR = 400;
    public static final int PAGE_DEVICE_EXCEPT = 401;
    public static final int PAGE_AD = 99;

    //    Log类型
    public static final String LOG_CHANGE = "change";


    public static int NET_ERR = 1000;
}
