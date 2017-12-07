package com.sxonecard.http;

/**
 * Created by Administrator on 2017-5-23.
 */

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.sxonecard.CardApplication;
import com.sxonecard.base.RxBus;
import com.sxonecard.http.bean.RechargeCardBean;
import com.sxonecard.util.ByteUtil;
import com.sxonecard.util.Crc16;
import com.sxonecard.util.DateTools;
import com.sxonecard.util.SyncSysTime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import static com.sxonecard.http.Constants.LOG_CHANGE;

/**
 * 串口操作类
 */
public class SerialPort {
    private static String TAG = "serialPort";
    private static SerialPort portUtil;
    private android_serialport_api.SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private String path = "/dev/ttyS3";
    private int baudrate = 115200;
    //串口开关
    private boolean isStop = false;
    //是否连接成功
    private boolean isConnect = false;

    public static SerialPort getInstance() {
        if (null == portUtil) {
            portUtil = new SerialPort();
        }
        return portUtil;
    }


    /**
     * 初始化串口信息
     */
    public void init() {
        try {
            mSerialPort = new android_serialport_api.SerialPort(new File(path), baudrate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            mReadThread = new ReadThread(TAG + "_thread");
            isStop = false;
            mReadThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendBuffer(byte[] mBuffer) {
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
                mOutputStream.flush();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void autoRun() {
        byte[] buff = new byte[11];
        ByteUtil.hexStringToBytes("43,56,65,ff,ff,04,00,0b,00,2f,be", buff, 0);
        boolean result = sendBuffer(buff);
        Log.i(TAG, result ? "发送自动测试命令成功" : "发送自动测试命令失败");
//        int crc_value = Crc16.getChk(buff, index);
//        buff[index++] = ByteUtil.byte_toL(crc_value);
//        buff[index++] = ByteUtil.byte_toH(crc_value);
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
        }
    }

    private void onDataReceive(byte[] srcBuffer, int size) {
        try {
            int flag = srcBuffer[7] & 0xFF;
            Log.i(TAG, "onDataReceive Command " + flag);
            switch (flag) {
                case 1:
                    //测试链接.
                    Log.i(TAG, "onDataReceive 链接成功");
                    onConnection(srcBuffer, size);
                    break;
                case 2:
                    //刷卡设备各功能模块检测.
                    onCheckModule(srcBuffer);
                    break;
                case 3:
                    //接收感应区卡片返回信息
                    onCheckCardData(srcBuffer);
                    break;
                case 4:
                    //接收刷卡模块刷卡命令结果
                    onChargeCardCmd(srcBuffer);
                    break;
                case 5:
                    //关闭刷卡模块
                    parseShutDownCmd(srcBuffer);
                    break;
                case 6:
                    //时间同步.
                    Log.i(TAG, "时间同步");
                    break;
                case 7:
                    //检测到卡拿走
                    onInterrupt();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {

        }
    }

    private void onConnection(byte[] srcBuffer, int size) {
        String temp = ByteUtil.bytesToAscii(srcBuffer, 12, 12);
        // TODO: 2017/11/21 给IMEI赋值，根据IMid请求默认广告的地址保存到本地，并显示
        CardApplication.IMEI = temp;
        if (!isConnect) {
            RxBus.get().post("ads", "init_default");
            isConnect = true;
        }
        byte[] moduleCheckByte = moduleCheck();
        sendBuffer(moduleCheckByte);
    }

    private byte[] moduleCheck() {
        byte[] buff = new byte[12];
        int index = ByteUtil.hexStringToBytes("43,56,65,FF,FF,02,00,02,00,00", buff, 0);
        int crc_value = Crc16.getChk(buff, index);
        buff[index++] = ByteUtil.byte_toL(crc_value);
        buff[index++] = ByteUtil.byte_toH(crc_value);
        return buff;
    }

    public void checkDevice() {
        Log.i(TAG, "检查设备中");
        sendBuffer(testConnections());
        //5秒后检查设备标志
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //设备未检查到，跳转至维护页面
                if (!CardApplication.getInstance().isDeviceOn()) {
                    RxBus.get().post("checkDevice", "0");
                    //继续请求
                    checkDevice();
                }
            }
        }, 5000);
    }

    private byte[] testConnections() {
        byte[] buff = new byte[12];
        int index = ByteUtil.hexStringToBytes("43,56,65,FF,FF,00,00,01,00,00", buff, 0);
        int crc_value = Crc16.getChk(buff, index);
        buff[index++] = ByteUtil.byte_toL(crc_value);
        buff[index++] = ByteUtil.byte_toH(crc_value);
        return buff;
    }

    public void deviceTimeSync() {
        sendBuffer(timeSync());
    }

    private byte[] timeSync() {
        byte[] buff = new byte[20];
        int index = ByteUtil.hexStringToBytes("43,56,65,FF,FF,06,00,06,07,00", buff, 0);
        String phpTime = CardApplication.getInstance().getConfig().getRetTime();
        index = ByteUtil.date_tobuff(phpTime, buff, index);

        //修改系统时间.
        try {
            SyncSysTime.setDateTime(phpTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, DateTools.getCurrent());

        int crc_value = Crc16.getChk(buff, index);
        buff[index++] = ByteUtil.byte_toL(crc_value);
        buff[index++] = ByteUtil.byte_toH(crc_value);

        return buff;
    }

    /**
     * 发送签退命令
     *
     * @return
     */
    public void exitDevice() {
        byte[] buff = new byte[11];
        ByteUtil.hexStringToBytes("43,56,65,ff,ff,04,00,09,00,2f,be", buff, 0);
        boolean result = sendBuffer(buff);
        Log.i(TAG, result ? "发送签退命令成功" : "发送签退命令失败");
    }

    public void deviceShutDown() {
        Log.i(TAG, "发送关机命令");
        sendBuffer(shutDownDevice());
    }

    private byte[] shutDownDevice() {
        byte[] buff = new byte[20];
        int index = ByteUtil.hexStringToBytes("43,56,65,FF,FF,05,00,05,08,00,FA", buff, 0);
        //TODO：开机时间设定
        String startTime = CardApplication.getInstance().getConfig().getStartTime();
        index = ByteUtil.date_tobuff(startTime, buff, index);

//        //关机时间
//        String endTime = CardApplication.getInstance().getConfig().getEndTime();
//        index = ByteUtil.date_tobuff(endTime, buff, index);

        int crc_value = Crc16.getChk(buff, index);
        buff[index++] = ByteUtil.byte_toL(crc_value);
        buff[index++] = ByteUtil.byte_toH(crc_value);

        return buff;
    }


    private void parseShutDownCmd(byte[] destBuff) {
        if (205 == Integer.valueOf(destBuff[10])) {
            Log.i(TAG, "syncShutDownCmd success." + destBuff[10]);
        } else {
            Log.i(TAG, "syncShutDownCmd error." + destBuff[10]);
        }
    }

    private void onCheckModule(byte[] destBuff) {
        String status;
        if (5 == Integer.valueOf(destBuff[11])) {
            //各模块正常.
            status = "1";
            CardApplication.getInstance().setDevice(true);
            Log.i(TAG, "模块检测成功");
        } else {
            status = "0";
            Log.i(TAG, "模块检测失败");
        }
        RxBus.get().post("checkDevice", status);
    }

    /**
     * 接收感应区卡片返回信息.
     *
     * @param destBuff
     */
    private void onCheckCardData(byte[] destBuff) {
        String byteString = ByteUtil.bytesToHexString(destBuff);
        String balance = byteString.substring(11 * 2, 15 * 2);
        String cardNumber = byteString.substring(19 * 2, 23 * 2);
        String cardNumDate = byteString.substring(27 * 2, 31 * 2);
        String cardType = byteString.substring(31 * 2, 33 * 2);
        String cardStatus = byteString.substring(33 * 2, 34 * 2);
        int value = 0;
        try {
            value = Integer.parseInt(balance, 16);
        } catch (Exception e) {

        }
        RechargeCardBean cardBean = new RechargeCardBean(cardNumber,
                value, cardNumDate, cardType, cardStatus);
        Gson gson = new Gson();
        RxBus.get().post("checkCard", gson.toJson(cardBean));
    }

    /**
     * 解析卡充值返回的数据.
     *
     * @param destBuff
     */
    private void onChargeCardCmd(byte[] destBuff) {
        try {
            String byteString = ByteUtil.bytesToHexString(destBuff);
            int status = destBuff[10] & 0x0FF;
            Log.i(TAG, status == 0 ? "充值成功" : "充值失败");
            Log.i(LOG_CHANGE, "串口返回" + (status == 0 ? "充值成功" : "充值失败"));
            if (status == 0) {
                String balance = byteString.substring(11 * 2, 15 * 2);
                String cardNumber = byteString.substring(19 * 2, 23 * 2);
                String order = byteString.substring(23 * 2, 31 * 2);
                RechargeCardBean cardBean = new RechargeCardBean();
                try {
                    cardBean.setAmount(Integer.parseInt(balance, 16));
                } catch (Exception e) {
                    cardBean.setAmount(0);
                }
                cardBean.setCardNumber(cardNumber);
                cardBean.setOrderNo(order);
                RxBus.get().post("reChangeCard", cardBean);
            } else {
                // TODO: 2017/12/6 写入补充值暂存信息
                RxBus.get().post("chargeError", String.valueOf(status));
            }
        } catch (Exception e) {

        }
    }

    /**
     * @功能描述 :向刷卡模块发送充值命令
     * @返回值类型 :void
     */

    public boolean sendRechargeCmd(int money) {
        boolean result = sendBuffer(rechargeCmd(money));
        Log.i(TAG, result ? "发送充值命令成功" : "发送充值命令失败");
        return result;
    }

    private byte[] rechargeCmd(int money) {
        byte[] buff = new byte[23];
        int index = ByteUtil.hexStringToBytes("43,56,65,FF,FF,05,00,04,0B,00", buff, 0);
        //金额占4位
        index = ByteUtil.int_tobuff4(money, buff, index);
        //data
        index = ByteUtil.now_tobuff(buff, index);
        int crc_value = Crc16.getChk(buff, 21);
        buff[index++] = ByteUtil.byte_toL(crc_value);
        buff[index++] = ByteUtil.byte_toH(crc_value);
        return buff;
    }

    private void onInterrupt() {
        RxBus.get().post("interrupt", "card disconnected");
    }


    private class ReadThread extends Thread {
        private byte[] buffer = new byte[512];

        public ReadThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            super.run();
            while (!isStop && !isInterrupted()) {
                int size;
                try {
                    if (mInputStream == null) {
                        return;
                    }
                    Arrays.fill(buffer, (byte) 0);
//                    byte[] buffer = new byte[512];
                    //TODO 收到串口状态，读取包头开始（串口状态监听）
                    size = mInputStream.read(buffer, 0, 11);
                    if (size == 11) {
                        //判断是否数据开头，不是丢掉该桢数据
                        if (!ByteUtil.bytesToHexString(buffer, 5, false).equalsIgnoreCase("435665FFFF")) {
                            Thread.sleep(300);
                            mInputStream.read(buffer);
                            Log.i(TAG, "1 读取数据错误");
                        } else {
                            //提取长度
                            int data_length = ((buffer[9] << 8 | buffer[8]) & 0x0FFFF) + 1;
                            int total = size + data_length;
                            Log.i(TAG, "length=" + String.valueOf(data_length));
                            //追加数据
                            size += mInputStream.read(buffer, size, data_length);
                            //只有接收到正确数据才解析
                            if (total == size) {
                                Log.i(TAG, "2 " + ByteUtil.bytesToHexString(buffer, total, true));
                                onDataReceive(buffer, size);
                            }
                        }
                    } else {
                        Log.i(TAG, "1 error_data= " + ByteUtil.bytesToHexString(buffer, 5, false));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}