package com.szip.sport.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.BaseFragment;
import com.szip.blewatch.base.View.MyAlerDialog;
import com.szip.sport.R;
import com.szip.sport.gpsSport.GpsActivity;

/**
 * Created by Administrator on 2019/12/1.
 */

public class SportFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout startRl;
    private TextView runTv,walkTv;
    private ImageView gpsIv;

    private int sportType = 2; //2:跑步运动 1:徒步运动

    @Override
    protected int getLayoutId() {
        return R.layout.sport_fragment_sport;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        checkPermission();
        initView();
        initEvent();
    }


    /**
     * 获取权限
     * */
    private void checkPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("data******","state = "+getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)+"Build.VERSION.SDK_INT = "+Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                        ||getActivity().checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                    MyAlerDialog.getSingle().showAlerDialog("权限提示", "运动需要请求权限", "同意",
                            "拒绝", false, new MyAlerDialog.AlerDialogOnclickListener() {
                                @Override
                                public void onDialogTouch(boolean flag) {
                                    if (flag){
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACTIVITY_RECOGNITION},
                                                102);
                                    }
                                }
                            }, getActivity());
                }
            }else {

                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    MyAlerDialog.getSingle().showAlerDialog("权限提示", "运动需要请求权限", "同意",
                            "拒绝",false, new MyAlerDialog.AlerDialogOnclickListener() {
                                @Override
                                public void onDialogTouch(boolean flag) {
                                    if (flag){
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                101);
                                    }
                                }
                            }, getActivity());
                }
            }

        }
    }

    private void initEvent() {
        runTv.setOnClickListener(this);
        walkTv.setOnClickListener(this);
        startRl.setOnClickListener(this);
    }

    private void initView() {
        gpsIv = getView().findViewById(R.id.gpsIv);
        runTv = getView().findViewById(R.id.runTv);
        walkTv = getView().findViewById(R.id.walkTv);
        startRl = getView().findViewById(R.id.startRl);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.runTv) {
            sportType = 2;
            runTv.setTextColor(Color.BLUE);
            walkTv.setTextColor(Color.GRAY);
        } else if (id == R.id.walkTv) {
            sportType = 1;
            runTv.setTextColor(Color.GRAY);
            walkTv.setTextColor(Color.BLUE);
        } else if (id == R.id.startRl) {//                if (MainService.getInstance().getState()!=3){

            if (!MathUtil.newInstance().needLogin(getActivity())){
                Intent intent = new Intent(getActivity(), GpsActivity.class);
                intent.putExtra("sportType", sportType);
                startActivity(intent);
            }

//                }else {
//                    if (MyApplication.getInstance().isSyncSport()){
//                        EXCDController.getInstance().writeForStartSport(sportType);
//                    }else {
//                        Intent intent = new Intent(getActivity(), GpsActivity.class);
//                        intent.putExtra("sportType",sportType);
//                        startActivity(intent);
//                    }
//                }
        }
    }
}
