package com.szip.healthy.ModuleMain;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Interfere.OnItemClickListener;
import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.BaseFragment;
import com.szip.blewatch.base.Broadcast.MyHandle;
import com.szip.blewatch.base.Broadcast.ToActivityBroadcast;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.blewatch.base.db.dbModel.UserModel;
import com.szip.blewatch.base.Model.SportTypeModel;
import com.szip.healthy.Activity.card.CardEditActivity;
import com.szip.healthy.Adapter.HealthyCardAdapter;
import com.szip.healthy.R;
import com.szip.healthy.Model.HealthyData;
import com.szip.healthy.Activity.sport.SportListActivity;
import com.szip.healthy.View.ColorArcProgressBar;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.szip.blewatch.base.Const.RouterPathConst.PATH_ACTIVITY_REPORT;
import static com.szip.blewatch.base.Const.RouterPathConst.PATH_ACTIVITY_SPORT_RESULT;
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

    private SportData sportData;
    private List<HealthyData> healthyDataList;


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
        healthyCardAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (!MathUtil.newInstance().needLogin(getActivity())) {
                    if (healthyDataList.size()%2!=0&&position == healthyDataList.size()){
                        startActivity(new Intent(getActivity(),CardEditActivity.class));
                    }else {
                        ARouter.getInstance().build(PATH_ACTIVITY_REPORT+healthyDataList.get(position).getType())
                                .navigation();
                    }
                }
            }
        });
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
        iHealthyPresenter.initStepData();
        iHealthyPresenter.initLastSport();
        iHealthyPresenter.initHealthyCard();
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
            if (!MathUtil.newInstance().needLogin(getActivity())){
                startActivity(new Intent(getActivity(),SportListActivity.class));
            }
        }else if (id == R.id.lastSportLl){
            if (!MathUtil.newInstance().needLogin(getActivity())){
                if (sportData!=null){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sportData",sportData);
                    ARouter.getInstance().build(PATH_ACTIVITY_SPORT_RESULT)
                            .withBundle("bundle",bundle)
                            .navigation();
                }
            }
        }else if (id == R.id.editTv){
            startActivity(new Intent(getActivity(), CardEditActivity.class));
        }
    }

    @Override
    public void updateSportData(int step, int calorie, int distance) {
        UserModel userModel = LoadDataUtil.newInstance().getUserInfo(MathUtil.newInstance().getUserId(getActivity().getApplicationContext()));
        if (userModel==null)
            return;
        String stepStr = String.format("<big>%d</big> steps",step);
        String calorieStr = String.format(Locale.ENGLISH,"<big>%.1f</big> kcal",((calorie+55)/100)/10f);
        String distanceStr = "";
        if (userModel.unit == 0){
            distanceStr = String.format(Locale.ENGLISH,"<big>%.2f</big> km",(distance+55)/100/100f);
        }else {
            distanceStr = String.format(Locale.ENGLISH,"<big>%.2f</big> mile",MathUtil.newInstance().km2Miles(distance/10));
        }
        stepTv.setText(Html.fromHtml(stepStr));
        caloriesTv.setText(Html.fromHtml(calorieStr));
        distanceTv.setText(Html.fromHtml(distanceStr));
        colorArcProgressBar.setCurrentValues(step,distance/10,calorie);
    }

    @Override
    public void updateLastSport(SportData sportData) {
        this.sportData = sportData;
        sportDataTv.setText(String.format(Locale.ENGLISH,
                "%.2fkm  %02dh%02dmin  %.1fkcal",(sportData.distance+5)/10/100f,
                sportData.sportTime/3600, sportData.sportTime%3600/60, ((sportData.calorie+55)/100)/10f));
        SportTypeModel sportTypeModel = MathUtil.newInstance().getSportType(sportData.type,getActivity().getApplicationContext());
        if (sportTypeModel==null)
            return;
        sportTypeTv.setText(getActivity().getString(R.string.healthy_last_sport)+":"+sportTypeModel.getSportStr()+","+
                DateUtil.getStringDateFromSecond(sportData.time,"yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void updateHealthyCard(List<HealthyData> healthyDataList) {
        this.healthyDataList = healthyDataList;
        if (healthyDataList.size()%2 == 0)
            editTv.setVisibility(View.VISIBLE);
        else
            editTv.setVisibility(View.GONE);

        healthyCardAdapter.setHealthyDataList(healthyDataList);
    }
}
