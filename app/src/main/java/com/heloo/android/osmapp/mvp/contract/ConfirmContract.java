package com.heloo.android.osmapp.mvp.contract;

import com.heloo.android.osmapp.model.OrderPriceBO;
import com.heloo.android.osmapp.model.PayBean;
import com.heloo.android.osmapp.model.ShopAddressList;
import com.heloo.android.osmapp.model.UserInfo;
import com.heloo.android.osmapp.mvp.BasePresenter;
import com.heloo.android.osmapp.mvp.BaseRequestView;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class ConfirmContract {
    public interface View extends BaseRequestView {

        void getAddress(ShopAddressList address);

        void getshopInter(OrderPriceBO priceBO);

        void getAllInter(UserInfo info);

        void pay(PayBean payBean, String orderId);
    }

    public interface Presenter extends BasePresenter<View> {


    }
}
