package com.sxonecard.ui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sxonecard.CardApplication;
import com.sxonecard.R;
import com.sxonecard.background.OrderDb;
import com.sxonecard.background.SoundService;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.http.HttpDataListener;
import com.sxonecard.http.HttpDataSubscriber;
import com.sxonecard.http.HttpRequestProxy;
import com.sxonecard.http.MyCountDownTimer;
import com.sxonecard.util.DateTools;
import com.sxonecard.util.PrinterUtil;
import com.sxonecard.util.StatusUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

import static com.sxonecard.CardApplication.a_money;
import static com.sxonecard.http.Constants.PAGE_CHECK_CARD;

/**
 * Created by pc on 2017-04-27.
 * 充值成功，打印小票，上传交易数据，超时返回首页
 */

public class FragmentSeven extends BaseFragment {
    @Bind(R.id.user_recharge_money)
    TextView userRechargeMoney;
    @Bind(R.id.print_ticket)
    Button printTicket;
    @Bind(R.id.tv_back)
    TextView mBackTv;
    private MyCountDownTimer timer;
    private String msg;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_7;
    }

    @Override
    public void initView() {
        //监听返回数据.
        setVoice(SoundService.CHONGZHI_SUCCESS);
        msg = getArguments().getString("msg");
        userRechargeMoney.setText("卡内当前余额:" + msg + "元");
        timer = new MyCountDownTimer(5 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                navHandle.sendEmptyMessage(PAGE_CHECK_CARD);
            }
        };
        timer.start();

        printTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrinterUtil.getInstance().send();
            }
        });

        StatusUtil.uploadTradeData();
        mBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navHandle.sendEmptyMessage(PAGE_CHECK_CARD);
            }
        });
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onDestroy() {
        if (null != timer) {
            timer.cancel();
        }
        super.onDestroy();
    }

}
