package com.heloo.android.osmapp.base;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.gyf.barlibrary.ImmersionBar;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.ui.login.LoginActivity;
import com.heloo.android.osmapp.utils.AppManager;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.LogUtils;
import com.leaf.library.StatusBarUtil;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * 作者 by witness 时间 16/10/31.
 * <p>
 * 所有activity的基类，此处建立了一个activity的栈，用于管理activity
 */

public abstract class BaseActivity extends RxAppCompatActivity {


    private SVProgressHUD svProgressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        ImmersionBar.with(this).init();   //解决虚拟按键与状态栏沉浸冲突
        LogUtils.init(this);
        svProgressHUD = new SVProgressHUD(this);
        AppManager.getAppManager().addActivity(this);
        StatusBarUtil.setDarkMode(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        AppManager.getAppManager().removeActivity(this);
    }

    /**
     * 常用的跳转方法
     */
    public void gotoActivity(Class<?> cls, boolean isFinish) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public void gotoActivity(Class<?> cls, Bundle bundle, boolean isFinish) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(bundle);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    /**
     * 设置返回键
     */
    protected void goBack() {
        ImageButton back = (ImageButton) findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * 修改标题栏标题
     */
    protected void setTitle(String title) {
        TextView text = (TextView) findViewById(R.id.title);
        text.setText(title);
    }

    protected void setHeader(){
        findViewById(R.id.headLayout).post(() -> findViewById(R.id.headLayout).setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
    }

    /**
     * 注册
     */
    protected void goRegister(){
        findViewById(R.id.registerBtn).setVisibility(View.VISIBLE);
    }


    /**
     * 隐藏返回按钮
     */
    protected void coverBack(){
        findViewById(R.id.backBtn).setVisibility(View.INVISIBLE);
    }
    /**
     * 设置标题栏右边图片及事件
     */
    protected void setRightImage(int res, View.OnClickListener listener) {
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.right_bg_layout);
//        linearLayout.setOnClickListener(listener);
//        linearLayout.setVisibility(View.VISIBLE);
//        ImageView imageView = (ImageView) findViewById(R.id.right_bg);
//        imageView.setBackgroundResource(res);
    }

    /**
     * 跳入登陆
     */
    public boolean goLogin() {
        if (MyApplication.isLogin == ConditionEnum.NOLOGIN) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
            return false;
        }
        return true;
    }


    /**
     * 初始化下拉刷新控件
     */
    protected void invitionSwipeRefresh(SwipeRefreshLayout mSwipeLayout) {
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(300);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColor(R.color.white); // 设定下拉圆圈的背景
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT); // 设置圆圈的大小
    }

    /**
     * 显示加载进度弹窗,点击屏幕消失
     */
    protected void showProgress(String msg) {
        svProgressHUD.showWithStatus(msg, SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
    }

    /**
     * 显示加载进度弹窗,点击屏幕不可消失
     */
    protected void showNoProgress(String msg) {
        svProgressHUD.showWithStatus(msg, SVProgressHUD.SVProgressHUDMaskType.Black);
    }

    /**
     * 停止弹窗
     */
    protected void stopProgress() {
        if (svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
    }

    /**
     * 分配触摸事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {// 判断动作，如点击，按下等
            View v = getCurrentFocus();// 得到获取焦点的view
            if (isShouldHideInput(v, ev)) {// 点击的位子
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {// 判断view是否为空，
            // view是否为EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);// 获取view的焦点坐标
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();// 计算坐标
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {// 比较坐标
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }


    /**
     * 多种隐藏软件盘方法的其中一种
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    protected void hideSoftInput(View view){
        //隐藏软键盘
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }
}
