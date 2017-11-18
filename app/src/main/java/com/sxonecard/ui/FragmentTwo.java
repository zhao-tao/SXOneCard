package com.sxonecard.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.sxonecard.BuildConfig;
import com.sxonecard.CardApplication;
import com.sxonecard.R;
import com.sxonecard.background.SoundService;
import com.sxonecard.base.BaseFragment;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


/**
 * 选择充值方式
 */
public class FragmentTwo extends BaseFragment {
    @Bind(R.id.serviceTypeView)
    GridView serviceTypeView;
    @Bind(R.id.user_money)
    TextView userMoney;

    //    private int[] icon = {R.drawable.anniu1, R.drawable.anniu2, R.drawable.anniu2};
//    private String[] name = {"充值", "水电", "购卡"};
    private static List<Integer> services = new ArrayList<>();

    static {
        services.add(R.drawable.bus_selected);
        services.add(R.drawable.elec_normal);
        services.add(R.drawable.card_normal);
    }

    //    private Integer[] images = {R.drawable.bus_selected,R.drawable.elec_normal,R.drawable.card_normal};
//    private List<Map<String, String>> dataList = new ArrayList<Map<String, String>>(3);
    @Override
    public int getLayoutId() {
        return R.layout.fragment_2;
    }

    @Override
    public void initView() {
        setVoice(SoundService.WUQUZOUKAPIAN);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String money = (String) bundle.get("msg");
            CardApplication.a_money = Double.parseDouble(money);
            userMoney.setText("当前余额：" + money + "元");
        }
//        dataList = getData();
        serviceTypeView.setAdapter(new CommonAdapter<Integer>(
                getContext(), R.layout.item_service_type_2, services) {
            @Override
            protected void convert(ViewHolder viewHolder, Integer imgId, int position) {
                viewHolder.setImageResource(R.id.service_img, imgId);
            }
        });
        serviceTypeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        navHandle.sendEmptyMessage(2);
                        break;
                    case 1:
                        Toast.makeText(FragmentTwo.super.context, "水电服务暂未开放!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(FragmentTwo.super.context, "购卡服务暂未开放!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(FragmentTwo.super.context, "未知服务...",
                                Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        if(BuildConfig.AUTO_TEST) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(serviceTypeView == null)
                        return;
                    serviceTypeView.performItemClick(serviceTypeView.getChildAt(0),
                            0,serviceTypeView.getItemIdAtPosition(0));
                }
            },3000);
//
//            navHandle.sendEmptyMessageDelayed(2,3000);
        }
    }

    @Override
    public void loadData() {
    }


//    private List<Map<String, String>> getData() {
//        //填充数据源
//        Map<String, String> map = null;
//        for (int i = 0; i < name.length; i++) {
//            map = new HashMap<>();
//            map.put("image", String.valueOf(icon[i]));
//            map.put("name", name[i]);
//            dataList.add(map);
//        }
//        return dataList;
//    }
}
