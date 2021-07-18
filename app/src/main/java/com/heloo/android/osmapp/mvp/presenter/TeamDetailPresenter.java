package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.TeamDetailContract;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class TeamDetailPresenter extends BasePresenterImpl<TeamDetailContract.View>
        implements TeamDetailContract.Presenter{
    @Override
    public void getData(String token, int pageNum, int pageSize, String username) {
        HttpInterfaceIml.getTeamDetail(token,pageNum,pageSize,username).subscribe(new Subscriber<ResponseBody>() {
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
                    mView.getData(s);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
