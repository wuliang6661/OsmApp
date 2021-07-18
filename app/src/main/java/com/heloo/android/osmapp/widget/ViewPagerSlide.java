package com.heloo.android.osmapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by witness on 2018/9/14.
 *  禁止左右滑动
 */

public class ViewPagerSlide extends ViewPager {
    private boolean enabled = false;

    public ViewPagerSlide(Context context) {
        super(context);
    }

    public ViewPagerSlide(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置其是否能滑动换页
     * @param isCanScroll false 不能换页， true 可以滑动换页
     */
    public void setScanScroll(boolean isCanScroll) {
//        this.isCanScroll = isCanScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 不处理触摸拦截事件
        if (this.enabled) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 触摸事件不触发
        if (this.enabled) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 分发事件，这个是必须要的，如果把这个方法覆盖了，那么ViewPager的子View就接收不到事件了
        if (this.enabled) {
            return super.dispatchTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,false);
    }
}
