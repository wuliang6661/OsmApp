package com.heloo.android.osmapp.ui.main.home;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.heloo.android.osmapp.databinding.FragmentPearlBinding;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.PearlContract;
import com.heloo.android.osmapp.mvp.presenter.PearlPresenter;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * 珠光
 */
public class PearlFragment extends MVPBaseFragment<PearlContract.View, PearlPresenter, FragmentPearlBinding>
        implements PearlContract.View{


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