package com.sxonecard.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.sxonecard.R;

/**
 * Created by Administrator on 2017/8/30 0030.
 */

public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        Window wm = getWindow();
        WindowManager.LayoutParams params = wm.getAttributes();
        params.y = 600;
        setCanceledOnTouchOutside(false);
    }
}
