package com.heloo.android.osmapp.ui.main.home;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.heloo.android.osmapp.databinding.FragmentBeautyBinding;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.BeautyContract;
import com.heloo.android.osmapp.mvp.presenter.BeautyPresenter;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * 美丽汇
 */
public class BeautyFragment extends MVPBaseFragment<BeautyContract.View, BeautyPresenter, FragmentBeautyBinding>
        implements BeautyContract.View{


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