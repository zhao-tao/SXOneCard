package com.sxonecard.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sxonecard.util.DateTools;
import com.sxonecard.util.DownLoadFile;
import com.sxonecard.util.LogcatHelper;
import com.sxonecard.util.PrinterTestUtil;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;

import static com.sxonecard.http.Constants.PAGE_AD;
import static com.sxonecard.http.Constants.PAGE_CHECK_CARD;
import static com.sxonecard.http.Constants.PAGE_CHOOSE_MONEY;
import static com.sxonecard.http.Constants.PAGE_CHOOSE_SERVICE;
import static com.sxonecard.http.Constants.PAGE_DEVICE_EXCEPT;
import static com.sxonecard.http.Constants.PAGE_PAY_METHOD;
import static com.sxonecard.http.Constants.PAGE_PAY_SUCCESS;
import static com.sxonecard.http.Constants.PAGE_QR_CODE;
import static com.sxonecard.http.Constants.PAGE_RECHANGE;
import static com.sxonecard.http.Constants.PAGE_RECHANGE_ERROR;
import static com.sxonecard.http.Constants.isDebug;

/**
 * @Author
 */

public class CardActivity extends FragmentActivity {
    private static String TAG = "CardActivity";
    @Bind(R.id.ll_test)
    LinearLayout llTest;
    @Bind(R.id.iv_logo)
    ImageView ivLogo;
    @Bind(R.id.btn_exit)
    Button btnExit;
    @Bind(R.id.btn_print)
    Button btnPrint;

    final Handler navHandler = new NavigationHandler(this);
    final Handler navHandler_gg = new NavigationHandler(this);
    Observable<String> adObservable;
    Observable<String> checkModuleObservable;
    Observable<String> interruptObservable;
    //    当前广告显示
    private String curr_ads = "";
    private int current_fragment = 0;

    //    是否全屏显示
    private boolean isFullScreen = false;

    //    TODO:定时关机
    private Handler deviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
//                    SerialPort.getInstance().deviceShutDown();
                    break;
            }
        }
    };

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
        setListener();
        if (isDebug) {
            isFullScreen = false;
            ivLogo.setVisibility(View.GONE);
        } else {
            isFullScreen = true;
            llTest.setVisibility(View.GONE);
        }
//        setFullScreen(isFullScreen);
        // FIXME: 2017/11/17 本地记录特定的Log日志文件
        LogcatHelper.getInstance().start();
    }

    private void setListener() {
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SerialPort.getInstance().exitDevice();
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                测试打印小票
                PrinterTestUtil.getInstance().send();
            }
        });

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setFullScreen(isFullScreen);
            }
        });
    }

    public void setFullScreen(boolean isFullScreen) {

        //隐藏导航栏，并且点击不出现
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (isFullScreen) {
            //设置为非全屏
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
        } else {
            //设置为全屏(隐藏状态栏和导航栏，禁止状态栏下拉和导航栏点击出现)
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        window.setAttributes(attributes);
    }

    /**
     * 初始化设备
     */
    private void initDevice() {
//        设备初始化设备，打开串口接收
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
                }, 3000);
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
                }
            }
        });
        interruptObservable = RxBus.get().register("interrupt", String.class);
        interruptObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String deviceStr) {
                if (current_fragment != 0) {
                    Message msg = Message.obtain(navHandler, 0, "提示");
                    msg.sendToTarget();
                }
            }
        });

        adObservable = RxBus.get().register("ads", String.class);
        adObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String ads_json) {

//                判断时间未到时，显示默认广告
                if ("default".equals(ads_json)) {
                    SharedPreferences sharedata = getApplication().getSharedPreferences("sxcard", MODE_PRIVATE);
                    ads_json = sharedata.getString("default_ads", "");
                }

//                首次进入获取到设备id时，获取并显示默认广告
                if ("init_default".equals(ads_json)) {
                    if (isNetworkAvailable(CardActivity.this)) {
                        defaultads();
                        return;
                    } else {
                        SharedPreferences sharedata = getApplication().getSharedPreferences("sxcard", MODE_PRIVATE);
                        ads_json = sharedata.getString("default_ads", "");
                    }
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

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return (info.getState() == NetworkInfo.State.CONNECTED);
            }
        }
        return false;
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
        if (current_fragment == index) {
            return;
        }

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BaseFragment fragment = null;
        current_fragment = index;
        Log.i(TAG, "changeAction: ===" + index);
        switch (index) {
            case PAGE_CHECK_CARD:
                fragment = new FragmentOne();
                break;
            case PAGE_CHOOSE_SERVICE:
                fragment = new FragmentTwo();
                break;
            case PAGE_CHOOSE_MONEY:
                fragment = new FragmentThree();
                break;
//            case 3:
//                fragment = new FragmentFour();
//                break;
            case PAGE_PAY_METHOD:
                fragment = new FragmentFive();
                break;
            case PAGE_QR_CODE:
                fragment = new FragmentSix();
                break;
            case PAGE_PAY_SUCCESS:
                fragment = new FragmentSeven();
                break;
            case PAGE_RECHANGE:
                fragment = new FragmentReChange();
                break;
            case PAGE_RECHANGE_ERROR:
                fragment = new ReChangeError();
                break;
            case PAGE_DEVICE_EXCEPT:
                fragment = new DeviceException();
                break;
            case PAGE_AD:
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

        if (PAGE_AD == index) {
            return;
        }

        //        网络不可用时返回首页
        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.netErr, Toast.LENGTH_LONG).show();
            if (PAGE_CHECK_CARD != index && PAGE_DEVICE_EXCEPT != index) {
                fragment = new FragmentOne();
            }
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != CardApplication.getInstance().getConfig() &&
                        null != CardApplication.getInstance().getConfig().getEndTime()) {
                    //设备未连接，不发送
                    if (!CardApplication.getInstance().isDeviceSuccess())
                        return;
                    //开关机设置，指定的开机时间设置
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            deviceHandler.sendEmptyMessage(1);
                        }
                    };
                    Timer timer = new Timer(true);
                    // TODO: 2017/12/5 定时在指定关机时间执行
//                    timer.schedule(task, DateTools.strToDateLong("2017-12-05 11:18:10"));
                    timer.schedule(task, DateTools.strToDateLong(CardApplication.getInstance().getConfig().getEndTime()));
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
