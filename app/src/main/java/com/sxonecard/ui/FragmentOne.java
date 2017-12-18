package com.sxonecard.ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.sxonecard.BuildConfig;
import com.sxonecard.CardApplication;
import com.sxonecard.R;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.base.RxBus;
import com.sxonecard.http.SerialPort;
import com.sxonecard.http.bean.ReChangeSQL;
import com.sxonecard.http.bean.RechargeCardBean;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import rx.Observable;
import rx.functions.Action1;

import static com.sxonecard.CardApplication.a_money;
import static com.sxonecard.CardApplication.b_money;
import static com.sxonecard.CardApplication.reChangeSQL;
import static com.sxonecard.http.Constants.PAGE_CHOOSE_SERVICE;
import static com.sxonecard.http.Constants.PAGE_RECHANGE;

/**
 * Created by HeQiang on 2017/4/22.
 */


public class FragmentOne extends BaseFragment {
    private AnimationDrawable animationDrawable;
    @Bind(R.id.gongjiaoka)
    ImageView gongjiaoka;
    ErrorDialog dialog;
    DialogHandler handler = new DialogHandler(this);

    @Override
    public int getLayoutId() {
        return R.layout.fragment_1;
    }


    @Override
    public void initView() {
        //注册检测感应区卡片bus.
        registerCheckCardBus();
        animationDrawable = (AnimationDrawable) gongjiaoka.getDrawable();
        animationDrawable.start();
        Bundle bundle = getArguments();
        dialog = new ErrorDialog(getContext());
        if (bundle != null) {
            showDialog(R.drawable.tishi5, 2000);
        }
        if (BuildConfig.AUTO_TEST) {
            Random random = new Random();
            double rnd = random.nextDouble();
            int delay;
//            if(rnd > 0.97)
//                delay = 3600;
//            else
            delay = 5;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SerialPort.getInstance().autoRun();
                }
            }, delay * 1000);
        }
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister("checkCard", checkCardObservable);
    }

    @Override
    public void onDestroyView() {
        if (animationDrawable != null)
            animationDrawable.stop();
        super.onDestroyView();

    }

    Observable<String> checkCardObservable;

    private void registerCheckCardBus() {
        checkCardObservable = RxBus.get().register("checkCard", String.class);
        checkCardObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String ckeckCardjson) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                Gson gson = new Gson();
                RechargeCardBean checkCardBean = gson.fromJson(ckeckCardjson, RechargeCardBean.class);

                a_money = checkCardBean.getAmount() * 1.0 / 100;

                if (checkCardBean == null)
                    return;

                if (checkCardBean.getType().equalsIgnoreCase("0105")) {
                    Log.i("checkCard", "爱心卡");
                    handler.sendEmptyMessage(3);
                    return;
                }
                if (checkCardBean.getType().equalsIgnoreCase("0102")) {
                    Log.i("checkCard", "老年卡");
                    handler.sendEmptyMessage(4);
                    return;
                }
                //卡过期
                if (isExpired(checkCardBean.getExpireDate())) {
                    Log.i("checkCard", "卡过期");
                    handler.sendEmptyMessage(2);
                    return;
                }
                if (checkCardBean.getStatus().equalsIgnoreCase("01")) {
                    CardApplication.getInstance().setCheckCard(checkCardBean);
                    Log.i("checkCard", "正常卡" + checkCardBean.getCardNO());
//                    清除补充值次数为0的数据库数据
                    DataSupport.deleteAll(ReChangeSQL.class, "ChangeTime=?", "0");
                    // TODO: 2017/12/6 获取补充值暂存信息(删除过期的补充值信息，遍历卡号)
                    List<ReChangeSQL> reChangeData = DataSupport.findAll(ReChangeSQL.class);
                    if (null != reChangeData && reChangeData.size() != 0) {
                        int index = -1;
                        for (int i = 0; i < reChangeData.size(); i++) {
//                            判断30分钟以上的失败交易,补充值过期删除记录
                            if (System.currentTimeMillis() - reChangeData.get(i).getTime() < 1800 * 1000) {
//                                当前卡号等于数据库中的补充值卡号
                                if (reChangeData.get(i).getCard().equals(checkCardBean.getCardNO())) {
                                    index = i;
                                }
                            } else {
                                DataSupport.deleteAll(ReChangeSQL.class, "card=?", reChangeData.get(i).getCard());
                            }
                        }
                        if (index != -1) {
                            reChangeSQL = reChangeData.get(index);
                            //修改当前交易订单号
                            CardApplication.getInstance().setCurrentOrderId(reChangeData.get(index).getOrderId());
                            b_money = Double.parseDouble(reChangeData.get(index).getMoney());
                            navHandle.sendEmptyMessage(PAGE_RECHANGE);
                            return;
                        }
                    }
                    navHandle.sendEmptyMessage(PAGE_CHOOSE_SERVICE);
                } else {
                    Log.i("checkCard", "无效卡");
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    private void showDialog(int resId, final int delay) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog.show();
        dialog.setBackground(resId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, delay);
    }

    private boolean isExpired(String time) {
        if (TextUtils.isEmpty(time))
            return false;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String today = format.format(Calendar.getInstance().getTime());
        int current = Integer.parseInt(today);
        int input = Integer.parseInt(time);
        return current > input;
    }

    /**
     * 卡片类型提示
     *
     * @param what
     * @param obj
     */
    private void changeErrorDialog(int what, Object obj) {
        if (what == 1) {
            showDialog(R.drawable.tishi4, 3000);
        } else if (what == 3) {
            showDialog(R.drawable.tishi6, 3000);
        } else if (what == 2) {
            showDialog(R.drawable.tishi7, 3000);
        } else {
            showDialog(R.drawable.tishi1, 3000);
        }
    }

    private static class DialogHandler extends Handler {
        private final WeakReference<FragmentOne> activityWeakReference;

        public DialogHandler(FragmentOne activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FragmentOne fragmentOne = activityWeakReference.get();
            if (fragmentOne != null) {
                fragmentOne.changeErrorDialog(msg.what, msg.obj);
            }
        }
    }
}
