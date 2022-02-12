package com.szip.sport.SportResult;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.sport.R;

import java.util.ArrayList;
import java.util.List;

public class MapUtilGoogleImp implements IMapUtil {

    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private GoogleMap googleMap;
    private double [] option;
    public MapUtilGoogleImp(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void setLatlng(String[] lats, String[] lngs) {
        googleMap.getUiSettings().setZoomControlsEnabled(false);// 隐藏缩放按钮
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        option = MathUtil.newInstance().getMapOption(lats,lngs);
        for (int a = 1;a<lats.length;a++){
            latLngs.add(new LatLng((Integer.valueOf(lats[0])+Integer.valueOf(lats[a]))/1000000.0,
                    (Integer.valueOf(lngs[0])+Integer.valueOf(lngs[a]))/1000000.0));
        }
    }

    @Override
    public void moveCamera() {

        googleMap.moveCamera(CameraUpdateFactory.zoomTo((float) option[2]));
        LatLng centerBJPoint= new LatLng(option[0],
                option[1]);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(centerBJPoint));
    }

    @Override
    public void addMarker() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngs.get(0));
        markerOptions.icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_start));
        googleMap.addMarker(markerOptions);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(latLngs.get(latLngs.size()-1));
        markerOptions1.icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_destination));
        googleMap.addMarker(markerOptions1);
    }

    @Override
    public void addPolyline() {
        googleMap.addPolyline(new PolylineOptions().
                addAll(latLngs).
                width(30).
                color(Color.parseColor("#bbf246")));

        googleMap.addPolyline(new PolylineOptions().
                addAll(latLngs).
                width(12).
                color(Color.parseColor("#000000")));
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }


//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        this.googleMap = googleMap;
//    }
}
