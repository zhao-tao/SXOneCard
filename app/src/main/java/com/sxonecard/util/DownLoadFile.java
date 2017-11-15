package com.sxonecard.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017-6-19.
 */

public class DownLoadFile {

    public String PACK_NAME = "com.sxonecard";

    public String APP_FILE = "snonecard.apk";

    Context context = null;

    public DownLoadFile(Context c){
        if(c!=null){
            this.context = c;
        }
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public void downFiletoDecive(final String urlStr){
        new Thread(new Runnable(){
            @Override
            public void run() {
                boolean downfalg = false;
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    //设置超时间为3秒
                    conn.setConnectTimeout(3*1000);
                    //防止屏蔽程序抓取而返回403错误
                    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

                    //得到输入流
                    InputStream inputStream = conn.getInputStream();
                    //获取自己数组
                    byte[] getData = readInputStream(inputStream);

                    File file = new File(Environment.getExternalStorageDirectory(),APP_FILE);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(getData);
                    if(fos!=null){
                        fos.close();
                    }
                    if(inputStream!=null){
                        inputStream.close();
                    }
                    downfalg = true;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(downfalg){
                        Log.d("UPDATE","安装更新");
                        down();
                    }
                }
            }
        }).start();

    }

    private void down() {
        update();
    }

    private void closeTargetApp(String packageName)
    {
        SuUtil.kill(packageName);
    }

    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        File file = new File(Environment.getExternalStorageDirectory(), APP_FILE);

        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
