package com.szip.sport.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Const.SportConst;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.BaseFragment;
import com.szip.blewatch.base.View.CircularImageView;
import com.szip.blewatch.base.View.MyAlerDialog;
import com.szip.blewatch.base.broadcast.MyHandle;
import com.szip.blewatch.base.broadcast.ToActivityBroadcast;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.sport.R;
import com.szip.sport.adapter.LastSportAdapter;
import com.szip.sport.gpsSport.GpsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/12/1.
 */

public class SportFragment extends BaseFragment implements View.OnClickListener, MyHandle,ILastSportView {
    private TextView runTv,walkTv,runIndoorTv;
    private ImageView gpsIv,startIv;
    private CircularImageView mapIv;
    private RecyclerView lastSportListView;
    private LastSportAdapter lastSportAdapter;

    private int sportType = SportConst.RUN; //2:跑步运动 1:徒步运动
    private ToActivityBroadcast toActivityBroadcast;
    private ILastSportPresenter iLastSportPresenter;
    private List<SportData> sportDataList = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.sport_fragment_sport;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        checkPermission();
        initView();
        initEvent();
        iLastSportPresenter = new LastSportImpl(this,getActivity());
        iLastSportPresenter.initLastSport();
        iLastSportPresenter.initLocation((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(toActivityBroadcast==null)
            toActivityBroadcast = new ToActivityBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConst.UPDATE_SPORT_VIEW);
        toActivityBroadcast.registerReceive(this,getActivity(),intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (toActivityBroadcast!=null)
            toActivityBroadcast.unregister(getActivity());
    }

    /**
     * 获取权限
     * */
    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                        ||getActivity().checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                    MyAlerDialog.getSingle().showAlerDialog(getString(R.string.permission_tip), getString(R.string.sport_permission),
                            getString(R.string.agree), getString(R.string.disagree), false, new MyAlerDialog.AlerDialogOnclickListener() {
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
                    MyAlerDialog.getSingle().showAlerDialog(getString(R.string.permission_tip), getString(R.string.sport_permission),
                            getString(R.string.agree), getString(R.string.disagree),false, new MyAlerDialog.AlerDialogOnclickListener() {
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
        startIv.setOnClickListener(this);
        runIndoorTv.setOnClickListener(this);
    }

    private void initView() {
        setTitle(getString(R.string.sport_sport_last));
        gpsIv = getView().findViewById(R.id.gpsIv);
        mapIv = getView().findViewById(R.id.mapIv);
        runTv = getView().findViewById(R.id.runTv);
        walkTv = getView().findViewById(R.id.walkTv);
        startIv = getView().findViewById(R.id.startIv);
        runIndoorTv = getView().findViewById(R.id.runIndoorTv);

        lastSportListView = getView().findViewById(R.id.lastSportListView);
        lastSportListView.setHasFixedSize(true);
        lastSportListView.setNestedScrollingEnabled(false);
        lastSportAdapter = new LastSportAdapter(getActivity().getApplicationContext());
        lastSportListView.setAdapter(lastSportAdapter);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.runTv) {
            sportType = SportConst.RUN;
            runTv.setTextColor(Color.BLACK);
            walkTv.setTextColor(getView().getResources().getColor(R.color.sport_gray_text));
            runIndoorTv.setTextColor(getView().getResources().getColor(R.color.sport_gray_text));
        } else if (id == R.id.walkTv) {
            sportType = SportConst.WALK;
            runTv.setTextColor(getView().getResources().getColor(R.color.sport_gray_text));
            walkTv.setTextColor(Color.BLACK);
            runIndoorTv.setTextColor(getView().getResources().getColor(R.color.sport_gray_text));
        } else if (id == R.id.runIndoorTv) {
            sportType = SportConst.RUN_INDOOR;
            runTv.setTextColor(getView().getResources().getColor(R.color.sport_gray_text));
            walkTv.setTextColor(getView().getResources().getColor(R.color.sport_gray_text));
            runIndoorTv.setTextColor(Color.BLACK);
        } else if (id == R.id.startIv) {
            if (!MathUtil.newInstance().needLogin(getActivity())){
                Intent intent = new Intent(getActivity(), GpsActivity.class);
                intent.putExtra("sportType", sportType);
                startActivity(intent);
            }
        }
    }

    @Override
    public void updateLastSport(List<SportData> sportDataList) {
        this.sportDataList = sportDataList;
        if (lastSportAdapter!=null&&sportDataList.size()!=0){
            getView().findViewById(R.id.tipTv).setVisibility(View.GONE);
            lastSportListView.setLayoutManager(new GridLayoutManager(getContext(), sportDataList.size()));
            lastSportAdapter.setList(sportDataList);
        }else {
            getView().findViewById(R.id.tipTv).setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void updateLocation(String url, float acc) {
        if (acc == 0){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_0);
        }else if (acc>=29){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_1);
        }else if (acc>=15){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_2);
        }else {
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_3);
        }

        Glide.with(getActivity()).load(url).into(mapIv);
    }

    @Override
    public void onReceive(Intent intent) {
        switch (intent.getAction()){
            case BroadcastConst.UPDATE_SPORT_VIEW:
                if (iLastSportPresenter!=null)
                    iLastSportPresenter.initLastSport();
                break;
        }
    }


}
