package com.heloo.android.osmapp.utils.rx;

import com.google.gson.Gson;
import com.heloo.android.osmapp.model.BaseResult;
import com.heloo.android.osmapp.model.ErrorBean;
import com.heloo.android.osmapp.utils.LogUtils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 作者 by wuliang 时间 16/11/24.
 * <p>
 * 此类用于对请求返回的数据解析并判断筛选
 */

public class RxResultHelper {

    private static final String TAG = "RxResultHelper";

    public static <T> Observable.Transformer<BaseResult<T>, T> httpRusult() {
        return new Observable.Transformer<BaseResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<BaseResult<T>> apiResponseObservable) {
                return apiResponseObservable.flatMap(
                        new Func1<BaseResult<T>, Observable<T>>() {
                            @Override
                            public Observable<T> call(BaseResult<T> result) {
                                if (result.surcess()) {
                                    return createData(result.getData());
                                } else {
                                    LogUtils.E("请求报错啦！！！");
                                    if (result.getData() instanceof String) {
                                        return Observable.error(new RuntimeException((String) result.getData()));
                                    } else {
                                        String str = new Gson().toJson(result.getData());
                                        ErrorBean errorBean = new Gson().fromJson(str, ErrorBean.class);
                                        return Observable.error(new RuntimeException(errorBean.errMsg));
                                    }
                                }
                            }
                        }
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    private static <T> Observable<T> createData(final T t) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
