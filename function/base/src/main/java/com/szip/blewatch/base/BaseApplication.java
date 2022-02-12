package com.szip.blewatch.base;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.location.AMapLocationClient;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.szip.blewatch.base.Util.ble.ClientManager;
import com.szip.blewatch.base.Util.http.HttpClientUtils;
import com.szip.blewatch.base.Util.LogUtil;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this,true);
        initARouter();
        ClientManager.getInstance().init(this);
        FlowManager.init(this);
        HttpClientUtils.newInstance().init(this);
        LogUtil.getInstance().init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initARouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }
}
