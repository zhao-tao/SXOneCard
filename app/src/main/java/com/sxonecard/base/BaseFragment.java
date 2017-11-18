package com.sxonecard.base;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sxonecard.CardApplication;
import com.sxonecard.R;

import java.io.IOException;

import butterknife.ButterKnife;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.sxonecard.background.SoundService.CAOZUOTISHI;
import static com.sxonecard.background.SoundService.CHONGZHIZHONG;
import static com.sxonecard.background.SoundService.CHONGZHI_FAIL;
import static com.sxonecard.background.SoundService.CHONGZHI_SUCCESS;
import static com.sxonecard.background.SoundService.ERWEIMA;
import static com.sxonecard.background.SoundService.ERWEIMAGUOQI;
import static com.sxonecard.background.SoundService.WUQUZOUKAPIAN;
import static com.sxonecard.background.SoundService.XUANZEJINE;
import static com.sxonecard.background.SoundService.ZHIFUFANGSHI;

/**
 * Created by HeQiang on 2016/10/26.
 */

public abstract class BaseFragment extends Fragment {
    protected Activity context;
    protected View fragmentView;
    protected boolean isVisibleToUser;
    protected Handler navHandle;

    public void setNavHandle(Handler handle) {
        this.navHandle = handle;
    }

    /**
     * 控件是否初始化完成
     */
    private boolean isViewCreated = false;
    //
    private boolean isDataInitiated = false;

    public void onAttach(Activity context) {
        super.onAttach(context);
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == fragmentView) {
            fragmentView = inflater.inflate(getLayoutId(), null);
            ButterKnife.bind(this, fragmentView);
            initView();
        }
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewCreated = true;
        prepareFetchData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        fragmentView = null;
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void loadData();

    @Override

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser)
            prepareFetchData();
    }

    public boolean prepareFetchData() {
        return prepareFetchData(false);

    }

    public boolean prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewCreated && (!isDataInitiated || forceUpdate)) {
            loadData();
            isDataInitiated = true;
            return true;
        }
        return false;
    }

    public void hideSoft(EditText editText) {
        if (editText != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public void setVoice(String name) {
//        Intent intent = new Intent(name, null, getActivity(), SoundService.class);
//        getContext().startService(intent);
        Uri uri = null;
        switch (name) {
            case CAOZUOTISHI:
                uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.caozuotishi);
                break;
            case XUANZEJINE:
                uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.xuanzejine);
                break;
            case ZHIFUFANGSHI:
                uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.zhifufangshi);
                break;
            case ERWEIMA:
                uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.erweima_1);
                break;
            case CHONGZHIZHONG:
                uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.chongzhizhong_1);
                break;
            case CHONGZHI_SUCCESS:
                uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.chongzhi_success);
                break;
            case CHONGZHI_FAIL:
                uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.chongzhishibai_1);
                break;
            case ERWEIMAGUOQI:
                uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.chongzhi_fail);
                break;
            case WUQUZOUKAPIAN:
                uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.wuquzoukapian);
                break;
            default:
                break;
        }

        if (uri != null) {
            MediaPlayer mediaPlayer = CardApplication.mediaPlayer;
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getActivity(), uri);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM,0);
//        int id = soundPool.load(getActivity(), R.raw.caozuotishi,1);
//        int res = soundPool.play(id,1,1,0,0,1);
//        Log.i("sound",res+"");
    }
}
