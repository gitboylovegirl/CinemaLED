package com.cinemaled.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cinemaled.R;

/**
 * created by fred
 * on 2020/1/15
 */
public class AlertDialogUtils {
    public static void showConfirmDialog(final Activity context, final OnButtonClickListener listener) {
        final Dialog alertDialog = new Dialog(context, R.style.BottomDialogStyle);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.show();
        View view = View.inflate(context, R.layout.layout_posid_dialog, null);
        TextView tvConfirm=view.findViewById(R.id.dialog_confirm);
        final LinearLayout  posid=view.findViewById(R.id.posid_view);
        final RelativeLayout loading=view.findViewById(R.id.loading);
        final EditText content=view.findViewById(R.id.dialog_content);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = content.getText().toString().trim();
                if(trim.isEmpty()){
                    ToastUtil.showShort(context,"posid不可为空");
                    return;
                }
                if(listener!=null){
                    listener.onPositiveButtonClick();
                }
                PreferencesUtil.getInstance().setPosid("square_01");
                //PreferencesUtil.getInstance().setPosid(trim);
                posid.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                //alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().setContentView(view);
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        WindowManager manager = context.getWindowManager();
        Display display = manager.getDefaultDisplay();
        //设置宽度
        lp.width = (new Double(display.getWidth()*0.5)).intValue();
        alertDialog.getWindow().setAttributes(lp);
    }

    /**
     * 点击接口
     */
    private static OnButtonClickListener onButtonClickListener;

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    /**
     * 按钮点击回调接口
     */
    public interface OnButtonClickListener {
        /**
         * 确定按钮点击回调方法
         */
        void onPositiveButtonClick();

        /**
         * 取消按钮点击回调方法
         */
        void onNegativeButtonClick();
    }


}
