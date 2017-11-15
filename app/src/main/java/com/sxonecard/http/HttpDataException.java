package com.sxonecard.http;

/**
 * Http请求异常
 */
public class HttpDataException extends RuntimeException {


    private int code;

    public HttpDataException(int resultCode, String detailMessage) {
        super(detailMessage);
        this.code = resultCode;
    }

    public HttpDataException(String detailMessage) {
        super(detailMessage);
    }

    public int getCode() {
        return code;
    }

}

