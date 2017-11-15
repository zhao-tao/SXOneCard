package com.sxonecard.ui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxonecard.R;
import com.sxonecard.base.BaseFragment;
import com.sxonecard.base.MyVideoView;

import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.util.List;

import butterknife.Bind;

public class FragmentMvAction extends BaseFragment {

    @Bind(R.id.banner_mv)
    MyVideoView videoView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_mv;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        this.onAttach(this.fragmentView.getContext());

        Bundle bundle = this.getArguments();
        String mv_json = bundle.getString("msg");
        Gson gson = new Gson();
        List<String> adslist = gson.fromJson(mv_json, new TypeToken<List<String>>() {
        }.getType());


        String url = "http://pay.thecitypass.cn/upload/video/2017/0502/a1c8ed1ce122b7bac9992a1dd74c6231.mp4";

        url = adslist.get(0);

        Uri uri = Uri.parse(url);
        videoView.setVideoURI(uri);
//        videoView.requestFocus();
        videoView.start();

//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                videoView.requestFocus();
//                videoView.start();
//            }
//        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);

            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
//                        myVideoView.setVideoPath(videopath);
                        videoView.start();

                    }
                });
    }

    @Override
    public void loadData() {

    }

}