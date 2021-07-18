package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.LiveContract;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class LivePresenter extends BasePresenterImpl<LiveContract.View>
        implements LiveContract.Presenter{

    @Override
    public void getLives(String token, int pageNum, int pageSize) {
        HttpInterfaceIml.getLives(token,pageNum,pageSize).subscribe(new Subscriber<ResponseBody>() {
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
                    mView.getLives(s);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
