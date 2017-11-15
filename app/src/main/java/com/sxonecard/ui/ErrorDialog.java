package com.sxonecard.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sxonecard.R;

/**
 * Created by Administrator on 2017/8/30 0030.
 */

public class ErrorDialog extends Dialog {
    public ErrorDialog(Context context) {
        super(context,R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_err);
        Window wm = getWindow();
        WindowManager.LayoutParams params = wm.getAttributes();
        params.y = 600;
        setCanceledOnTouchOutside(false);
        findViewById(R.id.err_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //3秒自动消失
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        },3000);
    }

    public void setBackground(int resId){
        ImageView img  = (ImageView)findViewById(R.id.err_img);
        img.setImageResource(resId);
    }
}
