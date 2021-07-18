package com.heloo.android.osmapp.mvp;

/**
 * MVPPlugin
 * �wuliang
 */

public interface  BasePresenter <V extends BaseView>{
    void attachView(V view);

    void detachView();
}
