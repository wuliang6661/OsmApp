package com.heloo.android.osmapp.ui.address;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.ActivityAddAddressBinding;
import com.heloo.android.osmapp.model.AddressBean;
import com.heloo.android.osmapp.model.JsonBean;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.AddAddressContract;
import com.heloo.android.osmapp.mvp.presenter.AddAddressPresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.GetJsonDataUtil;
import com.heloo.android.osmapp.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 添加地址
 */
public class AddAddressActivity extends MVPBaseActivity<AddAddressContract.View, AddAddressPresenter, ActivityAddAddressBinding>
    implements AddAddressContract.View, View.OnClickListener {

    private static boolean isLoaded = false;
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private String addProvince = "";//省
    private String addCity = "";//市
    private String addArea = "";//区
    private boolean isDefault = false;//默认地址
    private AddressBean addressBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        if (getIntent().getSerializableExtra("address") != null){
            addressBean = (AddressBean) getIntent().getSerializableExtra("address");
        }
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void initView() {
        goBack();
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.selectAddressBtn.setOnClickListener(this);
        viewBinding.defaultBtn.setOnClickListener(this);
        viewBinding.submitBtn.setOnClickListener(this);
        if (addressBean != null){
            addProvince = addressBean.getProvince();
            addCity = addressBean.getCity();
            addArea = addressBean.getArea();
            viewBinding.nameInput.setText(addressBean.getName());
            viewBinding.phoneInput.setText(addressBean.getPhone());
            viewBinding.addressSelect.setText(String.format("%s%s%s",addressBean.getProvince(),addressBean.getCity(),addressBean.getArea()));
            viewBinding.detailAddressInput.setText(addressBean.getAddress());
            viewBinding.codeInput.setText(addressBean.getPostcode());
            if (addressBean.getStatus() == 1){
                viewBinding.defaultImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.address_select_yes,null));
                isDefault = true;
            }else {
                viewBinding.defaultImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.address_select_no,null));
                isDefault = false;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selectAddressBtn:
                if (isLoaded) {
                    showPickerView();
                }
                break;
            case R.id.defaultBtn:
                if (isDefault){
                    viewBinding.defaultImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.address_select_no,null));
                    isDefault = false;
                }else {
                    viewBinding.defaultImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.address_select_yes,null));
                    isDefault = true;
                }
                break;
            case R.id.submitBtn:
                if (verify()) {
                    String defaultAddress;
                    if (isDefault){
                        defaultAddress = "1";
                    }else {
                        defaultAddress = "2";
                    }
                    showProgress("");
                    if (addressBean != null){
                        mPresenter.modifyAddress(MyApplication.spUtils.getString("token", ""),
                                addressBean.getId(),
                                viewBinding.detailAddressInput.getText().toString(),
                                addProvince,addCity,addArea,viewBinding.nameInput.getText().toString(),
                                viewBinding.phoneInput.getText().toString(),viewBinding.codeInput.getText().toString(),
                                defaultAddress);
                        return;
                    }
                    mPresenter.addAddress(MyApplication.spUtils.getString("token", ""),
                            viewBinding.detailAddressInput.getText().toString(),
                            addProvince,addCity,addArea,viewBinding.nameInput.getText().toString(),
                            viewBinding.phoneInput.getText().toString(),viewBinding.codeInput.getText().toString(),
                            defaultAddress);
                }
                break;
        }
    }

    /**
     * 校验输入
     */
    private boolean verify() {
        if (TextUtils.isEmpty(viewBinding.nameInput.getText())){
            ToastUtils.showShortToast("请输入姓名");
            return false;
        }
        if (TextUtils.isEmpty(viewBinding.phoneInput.getText())){
            ToastUtils.showShortToast("请输入联系电话");
            return false;
        }
        if (!viewBinding.phoneInput.getText().toString().startsWith("1") ||
                viewBinding.phoneInput.getText().toString().length() != 11){
            ToastUtils.showShortToast("请输入正确格式的联系电话");
            return false;
        }
        if (addProvince.equals("")){
            ToastUtils.showShortToast("请选择地址");
            return false;
        }
        if (TextUtils.isEmpty(viewBinding.detailAddressInput.getText())){
            ToastUtils.showShortToast("请输入详细地址");
            return false;
        }
        if (TextUtils.isEmpty(viewBinding.codeInput.getText())){
            ToastUtils.showShortToast("请输入邮政编码");
            return false;
        }
        return true;
    }

    @Override
    public void getAddResult(ResponseBody addResult) throws JSONException, IOException {
        String s = new String(addResult.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            finish();
        }
    }

    @Override
    public void modifyAddress(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            finish();
        }
    }

    @Override
    public void onRequestError(String msg) {

    }

    @Override
    public void onRequestEnd() {

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;
                case MSG_LOAD_FAILED:
                    break;
            }
        }
    };

    private void showPickerView() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";
                addProvince = opt1tx;
                addCity = opt2tx;
                addArea = opt3tx;

                viewBinding.addressSelect.setText(String.format("%s %s %s",opt1tx,opt2tx,opt3tx));
            }
        })

                .setTitleText("地址选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setTitleColor(Color.WHITE)//标题文字颜色
                .setSubmitColor(Color.WHITE)//确定按钮文字颜色
                .setCancelColor(Color.WHITE)//取消按钮文字颜色
                .setTitleBgColor(0xFFE3C27D)//标题背景颜色 Night mode
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }


    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

}