package com.sxonecard.ui;

import android.os.Bundle;

import android.support.annotation.Nullable;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxonecard.R;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.base.NetworkImageHolderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class FragmentPicAction extends BaseFragment {

    List<String> banners = new ArrayList<>();
    @Bind(R.id.banner_pic)
    ConvenientBanner banner;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_pic;
    }

    @Override
    public void initView() {

        Bundle bundle = this.getArguments();
        String pic_json = bundle.getString("msg");
        Gson gson = new Gson();
        List<String> adslist = gson.fromJson(pic_json, new TypeToken<List<String>>() {
        }.getType());

        banners.addAll(adslist);
        banner.setPages(
                new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, banners);
//                .setPageIndicator(new int[]{R.drawable.banner_page_indicator, R.drawable.banner_page_indicator_focus})
//                //设置指示器的方向
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        banner.startTurning(8000).setEnabled(false);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
