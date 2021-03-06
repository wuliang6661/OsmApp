package com.heloo.android.osmapp.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.heloo.android.osmapp.R;


/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/209:17
 * desc   :
 * version: 1.0
 */
public class PopShare extends PopupWindow {

    private Activity activity;

    private LinearLayout shareFriend;
    private LinearLayout shareComments;
    private LinearLayout qq;
    private LinearLayout qqZone;
    private TextView cancle;

    public PopShare(Activity activity) {
        super(activity);

        this.activity = activity;
        View dialogView = activity.getLayoutInflater().inflate(R.layout.pop_share, null);
        shareFriend = dialogView.findViewById(R.id.share_friend);
        shareComments = dialogView.findViewById(R.id.share_moments);
        qq = dialogView.findViewById(R.id.share_qq);
        qqZone = dialogView.findViewById(R.id.share_zone);

        cancle = dialogView.findViewById(R.id.cancle);

        cancle.setOnClickListener(view -> dismiss());
        shareFriend.setOnClickListener(view -> {
            dismiss();
            if (listener != null) {
                listener.shareFriend();
            }
        });
        shareComments.setOnClickListener(view -> {
            dismiss();
            if (listener != null) {
                listener.shareMenmens();
            }
        });
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (listener != null) {
                    listener.shareQQ();
                }
            }
        });
        qqZone.setOnClickListener(view -> {
            dismiss();
            if (listener != null) {
                listener.shareQQZone();
            }
        });
        this.setBackgroundDrawable(new ColorDrawable(0));
        this.setContentView(dialogView);
        //??????PopupWindow??????????????????
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        //??????PopupWindow??????????????????
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        //??????PopupWindow?????????????????????
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //??????SelectPicPopupWindow????????????????????????
        this.setAnimationStyle(R.style.anim_menu_bottombar);
        //???????????????ColorDrawable??????????????????
        // ColorDrawable dw = new ColorDrawable(0x808080);
        //??????SelectPicPopupWindow?????????????????????
        // this.setBackgroundDrawable(dw);
        this.setOnDismissListener(() -> backgroundAlpha(1f));
    }


    /***
     * ??????????????????????????????
     */
    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        backgroundAlpha(0.5f);
    }


    /**
     * ????????????????????????????????????
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }


    private onCommitListener listener;

    public void setListener(onCommitListener listener) {
        this.listener = listener;
    }

    public interface onCommitListener {

        void shareFriend();

        void shareMenmens();

        void shareQQ();

        void shareQQZone();
    }

}
