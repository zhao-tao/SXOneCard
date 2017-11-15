package com.sxonecard.http;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by liukun on 16/3/10.
 */
public abstract class HttpDataListener<T> implements DataListener<T> {
    public abstract void onNext(T t);

    public void onError(Context context, int code, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
