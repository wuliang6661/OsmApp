package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.model.ShopDetailsBO;
import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.StoreDetailContract;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class StoreDetailPresenter extends BasePresenterImpl<StoreDetailContract.View>
        implements StoreDetailContract.Presenter {

    @Override
    public void getDetail(String id) {
        HttpInterfaceIml.getProductDetail(id).subscribe(new HttpResultSubscriber<ShopDetailsBO>() {

            @Override
            public void onSuccess(ShopDetailsBO shopDetailsBO) {
                if (mView != null) {
                    mView.getDetail(shopDetailsBO);
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
    public void addCart(String goodsId, String id, String num) {
        HttpInterfaceIml.addCart(goodsId, id, num).subscribe(new HttpResultSubscriber<String>() {

            @Override
            public void onSuccess(String s) {
                if(mView != null){
                    mView.getAddCart(s);
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
