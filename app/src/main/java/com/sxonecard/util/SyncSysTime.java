package com.sxonecard.util;

import android.app.AlarmManager;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pc on 2017-06-21.
 */

public class SyncSysTime {

    /*public static void setSystemTime(String datetimes) {
        DataOutputStream os = null;
        try {
            Process process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(
                    process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT+8\n");
            os.writeBytes("/system/bin/date -s " + datetimes + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            Log.e("请获取Root权限", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                os.close();
                os = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    static Process createSuProcess() throws IOException {
        File rootUser = new File("/system/xbin/ru");
        if (rootUser.exists()) {
            return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
        } else {
            return Runtime.getRuntime().exec("su");
        }
    }

    static Process createSuProcess(String cmd) throws IOException {

        DataOutputStream os = null;
        Process process = createSuProcess();

        try {
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit $?\n");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }

        return process;
    }

    static void requestPermission() throws InterruptedException, IOException {
        createSuProcess("chmod 666 /dev/alarm").waitFor();
    }

    public static void setDateTime(String time){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(time);
            requestPermission();
            boolean isSuc = SystemClock.setCurrentTimeMillis(date.getTime());//需要Root权限
            Log.d("setTime", isSuc + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDateTime(int year, int month, int day, int hour, int minute, int second) {
        try {
            requestPermission();

            Calendar c = Calendar.getInstance();

            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month - 1);
            c.set(Calendar.DAY_OF_MONTH, day);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, second);

            long when = c.getTimeInMillis();

            if (when / 1000 < Integer.MAX_VALUE) {
                boolean isSuc = SystemClock.setCurrentTimeMillis(when);//需要Root权限
                Log.d("setTime", isSuc + "");
            }
            long now = Calendar.getInstance().getTimeInMillis();

            if (now - when > 1000) {
                throw new IOException("failed to set Date.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("modify sysTime", e.getMessage());
        }
    }

}
