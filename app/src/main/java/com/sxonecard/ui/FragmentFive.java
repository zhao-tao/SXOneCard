package com.sxonecard.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sxonecard.BuildConfig;
import com.sxonecard.CardApplication;
import com.sxonecard.R;
import com.sxonecard.background.SoundService;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.http.DateUtil;
import com.sxonecard.http.HttpDataListener;
import com.sxonecard.http.HttpDataSubscriber;
import com.sxonecard.http.HttpRequestProxy;
import com.sxonecard.http.bean.AlipayBean;
import com.sxonecard.http.bean.GsonData;
import com.sxonecard.util.EncryptUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by pc on 2017-04-25.
 * 选择支付方式（微信，支付宝）
 */

public class FragmentFive extends BaseFragment {
    @Bind(R.id.user_recharge_money)
    TextView userRechangeMoney;
    @Bind(R.id.weixinPay)
    ImageView weixinPay;
    @Bind(R.id.zhifubaoPay)
    ImageView zhifubaoPay;
    @Bind(R.id.tv_back)
    TextView mBackTv;

    private String rechagePrice;
    private String imeiId = CardApplication.IMEI;
    ProgressDialog progressDialog;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_5;
    }

    @Override
    public void initView() {
        setVoice(SoundService.ZHIFUFANGSHI);
        rechagePrice = this.getArguments().getString("msg");
        CardApplication.b_money = Double.parseDouble(rechagePrice);
        userRechangeMoney.setText("用户充值金额:" + rechagePrice + "元");
        progressDialog = new ProgressDialog(getContext());

        if (BuildConfig.AUTO_TEST) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (weixinPay == null)
                        return;
                    weixinPay.performClick();
                }
            }, 5000);
        }
        mBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                返回选择金额
                navHandle.sendEmptyMessage(2);
            }
        });
    }

    @Override
    public void loadData() {
    }


    @OnClick({R.id.weixinPay, R.id.zhifubaoPay})
    public void onViewClicked(View view) {
        // Message msg = null;
        switch (view.getId()) {
            case R.id.weixinPay:
                sendWeixinData();
                CardApplication.chong_type = 1;
                break;
            case R.id.zhifubaoPay:
                sendAlipayData();
                CardApplication.chong_type = 2;
                break;
        }
        weixinPay.setEnabled(false);
        zhifubaoPay.setEnabled(false);
    }

    private void sendWeixinData() {
        progressDialog.show();
        HttpDataListener listener = new HttpDataListener<AlipayBean>() {
            @Override
            public void onNext(AlipayBean alipayBean) {
                progressDialog.dismiss();
                if (TextUtils.isEmpty(alipayBean.getQrCodeString())) {
                    weixinPay.setEnabled(true);
                    zhifubaoPay.setEnabled(true);
                    return;
                }
                CardApplication.getInstance().setCurrentOrderId(alipayBean.getOrderId());
                Message msgCode = Message.obtain();
                GsonData gsonData = new GsonData();
                gsonData.setOrderId(alipayBean.getOrderId());
                gsonData.setQrCodeString(alipayBean.getQrCodeString());
                gsonData.setRechangeFee(rechagePrice);
                gsonData.setImeiId(imeiId);

                // 利用gson对象生成json字符串
                Gson gson = new Gson();
                msgCode.obj = gson.toJson(gsonData);
                msgCode.what = 5;
                navHandle.sendMessage(msgCode);
            }

            @Override
            public void onError(Context context, int code, String msg) {
                super.onError(context, code, msg);
                progressDialog.dismiss();
                weixinPay.setEnabled(true);
                zhifubaoPay.setEnabled(true);
            }
        };

        //TODO  按字典顺序做mdn5转大写.
        String act = "wxpay";
        String time = DateUtil.getCurrentTime();
        String fee = rechagePrice;
        String md5Code = EncryptUtil.getMd5(act + fee + imeiId + time).toUpperCase();
        Map<String, String> paramMap = new HashMap<String, String>(5);
        paramMap.put("Act", act);
        paramMap.put("Time", time);
        paramMap.put("Fee", fee);
        paramMap.put("CardNo", CardApplication.getInstance().getCheckCard().getCardNumber());

        paramMap.put("ImeiId", imeiId);
        paramMap.put("Md5Code", md5Code);

        HttpRequestProxy.getInstance().requestWeiXinString(new HttpDataSubscriber<AlipayBean>(listener,
                getContext(), false), paramMap);
    }

    private void sendAlipayData() {
        progressDialog.show();
        HttpDataListener alipayListener = new HttpDataListener<AlipayBean>() {
            @Override
            public void onNext(AlipayBean alipayBean) {
                progressDialog.dismiss();
                if (TextUtils.isEmpty(alipayBean.getQrCodeString())) {
                    weixinPay.setEnabled(true);
                    zhifubaoPay.setEnabled(true);
                    return;
                }
                CardApplication.getInstance().setCurrentOrderId(alipayBean.getOrderId());
                Message msgCode = Message.obtain();
                GsonData gsonData = new GsonData();
                gsonData.setOrderId(alipayBean.getOrderId());
                gsonData.setQrCodeString(alipayBean.getQrCodeString());
                gsonData.setRechangeFee(rechagePrice);
                gsonData.setImeiId(imeiId);
                // 利用gson对象生成json字符串
                Gson gson = new Gson();
                msgCode.obj = gson.toJson(gsonData);
                msgCode.what = 5;
                navHandle.sendMessage(msgCode);
            }

            @Override
            public void onError(Context context, int code, String msg) {
                super.onError(context, code, msg);
                progressDialog.dismiss();
                zhifubaoPay.setEnabled(true);
                weixinPay.setEnabled(true);
            }
        };

        //TODO  按字典顺序做mdn5转大写.
        String act = "alipay";
        String time = DateUtil.getCurrentTime();
        String fee = rechagePrice;
        String md5Code = EncryptUtil.getMd5(act + fee + imeiId + time).toUpperCase();
        Map<String, String> paramMap = new HashMap<String, String>(5);
        paramMap.put("Act", act);
        paramMap.put("Time", time);
        paramMap.put("Fee", fee);
        paramMap.put("ImeiId", imeiId);
        paramMap.put("CardNo", CardApplication.getInstance().getCheckCard().getCardNumber());
        paramMap.put("Md5Code", md5Code);

        HttpRequestProxy.getInstance().requestAlipayString(new HttpDataSubscriber<AlipayBean>
                (alipayListener, getContext(), false), paramMap);
    }
}
