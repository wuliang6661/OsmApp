package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.model.CreateOrderBo;
import com.heloo.android.osmapp.model.MyAdressBean;
import com.heloo.android.osmapp.model.OrderPriceBO;
import com.heloo.android.osmapp.model.PayBean;
import com.heloo.android.osmapp.model.ShopAddressList;
import com.heloo.android.osmapp.model.UserInfo;
import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.ConfirmContract;

import java.util.List;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class ConfirmPresenter extends BasePresenterImpl<ConfirmContract.View>
        implements ConfirmContract.Presenter {

    public void getUserAdd() {
        HttpInterfaceIml.getUserAdd(LocalConfiguration.userInfo.getId() + "")
                .subscribe(new HttpResultSubscriber<ShopAddressList>() {
                    @Override
                    public void onSuccess(ShopAddressList s) {
                        if (mView != null) {
                            mView.getAddress(s);
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


    public void getUserIntegration() {
        HttpInterfaceIml.getUserIntegration(LocalConfiguration.userInfo.getUid() + "")
                .subscribe(new HttpResultSubscriber<UserInfo>() {
                    @Override
                    public void onSuccess(UserInfo s) {
                        if (mView != null) {
                            mView.getAllInter(s);
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


    public void getshopInter(String shopIds, String shopNums) {
        HttpInterfaceIml.getshopInter(shopIds, shopNums).subscribe(new HttpResultSubscriber<OrderPriceBO>() {
            @Override
            public void onSuccess(OrderPriceBO s) {
                if (mView != null) {
                    mView.getshopInter(s);
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


    public void createOrder(String addressId, String shopIds, String shopNums,
                            String remarks, Integer scoreflag) {
        HttpInterfaceIml.createOrder(addressId, shopIds, shopNums, remarks, scoreflag)
                .subscribe(new HttpResultSubscriber<CreateOrderBo>() {
                    @Override
                    public void onSuccess(CreateOrderBo s) {
                        pay(s.orderId);
                    }

                    @Override
                    public void onFiled(String message) {
                        if (mView != null) {
                            mView.onRequestError(message);
                        }
                    }
                });
    }


    public void pay(String orderId) {
        HttpInterfaceIml.pay(orderId).subscribe(new HttpResultSubscriber<PayBean>() {
            @Override
            public void onSuccess(PayBean s) {
                if (mView != null) {
                    mView.pay(s, orderId);
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


    public void getSincePoint() {
        HttpInterfaceIml.getSincePoint().subscribe(new HttpResultSubscriber<List<MyAdressBean>>() {
            @Override
            public void onSuccess(List<MyAdressBean> s) {
                if (mView != null) {
                    if (s.size() > 0) {
                        mView.getZtAddress(s.get(0));
                    }
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
