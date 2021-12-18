package com.heloo.android.osmapp.ui.address;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.model.MyAdressBean;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGRecycleViewAdapter;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGViewHolder;

import java.util.List;

public class ZtAddressActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private RelativeLayout headLayout;

    private Integer id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_zt_address);

        goBack();
        setTitle("取货地址");
        recyclerView = findViewById(R.id.recyclerview);
        headLayout = findViewById(R.id.headLayout);
        headLayout.post(() -> headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(ZtAddressActivity.this), 0, 0));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        id = getIntent().getIntExtra("id", 0);
        getSincePoint();
    }


    public void getSincePoint() {
        HttpInterfaceIml.getSincePoint().subscribe(new HttpResultSubscriber<List<MyAdressBean>>() {
            @Override
            public void onSuccess(List<MyAdressBean> s) {
                setAdapter(s);
            }

            @Override
            public void onFiled(String message) {
                ToastUtils.showShortToast(message);
            }
        });
    }


    private void setAdapter(List<MyAdressBean> s) {
        LGRecycleViewAdapter<MyAdressBean> adapter = new LGRecycleViewAdapter<MyAdressBean>(s) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_zt_address;
            }

            @Override
            public void convert(LGViewHolder holder, MyAdressBean myAdressBean, int position) {
                holder.setText(R.id.zt_name, myAdressBean.pointName);
                holder.setText(R.id.zt_address, "地址：" + myAdressBean.address + "\n取货时间：" + myAdressBean.pointTime);
                CheckBox checkBox = (CheckBox) holder.getView(R.id.checkbox);
                if (id == myAdressBean.id.intValue()) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
            }
        };
        adapter.setOnItemClickListener(R.id.item_layout, (view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("address", s.get(position));
            setResult(0x22, intent);
            finish();
        });
        recyclerView.setAdapter(adapter);
    }
}
