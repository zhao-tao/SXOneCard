package com.sxonecard.http;


import com.sxonecard.http.bean.AdResult;
import com.sxonecard.http.bean.PayBean;
import com.sxonecard.http.bean.SetBean;
import com.sxonecard.http.bean.ShutDownBean;
import com.sxonecard.http.bean.TradeStatusBean;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by liukun on 16/3/9.
 */
public interface HttpDataService {

    //或者采用@Query("ImeiId") String id也可以
    @GET("api/index.php")
    Observable<AdResult> getAd(@Query("Act") String Act, @Query("ImeiId") String ImeiId);

    @GET("api/index.php")
    Observable<HttpResult<SetBean>> getSet(@Query("Act") String set, @Query("ImeiId") String ImeiId);

    //post数据不使用key.
    @FormUrlEncoded
    @POST("api/index.php")
    Observable<HttpResult<String>> uploadTrade(@FieldMap Map<String, String> param);

    @FormUrlEncoded
    @POST("api/failtrade.php")
    Observable<HttpResult<String>> failTrade(@FieldMap Map<String, String> param);


    @GET("api/scancode.php")
    Observable<HttpResult<PayBean>> requestAlipayForString(@QueryMap Map<String, String> mapParam);

    @GET("api/scancode.php")
    Observable<HttpResult<PayBean>> requestWeiXinForString(@QueryMap Map<String, String> mapParam);

    @FormUrlEncoded
    @POST("api/index.php?Act=run")
    Observable<HttpResult<String>> phphreat(@FieldMap Map<String, String> param);

    /**
     * 订单支付成功后订单轮询
     *
     * @param imeiId
     * @param orderId
     * @param md5Code
     * @return
     */
    @GET("api/index.php?Act=order")
    Observable<HttpResult<TradeStatusBean>> pollingRequestForTradeStatus(
            @Query("ImeiId") String imeiId
            , @Query("OrderId") String orderId, @Query("Md5Code") String md5Code);

    /**
     * 开关机设置接口.
     *
     * @param imeiId
     * @return
     */
    @GET("/api/index.php?Act=set")
    Observable<HttpResult<ShutDownBean>> shutDown(@Query("ImeiId") String imeiId);

    /**
     * 上传日志接口.
     *
     * @return
     */
    @GET("/test")
    Observable<HttpResult<String>> uploadLog(@QueryMap Map<String, String> param);
}
