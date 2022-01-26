package com.szip.healthy.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.szip.blewatch.base.Util.BroadcastConst;
import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.BaseFragment;
import com.szip.blewatch.base.broadcast.MyHandle;
import com.szip.blewatch.base.broadcast.ToActivityBroadcast;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.blewatch.base.db.dbModel.UserModel;
import com.szip.healthy.Adapter.HealthyCardAdapter;
import com.szip.healthy.R;
import com.szip.healthy.model.HealthyData;
import com.szip.healthy.activity.sport.SportListActivity;
import com.szip.healthy.view.ColorArcProgressBar;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.szip.blewatch.base.Util.MathUtil.FILE;


/**
 * Created by Administrator on 2019/12/1.
 */

public class HealthyFragment extends BaseFragment implements MyHandle,IHealthyView, View.OnClickListener {


    private ToActivityBroadcast toActivityBroadcast;
    private IHealthyPresenter iHealthyPresenter;

    private TextView stepTv,caloriesTv,distanceTv,sportDataTv,sportTypeTv,editTv;

    private RecyclerView cardRecyclerView;
    private HealthyCardAdapter healthyCardAdapter;

    private ColorArcProgressBar colorArcProgressBar;


    @Override
    protected int getLayoutId() {
        return R.layout.healthy_fragment_healthy;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState){
        iHealthyPresenter = new HealthyPresenterImpl(getActivity().getApplicationContext(),this);
        initView();
        initEvent();
        initWeather();
        iHealthyPresenter.initStepData();
        iHealthyPresenter.initLastSport();
        iHealthyPresenter.initHealthyCard();
    }

    private void initView() {
        setTitle(getString(R.string.healthy_my_state));
        colorArcProgressBar = getView().findViewById(R.id.healthyStateView);
        stepTv = getView().findViewById(R.id.stepTv);
        caloriesTv = getView().findViewById(R.id.caloriesTv);
        distanceTv = getView().findViewById(R.id.distanceTv);
        sportDataTv = getView().findViewById(R.id.sportDataTv);
        sportTypeTv = getView().findViewById(R.id.sportTypeTv);
        editTv = getView().findViewById(R.id.editTv);
        cardRecyclerView = getView().findViewById(R.id.cardRecyclerView);
        cardRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        cardRecyclerView.setHasFixedSize(true);
        cardRecyclerView.setNestedScrollingEnabled(false);
        healthyCardAdapter = new HealthyCardAdapter(getActivity().getApplicationContext());
        cardRecyclerView.setAdapter(healthyCardAdapter);

        UserModel userModel = LoadDataUtil.newInstance().getUserInfo(MathUtil.newInstance().getUserId(getActivity().getApplicationContext()));
        if (userModel!=null)
            colorArcProgressBar.setMaxValues(userModel.stepsPlan);
    }

    private void initEvent() {
        getView().findViewById(R.id.moreTv).setOnClickListener(this);
        getView().findViewById(R.id.lastSportLl).setOnClickListener(this);
        editTv.setOnClickListener(this);
    }

    private void initWeather() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(FILE,MODE_PRIVATE);
        long time = sharedPreferences.getLong("weatherTime",0);
        if (Calendar.getInstance().getTimeInMillis()-time>60*60*1000){
            LogUtil.getInstance().logd("LOCATION******","开始定位");
            iHealthyPresenter.initWeather((LocationManager) getActivity().getSystemService(LOCATION_SERVICE));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(toActivityBroadcast==null)
        toActivityBroadcast = new ToActivityBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConst.UPDATE_SPORT_VIEW);
        intentFilter.addAction(BroadcastConst.UPDATE_STEP_VIEW);
        intentFilter.addAction(BroadcastConst.UPDATE_HEALTHY_VIEW);
        toActivityBroadcast.registerReceive(this,getActivity(),intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        toActivityBroadcast.unregister(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onReceive(Intent intent) {
        switch (intent.getAction()){
            case BroadcastConst.UPDATE_SPORT_VIEW:
                if (iHealthyPresenter!=null)
                    iHealthyPresenter.initLastSport();
                break;
            case BroadcastConst.UPDATE_STEP_VIEW:
                if (iHealthyPresenter!=null)
                    iHealthyPresenter.initStepData();
                break;
            case BroadcastConst.UPDATE_HEALTHY_VIEW:

                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.moreTv){
            startActivity(new Intent(getActivity(),SportListActivity.class));
        }else if (id == R.id.lastSportLl){

        }
    }

    @Override
    public void updateSportData(int step, int calorie, int distance) {
        String stepStr = String.format("%d steps",step);
        String calorieStr = String.format(Locale.ENGLISH,"%.1f kcal",((calorie+55)/100)/10f);
        String distanceStr = "";
        UserModel userModel = LoadDataUtil.newInstance().getUserInfo(MathUtil.newInstance().getUserId(getActivity().getApplicationContext()));
        if (userModel.unit == 0){
            distanceStr = String.format(Locale.ENGLISH,"%.2f km",(distance+55)/100/100f);
        }else {
            distanceStr = String.format(Locale.ENGLISH,"%.2f mile",MathUtil.newInstance().km2Miles(distance/10));
        }
        stepTv.setText(stepStr);
        caloriesTv.setText(calorieStr);
        distanceTv.setText(distanceStr);
        colorArcProgressBar.setCurrentValues(step,distance/10,calorie);
    }

    @Override
    public void updateLastSport(SportData sportData) {
        sportDataTv.setText(String.format(Locale.ENGLISH,
                "%.2f km,%02d:%02d:%02d,%.1f kcal",(sportData.distance+55)/100/100f,
                sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60,
                ((sportData.calorie+55)/100)/10f));

        sportTypeTv.setText(getActivity().getString(R.string.healthy_last_sport)+sportData.type+","+
                DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
    }

    @Override
    public void updateHealthyCard(List<HealthyData> healthyDataList) {
        LogUtil.getInstance().logd("data******","size = "+healthyDataList.size());
        if (healthyDataList.size()%2 == 0)
            editTv.setVisibility(View.VISIBLE);
        else
            editTv.setVisibility(View.GONE);

        healthyCardAdapter.setHealthyDataList(healthyDataList);
    }
}
