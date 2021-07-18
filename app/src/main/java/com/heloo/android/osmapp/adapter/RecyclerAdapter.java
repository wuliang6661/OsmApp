package com.heloo.android.osmapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.model.RouteDataBO;
import com.heloo.android.osmapp.ui.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by witness on 2018/4/26.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * context
     */
    public Context mContext;

    /**
     * 集合
     */
    public List<RouteDataBO> mDatas = new ArrayList<>();
    /**
     * data
     */
    public RouteDataBO mData;

    public RecyclerAdapter(Context mContext, List<RouteDataBO> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    static class ViewHolde extends RecyclerView.ViewHolder {

        TextView title;
        LinearLayout btn;
        TextView time;
        TextView liveStatus;
        ShapeableImageView liveImage;
        TextView notice;

        public ViewHolde(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            btn = itemView.findViewById(R.id.btn);
            time = itemView.findViewById(R.id.time);
            liveStatus = itemView.findViewById(R.id.liveStatus);
            liveImage = itemView.findViewById(R.id.liveImage);
            notice = itemView.findViewById(R.id.notice);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 1){//标题
            View item = LayoutInflater.from(mContext).inflate(R.layout.live_title_layout , parent ,false);
            item.setTag(true);
            return new ViewHolde(item);
        }else{
            View item = LayoutInflater.from(mContext).inflate(R.layout.live_item_layout , parent ,false);
            item.setTag(false);
            return new ViewHolde(item);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  ViewHolde){
            if (mDatas.size() > position) {
                mData = mDatas.get(position);
                ((ViewHolde) holder).title.setText(mData.getName());
                if (mData.getType() == 1) {
                    ((ViewHolde) holder).title.setTextColor(Color.parseColor("#333333"));
                    if (mDatas.get(position+1) != null && mDatas.get(position+1).getId().equals("")){
                        ((ViewHolde) holder).notice.setVisibility(View.VISIBLE);
                    }else {
                        ((ViewHolde) holder).notice.setVisibility(View.GONE);
                    }
                }else {
                    ((ViewHolde) holder).time.setText(String.format("直播时间:%s",mData.getTime()));
                    ((ViewHolde) holder).title.setText(mData.getName());
                    Glide.with(mContext).asBitmap().load(mData.getPic()).into(((ViewHolde) holder).liveImage);
                    switch (mData.getLiveType()){
                        case 1:
                            ((ViewHolde) holder).liveStatus.setText("正在直播");
                            ((ViewHolde) holder).liveStatus.setTextColor(Color.parseColor("#CB9D3E"));
                            ((ViewHolde) holder).liveStatus.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.getResources(),R.mipmap.live_pic_icon,null),
                                    null, ResourcesCompat.getDrawable(mContext.getResources(),R.mipmap.yellow_right_icon,null),null);
                            break;
                        case 2:
                            ((ViewHolde) holder).liveStatus.setText("暂未开始");
                            ((ViewHolde) holder).liveStatus.setTextColor(Color.parseColor("#B6B6B6"));
                            ((ViewHolde) holder).liveStatus.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.getResources(),R.mipmap.clock,null),
                                    null, null,null);
                            break;
                        case 3:
                            ((ViewHolde) holder).liveStatus.setText("直播结束");
                            ((ViewHolde) holder).liveStatus.setTextColor(Color.parseColor("#B6B6B6"));
                            ((ViewHolde) holder).liveStatus.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(mContext.getResources(),R.mipmap.clock,null),
                                    null, null,null);
                            break;
                    }
                    ((ViewHolde) holder).btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, WebViewActivity.class);
                            if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                                intent.putExtra("url", HttpInterface.URL + LocalConfiguration.liveUrl + "?liveid=" + mDatas.get(position).getId()
                                        + "&uid=" + LocalConfiguration.userInfo.getUid()+ "&username=" + LocalConfiguration.userInfo.getUsername()+"&app=1");
                            }else {
                                intent.putExtra("url", HttpInterface.URL + LocalConfiguration.liveUrl + "?liveid=" + mDatas.get(position).getId()+"&app=1");
                            }
                            mContext.startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1){
            return 0;
        }else{
            return mDatas.get(position).getType();
        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
