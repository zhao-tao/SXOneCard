package com.sxonecard.ui;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxonecard.R;
import com.sxonecard.background.SoundService;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.http.DateUtil;
import com.sxonecard.http.MyCountDownTimer;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by HeQiang on 2017/6/10.
 */

public class ReChangeError extends BaseFragment {

    @Bind(R.id.errReason)
    TextView errReason;
    @Bind(R.id.error)
    ImageView error;
    private final int wait = 5;
    private MyCountDownTimer timer;

    @Override
    public int getLayoutId() {
        return R.layout.error;
    }

    @Override
    public void initView() {

        Bundle bundle = getArguments();
        if(bundle !=  null) {
            String tip = bundle.getString("msg");
            errReason.setText(tip);
            if(tip.equalsIgnoreCase(getString(R.string.chargeError)))
                setVoice(SoundService.CHONGZHI_FAIL);
            else
                setVoice(SoundService.ERWEIMAGUOQI);
        }
        timer = new MyCountDownTimer(wait * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished)
            {
//                errReason.setText("充值失败，" + millisUntilFinished/1000 + "秒后跳转到首页");
            }

            @Override
            public void onFinish()
            {
                Log.d("reChangeError","redirect first page");
                navHandle.sendEmptyMessage(0);
            }
        };
        timer.start();
    }

    @Override
    public void loadData() {

    }


    @Override
    public void onDestroy() {
        if(null != timer)
        {
            timer.cancel();
        }
        super.onDestroy();
    }

}
