package com.szip.healthy.fragment;

import android.location.LocationManager;

public interface IHealthyPresenter {
    void initWeather(LocationManager locationManager);
    void initStepData();
    void initLastSport();
    void initHealthyCard();
}
