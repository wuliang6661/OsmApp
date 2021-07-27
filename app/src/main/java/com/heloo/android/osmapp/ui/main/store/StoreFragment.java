package com.heloo.android.osmapp.ui.main.store;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.adapter.BaseRecyclerHolder;
import com.heloo.android.osmapp.adapter.BaseRecyclerViewAdater;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.databinding.FragmentStoreBinding;
import com.heloo.android.osmapp.model.LinkBean;
import com.heloo.android.osmapp.model.ShopBannarBO;
import com.heloo.android.osmapp.model.ShopListBO;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.StoreContract;
import com.heloo.android.osmapp.mvp.presenter.StorePresenter;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.ui.cart.CartActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.stx.xhb.androidx.XBanner;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 商城
 */
public class StoreFragment extends MVPBaseFragment<StoreContract.View, StorePresenter, FragmentStoreBinding>
        implements StoreContract.View {

    private List<String> bannerData = new ArrayList<>();
    private LinkBean linkBean;
    private LAdapter lAdapter;
    private RAdapter rAdapter;
    private boolean moveToTop = false;
    private int index;
    private PathMeasure mPathMeasure;
    private ShopBannarBO bannarBO;
    private ShopListBO shopListBO;

    /**
     * 贝塞尔曲线中间过程的点的坐标
     */
    private float[] mCurrentPosition = new float[2];
    private int i = 0;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        mPresenter.getBanner();
        mPresenter.getClassify();
        showProgress("");
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.getClassify();
            mPresenter.getBanner();
        }
    }

    private void initViews() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(getActivity()), 0, 0));
        ViewGroup.LayoutParams paramsBanner1 = viewBinding.banner.getLayoutParams();
        paramsBanner1.height = (int) (ScreenUtils.getScreenWidth() * 0.53);
        viewBinding.banner.setLayoutParams(paramsBanner1);
        //初始化banner
        initBanner(viewBinding.banner);
        viewBinding.banner.setAutoPlayAble(bannerData.size() > 1);
        viewBinding.banner.setPointsIsVisible(true);
        viewBinding.banner.setData(R.layout.home_banner_layout, bannerData, null);
        viewBinding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset >= 0) {
                viewBinding.refreshLayout.setEnabled(true);
            } else {
                viewBinding.refreshLayout.setEnabled(false);
            }
        });
    }


    private void initData() {
        linkBean = new LinkBean();
        linkBean.itemLS = new ArrayList<>();
        linkBean.itemS = new ArrayList<>();
        for (int i = 0; i < shopListBO.list.size(); i++) {
            LinkBean.ItemL itemL = new LinkBean.ItemL();
            if (!linkBean.itemLS.contains(itemL)) {
                itemL.setTitle(shopListBO.list.get(i).name);
                linkBean.itemLS.add(itemL);
            }
            for (int j = 0; j < shopListBO.list.get(0).typeGoodsMoels.size(); j++) {
                LinkBean.Item item = new LinkBean.Item();
                item.setName(shopListBO.list.get(0).typeGoodsMoels.get(i).goodsMoels.get(j).name);
                item.setChildList(shopListBO.list.get(0).typeGoodsMoels.get(j).goodsMoels);
                linkBean.itemS.add(item);
            }
        }
        viewBinding.tvHeader.setText(linkBean.itemS.get(0).getName());
        viewBinding.rvLeft.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewBinding.rvRight.setLayoutManager(new LinearLayoutManager(getActivity()));
        lAdapter = new LAdapter(getActivity(), R.layout.item, linkBean.itemLS);
        lAdapter.bindToRecyclerView(viewBinding.rvLeft);
        viewBinding.rvLeft.setAdapter(lAdapter);
        rAdapter = new RAdapter(getActivity(), R.layout.item_goods, linkBean.itemS);
        viewBinding.rvRight.setAdapter(rAdapter);
    }

    private void initListener() {
        lAdapter.setOnItemClickListener(new BaseRecyclerViewAdater.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (viewBinding.rvRight.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) return;
                lAdapter.fromClick = true;
                lAdapter.setChecked(position);
                String tag = lAdapter.getmData().get(position).getTitle();
                for (int i = 0; i < rAdapter.getmData().size(); i++) {
                    //根据左边选中的条目获取到右面此条目Title相同的位置索引；
                    if (TextUtils.equals(tag, rAdapter.getmData().get(i).getOne_id())) {
                        index = i;
                        moveToPosition_R(index);
                        return;
                    }
                }
            }
        });

        viewBinding.rvRight.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) viewBinding.rvRight.getLayoutManager();
                if (moveToTop) { //向下滑动时，只会把改条目显示出来；我们还需要让该条目滑动到顶部；
                    moveToTop = false;
                    int m = index - layoutManager.findFirstVisibleItemPosition();
                    if (m >= 0 && m <= layoutManager.getChildCount()) {
                        int top = layoutManager.getChildAt(m).getTop();
                        viewBinding.rvRight.smoothScrollBy(0, top);
                    }
                } else {
                    int index = layoutManager.findFirstVisibleItemPosition();
                    viewBinding.tvHeader.setText(rAdapter.getmData().get(index).getName());
                    lAdapter.setToPosition(rAdapter.getmData().get(index).getOne_id());
                }
            }
        });

        viewBinding.rvRight.setOnTouchListener((view, motionEvent) -> {
            lAdapter.fromClick = false;
            return false;
        });

        viewBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getClassify();
                mPresenter.getBanner();
                refreshLayout.finishRefresh(1000);
            }
        });
        viewBinding.cartBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), CartActivity.class)));
    }


    private void moveToPosition_R(int index) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) viewBinding.rvRight.getLayoutManager();
        int f = layoutManager.findFirstVisibleItemPosition();
        int l = layoutManager.findLastVisibleItemPosition();
        if (index <= f) { //向上移动时
            layoutManager.scrollToPosition(index);
        } else if (index <= l) { //已经再屏幕上面显示时
            int m = index - f;
            if (0 <= m && m <= layoutManager.getChildCount()) {
                int top = layoutManager.getChildAt(m).getTop();
                viewBinding.rvRight.smoothScrollBy(0, top);
            }
        } else { //向下移动时
            moveToTop = true;
            layoutManager.scrollToPosition(index);
        }
    }

    /**
     * 轮播图
     *
     * @param banner
     */
    private void initBanner(XBanner banner) {
        //设置广告图片点击事件
        banner.setOnItemClickListener((banner12, model, view, position) -> {
            if (bannarBO == null) {
                return;
            }
            Intent intent = new Intent(getContext(), WebViewActivity.class);
            intent.putExtra("url", bannarBO.banner.get(position).jumpUrl);
            startActivity(intent);
        });
        //加载广告图片
        banner.loadImage((banner1, model, view, position) -> {
            ImageView image = view.findViewById(R.id.image);
            Glide.with(getActivity()).load(bannerData.get(position)).into(image);
        });

    }

    @Override
    public void onRequestError(String msg) {
        stopProgress();
    }

    @Override
    public void onRequestEnd() {
    }

    @Override
    public void getClassify(ShopListBO body) {
        stopProgress();
        shopListBO = body;
        initData();
        initListener();
    }

    @Override
    public void getBanner(ShopBannarBO body) {
        stopProgress();
        bannarBO = body;
        if (body == null) {
            return;
        }
        for (ShopBannarBO.BannerBean item : body.banner) {
            bannerData.add(item.imgurl);
        }
        viewBinding.banner.setData(R.layout.home_banner_layout, bannerData, null);
    }


    class LAdapter extends BaseRecyclerViewAdater<LinkBean.ItemL> {


        public LAdapter(Context context, int resLayout, List<LinkBean.ItemL> data) {
            super(context, resLayout, data);
        }

        @Override
        public void convert(BaseRecyclerHolder holder, final int position) {
            TextView tv = (holder.getView(R.id.tv));
            tv.setText(getmData().get(position).getTitle());
            if (checked == position) {
                tv.setTextColor(Color.parseColor("#D3A952"));
                tv.setBackgroundResource(R.color.colorfff);
                tv.getPaint().setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            } else {
                tv.setTextColor(Color.parseColor("#333333"));
                tv.setBackgroundResource(R.color.colorF8);
                tv.getPaint().setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            }

        }

        private int checked; //当前选中项
        public boolean fromClick; //是否是自己点击的

        public void setChecked(int checked) {
            this.checked = checked;
            notifyDataSetChanged();
        }

        //让左边的额条目选中
        public void setToPosition(String title) {
            if (fromClick) return;
            if (TextUtils.equals(title, getmData().get(checked).getOne_id())) return;
            if (TextUtils.isEmpty(title)) return;
            for (int i = 0; i < getmData().size(); i++) {
                if (TextUtils.equals(getmData().get(i).getOne_id(), title)) {
                    setChecked(i);
                    moveToPosition(i);
                    return;
                }
            }

        }

        private void moveToPosition(int index) {
            //如果选中的条目不在显示范围内，要滑动条目让该条目显示出来
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getRecyclerView().getLayoutManager();
            int f = linearLayoutManager.findFirstVisibleItemPosition();
            int l = linearLayoutManager.findLastVisibleItemPosition();
            if (index <= f || index >= l) {
                linearLayoutManager.scrollToPosition(index);
            }

        }

    }

    class RAdapter extends BaseRecyclerViewAdater<LinkBean.Item> {


        public RAdapter(Context context, int resLayout, List<LinkBean.Item> data) {
            super(context, resLayout, data);
        }

        @Override
        public void convert(BaseRecyclerHolder holder, final int position) {
            RecyclerView productLayout = holder.getView(R.id.productLayout);
            //悬停的标题头
            FrameLayout headLayout = holder.getView(R.id.stick_header);
            TextView tvHead = holder.getView(R.id.tvHeader);
            if (position == 0) {
                headLayout.setVisibility(View.VISIBLE);
                tvHead.setText(getmData().get(position).getName());
            } else {
                if (TextUtils.equals(getmData().get(position).getName(), getmData().get(position - 1).getName())) {
                    headLayout.setVisibility(View.GONE);
                } else {
                    headLayout.setVisibility(View.VISIBLE);
                    tvHead.setText(getmData().get(position).getName());
                }
            }
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            productLayout.setLayoutManager(manager);
            CommonAdapter<ShopListBO.ListBean.TypeGoodsMoelsBean.GoodsMoelsBean> adapter
                    = new CommonAdapter<ShopListBO.ListBean.TypeGoodsMoelsBean.GoodsMoelsBean>(getActivity(),
                    R.layout.product_item_layout, getmData().get(position).getChildList()) {
                @Override
                protected void convert(ViewHolder holder, ShopListBO.ListBean.TypeGoodsMoelsBean.GoodsMoelsBean items, int position) {
                    ((TextView) holder.getView(R.id.tvPrice)).setText(items.integralPrice + "");
                    ((TextView) holder.getView(R.id.tvName)).setText(items.name);
                    ImageView productImg = holder.getView(R.id.productImg);
                    holder.setText(R.id.tvPrice, String.format("￥%s", items.integralPrice));
                    holder.getView(R.id.coinLayout).setVisibility(View.GONE);
                    if (items.icon.startsWith("http")) {
                        Glide.with(getActivity()).load(items.icon).into(productImg);
                    } else {
                        Glide.with(getActivity()).load(HttpInterface.URL + items.icon).into(productImg);
                    }
                    holder.itemView.setOnClickListener(view -> {
//                        addCart(productImg);
                        Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
                        intent.putExtra("id", items.id);
                        startActivity(intent);
                    });
                }
            };
            productLayout.setAdapter(adapter);
        }
    }

    /**
     * ★★★★★把商品添加到购物车的动画效果★★★★★
     *
     * @param iv
     * @auther WITNESS
     */
    private void addCart(ImageView iv) {
        //一、创造出执行动画的主题---imageview
        //代码new一个ShapeableImageView，图片资源是上面的imageview的图片
        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线），移动到购物车里)
        final ShapeableImageView goods = new ShapeableImageView(getActivity());
        goods.setShapeAppearanceModel(new ShapeAppearanceModel().withCornerSize(50f));
        goods.setImageDrawable(iv.getDrawable());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        viewBinding.parentLayout.addView(goods, params);

        //二、计算动画开始/结束点的坐标的准备工作
        //得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        int[] parentLocation = new int[2];
        viewBinding.parentLayout.getLocationInWindow(parentLocation);

        //得到商品图片的坐标（用于计算动画开始的坐标）
        int startLoc[] = new int[2];
        iv.getLocationInWindow(startLoc);

        //得到购物车图片的坐标(用于计算动画结束后的坐标)
        int endLoc[] = new int[2];
        viewBinding.cartBtn.getLocationInWindow(endLoc);


        //三、正式开始计算动画开始/结束的坐标
        //开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
        float startX = startLoc[0] - parentLocation[0] + iv.getWidth() / 2;
        float startY = startLoc[1] - parentLocation[1] + iv.getHeight() / 2;

        //商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        float toX = endLoc[0] - parentLocation[0] + viewBinding.cartBtn.getWidth() / 5;
        float toY = endLoc[1] - parentLocation[1];

        //四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
        //开始绘制贝塞尔曲线
        Path path = new Path();
        //移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY);
        //使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
        // 如果是true，path会形成一个闭环
        mPathMeasure = new PathMeasure(path, false);

        //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(800);
        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值，
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // ★★★★★获取当前点坐标封装到mCurrentPosition
                // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                mPathMeasure.getPosTan(value, mCurrentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                goods.setTranslationX(mCurrentPosition[0]);
                goods.setTranslationY(mCurrentPosition[1]);
            }
        });
        //五、 开始执行动画
        valueAnimator.start();

        //六、动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            //当动画结束后：
            @Override
            public void onAnimationEnd(Animator animation) {
                // 购物车的数量加1
                i++;
                viewBinding.cartNum.setVisibility(View.VISIBLE);
                viewBinding.cartNum.setText(String.valueOf(i));
                // 把移动的图片imageview从父布局里移除
                viewBinding.parentLayout.removeView(goods);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}