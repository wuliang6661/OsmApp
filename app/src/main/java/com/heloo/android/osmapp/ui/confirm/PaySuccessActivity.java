package com.heloo.android.osmapp.ui.confirm;

import android.os.Bundle;
import android.view.View;

import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.databinding.ActivityPaySuccessBinding;
import com.heloo.android.osmapp.ui.order.OrderDetailActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;

/**
 * 支付成功
 */
public class PaySuccessActivity extends BaseActivity {

    private ActivityPaySuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaySuccessBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        goBack();
        binding.headLayout.post(() -> binding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        binding.seeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", getIntent().getExtras().getString("id"));
                gotoActivity(OrderDetailActivity.class, bundle, true);
            }
        });
    }
}