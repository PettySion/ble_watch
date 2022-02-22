package com.szip.blewatch;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.szip.blewatch.base.BaseApplication;
import com.szip.blewatch.base.Const.HealthyConst;
import com.szip.blewatch.base.Constant;
import com.szip.blewatch.base.Notification.MyNotificationReceiver;
import com.szip.blewatch.base.Service.BleService;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.db.dbModel.AnimalHeatData;
import com.szip.blewatch.base.db.dbModel.BloodOxygenData;
import com.szip.blewatch.base.db.dbModel.BloodPressureData;
import com.szip.blewatch.base.db.dbModel.HealthyCardData;
import com.szip.blewatch.base.db.dbModel.HealthyCardData_Table;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.blewatch.base.db.dbModel.SportData_Table;

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
        startService(new Intent(this, BleService.class));
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

        //随机添加测试数据
//        List<AnimalHeatData> animalHeatData = new ArrayList<>();
//        List<BloodOxygenData> bloodOxygenData = new ArrayList<>();
//        List<BloodPressureData> bloodPressureData = new ArrayList<>();
//        for (int i = 0;i<100;i++){
//            animalHeatData.add(new AnimalHeatData(1645459200+i*12*60*60, new Random().nextInt(40)+360));
//            bloodOxygenData.add(new BloodOxygenData(1645459200+i*12*60*60,new Random().nextInt(5)+95));
//            bloodPressureData.add(new BloodPressureData(1645459200+i*12*60*60,new Random().nextInt(20)+110,
//                    new Random().nextInt(10)+50));
//        }
//        SaveDataUtil.newInstance().saveBloodOxygenDataListData(bloodOxygenData,getApplicationContext());
//        SaveDataUtil.newInstance().saveBloodPressureDataListData(bloodPressureData,getApplicationContext());
//        SaveDataUtil.newInstance().saveAnimalHeatDataListData(animalHeatData,getApplicationContext());
//        List<SportData> sportDataList = new ArrayList<>();
//        for (int i = 0;i<50;i++){
//            sportDataList.add(new SportData(1645459200+i*12*60*60));
//        }
//        SaveDataUtil.newInstance().saveSportDataListData(sportDataList,getApplicationContext());

    }
}
