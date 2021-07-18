package com.heloo.android.osmapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.heloo.android.osmapp.widget.NineGridView;

import java.util.List;

/**
 * @author KCrason
 * @date 2018/4/27
 */
public class NineImageAdapter implements NineGridView.NineGridAdapter<String> {

    private List<String> mImageBeans;

    private Context mContext;


    public NineImageAdapter(Context context, List<String> imageBeans) {
        this.mContext = context;
        this.mImageBeans = imageBeans;
    }

    @Override
    public int getCount() {
        return mImageBeans == null ? 0 : mImageBeans.size();
    }

    @Override
    public String getItem(int position) {
        return mImageBeans == null ? null :
                position < mImageBeans.size() ? mImageBeans.get(position) : null;
    }

    @Override
    public View getView(int position, View itemView) {
        ShapeableImageView imageView;
        if (itemView == null) {
            imageView = new ShapeableImageView(mContext);
            imageView.setShapeAppearanceModel(new ShapeAppearanceModel().withCornerSize(10f));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.base_F2F2F2));
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        } else {
            imageView = (ShapeableImageView) itemView;
        }
        String url = mImageBeans.get(position);
        int itemSize = (ScreenUtils.getScreenWidth() - 2 * ScreenUtils.dp2px(4) - ScreenUtils.dp2px(54)) / 3;
        Glide.with(mContext).load(url).override(itemSize,itemSize).into(imageView);


        return imageView;
    }
}
