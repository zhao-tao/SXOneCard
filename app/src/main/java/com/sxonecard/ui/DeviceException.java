package com.sxonecard.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxonecard.R;
import com.sxonecard.base.BaseFragment;

import butterknife.Bind;

/**
 * Created by pc on 2017-06-20.
 * 设备维护
 */

public class DeviceException extends BaseFragment
{
    @Bind(R.id.errReason)
    TextView deviceErr;
    @Override
    public int getLayoutId() {
        return R.layout.error;
    }

    @Override
    public void initView() {
        Bundle bundle = getArguments();
        if(bundle != null){
            String tip = bundle.getString("msg");
            if(!TextUtils.isEmpty(tip))
                deviceErr.setText(tip);
        }
    }

    @Override
    public void loadData() {

    }
}
