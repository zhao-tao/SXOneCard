package com.sxonecard.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogcatHelper {

    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;

    private SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd");

    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static LogcatHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper();
        }
        return INSTANCE;
    }

    private LogcatHelper() {
        mPId = android.os.Process.myPid();
    }

    public void start() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        PATH_LOGCAT = path + File.separator+"citypass";
        File dir = new File(path+File.separator+"citypass");
        if(!dir.exists()){
            dir.mkdir();
        }

        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                File file = new File(dir, "logcat-" + simpleDateFormat2.format(new Date()) + ".log");
                if(!file.exists())
                    file.createNewFile();
                out = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             * log level：*:v , *:d , *:w , *:e , *:f , *:s
             * */
            cmds = "logcat *:e *:i *:d| grep \"(" + mPID + ")\"";

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);
                String line;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((simpleDateFormat2.format(new Date()) + "  " + line + "\n").getBytes("UTF-8"));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }
    }
}