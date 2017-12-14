package com.sxonecard.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sxonecard.BuildConfig;
import com.sxonecard.CardApplication;
import com.sxonecard.R;
import com.sxonecard.background.SoundService;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.base.RxBus;
import com.sxonecard.http.DateUtil;
import com.sxonecard.http.HttpDataListener;
import com.sxonecard.http.HttpDataSubscriber;
import com.sxonecard.http.HttpRequestProxy;
import com.sxonecard.http.MyCountDownTimer;
import com.sxonecard.http.SerialPort;
import com.sxonecard.http.bean.ChangeData;
import com.sxonecard.http.bean.ReChangeSQL;
import com.sxonecard.http.bean.RechargeCardBean;
import com.sxonecard.http.bean.TradeStatusBean;
import com.sxonecard.util.DateTools;
import com.sxonecard.util.EncryptUtil;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.functions.Action1;

import static com.sxonecard.CardApplication.a_money;
import static com.sxonecard.http.Constants.LOG_CHANGE;
import static com.sxonecard.http.Constants.PAGE_PAY_METHOD;
import static com.sxonecard.http.Constants.PAGE_PAY_SUCCESS;
import static com.sxonecard.http.Constants.isRechange;


/**
 * Created by pc on 2017-04-25.
 * 二维码支付，等待支付成功的提示框
 * 1 从选择支付方式跳入（带入信息）
 * 2 从补充值进入，直接进入支付页面
 */

public class FragmentSix extends BaseFragment {
    @Bind(R.id.qrImage)
    ImageView qrImage;
    @Bind(R.id.secondTime)
    TextView secondTime;
    @Bind(R.id.scan_layout)
    LinearLayout scanLayout;
    @Bind(R.id.progress_img)
    ImageView progressImg;
    @Bind(R.id.tv_back)
    TextView mBackTv;

