package com.sxonecard.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class EncryptUtil {
    /**
     * 获取一个字符串的MD5码
     *
     * @param plainText 源字符串
     * @return 返回字符串的MD5码
     * @author Administrator
     */
    public static String getMd5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            String TemStr = buf.toString().toUpperCase();
            return TemStr;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
