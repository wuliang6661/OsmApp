package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.model.TeamDetailBean;
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
    public void getData( String username) {
        HttpInterfaceIml.getTeamDetail(username).subscribe(new HttpResultSubscriber<TeamDetailBean>() {
            @Override
            public void onSuccess(TeamDetailBean teamDetailBean) {
                if(mView != null){
                    mView.getData(teamDetailBean);
                }
            }

            @Override
            public void onFiled(String message) {
                if(mView != null){
                    mView.onRequestError(message);
                }
            }
        });

    }
}
