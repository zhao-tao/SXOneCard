package com.sxonecard.http;

import android.content.Context;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * 用于在Http请求开始时，根据需要决定是否显示ProgressDialog
 * 如果显示ProgressDialog，在Http请求结束时，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 */
public class HttpDataSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {
    //http数据处理回调对象
    private HttpDataListener httpDataListener;
    //进度对话框
    private ProgressDialogHandler progressDialogHandler;

    private Context context;

    public HttpDataSubscriber(HttpDataListener httpDataListener, Context context, boolean hasProgressDialog) {
        this(httpDataListener, context);
        if (hasProgressDialog)
            progressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    public HttpDataSubscriber(HttpDataListener httpDataListener, Context context) {
        this.httpDataListener = httpDataListener;
        this.context = context;
    }

    private void showProgressDialog() {
        if (progressDialogHandler != null) {
            progressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (progressDialogHandler != null) {
            progressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            progressDialogHandler = null;
        }
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpDataException) {
            int code = ((HttpDataException) e).getCode();
            httpDataListener.onError(context, code, e.getMessage());
        }else{
            httpDataListener.onError(context, Constants.NET_ERR, "网络错误");
        }
        dismissProgressDialog();
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (httpDataListener != null) {
            httpDataListener.onNext(t);
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}