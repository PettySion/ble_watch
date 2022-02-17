package com.szip.sport.Activity.sportResult;

import android.os.Bundle;

public interface IMapUtil {
    void setLatlng(String[] lats,String[] lngs);
    void moveCamera();
    void addMarker();
    void addPolyline();
    void onResume();
    void onDestroy();
    void onCreate(Bundle savedInstanceState);
}
