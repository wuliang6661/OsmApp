package com.heloo.android.osmapp.ui.main;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.ActivityMainBinding;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.MainContract;
import com.heloo.android.osmapp.mvp.presenter.MainPresenter;
import com.heloo.android.osmapp.ui.main.circle.CircleFragment;
import com.heloo.android.osmapp.ui.main.home.HomeFragment;
import com.heloo.android.osmapp.ui.main.mine.MineFragment;
import com.heloo.android.osmapp.ui.main.nice.NiceFragment;
import com.heloo.android.osmapp.ui.main.store.StoreFragment;
import com.heloo.android.osmapp.utils.AppManager;
import com.heloo.android.osmapp.utils.ToastUtils;

import org.json.JSONException;
import java.io.IOException;

import cn.jzvd.Jzvd;
import okhttp3.ResponseBody;



public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter,ActivityMainBinding>
    implements MainContract.View{

    Snackbar snackbar;
    private Fragment mContent = null;

    private HomeFragment homeFragment = new HomeFragment();
    private CircleFragment circleFragment = new CircleFragment();
    private StoreFragment storeFragment = new StoreFragment();
    private MineFragment mineFragment = new MineFragment();
    private NiceFragment niceFragment = new NiceFragment();
    public static boolean isForeground = false;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
        isForeground = false;
    }

    private void initViews() {
        viewBinding.bottomNB.enableAnimation(true);
        viewBinding.bottomNB.enableShiftingMode(false);
        viewBinding.bottomNB.enableItemShiftingMode(true);
        setListener();
        goToFragment(homeFragment);
    }

    private void setListener() {
        viewBinding.bottomNB.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    releaseVideo();
                    goToFragment(homeFragment);
                    break;
                case R.id.beauty:
                    releaseVideo();
                    goToFragment(niceFragment);
//                    snackbar = Snackbar.make(viewBinding.getRoot(),"你点击按钮2",Snackbar.LENGTH_SHORT);
//                    snackbar.setAction("知道了", v -> snackbar.dismiss());
//                    snackbar.show();
                    break;
                case R.id.circle:
                    releaseVideo();
                    goToFragment(circleFragment);
                    break;
                case R.id.store:
                    releaseVideo();
                    goToFragment(storeFragment);
                    break;
                case R.id.mine:
                    releaseVideo();
                    goToFragment(mineFragment);
                    break;
            }
            return true;
        });
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

    /**
     * 修改显示的内容 不会重新加载
     **/
    public void goToFragment(Fragment to) {
        if (mContent != to) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
            if (!to.isAdded()) { // 先判断是否被add过
                if (mContent != null)
                    transaction.hide(mContent).add(R.id.fragment_container, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
                else
                    transaction.add(R.id.fragment_container, to).commitAllowingStateLoss();
            } else {
                if (mContent != null)
                    transaction.hide(mContent).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
                else
                    transaction.show(to).commitAllowingStateLoss();
            }
            mContent = to;
        }
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    ToastUtils.showShortToast("再按一次退出程序");
                    firstTime = secondTime;
                    return true;
                } else {
                    MyApplication.SESSIONID = null;
                    AppManager.getAppManager().finishAllActivity();
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void releaseVideo(){
        if (Jzvd.CURRENT_JZVD == null){
            return;
        }
        if (Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
            Jzvd.releaseAllVideos();
        }
    }
}