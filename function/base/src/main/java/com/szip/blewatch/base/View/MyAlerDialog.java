package com.szip.blewatch.base.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.szip.blewatch.base.R;


public class MyAlerDialog {
    private static MyAlerDialog dialogUtil;




    public static MyAlerDialog getSingle(){
        if (dialogUtil==null){
            synchronized (MyAlerDialog.class){
                if (dialogUtil == null){
                    return new MyAlerDialog();
                }
            }
        }
        return dialogUtil;
    }

//    public AlertDialog showAlerDialogWithPrivacy(String title, String msg, String positive, String negative, boolean cancelable,
//                                                 final AlerDialogOnclickListener onclickListener, final Context context){
//
//        final AlertDialog alertDialog = new AlertDialog.Builder(context)
//                .setCancelable(true)
//                .create();
//        alertDialog.show();
//        Window window = alertDialog.getWindow();
//        window.setContentView(R.layout.dialog_layout_pri);
//        TextView tv_title = window.findViewById(R.id.dialogTitle);
//        tv_title.setText(title);
//        TextView tv_message =  window.findViewById(R.id.msgTv);
//        tv_message.setText(msg);
//        alertDialog.setCancelable(cancelable);
//
//        Button cancel = window.findViewById(R.id.btn_cancel);
//        if (negative!=null)
//            cancel.setText(negative);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onclickListener!=null){
//                    onclickListener.onDialogTouch(false);
//                    alertDialog.dismiss();
//                }
//            }
//        });//取消按钮
//        Button confirm = window.findViewById(R.id.btn_comfirm);
//        if (positive!=null)
//            confirm.setText(positive);
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onclickListener!=null){
//                    onclickListener.onDialogTouch(true);
//                    alertDialog.dismiss();
//                }
//            }
//        });//确定按钮
//
//        window.findViewById(R.id.privacyTv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                context.startActivity(new Intent(context, PrivacyActivity.class));
//            }
//        });
//
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        return alertDialog;
//    }

    public AlertDialog showAlerDialog(String title, String msg, String positive, String negative, boolean cancelable,
                                      final AlerDialogOnclickListener onclickListener, Context context){

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(true)
                .create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_layout);
        TextView tv_title = window.findViewById(R.id.dialogTitle);
        tv_title.setText(title);
        TextView tv_message =  window.findViewById(R.id.msgTv);
        tv_message.setText(msg);
        alertDialog.setCancelable(cancelable);

        Button cancel = window.findViewById(R.id.btn_cancel);
        if (negative!=null)
            cancel.setText(negative);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickListener.onDialogTouch(false);
                alertDialog.dismiss();
            }
        });//取消按钮
        Button confirm = window.findViewById(R.id.btn_comfirm);
        if (positive!=null)
            confirm.setText(positive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onclickListener!=null){
                    onclickListener.onDialogTouch(true);
                    alertDialog.dismiss();
                }
            }
        });//确定按钮

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return alertDialog;
    }





    public interface AlerDialogOnclickListener {
        void onDialogTouch(boolean flag);
    }

    public interface AlerDialogEditOnclickListener {
        void onDialogEditTouch(String edit1);
    }

}
