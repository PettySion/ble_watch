package com.szip.sport.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.LocationUtil;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.MyAlerDialog;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.blewatch.base.db.dbModel.Weather;
import com.szip.sport.R;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.JsonGenericsSerializator;

import java.util.Calendar;
import java.util.List;

import okhttp3.Call;

public class LastSportImpl implements ILastSportPresenter{

    private ILastSportView iLastSportView;
    private LocationManager locationManager;
    private Context mContext;

    public LastSportImpl(ILastSportView iLastSportView, Context mContext) {
        this.iLastSportView = iLastSportView;
        this.mContext = mContext;
    }

    @Override
    public void initLastSport() {
        List<SportData> sportDataList = LoadDataUtil.newInstance().getLastSportDataList();
        if (iLastSportView!=null){
            iLastSportView.updateLastSport(sportDataList);
        }
    }

    @Override
    public void initLocation(LocationManager locationManager) {
        this.locationManager = locationManager;
        Location state = LocationUtil.getInstance().getLocation(locationManager,true,myListener,locationListener,mContext);
        LogUtil.getInstance().logd("data******","state = "+state);
        if (state == null){
            String url = String.format("https://restapi.amap.com/v3/staticmap?location=%f,%f&zoom=%d&" +
                            "size=%d*%d&markers=mid,,A:%f,%f&key=%s",116.397477,39.908692,18
                    ,500,500,116.397477,39.908692, "d7f783d2eb0a38000c900e7f836ee1a0");
            float acc = 0;
            if (iLastSportView!=null)
                iLastSportView.updateLocation(url,acc);
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
                location = LocationUtil.getInstance().getGaoLocation(location,mContext);
                String url = String.format("https://restapi.amap.com/v3/staticmap?location=%f,%f&zoom=%d&" +
                                "size=%d*%d&markers=mid,,A:%f,%f&key=%s",location.getLongitude(),location.getLatitude(),18
                        ,500,500,location.getLongitude(),location.getLatitude(), "d7f783d2eb0a38000c900e7f836ee1a0");
                float acc = location.getAccuracy();
                if (iLastSportView!=null)
                    iLastSportView.updateLocation(url,acc);
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
}
