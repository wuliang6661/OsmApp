package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.model.ShopCarBO;
import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.CartContract;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class CartPresenter extends BasePresenterImpl<CartContract.View>
        implements CartContract.Presenter {

    @Override
    public void addAddress(String distributorId, String name, String telephone, String province, String city, String area, String address, String timeStamp, String mac) {

    }


    public void getShopCar() {
        HttpInterfaceIml.getCar(LocalConfiguration.userInfo.getId() + "").subscribe(new HttpResultSubscriber<ShopCarBO>() {
            @Override
            public void onSuccess(ShopCarBO s) {
                if (mView != null) {
                    mView.getCar(s);
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView != null) {
                    mView.onRequestError(message);
                }
            }
        });
    }


    public void getNumCar() {
        HttpInterfaceIml.getNumCar(LocalConfiguration.userInfo.getId() + "").subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                if (mView != null) {
                    mView.getCarNum(s);
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView != null) {
                    mView.onRequestError(message);
                }
            }
        });
    }


    public void delCar(String id) {
        HttpInterfaceIml.delCar(id).subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                if(mView != null){
                    mView.deleteShopSource();
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView != null) {
                    mView.onRequestError(message);
                }
            }
        });
    }


    public void batchDelete(String shopIds) {
        HttpInterfaceIml.batchDelete(shopIds, LocalConfiguration.userInfo.getId() + "").subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                if(mView != null){
                    mView.deleteShopSource();
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView != null) {
                    mView.onRequestError(message);
                }
            }
        });
    }

    public void getUpdateNum(String shopIds, String shopNums) {
        HttpInterfaceIml.getUpdateNum(shopIds, shopNums, LocalConfiguration.userInfo.getId() + "").subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                if(mView != null){
                    mView.deleteShopSource();
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView != null) {
                    mView.onRequestError(message);
                }
            }
        });
    }

}
