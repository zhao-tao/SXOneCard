package com.sxonecard.ui;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.sxonecard.BuildConfig;
import com.sxonecard.CardApplication;
import com.sxonecard.R;
import com.sxonecard.background.SoundService;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.http.bean.RechargeCardBean;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.sxonecard.http.Constants.PAGE_PAY_METHOD;
import static com.sxonecard.http.Constants.isDebug;

/**
 * 选择充值金额
 */
public class FragmentThree extends BaseFragment {

    @Bind(R.id.price_grid)
    GridView priceGrid;
    private final List<Integer> priceImg = new ArrayList<>();
    private final List<Integer> prices = new ArrayList<>();

    @Bind(R.id.tv_back)
    TextView mBackTv;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_3;
    }

    @Override
    public void initView() {
        setVoice(SoundService.XUANZEJINE);
        prices.add(10);
        prices.add(20);
        prices.add(50);
        prices.add(100);
        prices.add(200);
        prices.add(500);
        priceImg.add(R.drawable.money1);
        priceImg.add(R.drawable.money2);
        priceImg.add(R.drawable.money3);
        priceImg.add(R.drawable.money4);
        priceImg.add(R.drawable.money5);
        priceImg.add(R.drawable.money6);

        priceGrid.setAdapter(new CommonAdapter<Integer>(getContext(), R.layout.item_price, prices) {
            @Override
            protected void convert(ViewHolder viewHolder, Integer item, int position) {
                viewHolder.setImageResource(R.id.price, priceImg.get(position));
            }
        });
        priceGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int price = prices.get(position);
                Message msgPrice = Message.obtain();
                msgPrice.obj = isDebug ? 1 : price;
                msgPrice.what = PAGE_PAY_METHOD;
                navHandle.sendMessage(msgPrice);
            }
        });


        mBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                返回时需携带余额值
                RechargeCardBean checkCard = CardApplication.getInstance().getCheckCard();
                Message msg = new Message();
                msg.what = 1;
                double balance = checkCard.getAmount() * 1.0 / 100;
                DecimalFormat df = new DecimalFormat("######0.00");
                msg.obj = df.format(balance);
                navHandle.sendMessage(msg);
            }
        });

        if (BuildConfig.AUTO_TEST) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (priceGrid == null)
                        return;
                    priceGrid.performItemClick(priceGrid.getChildAt(0), 0, priceGrid.getItemIdAtPosition(0));
                }
            }, 5000);
        }
    }

    @Override
    public void loadData() {
    }

}
