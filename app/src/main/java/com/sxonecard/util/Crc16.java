package com.sxonecard.util;

/**
 * Created by Administrator on 2017-5-24.
 */

public class Crc16 {
    public static int getChk(byte[] buff, int len) {
        int crc = 0xFFFF;
        for (int i = 0; i < len; i++) {
            crc ^= (buff[i]) & 0xFF;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) == 1) {
                    crc = (crc >> 1) ^ 0xA001;
                } else {
                    crc = (crc >> 1);
                }
            }
        }
        return crc;
    }

}
