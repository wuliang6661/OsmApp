package com.heloo.android.osmapp.ui.main.home;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.heloo.android.osmapp.databinding.FragmentPartyBinding;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.PartyContract;
import com.heloo.android.osmapp.mvp.presenter.PartyPresenter;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * 五美党建
 */
public class PartyFragment extends MVPBaseFragment<PartyContract.View, PartyPresenter, FragmentPartyBinding>
        implements PartyContract.View{


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void getAddResult(ResponseBody addResult) throws JSONException, IOException {

    }

    @Override
    public void onRequestError(String msg) {

    }

    @Override
    public void onRequestEnd() {

    }
}