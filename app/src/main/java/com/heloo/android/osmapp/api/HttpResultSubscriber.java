package com.heloo.android.osmapp.api;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.bigkoo.svprogresshud.SVProgressHUD;

import java.net.SocketException;
import java.net.UnknownHostException;

import rx.Subscriber;

/**
 * Created by wuliang on 2018/11/13.
 * <p>
 * 自定义的Subscriber订阅者
 */

public abstract class HttpResultSubscriber<T> extends Subscriber<T> {

    /**
     * 滚动的菊花
     */
    private SVProgressHUD svProgressHUD;


    public HttpResultSubscriber() {

    }

    public HttpResultSubscriber(Context context) {
        svProgressHUD = new SVProgressHUD(context);
        svProgressHUD.showWithStatus("加载中...", SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
    }


    @Override
    public void onNext(T t) {
        onSuccess(t);
//        if (svProgressHUD != null && svProgressHUD.isShowing()) {
//            svProgressHUD.dismiss();
//        }
    }


    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
//        if (svProgressHUD != null && svProgressHUD.isShowing()) {
//            svProgressHUD.dismiss();
//        }
        if (e instanceof NetworkErrorException) {
            onFiled("网络出错，请检查您的网络！");
        } else if (e instanceof SocketException) {
            onFiled("网络出错，请检查您的网络！");
        } else if (e instanceof UnknownHostException) {
            onFiled("网络出错，请检查您的网络！");
        } else {
            onFiled(e.getMessage());
        }
    }


    @Override
    public void onCompleted() {

    }


    public abstract void onSuccess(T t);

    public abstract void onFiled(String message);
}
