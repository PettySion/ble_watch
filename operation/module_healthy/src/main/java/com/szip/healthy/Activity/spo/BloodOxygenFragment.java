package com.szip.healthy.Activity.spo;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.szip.blewatch.base.Const.HealthyConst;
import com.szip.blewatch.base.Const.ReportConst;
import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.View.BaseLazyLoadingFragment;
import com.szip.healthy.Activity.reportInfo.ReportInfoActivity;
import com.szip.healthy.Adapter.ReportInfoAdapter;
import com.szip.healthy.Model.ReportData;
import com.szip.blewatch.base.Model.ReportInfoData;
import com.szip.healthy.R;
import com.szip.healthy.View.ReportTableView;

import java.util.List;

public class BloodOxygenFragment extends BaseLazyLoadingFragment implements IBloodOxygenView{

    private int type;
    private ReportTableView tableView;
    private TextView reportTypeTv,averageTv,maxTv,minTv,smallTv,measureTv;
    private RecyclerView infoList;

    private IBloodOxygenPresenter iBloodOxygenPresenter;

    public BloodOxygenFragment(int type) {
        this.type = type;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.healthy_fragment_oxygen_temp;
    }

    @Override
    protected void initView(View root) {
        ((TextView)root.findViewById(R.id.abnormalTv)).setText(getString(R.string.healthy_spo_abnormal));
        ((TextView)root.findViewById(R.id.averageTv)).setText(getString(R.string.healthy_spo_average));
        ((TextView)root.findViewById(R.id.maxTv)).setText(getString(R.string.healthy_spo_max));
        ((TextView)root.findViewById(R.id.minTv)).setText(getString(R.string.healthy_spo_min));
        tableView = root.findViewById(R.id.tableView);
        reportTypeTv = root.findViewById(R.id.reportTypeTv);
        averageTv = root.findViewById(R.id.averageDataTv);
        maxTv = root.findViewById(R.id.maxDataTv);
        minTv = root.findViewById(R.id.minDataTv);
        smallTv = root.findViewById(R.id.smallTv);
        measureTv = root.findViewById(R.id.measureTv);
        infoList = root.findViewById(R.id.infoList);

        root.findViewById(R.id.listLl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportInfoActivity.class);
                intent.putExtra("type", HealthyConst.BLOOD_OXYGEN);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        if (type == ReportConst.REPORT_DAY)
            iBloodOxygenPresenter = new BloodOxygenDayPresenterImpl(getActivity().getApplicationContext(),this);
        else if (type == ReportConst.REPORT_WEEK)
            iBloodOxygenPresenter = new BloodOxygenWeekPresenterImpl(getActivity().getApplicationContext(),this);
        else
            iBloodOxygenPresenter = new BloodOxygenMonthPresenterImpl(getActivity().getApplicationContext(),this);
        iBloodOxygenPresenter.loadData(DateUtil.getTimeOfToday());
    }

    @Override
    protected void onFragmentResume() {
        super.onFragmentResume();
        iBloodOxygenPresenter.register(this);
    }

    @Override
    protected void onFragmentPause() {
        super.onFragmentPause();
        iBloodOxygenPresenter.unRegister();
    }

    @Override
    public void updateTable(List<ReportData> reportData) {
        if (type == ReportConst.REPORT_DAY){
            tableView.setOxygenDayList(reportData);
        } else if (type == ReportConst.REPORT_WEEK) {
            tableView.setOxygenWeekList(reportData);
        } else {
            tableView.setOxygenMonthList(reportData);
        }

    }

    @Override
    public void updateView(String average, String max, String min, String abnormal) {
        if (type == ReportConst.REPORT_DAY){
            reportTypeTv.setText(getString(R.string.healthy_survey));
        } else if (type == ReportConst.REPORT_WEEK) {
            reportTypeTv.setText(getString(R.string.healthy_week_survey));
        } else {
            reportTypeTv.setText(getString(R.string.healthy_month_survey));
        }
        averageTv.setText(average);
        maxTv.setText(max);
        minTv.setText(min);
        smallTv.setText(abnormal);
    }

    @Override
    public void updateList(List<ReportInfoData> bloodOxygenDataList) {
        infoList.setVisibility(View.VISIBLE);
        infoList.setLayoutManager(new LinearLayoutManager(getContext()));
        infoList.setHasFixedSize(true);
        infoList.setNestedScrollingEnabled(false);
        ReportInfoAdapter reportInfoAdapter = new ReportInfoAdapter(bloodOxygenDataList);
        infoList.setAdapter(reportInfoAdapter);
    }
}
