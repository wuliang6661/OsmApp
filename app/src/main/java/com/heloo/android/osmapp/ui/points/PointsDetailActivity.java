package com.heloo.android.osmapp.ui.points;


import android.os.Bundle;
import android.view.View;

import com.heloo.android.osmapp.databinding.ActivityPointsDetailBinding;
import com.heloo.android.osmapp.model.AddScoreBean;
import com.heloo.android.osmapp.model.ScoreListBean;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.PointsDetailContract;
import com.heloo.android.osmapp.mvp.presenter.PointsDetailPresenter;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Description : 积分详情
 *
 * @author WITNESS
 * @date 4/26/21
 */
public class PointsDetailActivity extends MVPBaseActivity<PointsDetailContract.View, PointsDetailPresenter, ActivityPointsDetailBinding>
    implements PointsDetailContract.View{

    private String tag;//1 获取  2  消费
    private AddScoreBean addScoreBean;//增加
    private ScoreListBean scoreListBean;//消费


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra("tag") != null &&
                getIntent().getStringExtra("tag").equals("1")){
            addScoreBean = (AddScoreBean)getIntent().getSerializableExtra("data");
        }else {
            scoreListBean = (ScoreListBean)getIntent().getSerializableExtra("data");
        }
        initView();
    }

    private void initView() {
        goBack();
        setHeader();
        setTitle("珍币详情");
        if (addScoreBean != null){
            viewBinding.pointsNum.setText(String.format("+%s",addScoreBean.getScore()));
            viewBinding.pointsType.setText(addScoreBean.getDescription());
            viewBinding.pointsTime.setText(addScoreBean.getCreateDate());
            viewBinding.pointsCode.setText(addScoreBean.getId());
            viewBinding.lastScore.setVisibility(View.GONE);
            viewBinding.pointsComment.setText(addScoreBean.getSubject());
        }else {
            viewBinding.pointsNum.setText(String.format("-%s",scoreListBean.getConsumeScore()));
            viewBinding.pointsType.setText("积分消费");
            viewBinding.pointsTime.setText(scoreListBean.getCreateDate());
            viewBinding.pointsCode.setText(scoreListBean.getId());
            viewBinding.lastScore.setVisibility(View.VISIBLE);
            viewBinding.pointsLeft.setText(scoreListBean.getLastScore());
            viewBinding.pointsComment.setText(scoreListBean.getSubject());
        }
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