    private String imeiId;
    private String orderId;
    //刚进入页面的时间
    private long startTime;
    //    是否在有效的订单轮询时间（60s倒计时+2秒延迟容错）
    private boolean isRefreshStatus = true;
    private MyCountDownTimer timer;
    private MyCountDownTimer delayTimer;
    private static final int WAIT_TIME = 60;
    //    倒计时完成后，等待结果返回的时间
    private static final int DELAYED_TIME = 5;
    //    扫二维码期间请求到的订单状态0失败,1为成功
    private int transactionStatus = 0;
    Observable<RechargeCardBean> reChangeCardObservable;
    Observable<String> chargeErrorObservable;
    private ChangeData changeData;

    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        //logo大小为二维码整体大小的1/8
        float scaleFactor = srcWidth * 1.0f / 8 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_6;
    }

    @Override
    public void initView() {
        String msgArrag = this.getArguments().getString("msg").toString();
        Gson gson = new Gson();
        changeData = gson.fromJson(msgArrag, ChangeData.class);

//        判断是正常充值还是补充值
        if (isRechange) {
//            添加的补充值逻辑，直接跳转到支付流程
            TradeStatusBean tradeData = new TradeStatusBean();
            tradeData.setPrice(Integer.parseInt(changeData.getRechangeFee()) * 100);
            registerListener();
            sendSuccTradeData(tradeData);
            return;
        }

        setVoice(SoundService.ERWEIMA);

        String qrCodeString = changeData.getQrCodeString();
        imeiId = changeData.getImeiId();
        orderId = changeData.getOrderId();

        Bitmap qrBitmap = generateBitmap(qrCodeString, 400, 400);
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Bitmap bitmap = addLogo(qrBitmap, logoBitmap);
        qrImage.setImageBitmap(bitmap);
        waitingStatus();
        startTime = System.currentTimeMillis();
        registerListener();
        requestTradeStatus();
        Log.i("订单支付成功后订单轮询...", DateUtil.getCurrentDateTime());

        mBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                返回到支付方式选择（必须带入已选择的充值金额）
                Message message = new Message();
                message.obj = changeData.getRechangeFee();
                message.what = PAGE_PAY_METHOD;
                navHandle.sendMessage(message);
            }
        });
    }

    private void registerListener() {
        reChangeCardObservable = RxBus.get().register("reChangeCard", RechargeCardBean.class);
        reChangeCardObservable.subscribe(new Action1<RechargeCardBean>() {
            @Override
            public void call(RechargeCardBean cardBean) {
                double money = cardBean.getAmount() * 1.0 / 100;
                DecimalFormat df = new DecimalFormat("######0.00");
                Message msgCode = navHandle.obtainMessage(PAGE_PAY_SUCCESS, df.format(money));
                msgCode.sendToTarget();
            }
        });
        chargeErrorObservable = RxBus.get().register("chargeError", String.class);
        chargeErrorObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String reChangeCardJson) {
                // TODO: 2017/12/6 保存补充值暂存信息
                ReChangeSQL recharge = new ReChangeSQL();
                recharge.setValue(CardApplication.getInstance().getCheckCard().getCardNO(),
                        changeData.getRechangeFee(), CardApplication.getInstance().getCurrentOrderId(), System.currentTimeMillis());
                recharge.save();
                uploadFailData();
                Message msgCode = navHandle.obtainMessage(400, getText(R.string.chargeError));
                msgCode.sendToTarget();
            }
        });
    }

    /**
     * 上传交易失败数据.
     */
    private void uploadFailData() {
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
                getContext(), false), jsonObj);
    }

    private void waitingStatus() {
        timer = new MyCountDownTimer(WAIT_TIME * 1000, 1000) {
            int time = WAIT_TIME;

            @Override
            public void onTick(long millisUntilFinished) {
                secondTime.setText("  等待支付:( " + time + " 秒)  ");
                time--;
            }

            @Override
            public void onFinish() {
                Log.i("fragment", DateUtil.getCurrentDateTime());
                secondTime.setVisibility(View.GONE);
                checkPayResult();
            }
        };
        timer.start();
    }

    /**
     * 如果二维码倒计时结束,隐藏倒计时时间,用1秒处理获取的订单结果
     */
    private void checkPayResult() {
        delayTimer = new MyCountDownTimer(DELAYED_TIME * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (transactionStatus != 1) {
                    Message scan = navHandle.obtainMessage(400, getText(R.string.chargeExpired));
                    navHandle.sendMessageDelayed(scan, DELAYED_TIME * 1000);
                }
            }
        };
        delayTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTimer();
        RxBus.get().unregister("reChangeCard", reChangeCardObservable);
        RxBus.get().unregister("chargeError", chargeErrorObservable);
    }

    /**
     * 订单轮询
     */
    private void requestTradeStatus() {
        // FIXME: 2017/11/18 订单支付状态轮询
        HttpDataListener listener = new HttpDataListener<TradeStatusBean>() {
            @Override
            public void onNext(TradeStatusBean tradeStatus) {
//                接收到返回数据的时间
                long endTime = System.currentTimeMillis();
                if (BuildConfig.AUTO_TEST) {
                    if ((endTime - startTime) > 5000) {
                        tradeStatus.setStatus(1);
                    }
                }
                transactionStatus = tradeStatus.getStatus();
                if (0 == tradeStatus.getStatus()) {
                    if ((endTime - startTime) < (WAIT_TIME + DELAYED_TIME) * 1000) {
                        requestTradeStatus();
                    } else {
                        Log.i("okhttp", startTime + " " + endTime + " " + (endTime - startTime));
                    }
                } else if (1 == tradeStatus.getStatus()) {
                    Log.i(LOG_CHANGE, "付款成功，将要给卡充值");
                    sendSuccTradeData(tradeStatus);
                }
            }

            @Override
            public void onError(Context context, int code, String msg) {
                super.onError(context, code, msg);
                cancelTimer();
                Log.i(LOG_CHANGE, "请求后台充值交易错误 code:" + code + ",msg:" + msg);
                Message scan = navHandle.obtainMessage(400, getText(R.string.chargeExpired));
                scan.sendToTarget();
            }
        };
        String md5Code = EncryptUtil.getMd5(imeiId + orderId).toUpperCase();
        HttpRequestProxy.getInstance().pollingRequest(
                new HttpDataSubscriber<TradeStatusBean>(listener, getContext(), false),
                imeiId, orderId, md5Code);
    }

    //TODO：服务器支付成功，向下位机发送写卡命令
    private void sendSuccTradeData(TradeStatusBean status) {
        //清除定时器
        cancelTimer();
        if (SerialPort.getInstance().sendRechargeCmd(status.getPrice())) {
            setVoice(SoundService.CHONGZHIZHONG);
            scanLayout.setVisibility(View.GONE);
            mBackTv.setVisibility(View.GONE);
            progressImg.setVisibility(View.VISIBLE);
        } else {
            Log.i(LOG_CHANGE, "给串口发送充值命令失败");
            Message scan = navHandle.obtainMessage(400, getText(R.string.chargeError));
            scan.sendToTarget();
        }
    }

    @Override
    public void loadData() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cancelTimer() {
        if (null != timer)
            timer.cancel();
        if (null != delayTimer)
            delayTimer.cancel();
    }
}
