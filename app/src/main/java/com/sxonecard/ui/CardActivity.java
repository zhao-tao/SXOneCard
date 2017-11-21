package com.sxonecard.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.sxonecard.CardApplication;
import com.sxonecard.R;
import com.sxonecard.background.HeartBeatService;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.base.RxBus;
import com.sxonecard.http.HttpDataListener;
import com.sxonecard.http.HttpDataSubscriber;
import com.sxonecard.http.HttpRequestProxy;
import com.sxonecard.http.SerialPort;
import com.sxonecard.http.bean.AdBean;
import com.sxonecard.http.bean.AdResult;
import com.sxonecard.http.bean.SetBean;
import com.sxonecard.util.DownLoadFile;
import com.sxonecard.util.LogcatHelper;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;

/**
 * @Author
 */

public class CardActivity extends FragmentActivity {
    private static String TAG = "CardActivity";
    final Handler navHandler = new NavigationHandler(this);

    final Handler navHandler_gg = new NavigationHandler(this);
    Observable<String> adObservable;
    Observable<String> checkModuleObservable;
    Observable<String> interruptObservable;
    private String curr_ads = "";
    private int last_action = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 百度统计获取测试设备ID
//        String testDeviceId = StatService.getTestDeviceId(this);
//        android.util.Log.i("BaiduMobStat", "Test DeviceId : " + testDeviceId);
        // 百度统计日志输出
        StatService.setDebugOn(true);
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1, false);

