package com.szip.healthy.Fragment;

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
import com.szip.healthy.HttpModel.WeatherBean;
import com.szip.healthy.Model.HealthyData;
import com.szip.healthy.Utils.HttpMessageUtil;
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

        StepData stepData = LoadDataUtil.newInstance().getStepWithDay(DateUtil.getTimeOfToday());
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
                List<HeartData> heartDatas = LoadDataUtil.newInstance().getHeartWithDay(DateUtil.getTimeOfToday());
                if (heartDatas!=null&&heartDatas.size()!=0){
                    healthyData.setTime(heartDatas.get(heartDatas.size()-1).time);
                    healthyData.setData(heartDatas.get(heartDatas.size()-1).averageHeart);
                    healthyData.setHeartDataList(heartDatas);
                }
                healthyData.setTime(1644315680);
                healthyData.setData(89);
                heartDatas.add(new HeartData(1644250820,58));
                heartDatas.add(new HeartData(1644254420,96));
                heartDatas.add(new HeartData(1644258020,74));
                heartDatas.add(new HeartData(1644261620,97));
                heartDatas.add(new HeartData(1644265220,92));
                heartDatas.add(new HeartData(1644268820,58));
                heartDatas.add(new HeartData(1644272420,96));
                heartDatas.add(new HeartData(1644276020,74));
                heartDatas.add(new HeartData(1644279620,97));
                heartDatas.add(new HeartData(1644283220,92));
                heartDatas.add(new HeartData(1644286820,58));
                heartDatas.add(new HeartData(1644308420,96));
                heartDatas.add(new HeartData(1644312020,74));
                heartDatas.add(new HeartData(1644315620,97));
                heartDatas.add(new HeartData(1644315680,92));
                healthyData.setHeartDataList(heartDatas);
            }
                break;
            case 2: {
                StepData stepData = LoadDataUtil.newInstance().getStepWithDay(DateUtil.getTimeOfToday());
                if (stepData!=null){
                    healthyData.setDataStr(stepData.dataForHour);
                    healthyData.setTime(stepData.time);
                    healthyData.setData(stepData.steps);
                }
                healthyData.setTime(1644315680);
                healthyData.setData(1668);
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
                healthyData.setTime(1644315680);
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
                healthyData.setTime(1644315680);
                healthyData.setData(89);
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
                healthyData.setTime(1644315680);
                healthyData.setData(89);
                healthyData.setData1(150);
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
                healthyData.setTime(1644315680);
                healthyData.setData(368);
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
