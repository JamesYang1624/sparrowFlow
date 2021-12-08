package com.yangwz.common.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.yangwz.common.R;


/**
 * 进度条对话框
 */
public class MyProgressDialog extends Dialog {

    TextView tv_message;

    private Context mContext;
    private MyProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public static MyProgressDialog getInstance(Context context) {
        return new MyProgressDialog(context, R.style.CustomProgressDialog);
    }


    /**
     * @param message
     * @return
     */
    public MyProgressDialog init(String message) {
        setContentView(R.layout.progress_dialog);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        tv_message = findViewById(R.id.tv_message);
        if (null == message || "".equals(message)) {
            tv_message.setVisibility(View.GONE);
        } else {
            tv_message.setText(message);
        }
        return this;
    }

    /**
     * @return
     */
    public MyProgressDialog init() {
        return this.init("");
    }

    public void show(boolean isCancel) {
        if(mContext != null && mContext instanceof Activity){
            Activity activity = (Activity)mContext;
            if(activity.isFinishing()){
                return;
            }
        }
        setCancelable(isCancel);
        if(!isShowing()) {
            super.show();
        }
    }

    public void show(boolean isCancel, String msg) {
        setCancelable(isCancel);
        if (msg != null && !msg.equals("")) {
            tv_message.setText(msg);
        }
        super.show();
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}