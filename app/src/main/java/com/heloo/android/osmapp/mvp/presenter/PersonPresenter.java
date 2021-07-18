package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.PersonContract;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class PersonPresenter extends BasePresenterImpl<PersonContract.View>
        implements PersonContract.Presenter{

    @Override
    public void changeInfo(String token, List<MultipartBody.Part> partList) {
        HttpInterfaceIml.changeInfo(token,partList).subscribe(new Subscriber<ResponseBody>() {
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
                    mView.changeInfo(s);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
