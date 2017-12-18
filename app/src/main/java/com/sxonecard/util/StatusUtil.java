package com.sxonecard.util;

import android.content.Context;

import com.google.gson.Gson;
import com.sxonecard.CardApplication;
import com.sxonecard.background.OrderDb;
import com.sxonecard.http.HttpDataListener;
import com.sxonecard.http.HttpDataSubscriber;
import com.sxonecard.http.HttpRequestProxy;
import com.sxonecard.http.bean.ReChangeSQL;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sxonecard.CardApplication.a_money;
import static com.sxonecard.CardApplication.b_money;

/**
 * Created by Administrator on 2017/12/18.
 * 交易状态通知后台（成功或失败）
 */

public class StatusUtil {
    /**
     * 交易失败，保存补充值数据
     * FragmentSix上传交易失败数据.
     */
    public static void uploadFailData() {
        // TODO: 2017/12/6 保存补充值暂存信息（判断卡号不在数据库内）
        List<ReChangeSQL> list = DataSupport.where("card=?", CardApplication.getInstance().getCheckCard().getCardNO()).find(ReChangeSQL.class);
        if (null == list || list.size() == 0) {
            ReChangeSQL recharge = new ReChangeSQL();
            recharge.setValue(CardApplication.getInstance().getCheckCard().getCardNO(),
                    CardApplication.DoubleToString(b_money), CardApplication.getInstance().getCurrentOrderId(), System.currentTimeMillis(), 2);
            recharge.save();
        }

        final Map<String, String> jsonObj = new HashMap<>();
        HttpDataListener tradeListener = new HttpDataListener<String>() {
            @Override
            public void onNext(String tradeStatusBean) {

            }

            @Override
            public void onError(Context context, int code, String msg) {
                super.onError(context, code, msg);
                uploadFailData();
            }
        };

        jsonObj.put("LiushuiId", String.valueOf(System.currentTimeMillis()));
        jsonObj.put("Time", DateTools.getCurrent());
        jsonObj.put("ImeiId", CardApplication.IMEI);
        jsonObj.put("Type", String.valueOf(1000));
        jsonObj.put("Operator", "tom");
        jsonObj.put("ReaderSn", "111");
        jsonObj.put("ReaderVer", "111");
        jsonObj.put("CorpCode", "111");
        jsonObj.put("MerchantSn", "1111");
        jsonObj.put("TradeData", "111");
        jsonObj.put("OrderType", "1");

        jsonObj.put("OrderId", CardApplication.getInstance().getCurrentOrderId());
        jsonObj.put("CardNo", CardApplication.getInstance().getCheckCard().getCardNO() + "");
        jsonObj.put("mCardType", CardApplication.getInstance().getCheckCard().getType());
//        充值前金额
        jsonObj.put("oldMoney", String.valueOf(a_money));
        HttpRequestProxy.getInstance().failTrade(new HttpDataSubscriber(tradeListener,
                CardApplication.getInstance(), false), jsonObj);
    }


    /**
     * 删除充值成功的数据库
     * FragmentSeven上传成功的交易数据.
     */
    public static void uploadTradeData() {
        DataSupport.deleteAll(ReChangeSQL.class, "card=?", CardApplication.getInstance().getCheckCard().getCardNO());

        final Map<String, String> jsonObj = new HashMap<>(10);
        HttpDataListener tradeListener = new HttpDataListener<String>() {
            @Override
            public void onNext(String tradeStatusBean) {

            }

            @Override
            public void onError(Context context, int code, String msg) {
                super.onError(context, code, msg);
                Gson gson = new Gson();
                OrderDb.insert(gson.toJson(jsonObj));
                uploadTradeData();
            }
        };

        jsonObj.put("LiushuiId", String.valueOf(System.currentTimeMillis()));
        jsonObj.put("Time", DateTools.getCurrent());
        jsonObj.put("ImeiId", CardApplication.IMEI);
        jsonObj.put("Type", String.valueOf(1000));
        jsonObj.put("Operator", "tom");
        jsonObj.put("ReaderSn", "111");
        jsonObj.put("ReaderVer", "111");
        jsonObj.put("CorpCode", "111");
        jsonObj.put("MerchantSn", "1111");
        jsonObj.put("TradeData", "111");
        jsonObj.put("OrderType", "1");

        jsonObj.put("OrderId", CardApplication.getInstance().getCurrentOrderId());
        jsonObj.put("CardNo", CardApplication.getInstance().getCheckCard().getCardNO() + "");
        jsonObj.put("mCardType", CardApplication.getInstance().getCheckCard().getType());
        DecimalFormat decimalFormat = new DecimalFormat("######.##");
//        充值前金额
        jsonObj.put("oldMoney", decimalFormat.format(a_money));
//        充值后余额
        jsonObj.put("newMoney", decimalFormat.format(a_money + b_money));

        HttpRequestProxy.getInstance().uploadTrade(new HttpDataSubscriber(tradeListener,
                CardApplication.getInstance(), false), jsonObj);
    }

}