//        听云统计接入
//        NBSAppAgent.setLicenseKey("eed4916edbfb4925b37798daf8cc2e09")
//                .withLocationServiceEnabled(true)
//                .start(getApplicationContext());

        setContentView(R.layout.activity_card);
        ButterKnife.bind(this);
        initView();
        registerBus();
        initDevice();
        // FIXME: 2017/11/17 本地记录特定的Log日志文件
        LogcatHelper.getInstance().start();
    }

    /**
     * 初始化设备
     */
    private void initDevice() {
        SerialPort device = SerialPort.getInstance();
        device.init();
        //发送测试链接
        device.checkDevice();
    }

    /**
     * 初始化网络配置
     */
    private void initConfiguration() {
        HttpDataListener listener = new HttpDataListener<SetBean>() {
            @Override
            public void onNext(SetBean set) {
                CardApplication.getInstance().setConfig(set);
                syncTimeAndShutDownDevice();
                if (!android_update(set)) {
                    //开启心跳服务
                    Intent intent = new Intent(getApplicationContext(), HeartBeatService.class);
                    startService(intent);
                }
            }

            @Override
            public void onError(Context context, int code, String msg) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initConfiguration();
                    }
                }, 1000);
            }
        };

        HttpRequestProxy.getInstance().getSet(new HttpDataSubscriber(listener,
                this.getApplicationContext(), false), "set", CardApplication.IMEI);
    }

    private void registerBus() {
        checkModuleObservable = RxBus.get().register("checkDevice", String.class);
        checkModuleObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String deviceStr) {
                if ("0".equals(deviceStr.trim())) {
                    //硬件故障,跳转至维护页面.
                    Message msg = new Message();
                    msg.what = 401;
                    msg.obj = getApplicationContext().getText(R.string.deviceErr);
                    navHandler.sendMessage(msg);
                } else {
                    CardApplication.getInstance().setDeviceSuccess(true);
                    navHandler.sendEmptyMessage(0);
                    //请求初始化设置
                    initConfiguration();
                    defaultads();
                }
            }
        });
        interruptObservable = RxBus.get().register("interrupt", String.class);
        interruptObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String deviceStr) {
                if (last_action != 0) {
                    Message msg = Message.obtain(navHandler, 0, "提示");
                    msg.sendToTarget();
                }
            }
        });

        adObservable = RxBus.get().register("ads", String.class);
        adObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String ads_json) {
                if ("default".endsWith(ads_json)) {
                    SharedPreferences sharedata = getApplication().getSharedPreferences("sxcard", MODE_PRIVATE);
                    ads_json = sharedata.getString("default_ads", "");
                }
                try {
                    Gson gson = new Gson();
                    AdBean bean = gson.fromJson(ads_json, AdBean.class);
                    if ("1".equals(bean.getType())) {
                        changeAds(true, gson.toJson(bean.getFilepath()));
                    } else if ("2".equals(bean.getType())) {
                        changeAds(false, gson.toJson(bean.getFilepath()));
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void changeAds(boolean type, String json) {

        if (json.equals(curr_ads)) {
            return;
        }
        curr_ads = json;

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BaseFragment fragment;

        if (type) { // 图片类型
            fragment = new FragmentPicAction();
        } else { // 视频类型
            fragment = new FragmentMvAction();
        }

        fragment.setNavHandle(navHandler_gg);
        Bundle bundle = new Bundle();
        bundle.putString("msg", json);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_ad, fragment);
//        fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 获取默认广告
     */
    private void defaultads() {
        HttpDataListener listener = new HttpDataListener<AdResult>() {
            @Override
            public void onNext(AdResult result) {
                if (result.getData() != null && result.getData().size() > 0) {
                    AdBean bean = result.getData().get(0);
                    Gson gson = new Gson();
                    String default_ads = gson.toJson(bean);
                    Log.i(TAG, default_ads);
                    // 保存对象
                    SharedPreferences.Editor sharedata = getApplication()
                            .getSharedPreferences("sxcard", MODE_PRIVATE).edit();
                    //保存该16进制数组
                    sharedata.putString("default_ads", default_ads);
                    sharedata.commit();
                    RxBus.get().post("ads", default_ads);
                }
            }

            @Override
            public void onError(Context context, int code, String msg) {
                Log.i(TAG, "网络错误代码:" + code);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        defaultads();
                    }
                }, 5 * 1000);
            }
        };
        HttpRequestProxy.getInstance().getAd(new HttpDataSubscriber(listener,
                getApplicationContext(), false), "defaultads", CardApplication.IMEI);

    }

    private boolean android_update(SetBean set) {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            if (pi.versionCode < set.getAndroidVersion()) {
                Toast.makeText(this, "正在更新中...", Toast.LENGTH_LONG).show();
                DownLoadFile dlf = new DownLoadFile(this.getApplicationContext());
                dlf.downFiletoDecive(set.getAndroid());
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        RxBus.get().unregister("ads", adObservable);
        RxBus.get().unregister("checkDevice", checkModuleObservable);
        RxBus.get().unregister("interrupt", interruptObservable);
        SerialPort.getInstance().closeSerialPort();
        LogcatHelper.getInstance().stop();
    }

    public void initView() {
        FragmentOne f = new FragmentOne();
        f.setNavHandle(navHandler);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_action, f);
        fragmentTransaction.commit();
    }

    public void changeAction(int index, Object obj) {
        //前后页面相同，不切换
        if (last_action == index)
            return;
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BaseFragment fragment = null;
        last_action = index;
        Log.i(TAG, "changeAction: ===" + index);
        switch (index) {
            case 0:
                fragment = new FragmentOne();
                break;
            case 1:
                fragment = new FragmentTwo();
                break;
            case 2:
                fragment = new FragmentThree();
                break;
            case 3:
                fragment = new FragmentFour();
                break;
            case 4:
                fragment = new FragmentFive();
                break;
            case 5:
                fragment = new FragmentSix();
                break;
            case 6:
                fragment = new FragmentSeven();
                break;
            case 400:
                fragment = new ReChangeError();
                break;
            case 401:
                fragment = new DeviceException();
                break;
            case 99:

                List<AdBean> adlist = CardApplication.adlist;
                int ttt = CardApplication.index;
                AdBean ad = adlist.get(ttt);
                Gson gson = new Gson();
                RxBus.get().post("ads", gson.toJson(ad));
                if (ttt + 1 >= adlist.size()) {
                    CardApplication.index = adlist.size() - 1;
                } else {
                    CardApplication.index = ttt + 1;
                }
                break;
            default:
                fragment = new FragmentOne();
        }
        if (99 == index) {
            return;
        }

        fragment.setNavHandle(navHandler);
        if (obj != null) {
            Bundle bundle = new Bundle();
            bundle.putString("msg", String.valueOf(obj));
            fragment.setArguments(bundle);
        }
        fragmentTransaction.replace(R.id.fragment_action, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    //禁用返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    /**
     * 更新时间 设置开关机.
     */
    private void syncTimeAndShutDownDevice() {
        //延时20s执行
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != CardApplication.getInstance().getConfig() &&
                        null != CardApplication.getInstance().getConfig().getEndTime()) {
                    //设备未连接，不发送
                    if (!CardApplication.getInstance().isDeviceSuccess())
                        return;
                    //开关机设置.
                    SerialPort.getInstance().deviceShutDown();
                    //主从机时间同步.
                    SerialPort.getInstance().deviceTimeSync();
                }
            }
        }, 20 * 1000);
    }

    private static class NavigationHandler extends Handler {
        private final WeakReference<CardActivity> activityWeakReference;

        public NavigationHandler(CardActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            CardActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.changeAction(msg.what, msg.obj);
            }
        }
    }
}
