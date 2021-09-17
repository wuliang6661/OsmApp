package com.heloo.android.osmapp.ui.main.circle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.HotTopicLayoutBinding;
import com.heloo.android.osmapp.model.HotTopicBean;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Witness on 2020-03-11
 * Describe: 热门话题
 */
public class HotTopicActivity extends BaseActivity implements View.OnClickListener {


    private HotTopicLayoutBinding binding;
    private ImageView headImage;
    private RelativeLayout rlBackback;
    private HotTopicBean hotTopicBean;
    private List<HotTopicBean.ListBean.DataBean> data = new ArrayList<>();
    private CommonAdapter<HotTopicBean.ListBean.DataBean> adapter;

    private int pageNo = 1;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HotTopicLayoutBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        token = MyApplication.spUtils.getString("token", "");
        setContentView(rootView);
        setStatusBar();
        initView();
        getTopicList(pageNo);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 浸入式状态栏实现同时取消5.0以上的阴影
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        //修改字体颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void initView() {
        binding.headLayout.post(() -> binding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        binding.rlBack.setOnClickListener(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(binding.titleLayout.getLayoutParams());
        lp.setMargins(0, ScreenUtils.getStatusHeight(HotTopicActivity.this), 0, 0);
        binding.titleLayout.setLayoutParams(lp);
        View view = LayoutInflater.from(this).inflate(R.layout.hot_topic_head_layout, null);
        headImage = view.findViewById(R.id.headImage);
        rlBackback = view.findViewById(R.id.rlBack);
        ViewGroup.LayoutParams params = headImage.getLayoutParams();
        params.height = (int) (ScreenUtils.getScreenWidth() * 0.49);
        headImage.setLayoutParams(params);
        binding.topicList.addHeaderView(view);
        initListener();
    }

    private void initListener() {
        binding.topicList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                showSearchBarShow();
            }
        });
        binding.refreshRoot.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNo++;
                getTopicList(pageNo);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                getTopicList(pageNo);
            }
        });

        rlBackback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void showSearchBarShow() {
        int headBottomToParentTop = headImage.getHeight() + headImage.getTop();
        if (binding.refreshRoot.isNestedScrollingEnabled()) {//手指在界面滑动的情况
            int height = binding.titleLayout.getHeight();
            if (headBottomToParentTop > height) {
                binding.titleLayout.setVisibility(View.INVISIBLE);
                ScreenUtils.setStatusBarColor(HotTopicActivity.this, Color.parseColor("#00000000"));
            } else {//缓慢滑动，这部分代码工作正常，快速滑动，里面的数据就跟不上节奏了。
                float alpha = (height - headBottomToParentTop) * 1f / height;
                binding.titleLayout.setAlpha(alpha);
                binding.titleLayout.setVisibility(View.VISIBLE);
                ScreenUtils.setStatusBarColor(HotTopicActivity.this, Color.parseColor("#ffffff"));
            }
            if (!headImage.isShown()) {//解决快速滑动，上部分代码不能正常工作的问题。
                binding.titleLayout.setAlpha(1);
                binding.titleLayout.setVisibility(View.VISIBLE);
                ScreenUtils.setStatusBarColor(HotTopicActivity.this, Color.parseColor("#ffffff"));
            }
        } else {//手指离开，listview还在滑动，一般情况是列表快速滑动，这种情况直接设置导航栏的可见性
            if (!headImage.isShown()) {
                if (!binding.titleLayout.isShown()) {
                    binding.titleLayout.setVisibility(View.VISIBLE);
                    ScreenUtils.setStatusBarColor(HotTopicActivity.this, Color.parseColor("#ffffff"));
                    binding.titleLayout.setAlpha(1);
                }
            } else {
                if (binding.titleLayout.isShown()) {
                    binding.titleLayout.setVisibility(View.INVISIBLE);
                    ScreenUtils.setStatusBarColor(HotTopicActivity.this, Color.parseColor("#00000000"));
                }
            }
        }
    }

    private void setAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<HotTopicBean.ListBean.DataBean>(this, R.layout.hot_item_layout, data) {
            @Override
            protected void convert(ViewHolder holder, final HotTopicBean.ListBean.DataBean item, int position) {
                holder.setText(R.id.num, String.valueOf(position + 1));
                holder.setText(R.id.topicName, item.getName());
                holder.getView(R.id.itemBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getIntent().getStringExtra("tag") != null) {//选择话题
                            Intent i = new Intent();
                            i.putExtra("topicId", item.getId());
                            i.putExtra("topicName", item.getName());
                            setResult(666, i);
                            finish();
                        } else {
                            Intent intent = new Intent(HotTopicActivity.this, TopicDetailActivity.class);
                            intent.putExtra("topicId", item.getId());
                            intent.putExtra("topicName", item.getName());
                            intent.putExtra("pic", item.getIcon());
                            intent.putExtra("num", item.getPostNum());
                            intent.putExtra("des", item.getIntroduce());
                            startActivity(intent);
                        }
                    }
                });
            }
        };
        binding.topicList.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlBack:
                finish();
                break;
        }
    }

    /**
     * 获取热门话题
     */
    private void getTopicList(final int pageNo) {
        HttpInterfaceIml.getTopicList(token, pageNo, 20).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                binding.refreshRoot.finishLoadMore();
                binding.refreshRoot.finishRefresh();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(HotTopicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    hotTopicBean = JSON.parseObject(jsonObject.optString("data"), HotTopicBean.class);
                    if (pageNo == 1) {
                        data.clear();
                    }
                    data.addAll(hotTopicBean.getList().getData());
                    setAdapter();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
