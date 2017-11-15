package com.sxonecard.http;

import android.content.Context;

/**
 * Created by HeQiang on 2016/11/23.
 */

public interface DataListener<T> {
    void onNext(T t);

    void onError(Context context, int code, String msg);
}
