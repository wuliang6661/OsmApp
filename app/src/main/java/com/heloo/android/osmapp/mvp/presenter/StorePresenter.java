package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.model.ShopBannarBO;
import com.heloo.android.osmapp.model.ShopListBO;
import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.StoreContract;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class StorePresenter extends BasePresenterImpl<StoreContract.View>
        implements StoreContract.Presenter {

    public void getClassify() {
        HttpInterfaceIml.getStoreClassify().subscribe(new HttpResultSubscriber<ShopListBO>() {

            @Override
            public void onSuccess(ShopListBO shopListBO) {
                if (mView != null) {
                    mView.getClassify(shopListBO);
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView == null)
                    return;
                mView.onRequestError(message);
            }
        });
    }

    @Override
    public void getBanner() {
        HttpInterfaceIml.getStoreBanner().subscribe(new HttpResultSubscriber<ShopBannarBO>() {
            @Override
            public void onSuccess(ShopBannarBO s) {
                if (mView != null) {
                    mView.getBanner(s);
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView == null)
                    return;
                mView.onRequestError(message);
            }
        });
    }
}
