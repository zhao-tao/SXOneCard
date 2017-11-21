package com.sxonecard.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sxonecard.CardApplication;
import com.sxonecard.base.RxBus;
import com.sxonecard.http.DateUtil;
import com.sxonecard.http.HttpDataListener;
import com.sxonecard.http.HttpDataSubscriber;
import com.sxonecard.http.HttpRequestProxy;
import com.sxonecard.http.bean.AdBean;
import com.sxonecard.http.bean.AdResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeartBeatBroadcastReceiver extends BroadcastReceiver {

    String nextTime = CardApplication.nextTime;
    List<AdBean> adlist = CardApplication.adlist;
    int index = CardApplication.index;

    @Override
    public void onReceive(final Context context, Intent intent) {
        heartBeat(context);
        uploadOrder(context);
        //切换广告
        if (null != adlist) {
            int flag = intime(adlist);
            if (0 == flag || 1 == flag || 3 == flag || 5 == flag) { // 时间没到, 播放默认广告
                RxBus.get().post("ads", "default");
            } else if (2 == flag || 4 == flag) { // 播放本次广告
                AdBean ad = adlist.get(index);
                Gson gson = new Gson();
                RxBus.get().post("ads", gson.toJson(ad));
            }
        }
        //TODO 请求更新广告
        updateAds(context);
        CardApplication.getInstance().incTimeCount();
        Intent i = new Intent(context, HeartBeatService.class);
        context.startService(i);
    }

    /**
     * 向服务器定时提交状态
     *
     * @param context
     */
    private void heartBeat(final Context context) {
        int currentCount = CardApplication.getInstance().getTimeCount();
        int period = 1800;
        if (CardApplication.getInstance().getConfig() != null)
            period = CardApplication.getInstance().getConfig().getRunRate();
//        upload(context);
        //未到时间
        if (currentCount % period != 0)
            return;
        //上传错误日志
//        upload(context);
        Map<String, String> param = new HashMap<String, String>();
        param.put("ImeiId", CardApplication.IMEI);
        param.put("Status", "1");
        param.put("Note", "");
        param.put("Time", DateUtil.getCurrentTime());
        // 心跳
        HttpDataListener listener = new HttpDataListener<String>() {
            @Override
            public void onNext(String o) {
            }

            @Override
            public void onError(Context context, int code, String msg) {
                Log.e("HeartBeat", "心跳信号错误");
            }
        };
        HttpRequestProxy.getInstance().phphreat(new HttpDataSubscriber(listener,
                context, false), param);
    }

    /**
     * 上传失败交易信息
     *
     * @param context
     */
    private void uploadOrder(final Context context) {
        Map<Long, String> orders = OrderDb.find();
        if (orders != null && !orders.isEmpty()) {
            for (final Map.Entry<Long, String> entry : orders.entrySet()) {
                String order = entry.getValue();
                final long key = entry.getKey();
                Gson gson = new Gson();
                Map<String, String> param = gson.fromJson(order, HashMap.class);
                HttpDataListener tradeListener = new HttpDataListener<String>() {
                    @Override
                    public void onNext(String tradeStatusBean) {
                        OrderDb.delete(key);
                    }

                    @Override
                    public void onError(Context context, int code, String msg) {
                        super.onError(context, code, msg);
                    }
                };
                HttpRequestProxy.getInstance().uploadTrade(new HttpDataSubscriber(tradeListener,
                        context, false), param);

            }
        }
    }

    private void updateAds(final Context context) {
        if (isSendAds()) {
            HttpDataListener listener2 = new HttpDataListener<AdResult>() {
                @Override
                public void onNext(AdResult result) {
                    CardApplication.nextTime = result.getNexttime();
                    if (result.getData() != null && result.getData().size() > 0) {
                        CardApplication.adlist = result.getData();
                        CardApplication.index = 0;
                    }
                }
            };
            HttpRequestProxy.getInstance().getAd(new HttpDataSubscriber(listener2, context
                    , false), "ads", CardApplication.IMEI);
        }
    }

    /**
     * 判断是否需要获取广告
     *
     * @return
     */
    private boolean isSendAds() {
        if (TextUtils.isEmpty(nextTime)) {
            return true;
        }
        long next_time = getDateTime(nextTime);
        long curr_time = Calendar.getInstance().getTimeInMillis();
        return curr_time > next_time;
    }


    /**
     * 上传log日志（暂不启用，因产生的文件过大导致GC）
     *
     * @param context
     */
    private void upload(Context context) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(path + File.separator + "citypass");
        if (dir.exists()) {
            byte[] buff = new byte[1024];
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    try {
//                        FileInputStream inputStream = new FileInputStream(file);
//                        int length;
//                        String msg = file.getName().substring(0, file.getName().indexOf(".")) + "\n";
//                        while ((length = inputStream.read(buff)) != -1) {
//                            msg += new String(buff, 0, length);
//                        }
//                        inputStream.close();
//                        HttpDataListener listener = new HttpDataListener<String>() {
//                            @Override
//                            public void onNext(String o) {
//                            }
//
//                            @Override
//                            public void onError(Context context, int code, String msg) {
//                                super.onError(context, code, msg);
//                            }
//                        };
//                        Map<String, String> param = new HashMap<>(4);
//                        param.put("error", "error");
//                        param.put("des", msg);
//                        param.put("imeiid", CardApplication.IMEI);
//                        param.put("version", "" + PackageUtil.getVersion(context));
//                        HttpRequestProxy.getInstance().uploadLog(new HttpDataSubscriber(listener,
//                                context, false), param);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
                    } finally {
                        file.delete();
                    }
                }
            }
        }
    }




    /**
     * 获取日期的精确时间
     *
     * @param time
     * @return
     */
    private long getDateTime(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date ddd = sdf.parse(time);
            return ddd.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param ads
     * @return 1== 时间没有到新闻发布时间，播放默认广告  2==时间在发布时间中 3== 时间大于结束时间，播放默认广告 4==播放下一个广告
     */
    private int intime(List<AdBean> ads) {
        if (ads == null) {
            return 0;
        }
        // FIXME: 2017/11/16 index变量的作用？
        AdBean bean = ads.get(index);
        long start_time_long = getDateTime(bean.getStarttime());
        long end_time_long = getDateTime(bean.getEndtime());
        long curr_time = Calendar.getInstance().getTimeInMillis();

        if (start_time_long > curr_time) {
            return 1;
        } else if (start_time_long < curr_time && curr_time < end_time_long) {
            return 2;
        } else if (curr_time > end_time_long) {
            index = index + 1;
            if (index >= ads.size() - 1) {
                index = ads.size() - 1;
            }
            if (index == ads.size() - 1) {
                return 3;
            } else {
                AdBean next = ads.get(index);
                long start_time_long_next = getDateTime(next.getStarttime());
                long end_time_long_next = getDateTime(next.getEndtime());
                if (start_time_long_next < curr_time && curr_time < end_time_long_next) {
                    return 4;
                } else {
                    return 5;
                }
            }
        }
        return 0;

    }


}