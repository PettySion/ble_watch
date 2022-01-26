package com.szip.healthy.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.Util.LocationUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.db.dbModel.AnimalHeatData;
import com.szip.blewatch.base.db.dbModel.BloodOxygenData;
import com.szip.blewatch.base.db.dbModel.BloodPressureData;
import com.szip.blewatch.base.db.dbModel.HealthyCardData;
import com.szip.blewatch.base.db.dbModel.HeartData;
import com.szip.blewatch.base.db.dbModel.SleepData;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.blewatch.base.db.dbModel.StepData;
import com.szip.blewatch.base.db.dbModel.Weather;
import com.szip.healthy.httpModel.WeatherBean;
import com.szip.healthy.model.HealthyData;
import com.szip.healthy.utils.HttpMessageUtil;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.JsonGenericsSerializator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

public class HealthyPresenterImpl implements IHealthyPresenter{

    private Context mContext;
    private IHealthyView iHealthyView;
    private LocationManager locationManager;
    public HealthyPresenterImpl(Context mContext, IHealthyView iHealthyView) {
        this.mContext = mContext;
        this.iHealthyView = iHealthyView;
    }

    @Override
    public void initWeather(LocationManager locationManager) {
        this.locationManager = locationManager;
        LocationUtil.getInstance().getLocation(locationManager,true,myListener,locationListener,mContext);
    }

    @Override
    public void initStepData() {

        StepData stepData = LoadDataUtil.newInstance().getStepData(DateUtil.getTimeOfToday());
        if (iHealthyView!=null&&stepData!=null){
            if (iHealthyView!=null)
                iHealthyView.updateSportData(stepData.steps,stepData.calorie,stepData.distance);
        }
    }

    @Override
    public void initLastSport() {
        SportData sportData = LoadDataUtil.newInstance().getLastSportData();
        if (iHealthyView!=null&&sportData!=null){
            iHealthyView.updateLastSport(sportData);
        }
    }

    @Override
    public void initHealthyCard() {
        List<HealthyCardData> list = LoadDataUtil.newInstance().getHealthyCard(true);
        List<HealthyData> dataList = new ArrayList<>();
        Collections.sort(list);
        if (list!=null){
            for (int i = 0;i<list.size();i++){
                HealthyData data = loadData(list.get(i).type);
                dataList.add(data);
            }
        }
        if (iHealthyView!=null&&dataList.size()!=0){
            iHealthyView.updateHealthyCard(dataList);
        }
    }


