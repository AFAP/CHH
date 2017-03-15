package com.afap.discuz.chh.net;


import com.afap.utils.ToastUtil;
import com.tencent.bugly.crashreport.BuglyLog;

import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * 基础的Subscriber，主要是统一处理onError中的异常情况
 */
public class BaseSubscriber<T> extends Subscriber<T> {
    private final static String TAG = "BaseSubscriber";

    @Override
    public void onCompleted() {
        BuglyLog.i(TAG, "---->onCompleted");
    }

    @Override
    public void onError(Throwable error) {
        BuglyLog.i(TAG, "---->onError");
        error.printStackTrace();
        // 这里可根据不同的状态来进行统一提示
        if (error instanceof SocketTimeoutException) {
            ToastUtil.showShort("连接超时，请检查您的网络情况");
        } else {
            ToastUtil.showShort("服务器访问异常，请稍后再试");
        }
    }

    @Override
    public void onNext(T t) {
        BuglyLog.i(TAG, "---->onNext");
    }
}
