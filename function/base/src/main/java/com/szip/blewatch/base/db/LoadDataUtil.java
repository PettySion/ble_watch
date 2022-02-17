package com.szip.blewatch.base.db;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.szip.blewatch.base.db.dbModel.AnimalHeatData;
import com.szip.blewatch.base.db.dbModel.AnimalHeatData_Table;
import com.szip.blewatch.base.db.dbModel.BloodOxygenData;
import com.szip.blewatch.base.db.dbModel.BloodOxygenData_Table;
import com.szip.blewatch.base.db.dbModel.BloodPressureData;
import com.szip.blewatch.base.db.dbModel.BloodPressureData_Table;
import com.szip.blewatch.base.db.dbModel.HealthyCardData;
import com.szip.blewatch.base.db.dbModel.HealthyCardData_Table;
import com.szip.blewatch.base.db.dbModel.HeartData;
import com.szip.blewatch.base.db.dbModel.HeartData_Table;
import com.szip.blewatch.base.db.dbModel.NotificationData;
import com.szip.blewatch.base.db.dbModel.NotificationData_Table;
import com.szip.blewatch.base.db.dbModel.SleepData;
import com.szip.blewatch.base.db.dbModel.SleepData_Table;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.blewatch.base.db.dbModel.SportData_Table;
import com.szip.blewatch.base.db.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.blewatch.base.db.dbModel.SportWatchAppFunctionConfigDTO_Table;
import com.szip.blewatch.base.db.dbModel.StepData;
import com.szip.blewatch.base.db.dbModel.StepData_Table;
import com.szip.blewatch.base.db.dbModel.UserModel;
import com.szip.blewatch.base.db.dbModel.UserModel_Table;
import com.szip.blewatch.base.db.dbModel.Weather;
import com.szip.blewatch.base.db.dbModel.Weather_Table;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class LoadDataUtil {
    private static LoadDataUtil loadDataUtil;
    private LoadDataUtil(){

    }

    public static LoadDataUtil newInstance(){// 单例模式，双重锁
        if( loadDataUtil == null ){
            synchronized (LoadDataUtil.class){
                if( loadDataUtil == null ){
                    loadDataUtil = new LoadDataUtil();
                }
            }
        }
        return loadDataUtil;
    }

    public UserModel getUserInfo(long id){
        UserModel userModel = SQLite.select()
                .from(UserModel.class)
                .where(UserModel_Table.id.is(id))
                .querySingle();
        return userModel;
    }

    public String getMacAddress(long id){
        UserModel userModel = SQLite.select()
                .from(UserModel.class)
                .where(UserModel_Table.id.is(id))
                .querySingle();
        if (userModel==null)
            return null;
        return userModel.deviceCode;
    }

    public Weather getWeather(long id){
        Weather weather = SQLite.select()
                .from(Weather.class)
                .where(Weather_Table.id.is(id))
                .querySingle();
        return weather;
    }

    /**
     * 取运动数据
     * */
    public List<SportData> getSportList(int page){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(time*1000);
//        calendar.set(Calendar.DAY_OF_MONTH,1);
//        calendar.set(Calendar.HOUR_OF_DAY,0);
//        calendar.set(Calendar.MINUTE,0);
//        calendar.set(Calendar.SECOND,0);
//        calendar.set(Calendar.MILLISECOND,0);
//        long startTime = calendar.getTimeInMillis()/1000;//月报告的起始天数的第一秒
        List<SportData> list = SQLite.select()
                .from(SportData.class)
                .orderBy(OrderBy.fromString(SportData_Table.time+OrderBy.DESCENDING))
                .offset(page*20)
                .limit(20)
                .queryList();
        return list;
    }

    public List<NotificationData> getNotificationList(){
        List<NotificationData> list = SQLite.select()
                .from(NotificationData.class)
                .queryList();
        return list;
    }

    public boolean getNotificationAble(String packageName){
        NotificationData notificationData = SQLite.select()
                .from(NotificationData.class)
                .where(NotificationData_Table.packageName.is(packageName))
                .querySingle();

        if (notificationData==null)
            return false;
        return notificationData.state;
    }

    /**
     * 判断是否支持运动
     * */
    public SportWatchAppFunctionConfigDTO getSportConfig(long userId){

        UserModel userModel = SQLite.select()
                .from(UserModel.class)
                .where(UserModel_Table.id.is(userId))
                .querySingle();

        if (userModel!=null){
            SportWatchAppFunctionConfigDTO sportWatchAppFunctionConfigDTO = SQLite.select()
                    .from(SportWatchAppFunctionConfigDTO.class)
                    .where(SportWatchAppFunctionConfigDTO_Table.mac.is(userModel.deviceCode))
                    .querySingle();

            return sportWatchAppFunctionConfigDTO;
        }else {
            return null;
        }
    }


    /**
     * 取最近一次运动数据
     * */
    public SportData getLastSportData() {

        SportData sportData = SQLite.select()
                .from(SportData.class)
                .orderBy(OrderBy.fromString(SportData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();

        return sportData;
    }

    /**
     * 取最近六次运动数据
     * */
    public List<SportData> getLastSportDataList() {

        List<SportData> sportData = SQLite.select()
                .from(SportData.class)
                .orderBy(OrderBy.fromString(SportData_Table.time+OrderBy.DESCENDING))
                .limit(6)
                .queryList();

        return sportData;
    }

    /**
     * 取首页健康卡片的列表
     * */
    public List<HealthyCardData> getHealthyCard(boolean state) {

        List<HealthyCardData> healthyCardData = SQLite.select()
                .from(HealthyCardData.class)
                .where(HealthyCardData_Table.state.is(state))
                .orderBy(OrderBy.fromString(HealthyCardData_Table.sort+OrderBy.ASCENDING))
                .queryList();

        return healthyCardData;
    }

    /**
     * 取心率日报告
     * */
    public List<HeartData> getHeartWithDay(long time){
        List<HeartData> sqlData = SQLite.select()
                .from(HeartData.class)
                .where(HeartData_Table.time.lessThan(time+24*60*60-1),
                        HeartData_Table.time.greaterThanOrEq(time))
                .queryList();

        return sqlData;
    }

    /**
     * 取心率日报告
     * */
    public List<HeartData> getHeartWithWeek(long time){
        List<HeartData> sqlData = SQLite.select()
                .from(HeartData.class)
                .where(HeartData_Table.time.lessThan(time+7*24*60*60),
                        HeartData_Table.time.greaterThanOrEq(time))
                .queryList();

        return sqlData;
    }

    /**
     * 取计步日报告
     * */
    public StepData getStepWithDay(long time){
        StepData sqlData = SQLite.select()
                .from(StepData.class)
                .where(StepData_Table.time.is(time))
                .querySingle();
        return sqlData;
    }

    /**
     * 取计步周报告
     * */
    public List<StepData> getStepWithWeek(long time){
        List<StepData> sqlData = SQLite.select()
                .from(StepData.class)
                .where(StepData_Table.time.greaterThanOrEq(time),StepData_Table.time.lessThan(time*7*24*60*60))
                .queryList();
        return sqlData;
    }

    /**
     * 取计步月报告
     * */
    public List<StepData> getStepWithMonth(long time, long endTime){
        List<StepData> sqlData = SQLite.select()
                .from(StepData.class)
                .where(StepData_Table.time.greaterThanOrEq(time),StepData_Table.time.lessThan(endTime))
                .queryList();
        return sqlData;
    }

    /**
     * 取睡眠日报告
     * */
    public SleepData getSleepWithDay(long time){

        SleepData sleepData = SQLite.select()
                .from(SleepData.class)
                .where(SleepData_Table.time.is(time))
                .querySingle();


        return sleepData;
    }

    /**
     * 取睡眠周报告
     * */
    public List<SleepData> getSleepWithWeek(long time){

        List<SleepData> sleepData = SQLite.select()
                .from(SleepData.class)
                .where(SleepData_Table.time.greaterThanOrEq(time),SleepData_Table.time.lessThan(time+7*24*60*60))
                .queryList();

        return sleepData;
    }

    /**
     * 取睡眠月报告
     * */
    public List<SleepData> getSleepWithMonth(long time,long endTime){

        List<SleepData> sleepData = SQLite.select()
                .from(SleepData.class)
                .where(SleepData_Table.time.greaterThanOrEq(time),SleepData_Table.time.lessThan(endTime))
                .queryList();

        return sleepData;
    }

    /**
     * 取血氧日报告
     * */
    public List<BloodOxygenData> getBloodOxygenWithDay(long time){
        //绘图数据-70传入控件
        List<BloodOxygenData> list = SQLite.select()
                .from(BloodOxygenData.class)
                .where(BloodOxygenData_Table.time.lessThan(time+24*60*60-1),
                        BloodOxygenData_Table.time.greaterThanOrEq(time))
//                .orderBy(OrderBy.fromString(BloodPressureData_Table.time+OrderBy.DESCENDING))
                .queryList();
        return list;
    }

    /**
     * 取血压日报告
     * */
    public List<BloodPressureData> getBloodPressureWithDay(long time){
        //绘图数据-45传入控件

        List<BloodPressureData> list = SQLite.select()
                .from(BloodPressureData.class)
                .where(BloodPressureData_Table.time.lessThan(time+24*60*60-1),
                        BloodPressureData_Table.time.greaterThanOrEq(time))
                .queryList();

        return list;
    }

    /**
     * 取体温日报告
     * */
    public List<AnimalHeatData> getAnimalHeatWithDay(long time){
        //绘图数据-70传入控件
        List<AnimalHeatData> list = SQLite.select()
                .from(AnimalHeatData.class)
                .where(AnimalHeatData_Table.time.lessThan(time+24*60*60-1),
                        AnimalHeatData_Table.time.greaterThanOrEq(time))
                .queryList();

        return list;
    }

}
