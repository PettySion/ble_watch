package com.szip.sport.GpsSport;

import androidx.fragment.app.FragmentManager;

public interface IGpsPresenter {
    void startLocationService();
    void stopLocationService();
    void finishLocationService(boolean isSave);
    void openMap(FragmentManager fragmentManager);
    void setViewDestory();



}
