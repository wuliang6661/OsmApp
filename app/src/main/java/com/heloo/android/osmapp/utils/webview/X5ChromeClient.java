package com.heloo.android.osmapp.utils.webview;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;

import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import android.widget.FrameLayout;


import com.heloo.android.osmapp.widget.FullscreenHolder;
import com.sina.weibo.sdk.utils.LogUtil;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.RequiresApi;


/**
 * Created by wuliang on 2017/4/11.
 * <p>
 * 如果说WebViewClient是帮助WebView处理各种通知、请求事件的“内政大臣”的话，
 * 那么WebChromeClient就是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等偏外部事件的“外交大臣”。
 */

public class X5ChromeClient extends WebChromeClient {


    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadCallbackAboveL;
    public final static int REQUEST_CODE = 1234;
    public final static int VIDEO_REQUEST = 120;
    public final static int AUDIO_CODE = 1111;
    private Activity activity;
    private Uri imageUri;
    private FrameLayout mFrameLayout;
    private WebView mWebView;
    public onReceivedMessage listener;


    public X5ChromeClient(Activity context) {
        activity = context;
    }

    public void setOnReceivedListener(onReceivedMessage listener) {
        this.listener = listener;
    }

    /**
     * 获取页面数据接口
     */
    public interface onReceivedMessage {

        void getTitle(String title);
    }



    public void setData(FrameLayout frameLayout, WebView webView) {
        this.mFrameLayout = frameLayout;
        this.mWebView = webView;
    }


    /**
     * 当页面加载的进度发生改变时回调，用来告知主程序当前页面的加载进度。
     *
     * @param view
     * @param newProgress
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }


    /**
     * 用来接收web页面的icon，我们可以在这里将该页面的icon设置到Toolbar。
     *
     * @param view
     * @param icon
     */
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    /**
     * 用来接收web页面的title，我们可以在这里将页面的title设置到Toolbar。
     *
     * @param view
     * @param title
     */
    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (listener != null) {
            listener.getTitle(title);
        }
        super.onReceivedTitle(view, title);
    }

    //以下两个方法是为了支持web页面进入全屏模式而存在的（比如播放视频），
    //如果不实现这两个方法，该web上的内容便不能进入全屏模式。


    /**
     * 该方法在当前页面进入全屏模式时回调，主程序必须提供一个包含当前web内容（视频 or Something）的自定义的View。
     *
     * @param view
     * @param callback
     */

    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    public View customView;
    private FrameLayout fullscreenContainer;
    private IX5WebChromeClient.CustomViewCallback customViewCallback;


    /**
     * 视频播放全屏
     **/
    @Override
    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        activity.getWindow().getDecorView();
        FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(activity);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }


    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 隐藏视频全屏
     */
    @Override
    public void onHideCustomView() {
        if (customView == null) {
            return;
        }
        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        mWebView.setVisibility(View.VISIBLE);
    }

    /**
     * 当我们的Web页面包含视频时，我们可以在HTML里为它设置一个预览图，WebView会在绘制页面时根据它的宽高为它布局。
     * 而当我们处于弱网状态下时，我们没有比较快的获取该图片，
     * 那WebView绘制页面时的gitWidth()方法就会报出空指针异常~ 于是app就crash了。。
     * 这时我们就需要重写该方法，在我们尚未获取web页面上的video预览图时，给予它一个本地的图片，避免空指针的发生。
     *
     * @return
     */
    @Override
    public Bitmap getDefaultVideoPoster() {
        return super.getDefaultVideoPoster();
    }

    /**
     * 重写该方法可以在视频loading时给予一个自定义的View，可以是加载圆环 or something。
     *
     * @return
     */
    @Override
    public View getVideoLoadingProgressView() {
        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return frameLayout;
    }

//    @Override
//    public void onGeolocationPermissionsShowPrompt(String s, GeolocationPermissionsCallback geolocationPermissionsCallback) {
//        geolocationPermissionsCallback.invoke(origin, true, false);
//        super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
//    }

    /**
     * 处理Javascript中的Alert对话框。
     *
     * @return
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }

    /**
     * 处理Javascript中的Prompt对话框。
     *
     * @return
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    /**
     * 处理Javascript中的Confirm对话框
     *
     * @return
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return super.onJsConfirm(view, url, message, result);
    }

    /**
     * 该方法在用户进行了web上某个需要上传文件的操作时回调。我们应该在这里打开一个文件选择器，
     * 如果要取消这个请求我们可以调用filePathCallback.onReceiveValue(null)并返回true。
     *
     * @param webView
     * @param filePathCallback
     * @param fileChooserParams
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        mUploadCallbackAboveL = filePathCallback;
        String[] accept = fileChooserParams.getAcceptTypes();
        if (accept != null && accept.length > 0) {
            LogUtil.e("wuliang", accept[0]);
            switch (accept[0]) {
                case "audio/*":
                    audioStart();
                    break;
                case "video/*":
                    recordVideo();
                    break;
                default:
                    takePhoto();
                    break;
            }
        } else {
            takePhoto();
        }
        return true;

    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        takePhoto();
    }


    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        openFileChooser(uploadMsg);
    }


    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg);
    }


    /**
     * 录像
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        //限制时长
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        //开启摄像机
        activity.startActivityForResult(intent, VIDEO_REQUEST);
    }


    /**
     * 启动录音
     */
    private void audioStart() {
        Intent intentRecord = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intentRecord.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intentRecord.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        activity.startActivityForResult(intentRecord, VIDEO_REQUEST);
    }


    /**
     * 调用相机
     */
    private void takePhoto() {
        // 指定拍照存储位置的方式调起相机
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator;
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        imageUri = Uri.fromFile(new File(filePath + fileName));
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Intent Photo = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooserIntent = Intent.createChooser(Photo, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
        activity.startActivityForResult(chooserIntent, REQUEST_CODE);

    }

    /**
     * Android API < 21(Android 5.0)版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    public void chooseBelow(int resultCode, Intent data) {
        Log.e("WangJ", "返回调用方法--chooseBelow");

        if (Activity.RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对文件路径处理
                Uri uri = data.getData();
                if (uri != null) {
                    Log.e("WangJ", "系统返回URI：" + uri.toString());
                    mUploadMessage.onReceiveValue(uri);
                } else {
                    mUploadMessage.onReceiveValue(null);
                }
            } else {
                // 以指定图像存储路径的方式调起相机，成功后返回data为空
                Log.e("WangJ", "自定义结果：" + imageUri.toString());
                mUploadMessage.onReceiveValue(imageUri);
            }
        } else {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = null;
    }

    /**
     * Android API >= 21(Android 5.0) 版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    public void chooseAbove(int resultCode, Intent data) {
        Log.e("WangJ", "返回调用方法--chooseAbove");

        if (Activity.RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对从文件中选图片的处理
                Uri[] results;
                Uri uriData = data.getData();
                if (uriData != null) {
                    results = new Uri[]{uriData};
                    for (Uri uri : results) {
                        Log.e("WangJ", "系统返回URI：" + uri.toString());
                    }
                    mUploadCallbackAboveL.onReceiveValue(results);
                } else {
                    mUploadCallbackAboveL.onReceiveValue(null);
                }
            } else {
                Log.e("WangJ", "自定义结果：" + imageUri.toString());
                mUploadCallbackAboveL.onReceiveValue(new Uri[]{imageUri});
            }
        } else {
            mUploadCallbackAboveL.onReceiveValue(null);
        }
        mUploadCallbackAboveL = null;
    }

    private void updatePhotos() {
        // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        activity.sendBroadcast(intent);
    }
}


