package com.heloo.android.osmapp.ui.person;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityPersonBinding;
import com.heloo.android.osmapp.model.UserInfo;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.PersonContract;
import com.heloo.android.osmapp.mvp.presenter.PersonPresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.Glide4Engine;
import com.heloo.android.osmapp.utils.HttpImgUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Description : 个人信息
 *
 * @author WITNESS
 * @date 4/23/21
 */
public class PersonActivity extends MVPBaseActivity<PersonContract.View, PersonPresenter, ActivityPersonBinding>
    implements PersonContract.View, View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final int PRC_PHOTO_PICKER = 1;
    private List<Uri> mSelected;
    private UserInfo userInfo;
    private int gender = 1;//1 男  2 女

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = LocalConfiguration.userInfo;
        initView();
    }

    private void initView() {
        goBack();
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.headerImg.setOnClickListener(this);
        Glide.with(this).asBitmap().load(HttpImgUtils.getImgUrl(userInfo.getIcon()))
                .placeholder(R.drawable.default_head).error(R.drawable.default_head).into(viewBinding.headerImg);
        if (userInfo.getNickname() != null && !userInfo.getNickname().equals("")){
            viewBinding.name.setText(userInfo.getNickname());
        }else {
            viewBinding.name.setText(userInfo.getUsername());
        }
        viewBinding.phone.setText(userInfo.getPhone());
        if (userInfo.getGender() != null && userInfo.getGender().equals("1")){
            gender = 1;
            viewBinding.genderTxt.setText("男");
        }else {
            gender = 2;
            viewBinding.genderTxt.setText("女");
        }
        viewBinding.changeSex.setOnClickListener(this);
        viewBinding.submitBtn.setOnClickListener(this);
        viewBinding.name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoft();
                }
                return false;
            }
        });
    }

    /**
     * 关闭键盘
     */
    private void hideSoft(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(
                InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headerImg:
                choicePhotoWrapper();
                break;
            case R.id.changeSex:
                changeGender();
                break;
            case R.id.submitBtn:
                showProgress("");
                changeInfo();
                break;
        }
    }


    @Override
    public void onRequestError(String msg) {
        stopProgress();
    }

    @Override
    public void onRequestEnd() {
        stopProgress();
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

    @AfterPermissionGranted(PRC_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Matisse.from(PersonActivity.this)
                    .choose(MimeType.ofImage())
                    .countable(true)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .theme(R.style.Matisse_Zhihu)
                    .imageEngine(new Glide4Engine())
                    .forResult(666);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", PRC_PHOTO_PICKER, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            if (mSelected != null && mSelected.size() > 0) {
                Glide.with(PersonActivity.this)
                        .asBitmap()
                        .load(mSelected.get(0))
                        .into(viewBinding.headerImg);
            }
        }
    }


    /**
     * 修改信息
     */
    private void changeInfo(){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);//表单类型
        if (mSelected != null && mSelected.size()>0) {
            try {
                File file = new Compressor(this).compressToFile(uriToFile(mSelected.get(0)));
                RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                builder.addFormDataPart("iconfile", uriToFile(mSelected.get(0)).getName(), imageBody);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            builder.addFormDataPart("iconfile", "");
        }
        builder.addFormDataPart("gender",String.valueOf(gender));
        if (TextUtils.isEmpty(viewBinding.name.getText())) {
            builder.addFormDataPart("nickname", LocalConfiguration.userInfo.getUsername());
        }else {
            builder.addFormDataPart("nickname", viewBinding.name.getText().toString());
        }
        List<MultipartBody.Part> parts = builder.build().parts();

        mPresenter.changeInfo(MyApplication.spUtils.getString("token", ""),parts);
    }

    /**
     * 转换为file文件
     *返回值为file类型
     * @param uri
     * @return
     */
    private File uriToFile(Uri uri) {
        String img_path;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null,
                null, null);
        if (actualimagecursor == null) {
            img_path = uri.getPath();
        } else {
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            img_path = actualimagecursor
                    .getString(actual_image_column_index);
        }
        File file = new File(img_path);
        return file;
    }

    @Override
    public void changeInfo(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            ToastUtils.showShortToast("修改成功");
            finish();

        }
    }


    /**
     * 修改性别
     */
    private void changeGender() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.change_sex_layout, null);
        RelativeLayout maleBtn = v.findViewById(R.id.maleBtn);
        RelativeLayout femaleBtn = v.findViewById(R.id.femaleBtn);
        RelativeLayout cancelBtn = v.findViewById(R.id.cancelBtn);

        builder.setView(v);
        builder.setCancelable(true);
        final Dialog noticeDialog = builder.create();
        noticeDialog.getWindow().setGravity(Gravity.BOTTOM);
        noticeDialog.getWindow().setWindowAnimations(R.style.anim_menu_bottombar);
        noticeDialog.show();
        maleBtn.setOnClickListener(v13 -> {
            gender = 1;
            viewBinding.genderTxt.setText("男");
            noticeDialog.dismiss();
        });
        femaleBtn.setOnClickListener(v12 -> {
            gender = 2;
            viewBinding.genderTxt.setText("女");
            noticeDialog.dismiss();
        });
        cancelBtn.setOnClickListener(v1 -> noticeDialog.dismiss());

        WindowManager.LayoutParams layoutParams = noticeDialog.getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        noticeDialog.getWindow().setAttributes(layoutParams);
    }


}