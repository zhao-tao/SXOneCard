package com.sxonecard.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sxonecard.background.SoundService;

import butterknife.ButterKnife;

import static android.content.Context.INPUT_METHOD_SERVICE;

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

    public void setVoice(String name){
        Intent intent = new Intent(name, null, getActivity(), SoundService.class);
        getContext().startService(intent);
    }
}
