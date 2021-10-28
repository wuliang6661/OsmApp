package com.heloo.android.osmapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.AppUtils;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.config.FileConfig;
import com.heloo.android.osmapp.model.VersionBO;
import com.heloo.android.osmapp.ui.main.MainActivity;
import com.heloo.android.osmapp.widget.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2111:14
 * desc   : App检查更新的工具类
 * version: 1.0
 */
public class UpdateUtils {


    private Activity context;

    private boolean mIsCancel = false;
    private String version = "teach.apk";

    public void checkUpdate(Activity context, onUpdateListener listener) {
        this.context = context;
        HttpInterfaceIml.getVersionInfo().subscribe(new HttpResultSubscriber<VersionBO>() {
            @Override
            public void onSuccess(VersionBO s) {
                if (s == null) {
                    if (listener != null) {
                        listener.noUpdate();
                    }
                    return;
                }
                if (s.android.versionCode > AppUtils.getAppVersionCode()) {
                    if (s.android.minVersionCode > AppUtils.getAppVersionCode()) { //强制更新
                        createCustomDialogTwo(s,true);
                    } else {
                        createCustomDialogTwo(s,false);
                    }
                } else {
                    if (listener != null) {
                        listener.noUpdate();
                    }
                }
            }

            @Override
            public void onFiled(String message) {
                // 首页不显示异常弹窗，只有检测更新时弹出
                if (StringUtils.isEmpty(message) || AppManager.getAppManager().curremtActivity()
                        instanceof MainActivity) {
                    return;
                }
                ToastUtils.showShortToast(message);
            }
        });
    }


    public interface onUpdateListener {
        void noUpdate();
    }


    private ProgressDialog progressDialog;

    /*
     * 显示正在下载对话框
     */
    private void showDownloadDialog(String url) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载...");
        progressDialog.setCancelable(false);//不能手动取消下载进度对话框
        progressDialog.setProgressNumberFormat("");
        progressDialog.show();

        // 下载文件
        downloadAPK(url);
    }


    private void createCustomDialogTwo(VersionBO versionBO,boolean isNeed) {
       AlertDialog dialog =  new AlertDialog(context).builder().setGone().setTitle("发现新版本" + versionBO.android.versionName)
                .setMsg(versionBO.android.updateMessage)
                .setPositiveButton("去更新", v1 -> {
                    ToastUtils.showShortToast("开始下载新版本");
                    checkPrission(versionBO.android.url);
                });
       if(isNeed){
           dialog.setCancelable(false);
       }else{
           dialog.setNegativeButton("取消", null);
       }
       dialog.show();
    }


    private void checkPrission(String url) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(context,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 1);
        } else {
            downloadAPK(url);
        }
    }


    /* 开启新线程下载apk文件
     */
    public void downloadAPK(String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mIsCancel = false;
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File dir = new File(FileConfig.getApkFile());
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        // 下载文件
                        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
//                        int length = conn.getContentLength();

                        File apkFile = new File(FileConfig.getApkFile(), version);
                        FileOutputStream fos = new FileOutputStream(apkFile);

                        int count = 0;
                        byte[] buffer = new byte[1024];

                        while (!mIsCancel) {
                            int numread = is.read(buffer);
                            count += numread;
                            Message message = Message.obtain();
                            message.obj = count;
                            handler.sendMessage(message);
                            // 下载完成
                            if (numread < 0) {
                                handler.sendEmptyMessage(0x22);
                                AppUtils.installApp(apkFile);
                                break;
                            }
                            fos.write(buffer, 0, numread);
                        }
                        fos.close();
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(Thread.currentThread().getName(), "2");
        }
    };


}
