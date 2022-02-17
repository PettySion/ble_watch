package com.szip.healthy.Activity.sleep;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.View.BaseFragment;
import com.szip.blewatch.base.View.BaseLazyLoadingFragment;
import com.szip.healthy.Activity.step.StepDayPresenterImpl;
import com.szip.healthy.Model.ReportData;
import com.szip.healthy.R;
import com.szip.healthy.View.ReportTableView;
import com.szip.healthy.View.SleepArcProgressBar;

import java.util.List;
import java.util.Locale;

public class SleepDayFragment extends BaseLazyLoadingFragment implements ISleepReportView{

    private TextView deepTv,lightTv,bedTv,wakeTv,allSleepTv;
    private ReportTableView tableView;
    private SleepArcProgressBar sleepPb;
    private ISleepReportPresenter iSleepReportPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.healthy_fragment_sleep_day;
    }

    @Override
    protected void initView(View root) {
        deepTv = root.findViewById(R.id.deepTv);
        lightTv = root.findViewById(R.id.lightTv);
        bedTv = root.findViewById(R.id.bedTv);
        wakeTv = root.findViewById(R.id.wakeTv);
        allSleepTv = root.findViewById(R.id.allSleepTv);
        tableView = root.findViewById(R.id.tableView);
        sleepPb = root.findViewById(R.id.sleepPb);
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        iSleepReportPresenter = new SleepDayPresenterImpl(getActivity().getApplicationContext(),this);
        iSleepReportPresenter.loadSleep(DateUtil.getTimeOfToday());
    }

    @Override
    protected void onFragmentResume() {
        super.onFragmentResume();
        iSleepReportPresenter.register(this);
    }

    @Override
    protected void onFragmentPause() {
        super.onFragmentPause();
        iSleepReportPresenter.unRegister();
    }

    @Override
    public void updateTableView(List<ReportData> reportData, int startTime,int allTime) {
        tableView.setSleepDayList(reportData,startTime,allTime);
    }

    @Override
    public void updateProgressView(int startTime, int deepTime, int lightTime) {
        deepTv.setText(String.format(Locale.ENGLISH,"%dh%dmin",deepTime/60,deepTime%60));
        lightTv.setText(String.format(Locale.ENGLISH,"%dh%dmin",lightTime/60,lightTime%60));
        allSleepTv.setText(Html.fromHtml(String.format(Locale.ENGLISH,
                "<font color='#000000'><big><big>%d</big></big></font>h<font color='#000000'><big><big>%d</big></big></font>min",
                (lightTime+deepTime)/60,(lightTime+deepTime)%60)));
        bedTv.setText(String.format(Locale.ENGLISH,"%d:%d",startTime/60,startTime%60));
        wakeTv.setText(String.format(Locale.ENGLISH,"%d:%d",(startTime+deepTime+lightTime)%1440/60,
                (startTime+deepTime+lightTime)%1440%60));
        sleepPb.setCurrentValues(deepTime+lightTime,startTime);
    }

    @Override
    public void updateView(String allTime, String deepTime, String lightTime, String plan) {

    }
}
