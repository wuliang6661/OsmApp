package com.heloo.android.osmapp.ui.main.nice;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.databinding.FragmentNiceBinding;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.NiceContract;
import com.heloo.android.osmapp.mvp.presenter.NicePresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;

import org.json.JSONException;

import java.io.IOException;

import cn.jzvd.Jzvd;
import okhttp3.ResponseBody;

/**
 * 珍好看
 */
public class NiceFragment extends MVPBaseFragment<NiceContract.View, NicePresenter, FragmentNiceBinding>
        implements NiceContract.View, View.OnClickListener {

    private Fragment mContent = null;

    private VideoFragment videoFragment = new VideoFragment();
    private PictureFragment pictureFragment = new PictureFragment();
    private LiveFragment liveFragment = new LiveFragment();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(getActivity()), 0, 0));
        viewBinding.videoBtn.setOnClickListener(this);
        viewBinding.picBtn.setOnClickListener(this);
        viewBinding.liveBtn.setOnClickListener(this);
        viewBinding.title.setText("图集");
        releaseVideo();
        goToFragment(pictureFragment);
        viewBinding.videoImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.video_not_select, null));
        viewBinding.videoTxt.setTextColor(Color.parseColor("#888888"));
        viewBinding.picImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.pic_select, null));
        viewBinding.picTxt.setTextColor(Color.parseColor("#D5AC59"));
        viewBinding.liveImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.live_not_select, null));
        viewBinding.liveTxt.setTextColor(Color.parseColor("#888888"));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.videoBtn:
                viewBinding.title.setText("视频");
                goToFragment(videoFragment);
                viewBinding.videoImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.video_select, null));
                viewBinding.videoTxt.setTextColor(Color.parseColor("#D5AC59"));
                viewBinding.picImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.pic_not_select, null));
                viewBinding.picTxt.setTextColor(Color.parseColor("#888888"));
                viewBinding.liveImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.live_not_select, null));
                viewBinding.liveTxt.setTextColor(Color.parseColor("#888888"));
                break;
            case R.id.picBtn:
                viewBinding.title.setText("图集");
                releaseVideo();
                goToFragment(pictureFragment);
                viewBinding.videoImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.video_not_select, null));
                viewBinding.videoTxt.setTextColor(Color.parseColor("#888888"));
                viewBinding.picImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.pic_select, null));
                viewBinding.picTxt.setTextColor(Color.parseColor("#D5AC59"));
                viewBinding.liveImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.live_not_select, null));
                viewBinding.liveTxt.setTextColor(Color.parseColor("#888888"));
                break;
            case R.id.liveBtn:
                viewBinding.title.setText("图文直播");
                releaseVideo();
                goToFragment(liveFragment);
                viewBinding.videoImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.video_not_select, null));
                viewBinding.videoTxt.setTextColor(Color.parseColor("#888888"));
                viewBinding.picImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.pic_not_select, null));
                viewBinding.picTxt.setTextColor(Color.parseColor("#888888"));
                viewBinding.liveImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.live_select, null));
                viewBinding.liveTxt.setTextColor(Color.parseColor("#D5AC59"));
                break;
        }
    }

    private void releaseVideo() {
        if (Jzvd.CURRENT_JZVD == null) {
            return;
        }
        if (Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
            Jzvd.releaseAllVideos();
        }
    }

    /**
     * 修改显示的内容 不会重新加载
     **/
    public void goToFragment(Fragment to) {
        if (mContent != to) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            if (!to.isAdded()) { // 先判断是否被add过
                if (mContent != null)
                    transaction.hide(mContent).add(R.id.fragmentContainer, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
                else
                    transaction.add(R.id.fragmentContainer, to).commitAllowingStateLoss();
            } else {
                if (mContent != null)
                    transaction.hide(mContent).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
                else
                    transaction.show(to).commitAllowingStateLoss();
            }
            mContent = to;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        videoFragment.onHiddenChanged(hidden);
    }

}