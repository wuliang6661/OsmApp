package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.MineContract;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class MinePresenter extends BasePresenterImpl<MineContract.View>
        implements MineContract.Presenter{

    @Override
    public void getSignStatus(String token, String username) {
        HttpInterfaceIml.getSignStatus(token,username).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                if (mView == null)
                    return;
                mView.onRequestEnd();
            }

            @Override
            public void onError(Throwable e) {
                if (mView == null)
                    return;
                mView.onRequestError(e.getMessage());
            }

            @Override
            public void onNext(ResponseBody s) {
                if (mView == null)
                    return;
                try {
                    mView.getSignStatus(s);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
