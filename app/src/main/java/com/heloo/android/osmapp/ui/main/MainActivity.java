package com.heloo.android.osmapp.ui.main;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityMainBinding;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.MainContract;
import com.heloo.android.osmapp.mvp.presenter.MainPresenter;
import com.heloo.android.osmapp.service.PushMessageReceiver;
import com.heloo.android.osmapp.ui.WebActivity;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.ui.main.circle.CircleFragment;
import com.heloo.android.osmapp.ui.main.home.HomeFragment;
import com.heloo.android.osmapp.ui.main.mine.MineFragment;
import com.heloo.android.osmapp.ui.main.nice.NiceFragment;
import com.heloo.android.osmapp.ui.main.store.StoreFragmentNew;
import com.heloo.android.osmapp.utils.AppManager;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.heloo.android.osmapp.utils.ToastUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.TreeSet;

import cn.jpush.android.api.JPushInterface;
import cn.jzvd.Jzvd;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.ResponseBody;


public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter, ActivityMainBinding>
        implements MainContract.View {

    Snackbar snackbar;
    private Fragment mContent = null;

    private HomeFragment homeFragment = new HomeFragment();
    private CircleFragment circleFragment = new CircleFragment();
    private StoreFragmentNew storeFragment = new StoreFragmentNew();
    private MineFragment mineFragment = new MineFragment();
    private NiceFragment niceFragment = new NiceFragment();
    public static boolean isForeground = false;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private int count = 0;
    public static int pushUrlCount = 0;
    public static int lastUrlCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        registerPush();
        PushMessageReceiver.num = 0;
        ShortcutBadger.removeCount(this); //for 1.1.4+
        if (MyApplication.spUtils.getString("read") == null
                || !MyApplication.spUtils.getString("read").equals("yes")) {
            infoDialog();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        if (pushUrlCount != lastUrlCount) {
            if (getIntent().getExtras() != null) {
                String url = getIntent().getExtras().getString("value");
                Intent intent1 = new Intent(this, WebViewActivity.class);
                intent1.putExtra("url", url);
                startActivity(intent1);
            }
            lastUrlCount = pushUrlCount;
        }
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
                    count = 0;
                    releaseVideo();
                    goToFragment(homeFragment);
                    break;
                case R.id.beauty:
                    count = 1;
                    releaseVideo();
                    goToFragment(niceFragment);
//                    snackbar = Snackbar.make(viewBinding.getRoot(),"你点击按钮2",Snackbar.LENGTH_SHORT);
//                    snackbar.setAction("知道了", v -> snackbar.dismiss());
//                    snackbar.show();
                    break;
                case R.id.circle:
                    count = 2;
                    releaseVideo();
                    goToFragment(circleFragment);
                    break;
                case R.id.store:
                    releaseVideo();
                    if (goLogin()) {
                        count = 3;
                        goToFragment(storeFragment);
                    } else {
                        viewBinding.bottomNB.post(new Runnable() {
                            @Override
                            public void run() {
                                viewBinding.bottomNB.setCurrentItem(count);
                            }
                        });
                    }
                    break;
                case R.id.mine:
                    count = 4;
                    releaseVideo();
                    goToFragment(mineFragment);
                    break;
            }
            return true;
        });
    }


    private void registerPush() {
        if (LocalConfiguration.userInfo != null) {
            //极光推送注册
            JPushInterface.setAlias(this, 1, LocalConfiguration.userInfo.getPhone());
            TreeSet<String> treeSet = new TreeSet<>();
            treeSet.add(LocalConfiguration.userInfo.getPhone());
            JPushInterface.setTags(this, 1, treeSet);
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

    /**
     * 修改显示的内容 不会重新加载
     **/
    public void goToFragment(Fragment to) {
        if (mContent != to) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
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

    private void releaseVideo() {
        if (Jzvd.CURRENT_JZVD == null) {
            return;
        }
        if (Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
            Jzvd.releaseAllVideos();
        }
    }


    /**
     * 协议
     */
    private void infoDialog() {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.information_dialog_layout, null);
        TextView cancle = v.findViewById(R.id.cancle);
        TextView sure = v.findViewById(R.id.sure);
        TextView txt = v.findViewById(R.id.txt2);

        // SpannableStringBuilder 用法
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder();
        spannableBuilder.append(txt.getText().toString());
        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Bundle bundle = new Bundle();
                bundle.putString("url", HttpInterface.URL + LocalConfiguration.xieyiUrl);
                gotoActivity(WebActivity.class, bundle, false);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
//                设定的是span超链接的文本颜色，而不是点击后的颜色
                ds.setColor(Color.parseColor("#009FFF"));
                ds.setUnderlineText(false);    //去除超链接的下划线
                ds.clearShadowLayer();//清除阴影
            }

        };
        spannableBuilder.setSpan(clickableSpan, 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置部分文字点击事件
        ClickableSpan clickableSpan2 = new ClickableSpan() {//隐私声明
            @Override
            public void onClick(View widget) {
                Bundle bundle = new Bundle();
                bundle.putString("url", HttpInterface.URL + LocalConfiguration.xieyiUrl);
                gotoActivity(WebActivity.class, bundle, false);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
//                设定的是span超链接的文本颜色，而不是点击后的颜色
                ds.setColor(Color.parseColor("#009FFF"));
                ds.setUnderlineText(false);    //去除超链接的下划线
                ds.clearShadowLayer();//清除阴影
            }

        };
//        spannableBuilder.setSpan(clickableSpan2, 16, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txt.setText(spannableBuilder);
        txt.setHighlightColor(getResources().getColor(android.R.color.transparent));//点击后的背景颜色，Android4.0以上默认是淡绿色，低版本的是黄色
        txt.setMovementMethod(LinkMovementMethod.getInstance());

        builder.setView(v);
        builder.setCancelable(true);
        final Dialog noticeDialog = builder.create();
        noticeDialog.getWindow().setGravity(Gravity.CENTER);
        noticeDialog.setCancelable(false);
        noticeDialog.show();

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeDialog.dismiss();
                System.exit(0);
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeDialog.dismiss();
                MyApplication.spUtils.put("read", "yes");
            }
        });

        WindowManager.LayoutParams layoutParams = noticeDialog.getWindow().getAttributes();
        layoutParams.width = (int) (ScreenUtils.getScreenWidth() * 0.75);
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        noticeDialog.getWindow().setAttributes(layoutParams);
    }
}