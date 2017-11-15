package com.sxonecard.background;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sxonecard.R;

import java.io.IOException;
import java.util.Calendar;


/**
 * Created by HeQiang on 2017/10/11.
 */

public class SoundService extends Service {
    public static final String CAOZUOTISHI ="CAOZUOTISHI";
    public static final String XUANZEJINE ="XUANZEJINE";
    public static final String ZHIFUFANGSHI ="ZHIFUFANGSHI";
    public static final String ERWEIMA ="ERWEIMA";
    public static final String CHONGZHIZHONG ="CHONGZHIZHONG";
    public static final String CHONGZHI_SUCCESS ="CHONGZHI_SUCCESS";
    public static final String CHONGZHI_FAIL ="CHONGZHI_FAIL";
    public static final String ERWEIMAGUOQI ="ERWEIMAGUOQI";

    public static final String WUQUZOUKAPIAN ="WUQUZOUKAPIAN";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    private MediaPlayer mediaPlayer;


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.d("sound", intent.getAction());
            Uri uri = null;
            switch (intent.getAction()) {
                case CAOZUOTISHI:
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.caozuotishi);
                    break;
                case XUANZEJINE:
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.xuanzejine);
                    break;
                case ZHIFUFANGSHI:
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.zhifufangshi);
                    break;
                case ERWEIMA:
//                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.erweima);
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.erweima_1);
                    break;
                case CHONGZHIZHONG:
//                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chongzhizhong);
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chongzhizhong_1);
                    break;
                case CHONGZHI_SUCCESS:
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chongzhi_success);
                    break;
                case CHONGZHI_FAIL:
//                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chongzhi_fail);
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chongzhishibai_1);
                    break;
                case ERWEIMAGUOQI:
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chongzhi_fail);
                    break;


                case WUQUZOUKAPIAN:
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wuquzoukapian);
                    break;
                default:
                    break;
            }
            if (uri != null) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
