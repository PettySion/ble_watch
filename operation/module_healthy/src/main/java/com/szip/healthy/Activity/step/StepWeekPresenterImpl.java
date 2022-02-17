package com.szip.healthy.Activity.step;

import android.content.Context;

import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.dbModel.StepData;
import com.szip.blewatch.base.db.dbModel.UserModel;
import com.szip.healthy.Model.ReportData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StepWeekPresenterImpl implements IStepReportPresenter{

    private Context mContext;
    private IStepReportView iStepReportView;

    public StepWeekPresenterImpl(Context mContext, IStepReportView iStepReportView) {
        this.mContext = mContext;
        this.iStepReportView = iStepReportView;
    }

    @Override
    public void loadData(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time*1000);
        calendar.set(Calendar.DAY_OF_WEEK,1);
        time = calendar.getTimeInMillis()/1000;
        List<StepData> stepDataList = LoadDataUtil.newInstance().getStepWithWeek(time);
        if (stepDataList.size()==0)
            return;

        List<ReportData> reportDataList = new ArrayList<>();
        for (int i = 0;i<7;i++){
            reportDataList.add(new ReportData(0,time+i*24*60*60));
        }
        UserModel userModel = LoadDataUtil.newInstance().getUserInfo(MathUtil.newInstance().getUserId(mContext));
        int sum = 0;
        int allStep = 0;
        int allCalorie = 0;
        int allDistance = 0;
        int plan = 0;
        int max = 5;
        for (StepData stepData:stepDataList){
            int step = stepData.steps;
            long stepTime = stepData.time;
            ReportData reportData = new ReportData(step,stepTime);
            sum++;
            allStep+=stepData.steps;
            allDistance+=stepData.distance;
            allCalorie+=stepData.calorie;
            if (stepData.steps>max)
                max = stepData.steps;
            if (stepData.steps>userModel.stepsPlan)
                plan++;
            calendar.setTimeInMillis(stepTime*1000);
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            reportDataList.set(week-1,reportData);
        }

        if (iStepReportView!=null){
            iStepReportView.updateTableView(reportDataList,max);
            if (sum!=0){
                String step = String.format(Locale.ENGLISH,"%d steps",allStep/sum);
                String calorieStr = String.format(Locale.ENGLISH,"%.1f kcal",((allCalorie/sum+55)/100)/10f);
                String distanceStr = "";
                if (userModel.unit == 0){
                    distanceStr = String.format(Locale.ENGLISH,"%.2f km",(allDistance/sum+55)/100/100f);
                }else {
                    distanceStr = String.format(Locale.ENGLISH,"%.2f mile", MathUtil.newInstance().km2Miles(allDistance/sum/10));
                }
                String planStr = String.format(Locale.ENGLISH,"%.1f%%",plan/7f*100);
                iStepReportView.updateStepView(step,calorieStr,distanceStr,planStr);
            }
        }
    }

    @Override
    public void register(IStepReportView iStepReportView) {
        this.iStepReportView = iStepReportView;
    }

    @Override
    public void unregister() {
        this.iStepReportView = null;
    }
}
