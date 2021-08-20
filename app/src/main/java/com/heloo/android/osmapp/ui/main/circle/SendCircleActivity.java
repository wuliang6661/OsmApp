package com.heloo.android.osmapp.ui.main.circle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.SendCircleLayoutBinding;
import com.heloo.android.osmapp.utils.MessageEvent;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.heloo.android.osmapp.widget.LoadingProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Subscriber;

/**
 * Created by Witness on 2020-03-10
 * Describe:  发朋友圈
 */
public class SendCircleActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, BGASortableNinePhotoLayout.Delegate, View.OnClickListener {

    private SendCircleLayoutBinding binding;
    private static final int RC_CHOOSE_PHOTO = 1;
    private static final int RC_PHOTO_PREVIEW = 2;
    private static final int PRC_PHOTO_PICKER = 1;
    private static LoadingProgressDialog proDialog = null;

    private String topicId = "";//话题id
    private String topicName = "";
    private String token;
    private boolean isDefault = false;//是否匿名发布
    private String anonymous = "0";//1  匿名   0  不匿名

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SendCircleLayoutBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        token = MyApplication.spUtils.getString("token", "");
        setStatusBar();
        binding.cancle.setOnClickListener(this);
        binding.sendBtn.setOnClickListener(this);
        binding.chooseTopic.setOnClickListener(this);
        binding.defaultName.setOnClickListener(this);
        binding.picView.setEditable(true);
        binding.picView.setSortable(true);
        // 设置拖拽排序控件的代理
        binding.picView.setDelegate(this);
        binding.edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (StringUtils.isEmpty(topicId)) {
                        ToastUtils.showShortToast("请选择话题！");
                        binding.edittext.clearFocus();
                    } else {
                        binding.edittext.requestFocus();
                    }
                }
            }
        });
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

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        choicePhotoWrapper();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        binding.picView.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .previewPhotos(models) // 当前预览的图片路径集合
                .selectedPhotos(models) // 当前已选中的图片路径集合
                .maxChooseCount(binding.picView.getMaxItemCount()) // 图片选择张数的最大值
                .currentPosition(position) // 当前预览图片的索引
                .isFromTakePhoto(false) // 是否是拍完照后跳转过来
                .build();
        startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW);
    }

    @Override
    public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PRC_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            binding.picView.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data));
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            binding.picView.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        }
        if (requestCode == 666 && data != null) {
            topicId = data.getStringExtra("topicId");
            topicName = data.getStringExtra("topicName");
            binding.chooseTopic.setText(String.format("#%s#", topicName));
        }
    }


    @AfterPermissionGranted(PRC_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(binding.picView.getMaxItemCount() - binding.picView.getItemCount()) // 图片选择张数的最大值
                    .selectedPhotos(null) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", PRC_PHOTO_PICKER, perms);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle:
                finish();
                break;
            case R.id.sendBtn:
                if (StringUtils.isEmpty(topicId)) {
                    ToastUtils.showShortToast("请选择话题！");
                    return;
                }
                if (TextUtils.isEmpty(binding.edittext.getText())) {
                    ToastUtils.showShortToast("请输入想发表的想法");
                    return;
                }
                startProgressDialog("提交评论中...", SendCircleActivity.this);
                sendCircle(topicId, binding.edittext.getText().toString());
                break;
            case R.id.chooseTopic:
                Intent intent = new Intent(SendCircleActivity.this, HotTopicActivity.class);
                intent.putExtra("tag", "selected");
                startActivityForResult(intent, 666);
                break;
            case R.id.defaultName:
                if (!isDefault) {
                    isDefault = true;
                    Glide.with(SendCircleActivity.this).load(R.mipmap.select_true).into(binding.selectImg);
                    anonymous = "1";
                } else {
                    isDefault = false;
                    Glide.with(SendCircleActivity.this).load(R.mipmap.item_notselect).into(binding.selectImg);
                    anonymous = "0";
                }
                break;
        }
    }

    /**
     * 获取指定文件大小
     */
    public long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        return size;
    }


    /**
     * 发送圈子
     */
    private void sendCircle(String topicId, String descr) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);//表单类型
        if (binding.picView.getData() != null && binding.picView.getData().size() > 0) {
            for (int i = 0; i < binding.picView.getData().size(); i++) {
                try {
                    File file = new Compressor(this).compressToFile(new File(binding.picView.getData().get(i)));
                    RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), file);
//                    RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), new File(picView.getData().get(i)));
                    builder.addFormDataPart("pictures", new File(binding.picView.getData().get(i)).getName(), imageBody);//pictures 后台接收图片流的参数名
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        builder.addFormDataPart("topicId", topicId);
        builder.addFormDataPart("descr", descr);
        builder.addFormDataPart("anonymous", anonymous);
        List<MultipartBody.Part> parts = builder.build().parts();

        HttpInterfaceIml.sendCircle(token, parts).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                stopProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                stopProgressDialog();
                Toast.makeText(SendCircleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String code = jsonObject.optString("status");
                    if (code.equals("success")) {
                        ToastUtils.showShortToast("发布成功");
                        EventBus.getDefault().post(new MessageEvent("showNew", "yes"));
                        finish();
                    } else {
                        ToastUtils.showShortToast(jsonObject.optString("errMsg"));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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


    public static void startProgressDialog(String progressMsg, Context mcontext) {
        if (proDialog == null) {
            proDialog = LoadingProgressDialog.createDialog(mcontext);
            proDialog.setMessage(progressMsg);
            proDialog.setCanceledOnTouchOutside(false);
        }
        proDialog.show();
    }

    public static void stopProgressDialog() {
        if (proDialog != null) {
            proDialog.dismiss();
            proDialog = null;
        }
    }
}
