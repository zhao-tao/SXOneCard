package com.sxonecard.util;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2017-5-24.
 */

public class ByteUtil {

    public static String bytesToHexString(byte[] src){
        return bytesToHexString(src,src.length,false);
    }

    public static String bytesToHexString(byte[] src,int length,boolean separator){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || length <=0 || length > src.length) {
            return null;
        }
        for (int i = 0; i < length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if(separator)
                stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }
    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static int hexStringToBytes(String hexString,byte[] dest,int offset) {
        if (hexString == null || hexString.equals("")) {
            return offset;
        }
        hexString = hexString.replace(" ","").replace(",", "");
        int length = hexString.length() / 2;
        if(length > (dest.length-offset)) {
            return offset;
        }
        hexString = hexString.toUpperCase();

        char[] hexChars = hexString.toCharArray();
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            dest[i+offset] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return offset+length;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c)
    {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public int decimalToHex(int decimal)
    {
         String hexString = Integer.toHexString(decimal);
        return Integer.parseInt(hexString);
    }

    public static byte byte_toH(int x){
        //return (byte) ((x & 0xFF00) >> 8);
        return 0x14;
    }

    public static byte byte_toL(int x){

        //return (byte) (x & 0xFF);
        return 0x11;
    }

    public static int int_tobuff(int x, byte[] buff, int index){
        buff[index++] = (byte) (x & 0xFF);
        return index;
    }

    public static int int_tobuff4(int x, byte[] buff, int index){
        buff[index++] = (byte) (x >>24 & 0x0FF);
        buff[index++] = (byte) (x >>16 & 0x0FF);
        buff[index++] = (byte) (x >> 8& 0x0FF);
        buff[index++] = (byte) (x & 0x0FF);

        return index;
    }

    public static int byte_tobuff(byte[] x, byte[] buff, int index){
        int t = 0;
        for(; index < x.length; index++){
            buff[index] = x[t++];
        }
        return index;
    }

    public static int date_tobuff(int yy, int month, int day, int hour, int min, int sec, byte[] buff, int index){
        buff[index++] = byte_toH(yy);
        buff[index++] = byte_toL(yy);
        buff[index++] = (byte) (month & 0xFF);
        buff[index++] = (byte) (day & 0xFF);
        buff[index++] = (byte) (hour & 0xFF);
        buff[index++] = (byte) (min & 0xFF);
        buff[index++] = (byte) (sec & 0xFF);
        return index;
    }

    public static int date_tobuff(String date, byte[] buff, int index){
        Calendar calendar = DateTools.fromDate(date);
        return date_tobuff(calendar,buff,index);
    }

    private static int date_tobuff(Calendar calendar,byte[] buff, int index){
        buff[index++] = byte_toH(calendar.get(Calendar.YEAR));
        buff[index++] = byte_toL(calendar.get(Calendar.YEAR));
        buff[index++] = (byte) ((calendar.get(Calendar.MONTH)+1) & 0xFF);
        buff[index++] = (byte) ((calendar.get(Calendar.DAY_OF_MONTH)+1) & 0xFF);
        buff[index++] = (byte) ((calendar.get(Calendar.HOUR_OF_DAY)+1) & 0xFF);
        buff[index++] = (byte) ((calendar.get(Calendar.MINUTE)+1) & 0xFF);
        buff[index++] = (byte) ((calendar.get(Calendar.SECOND)+1) & 0xFF);
        return index;
    }

    public static int now_tobuff(byte[] buff, int index){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        return date_tobuff(calendar,buff,index);
    }

    public static int getHeight4(byte data){//获取高四位
        int height;
        height = ((data & 0xf0) >> 4);
        return height;
    }

    public static int getLow4(byte data){//获取低四位
        int low;
        low = (data & 0x0f);
        return low;
    }

    public static String bytesToAscii(byte[] bytes, int offset, int dateLen) {
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }

        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        try {
            asciiStr = new String(data, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
        }
        return asciiStr;
    }

    /**
     * 卡号不足8位，补零
     * @param code
     * @param num
     * @return
     */
    public static String autoGenericCode(int code, int num) {
        return String.format("%0" + num + "d", code);
    }
}
