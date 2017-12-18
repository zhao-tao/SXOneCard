package com.sxonecard;

import android.media.MediaPlayer;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.sxonecard.background.OrderDb;
import com.sxonecard.base.CrashHandler;
import com.sxonecard.http.Constants;
import com.sxonecard.http.bean.AdBean;
import com.sxonecard.http.bean.ReChangeSQL;
import com.sxonecard.http.bean.RechargeCardBean;
import com.sxonecard.http.bean.SetBean;

import org.litepal.LitePalApplication;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by HeQiang on 2017/4/22.
 */

public class CardApplication extends LitePalApplication {

    public static List<AdBean> adlist = null;
    public static String nextTime;
    public static int index = 0;
    //    以下值需要交易时提交到后台
    //    公交卡信息
    public RechargeCardBean checkCard;
    //    当前订单id
    private String currentOrderId;
    //当前补充值的卡信息
    public static ReChangeSQL reChangeSQL;


    //    首次请求获取的配置
    private SetBean config;
    //    IMEI在接收到串口信息，并连接成功后，从串口获取赋值
    public static String IMEI = "";

    //    a_money卡内初始金额，b_money充值金额，a_money+b_money=充值后卡内金额
    public static double a_money = 0.0;
    public static double b_money = 0.0;

    public static MediaPlayer mediaPlayer = new MediaPlayer();

    public static int chong_type = 1;  // 1微信  2支付宝

    private int timeCount;
    private static CardApplication instance;

    public static CardApplication getInstance() {
        return instance;
    }

    private boolean isDeviceSuccess = false;

    public SetBean getConfig() {
        return config;
    }

    public void setConfig(SetBean config) {
        this.config = config;
    }

    //设备是否正常
    private AtomicBoolean isDeviceOn = new AtomicBoolean(false);

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
        instance = this;
    }

    public void setDevice(boolean status) {
        isDeviceOn.set(status);
    }

    public boolean isDeviceOn() {
        boolean status = isDeviceOn.get();
        isDeviceOn.set(false);
        return status;
    }

    public void initImageLoader() {
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .considerExifParams(true)
                .cacheOnDisk(true)
                .displayer(new SimpleBitmapDisplayer())
//                .showImageOnFail(R.mipmap.img_null)
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .threadPoolSize(3)
                .defaultDisplayImageOptions(displayImageOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .imageDownloader(new BaseImageDownloader(getApplicationContext(),
                        5 * 1000, 10 * 1000))
                .build();
        ImageLoader.getInstance().init(configuration);
        OrderDb.init(getApplicationContext());
        if (!Constants.isDebug) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(this);
        }
    }

    public int getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }

    public void incTimeCount() {
        timeCount++;
    }

    public boolean isDeviceSuccess() {
        return isDeviceSuccess;
    }

    public void setDeviceSuccess(boolean deviceSuccess) {
        isDeviceSuccess = deviceSuccess;
    }

    public void setCheckCard(RechargeCardBean card) {
        this.checkCard = card;
    }

    public RechargeCardBean getCheckCard() {
        return checkCard;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(String currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    public static String DoubleToString(double price) {
        DecimalFormat df = new DecimalFormat("####.##");
        return df.format(price);
    }
}
