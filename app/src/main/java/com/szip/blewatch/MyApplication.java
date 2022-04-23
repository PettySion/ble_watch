package com.szip.blewatch;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.szip.blewatch.base.BaseApplication;
import com.szip.blewatch.base.Broadcast.UtilBroadcat;
import com.szip.blewatch.base.Const.HealthyConst;
import com.szip.blewatch.base.Constant;
import com.szip.blewatch.base.Notification.MyNotificationReceiver;
import com.szip.blewatch.base.Service.BleService;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.db.dbModel.AnimalHeatData;
import com.szip.blewatch.base.db.dbModel.BloodOxygenData;
import com.szip.blewatch.base.db.dbModel.BloodPressureData;
import com.szip.blewatch.base.db.dbModel.HealthyCardData;
import com.szip.blewatch.base.db.dbModel.HealthyCardData_Table;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.blewatch.base.db.dbModel.SportData_Table;
import com.szip.blewatch.base.db.dbModel.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        UserModel userModel = LoadDataUtil.newInstance().getUserInfo(MathUtil.newInstance().getUserId(this));
        if (userModel!=null)
            startService(new Intent(this, BleService.class));

        /**
         * 注册广播
         * */
        UtilBroadcat broadcat = new UtilBroadcat(getApplicationContext());
        broadcat.onRegister();

        String packageName = getPackageName();
        String strListener = Settings.Secure.getString(this.getContentResolver(),
                "enabled_notification_listeners");
        if (strListener != null
                && strListener
                .contains(packageName)) {
            ComponentName localComponentName = new ComponentName(this, MyNotificationReceiver.class);
            PackageManager localPackageManager = this.getPackageManager();
            localPackageManager.setComponentEnabledSetting(localComponentName, 2, 1);
            localPackageManager.setComponentEnabledSetting(localComponentName, 1, 1);
        }
    }
}