    private GpsStatus.Listener myListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int i) {

        }
    };

    //监听GPS位置改变后得到新的经纬度
    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location != null) {
                //获取国家，省份，城市的名称
                Log.e("LOCATION******", location.toString());
                HttpMessageUtil.newInstance().getWeather(location.getLatitude()+"", location.getLongitude()+"",
                        new GenericsCallback<WeatherBean>(new JsonGenericsSerializator()) {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.d("DATA******","error = "+e.getMessage());
                            }

                            @Override
                            public void onResponse(final WeatherBean response, int id) {
                                if (response.getCode()==200){
                                    Gson gson=new Gson();
                                    Weather weather = new Weather();
                                    weather.city = response.getData().getLocation().getCity();
                                    weather.elevation = response.getData().getLocation().getElevation();
                                    weather.weatherList = gson.toJson(response.getData().getForecasts());
                                    weather.id = MathUtil.newInstance().getUserId(mContext);
                                    SaveDataUtil.newInstance().saveWeatherData(weather);
                                    MathUtil.newInstance().saveIntData(mContext,"weatherTime",
                                            Calendar.getInstance().getTimeInMillis());
                                    Intent intent = new Intent(BroadcastConst.SEND_BLE_DATA);
                                    intent.putExtra("command","setWeather");
                                    mContext.sendBroadcast(intent);
                                }
                            }
                        });
                locationManager.removeUpdates(locationListener);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private HealthyData loadData(int type){
        HealthyData healthyData = new HealthyData(type);
        switch (type){
            case 1:{
                HeartData heartData = LoadDataUtil.newInstance().getHeartWithDay(DateUtil.getTimeOfToday());
                if (heartData!=null){
                    healthyData.setDataStr(heartData.heartArray);
                    healthyData.setTime(heartData.time);
                    String hearts[] = heartData.heartArray.split(",");
                    if (hearts.length!=0)
                        healthyData.setData(Integer.valueOf(hearts[hearts.length-1]));
                }
                healthyData.setDataStr("50,70,70,80,100,70,130,120,110,140,60");
            }
                break;
            case 2: {
                StepData stepData = LoadDataUtil.newInstance().getStepWithDay(DateUtil.getTimeOfToday());
                if (stepData!=null){
                    healthyData.setDataStr(stepData.dataForHour);
                    healthyData.setTime(stepData.time);
                    healthyData.setData(stepData.steps);
                }
                healthyData.setDataStr("0:150,1:260,2:170,3:380,4:290,5:1100,6:120,7:1120,8:2130,9:180,23:250");
            }
                break;
            case 3:{
                SleepData sleepData = LoadDataUtil.newInstance().getSleepWithDay(DateUtil.getTimeOfToday());
                if (sleepData!=null){
                    healthyData.setDataStr(sleepData.dataForHour);
                    healthyData.setTime(sleepData.time);
                    healthyData.setData(sleepData.deepTime);
                    healthyData.setData1(sleepData.lightTime);
                }
                healthyData.setDataStr("22:10,10:1");
                healthyData.setData(60);
                healthyData.setData1(100);
            }
                break;
            case 4:
                break;
            case 5:{
                List<BloodOxygenData> bloodOxygenDatas = LoadDataUtil.newInstance().getBloodOxygenWithDay(DateUtil.getTimeOfToday());
                if (bloodOxygenDatas!=null&&bloodOxygenDatas.size()!=0){
                    healthyData.setTime(bloodOxygenDatas.get(bloodOxygenDatas.size()-1).time);
                    healthyData.setData(bloodOxygenDatas.get(bloodOxygenDatas.size()-1).bloodOxygenData);
                    healthyData.setBloodOxygenDataList(bloodOxygenDatas);
                }
                bloodOxygenDatas.add(new BloodOxygenData(1642648391,98));
                bloodOxygenDatas.add(new BloodOxygenData(1642651991,96));
                bloodOxygenDatas.add(new BloodOxygenData(1642654211,94));
                bloodOxygenDatas.add(new BloodOxygenData(1642657811,97));
                bloodOxygenDatas.add(new BloodOxygenData(1642665011,92));
                healthyData.setBloodOxygenDataList(bloodOxygenDatas);
            }
                break;
            case 6:{
                List<BloodPressureData> bloodPressureData = LoadDataUtil.newInstance().getBloodPressureWithDay(DateUtil.getTimeOfToday());
                if (bloodPressureData!=null&&bloodPressureData.size()!=0){
                    healthyData.setTime(bloodPressureData.get(bloodPressureData.size()-1).time);
                    healthyData.setData(bloodPressureData.get(bloodPressureData.size()-1).dbpDate);
                    healthyData.setData1(bloodPressureData.get(bloodPressureData.size()-1).sbpDate);
                    healthyData.setBloodPressureDataList(bloodPressureData);
                }
                bloodPressureData.add(new BloodPressureData(1642648391,150,98));
                bloodPressureData.add(new BloodPressureData(1642651991,140,96));
                bloodPressureData.add(new BloodPressureData(1642654211,160,94));
                bloodPressureData.add(new BloodPressureData(1642657811,154,97));
                bloodPressureData.add(new BloodPressureData(1642665011,135,92));
                healthyData.setBloodPressureDataList(bloodPressureData);
            }
                break;
            case 7:{
                List<AnimalHeatData> animalHeatData = LoadDataUtil.newInstance().getAnimalHeatWithDay(DateUtil.getTimeOfToday());
                if (animalHeatData!=null&&animalHeatData.size()!=0){
                    healthyData.setTime(animalHeatData.get(animalHeatData.size()-1).time);
                    healthyData.setData(animalHeatData.get(animalHeatData.size()-1).tempData);
                    healthyData.setAnimalHeatDataList(animalHeatData);
                }
                animalHeatData.add(new AnimalHeatData(1642648391,368));
                animalHeatData.add(new AnimalHeatData(1642651991,356));
                animalHeatData.add(new AnimalHeatData(1642654211,364));
                animalHeatData.add(new AnimalHeatData(1642657811,357));
                animalHeatData.add(new AnimalHeatData(1642665011,372));
                healthyData.setAnimalHeatDataList(animalHeatData);
            }
                break;
        }
        return healthyData;
    }

}
