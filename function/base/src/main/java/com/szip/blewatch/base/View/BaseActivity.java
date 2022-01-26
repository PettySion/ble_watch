package com.szip.blewatch.base.View;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.szip.blewatch.base.R;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Util.MathUtil;


/**
 * Created by Administrator on 2019/11/28.
 */

public class BaseActivity extends AppCompatActivity {

    private TextView titleTv,titleBigTv;
    private MyScrollView myScrollView;

    protected void showToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    protected void setTitle(String msg){
        titleTv = findViewById(R.id.titleTv);
        titleBigTv = findViewById(R.id.titleBigTv);
        myScrollView = findViewById(R.id.scroll);

        if (titleBigTv!=null)
            titleBigTv.setText(msg);

        if (titleTv==null||myScrollView==null)
            return;

        myScrollView.setOnScrollListener(listener);
        titleTv.setText(msg);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 适配Android字体（随着系统字体改变而改变）
     * */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    /**
     * 修改状态栏字体颜色
     * */
    protected void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected boolean checkPermission(String permission){
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    private MyScrollView.OnScrollListener listener = new MyScrollView.OnScrollListener() {
        @Override
        public void onScroll(int scrollY) {
            if (titleTv!=null){
                if (scrollY>120)
                    titleTv.setVisibility(View.VISIBLE);
                else
                    titleTv.setVisibility(View.GONE);
            }
        }
    };

}
