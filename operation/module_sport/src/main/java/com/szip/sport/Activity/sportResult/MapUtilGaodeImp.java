package com.szip.sport.Activity.sportResult;

import android.graphics.Color;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.sport.R;

import java.util.ArrayList;
import java.util.List;

public class MapUtilGaodeImp implements IMapUtil {
    private MapView mapView;
    private AMap aMap;
    double [] option;
    List<LatLng> latLngs = new ArrayList<LatLng>();
    public MapUtilGaodeImp(MapView mapView) {
        this.mapView = mapView;
        aMap = mapView.getMap();
    }

    @Override
    public void setLatlng(String[] lats, String[] lngs) {
        option = MathUtil.newInstance().getMapOption(lats,lngs);
        for (int a = 1;a<lats.length;a++){
            latLngs.add(new LatLng((Integer.valueOf(lats[0])+Integer.valueOf(lats[a]))/1000000.0,
                    (Integer.valueOf(lngs[0])+Integer.valueOf(lngs[a]))/1000000.0));
        }
    }

    @Override
    public void moveCamera() {
        LatLng centerBJPoint= new LatLng(option[0],
                option[1]);
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(centerBJPoint,
                (float) option[2], 0, 0)));
    }

    @Override
    public void addMarker() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngs.get(0));
        markerOptions.icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_start));
        aMap.addMarker(markerOptions);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(latLngs.get(latLngs.size()-1));
        markerOptions1.icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_destination));
        aMap.addMarker(markerOptions1);
    }

    @Override
    public void addPolyline() {
        aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).
                width(30).
                useGradient(true).
                color(Color.parseColor("#bbf246")));

        aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).
                width(12).
                useGradient(true).
                color(Color.parseColor("#000000")));
    }

    @Override
    public void onResume() {
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
//        mapView.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
    }

}