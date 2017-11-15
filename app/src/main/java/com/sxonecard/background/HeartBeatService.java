package com.sxonecard.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sxonecard.CardApplication;

/**
 * Created by HeQiang on 2017/4/29.
 */

public class HeartBeatService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent heartBeatIntent = new Intent(this, HeartBeatBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, heartBeatIntent, 0);
        long time = SystemClock.elapsedRealtime()+1000;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,time , sender);
        return super.onStartCommand(intent, flags, startId);
    }
}
