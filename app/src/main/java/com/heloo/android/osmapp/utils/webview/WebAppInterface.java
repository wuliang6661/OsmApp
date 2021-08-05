package com.heloo.android.osmapp.utils.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSON;
import com.bigkoo.alertview.AlertView;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.model.ShareVo;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.utils.LogUtils;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.heloo.android.osmapp.widget.AlertDialog;
import com.tencent.smtt.sdk.WebView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/**
 * Created by wuliang on 2017/4/11.
 * <p>
 * 此处存放所有Html页面js调用的方法
 */
public class WebAppInterface implements UMShareListener {

    private Activity mContext;
    private WebView webView;
    private String articleId;

    public WebAppInterface(Activity context, WebView webView) {
        mContext = context;
        this.webView = webView;
    }

    /**
     * 此方法为例
     */
    @JavascriptInterface
    public void showToast(String message) {
        LogUtils.showToast(message);
    }

    /**
     * 打开一个新的web页面
     */
    @JavascriptInterface
    public void goNewWeb(String url) {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("url", url);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void logDebug(String logo) {
        LogUtils.I(logo);
    }

    /**
     * 约人看电影
     */
    @JavascriptInterface
    public void dating(String url) {
        LogUtils.showToast("敬请期待！");
    }

    /**
     * 保存海报
     */
    @JavascriptInterface
    public void saveImg(String base64) {
        LogUtils.d("wwwww",base64);
        new AlertDialog(mContext).builder().setGone().setMsg("确认保存海报？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", v -> {
                    saveImgToGallery(mContext, base64);
                }).show();
    }


    @SuppressLint("CheckResult")
    public void saveImgToGallery(final Context context, final String imgUrl) {
        String[] PERMISSIONS = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"};
        //检测是否有写的权限
        int permission = ContextCompat.checkSelfPermission(mContext,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(mContext, PERMISSIONS, 1);
            return;
        }
        saveImage(imgUrl);
    }


    private void saveImage(String base64) {
        if (TextUtils.isEmpty(base64)) //图像数据为空
            return;
        OutputStream fos = null;
        try {
            byte[] b = Base64.decode(base64, Base64.DEFAULT);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            String path = Environment.getExternalStorageDirectory()
                    + File.separator + Environment.DIRECTORY_DCIM
                    + File.separator + "Camera" + File.separator;
            File currentFile = new File(path, System.currentTimeMillis() + ".jpg");
            fos = new FileOutputStream(currentFile);
            fos.write(b);
            fos.flush();
            fos.close();
            // 最后通知图库更新
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(currentFile)));
            ToastUtils.showShortToast("保存图片成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            ToastUtils.showShortToast("保存失败");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 用户未登陆，去登陆
     */
    @JavascriptInterface
    public void goLogin(String str) {
        Message message = new Message();
        message.obj = str;
        message.what = 0x22;
        handler.sendMessage(message);
    }


    int i = 0;

    /**
     * 提示弹窗
     */
    @JavascriptInterface
    public void showAlert(String str) {
        i++;
        LogUtils.I("提示弹窗！" + i + "次");
        Message message = new Message();
        message.obj = str;
        message.what = 0x33;
        handler.sendMessage(message);
    }


    /**
     * 关闭当前页面
     */
    @JavascriptInterface
    public void finishWeb() {
        mContext.finish();
    }


    /**
     * 分享
     */
    @JavascriptInterface
    public void jsShare(String data) {
        if (data != null) {
            ShareVo shareVo = JSON.parseObject(data, ShareVo.class);
            if (shareVo != null) {
                SHARE_MEDIA platform;
                switch (shareVo.getType()) {
                    case "wechatSession":
                        platform = SHARE_MEDIA.WEIXIN;
                        break;
                    case "wechatTimeline":
                        platform = SHARE_MEDIA.WEIXIN_CIRCLE;
                        break;
                    case "qqSession":
                        platform = SHARE_MEDIA.QQ;
                        break;
                    case "qqQzone":
                        platform = SHARE_MEDIA.QZONE;
                        break;
                    case "weibo":
                        platform = SHARE_MEDIA.SINA;
                        break;
                    default:
                        platform = SHARE_MEDIA.WEIXIN;
                        break;
                }
                ShareAction shareAction = new ShareAction(mContext).setPlatform(platform);
                if (shareVo.getUrl() != null) {
                    String shareUrl;
                    if (LocalConfiguration.userInfo != null) {
                        shareUrl = shareVo.getUrl() + "&uid=" + LocalConfiguration.userInfo.getUid();
                    } else {
                        shareUrl = shareVo.getUrl();
                    }
                    UMWeb web = new UMWeb(shareUrl);
                    web.setTitle(shareVo.getTitle());//标题
                    web.setDescription(shareVo.getContent());//描述
//                    web.setmExtra(SocialConstants.PARAM_TARGET_URL, shareUrl);
                    if (shareVo.getImage() != null) {
                        UMImage umImage = new UMImage(mContext, shareVo.getImage());
                        umImage.setTitle(shareVo.getTitle());
                        umImage.setDescription(shareVo.getContent());
                        if (shareVo.getType().equals("weibo")) {
                            shareAction.withMedia(umImage);
                        } else {
                            web.setThumb(umImage);
                        }
                    }
                    if (!shareVo.getType().equals("weibo")) {
                        shareAction.withMedia(web);
                    }
                }

                if (shareVo.getType().equals("weibo")) {
                    shareAction.withSubject(shareVo.getTitle()).withText(shareVo.getContent() + shareVo.getUrl()).setCallback((UMShareListener) mContext).share();
                }
                shareAction.withSubject(shareVo.getTitle()).withText(shareVo.getContent()).
                        setCallback(this).share();
                articleId = shareVo.getArticleId();
                if (!StringUtils.isEmpty(articleId)) {
                    articleforward(articleId);
                }
            }
        }
    }


    private void articleforward(String articleId) {
        HttpInterfaceIml.articleforward(articleId, LocalConfiguration.userInfo == null ? null :
                LocalConfiguration.userInfo.getUsername()).subscribe(new HttpResultSubscriber<Object>() {

            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onFiled(String message) {
                ToastUtils.showShortToast(message);
            }
        });
    }


    private AlertView alertView;

    /**
     * 处理界面变化
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:    //分享
//                    ShareBO shareBO = (ShareBO) msg.obj;
//                    new ShareDialog(mContext, shareBO).showAtLocation(webView, Gravity.BOTTOM, 0, 0);
                    break;
                case 0x22:  //去登陆
                    String message = (String) msg.obj;
//                    new AlertView("提示", message, null, new String[]{"确定"}, null, mContext, AlertView.Style.Alert, new OnItemClickListener() {
//                        @Override
//                        public void onItemClick(Object o, int position) {
//                            Intent intent = new Intent(mContext, LoginActivity.class);
//                            mContext.startActivityForResult(intent, 1);
//                        }
//                    }).show();
                    break;
                case 0x33:  //提示弹窗显示
                    String str01 = (String) msg.obj;
                    if (alertView != null && alertView.isShowing()) {
                        return;
                    }
                    alertView = new AlertView("提示", str01, null, null, null, mContext, AlertView.Style.Alert, null);
                    alertView.setCancelable(true);
                    alertView.show();
                    break;
            }
        }
    };


    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {

    }
}
