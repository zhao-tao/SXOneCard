package com.sxonecard.ui;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.sxonecard.R;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.http.bean.TradeStatusBean;

import butterknife.Bind;

import static android.content.Context.MODE_PRIVATE;


/**
 * 补充值页面（已确定卡号和补充值时间符合后）
 * 收到串口识别卡信息后，判断是否为补充值的卡，如果是，跳转到此页面（携带余额信息，同页面2）
 *
 */
public class FragmentReChange extends BaseFragment {
    @Bind(R.id.user_recharge_money)
    TextView userRechargeMoney;
    @Bind(R.id.tv_rechange)
    TextView tvReChange;
    @Bind(R.id.tv_refund)
    TextView tvReFund;
    @Bind(R.id.tv_back)
    TextView mBackTv;
    private String msg;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_rechange;
    }

    @Override
    public void initView() {
        //播放补充值操作语音
//        setVoice(SoundService.RECHANGE);
        msg = getArguments().getString("msg");
        userRechargeMoney.setText("卡内当前余额:" + msg + "元");
        mBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navHandle.sendEmptyMessage(0);
            }
        });
//        获取sharePref中的暂存补充值信息
        SharedPreferences change = context.getSharedPreferences("change", MODE_PRIVATE);

        TradeStatusBean trade = new TradeStatusBean();
        trade.setPrice(change.getInt("money", 0));


    }

    @Override
    public void loadData() {

    }

}
