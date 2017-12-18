package com.sxonecard.ui;

import android.content.ContentValues;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sxonecard.CardApplication;
import com.sxonecard.R;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.http.bean.ChangeData;
import com.sxonecard.http.bean.ReChangeSQL;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;

import butterknife.Bind;

import static com.sxonecard.CardApplication.b_money;
import static com.sxonecard.CardApplication.reChangeSQL;
import static com.sxonecard.http.Constants.PAGE_CHECK_CARD;
import static com.sxonecard.http.Constants.PAGE_QR_CODE;


/**
 * 补充值页面（已确定卡号和补充值时间符合后）仅带入String类型充值金额
 * 收到串口识别卡信息后，判断是否为补充值的卡，如果是，跳转到此页面（携带余额信息，同页面2）
 * <p>
 * 点退款删除数据库字段，返回首页
 * 点补充值到二维码页面，并跳过二维码，直接支付
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

    @Override
    public int getLayoutId() {
        return R.layout.fragment_rechange;
    }

    @Override
    public void initView() {
        //播放补充值操作语音
//        setVoice(SoundService.RECHANGE);
        userRechargeMoney.setText("可补充值金额:" + CardApplication.DoubleToString(b_money) + "元");
        mBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navHandle.sendEmptyMessage(0);
            }
        });
        initListener();

    }

    private void initListener() {
        tvReChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//删除本条补充值记录
                ContentValues values = new ContentValues();
                values.put("ChangeTime", reChangeSQL.getChangeTime() - 1);
                DataSupport.updateAll(ReChangeSQL.class, values, "card=?", CardApplication.getInstance().getCheckCard().getCardNO());

                Message msgCode = Message.obtain();
                ChangeData changeData = new ChangeData();
                DecimalFormat df = new DecimalFormat("######.##");
                changeData.setRechangeFee(CardApplication.DoubleToString(b_money));
                // 利用gson对象生成json字符串
                Gson gson = new Gson();
                msgCode.obj = gson.toJson(changeData);
                msgCode.what = PAGE_QR_CODE;
                navHandle.sendMessage(msgCode);
            }
        });

        tvReFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("ChangeTime", reChangeSQL.getChangeTime() - 1);
                DataSupport.updateAll(ReChangeSQL.class, values, "card=?", CardApplication.getInstance().getCheckCard().getCardNO());

                navHandle.sendEmptyMessage(PAGE_CHECK_CARD);
            }
        });
    }

    @Override
    public void loadData() {

    }

}
