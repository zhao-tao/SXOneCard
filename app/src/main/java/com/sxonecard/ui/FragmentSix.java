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
import com.sxonecard.R;
import com.sxonecard.background.SoundService;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.base.RxBus;
import com.sxonecard.http.DateUtil;
import com.sxonecard.http.HttpDataListener;
import com.sxonecard.http.HttpDataSubscriber;
import com.sxonecard.http.HttpRequestProxy;
import com.sxonecard.http.MyCountDownTimer;
import com.sxonecard.http.bean.GsonData;
import com.sxonecard.http.bean.RechargeCardBean;
import com.sxonecard.http.bean.TradeStatusBean;
import com.sxonecard.http.SerialPort;
import com.sxonecard.util.EncryptUtil;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.functions.Action1;


/**
 * Created by pc on 2017-04-25.
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

    private String imeiId;
    private String orderId;
    private long startTime;
    private MyCountDownTimer timer;
    private MyCountDownTimer delayTimer;
    private static final int WAIT_TIME = 60;
    private static final int DELAYED_TIME = 1;
    private int transactionStatus = 0;
    Observable<RechargeCardBean> reChangeCardObservable;
    Observable<String> chargeErrorObservable;

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
        setVoice(SoundService.ERWEIMA);
        String msgArrag = this.getArguments().getString("msg").toString();
        Gson gson = new Gson();
        GsonData gsonData = gson.fromJson(msgArrag, GsonData.class);

        String qrCodeString = gsonData.getQrCodeString();
        imeiId = gsonData.getImeiId();
        orderId = gsonData.getOrderId();

        Bitmap qrBitmap = generateBitmap(qrCodeString, 400, 400);
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Bitmap bitmap = addLogo(qrBitmap, logoBitmap);
        qrImage.setImageBitmap(bitmap);
        waitingStatus();
        startTime = System.currentTimeMillis();
        registerListener();
        requestTradeStatus();
    }

    private void registerListener(){
        reChangeCardObservable = RxBus.get().register("reChangeCard", RechargeCardBean.class);
        reChangeCardObservable.subscribe(new Action1<RechargeCardBean>() {
            @Override
            public void call(RechargeCardBean cardBean) {
//                uploadTradeData();
                double money = cardBean.getAmount() * 1.0 / 100;
                DecimalFormat df = new DecimalFormat("######0.00");
                Message msgCode = navHandle.obtainMessage(6,df.format(money));
                msgCode.sendToTarget();
            }
        });
        chargeErrorObservable = RxBus.get().register("chargeError", String.class);
        chargeErrorObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String reChangeCardJson) {
                Message msgCode = navHandle.obtainMessage(400, getText(R.string.chargeError));
                msgCode.sendToTarget();
            }
        });
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

    private void requestTradeStatus() {
        Log.i("订单支付成功后订单轮询...", DateUtil.getCurrentDateTime());
        HttpDataListener listener = new HttpDataListener<TradeStatusBean>() {
            @Override
            public void onNext(TradeStatusBean tradeStatus) {
                long endTime = System.currentTimeMillis();
                if(BuildConfig.AUTO_TEST){
                    if((endTime - startTime) > 5000){
                        tradeStatus.setStatus(1);
                    }
                }
                transactionStatus = tradeStatus.getStatus();
                if (0 == tradeStatus.getStatus() && (endTime - startTime) < (WAIT_TIME + DELAYED_TIME) * 1000) {
                    requestTradeStatus();
                } else if (1 == tradeStatus.getStatus()) {
                    Log.i("订单支付成功后订单支付...", "成功");
                    sendSuccTradeData(tradeStatus);
                }
            }

            @Override
            public void onError(Context context, int code, String msg) {
                super.onError(context, code, msg);
                cancelTimer();
                Log.d("FragmentSix", "code:" + code + ",msg:" + msg);
                Message scan = navHandle.obtainMessage(400, getText(R.string.chargeExpired));
                scan.sendToTarget();
            }
        };
        String md5Code = EncryptUtil.getMd5(imeiId + orderId).toUpperCase();
        HttpRequestProxy.getInstance().pollingRequest(
                new HttpDataSubscriber<TradeStatusBean>(listener, getContext(), false),
                imeiId, orderId, md5Code);
    }

    //服务器支付成功，向下位机发送写卡命令
    private void sendSuccTradeData(TradeStatusBean status) {
        //清除定时器
        cancelTimer();
        if (SerialPort.getInstance().sendRechargeCmd(status.getPrice())) {
            setVoice(SoundService.CHONGZHIZHONG);
            scanLayout.setVisibility(View.GONE);
            progressImg.setVisibility(View.VISIBLE);
        } else {
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

    private void cancelTimer(){
        if (null != timer)
            timer.cancel();
        if (null != delayTimer)
            delayTimer.cancel();
    }
}
