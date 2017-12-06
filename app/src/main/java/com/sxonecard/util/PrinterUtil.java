package com.sxonecard.util;

/**
 * Created by Administrator on 2017-5-23.
 */

import com.sxonecard.CardApplication;
import com.sxonecard.http.DateUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * 串口操作类
 *
 * @author Jerome
 *
 */
public class PrinterUtil {
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private String path = "/dev/ttyS0";
    private int baudrate = 9600;
    private static PrinterUtil portUtil;


    public static PrinterUtil getInstance() {
        if (null == portUtil) {
            portUtil = new PrinterUtil();
            portUtil.onCreate();
        }
        return portUtil;
    }

    /**
     * 初始化串口信息
     */
    public void onCreate() {
        try {
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(){

        String orderid = CardApplication.getInstance().getCurrentOrderId();
        String time = DateUtil.getCurrentTime();
        double b_money = CardApplication.b_money;
        double money = CardApplication.a_money + CardApplication.b_money;

        try {
            PRINT_ALIGN_CENTER();
                /*放大字体*/
            PRINT_DOUBLE_FONT();
            sendCmds("西安城派信息科技有限公司");
            PRINT_LR();
            PRINT_LR();

                /*左对齐*/
            PRINT_ALIGN_LEFT();
            /*正常字体*/
            PRINT_NORMAL_FONT();
            sendCmds("订单编号："+orderid);
            PRINT_LR();
            sendCmds("充值时间："+time);
            PRINT_LR();
            PRINT_LR();
            sendCmds("充值金额："+b_money+"元");
            PRINT_LR();
            sendCmds("当前金额："+money+"元");
            PRINT_LR();
            String chong_type = "微信支付";
            if(CardApplication.chong_type == 2){
                chong_type = "支付宝支付";
            }
            sendCmds("充值方式："+chong_type);

            PRINT_LR();
            PRINT_LR();
            sendCmds("谢谢惠顾");
            PRINT_LR();
            PRINT_LR();
            PRINT_ALIGN_CENTER();
            PRINT_NORMAL_FONT();
            sendCmds("客服电话：029-89688299");
            PRINT_LR();
            PRINT_LR();
            PRINT_ERWEIMA1();
            PRINT_ERWEIMA2();
            PRINT_ERWEIMA3();

            byte[] bt = {0x68, 0x74, 0x74, 0x70, 0x3a, 0x2f, 0x2f, 0x77, 0x77, 0x77, 0x2e,
                    0x74, 0x68, 0x65, 0x63, 0x69, 0x74, 0x79, 0x70, 0x61, 0x73, 0x73,
                    0x2e, 0x63, 0x6e};
            sendBuffer(bt);
            PRINT_ERWEIMA4();
            PRINT_LR();
            PRINT_LR();
            PRINT_LR();
            PRINT_LR();


            PRINT_CUT_PAPER();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送指令到串口
     *
     * @param cmd
     * @return
     */
    public boolean sendCmds(String cmd) {
        boolean result = true;
        try {
            byte[] mBuffer = cmd.getBytes("GBK");
            mOutputStream.write(mBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean sendBuffer(byte[] mBuffer) {
        boolean result = true;
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    private void PRINT_ERWEIMA1() throws IOException {
        sendBuffer(new byte[]{0x1d, 0x01, 0x03, 0x06});
    }

    private void PRINT_ERWEIMA2() throws IOException {
        sendBuffer(new byte[]{0x1d, 0x01, 0x04, 0x32});
    }

    private void PRINT_ERWEIMA3() throws IOException {
        sendBuffer(new byte[]{0x1d, 0x01, 0x01, 0x19, 00});
    }

    private void PRINT_ERWEIMA4() throws IOException {
        sendBuffer(new byte[]{0x1d, 0x01, 0x02});
    }

    private void PRINT_CLR_BUFFER() throws IOException {
        sendBuffer(new byte[]{0x1B,0x40});
    }

    private void PRINT_DOUBLE_FONT() throws IOException {
        sendBuffer(new byte[]{0x1B,0x21,0x30});
    }

    private void PRINT_NORMAL_FONT() throws IOException {
        sendBuffer(new byte[]{0x1B,0x21,0x00});
    }

    private void PRINT_ALIGN_LEFT() throws IOException {
        sendBuffer(new byte[]{0x1B,0x61,0x30});
    }
    private void PRINT_ALIGN_RIGHT() throws IOException {
        sendBuffer(new byte[]{0x1B,0x61,0x32});
    }

    private void PRINT_BARCODE_WIDTH() throws IOException {
        sendBuffer(new byte[]{0x1D,0x77,0x02});
    }

    private void PRINT_BARCODE_HEIGHT() throws IOException {
        sendBuffer(new byte[]{0x1d,0x68,0x60});//x1D\x68\x60
    }

    private void PRINT_BARCODE_WRT_ASC() throws IOException {
        sendBuffer(new byte[]{0x1D,0x48,0x02});
    }

    private void PRINT_BARCODE_DATA_CODE128() throws IOException {
        sendBuffer(new byte[]{0x1D,0x6B,0x48});
    }

    private void PRINT_CUT_PAPER() throws IOException {
        sendBuffer(new byte[]{0x1B,0x69});
    }

    private void PRINT_LR() throws IOException {
        sendBuffer(new byte[]{0xD,0xA});
    }

    private void PRINT_ALIGN_CENTER() throws IOException {
        sendBuffer(new byte[]{0x1B,0x61,0x31});
    }

}