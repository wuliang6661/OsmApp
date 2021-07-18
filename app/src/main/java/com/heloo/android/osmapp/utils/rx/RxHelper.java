package com.heloo.android.osmapp.utils.rx;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/2/6.
 */

public class RxHelper {
    public static <T> Observable.Transformer<T, T> httpRusult() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> apiResponseObservable) {
                return apiResponseObservable.flatMap(
                        new Func1<T, Observable<T>>() {
                            @Override
                            public Observable<T> call(T result) {
                                return createData(result);
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
