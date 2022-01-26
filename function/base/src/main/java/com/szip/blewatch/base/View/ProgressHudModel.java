package com.szip.blewatch.base.View;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;


/**
 * Created by Administrator on 2018/12/22.
 */

public class ProgressHudModel {
    private static ProgressHudModel progressHudModel;
    private KProgressHUD progressHUD;

    private ProgressHudModel(){

    }

    private Handler handler = new Handler();

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (progressHUD!=null){
                progressHUD.dismiss();
                progressHUD = null;
            }
        }
    };

    public static ProgressHudModel newInstance(){                     // 单例模式，双重锁
        if( progressHudModel == null ){
            synchronized (ProgressHudModel.class){
                if( progressHudModel == null ){
                    progressHudModel = new ProgressHudModel();
                }
            }
        }
        return progressHudModel ;
    }

    public boolean isShow(){
        if (progressHUD!=null)
            return true;
        else
            return false;
    }



    public void show(final Context mContext, String title,boolean cancelAble){
        progressHUD  = KProgressHUD.create(mContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setCancellable(cancelAble);
        progressHUD.show();
        handler.postDelayed(run,60000);
    }

    public void showWithPie(final Context mContext, String title,int max){
        progressHUD  = KProgressHUD.create(mContext)
                .setMaxProgress(max)
                .setStyle(KProgressHUD.Style.PIE_DETERMINATE)
                .setLabel(title)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        progressHUD.show();
        handler.postDelayed(run,300000);

    }

    public void setProgress(int num){
        if (progressHUD!=null)
            progressHUD.setProgress(num);
    }

    public void setLabel(String label){
        if (progressHUD!=null)
            progressHUD.setLabel(label);
    }

    public void diss(){
        if (progressHUD!=null){
            progressHUD.dismiss();
            progressHUD = null;
            handler.removeCallbacks(run);
        }
    }
}
