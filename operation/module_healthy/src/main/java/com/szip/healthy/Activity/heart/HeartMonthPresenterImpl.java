package com.szip.healthy.Activity.heart;

import android.content.Context;

import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.dbModel.HeartData;
import com.szip.healthy.Model.ReportData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HeartMonthPresenterImpl implements IHeartPresenter{
    private Context mContext;
    private IHeartView iHeartView;

    public HeartMonthPresenterImpl(Context mContext, IHeartView iHeartView) {
        this.mContext = mContext;
        this.iHeartView = iHeartView;
    }

    @Override
    public void loadDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time*1000);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        time = calendar.getTimeInMillis()/1000;
        calendar.add(Calendar.MONTH,1);
        calendar.add(Calendar.HOUR_OF_DAY,-1);
        int dataSize = calendar.get(Calendar.DAY_OF_MONTH);
        List<ReportData> reportDataList = new ArrayList<>();
        int allMax = 0;
        for (int i = 0;i<dataSize;i++){
            List<HeartData> heartDataList = LoadDataUtil.newInstance().getHeartWithDay(time+i*24*60*60);
            if (heartDataList.size() == 0){
                reportDataList.add(new ReportData(0,0,0,time+i*24*60*60));
                continue;
            }
            int allHeart = 0;
            int sum = 0;
            int min = 0;
            int max = 0;
            for (int a = 0;a<heartDataList.size();a++){
                HeartData heartData = heartDataList.get(a);
                LogUtil.getInstance().logd("data******","heart = "+heartData.averageHeart+
                        " ;time = "+ DateUtil.getStringDateFromSecond(heartData.time,"yyyy/MM/dd HH:mm:ss"));
                sum++;
                allHeart+=heartData.averageHeart;
            }
            Collections.sort(heartDataList);
            min = heartDataList.get(0).averageHeart;
            max = heartDataList.get(heartDataList.size()-1).averageHeart;
            LogUtil.getInstance().logd("data******","max = "+max+
                    " ;min = "+ min);

            if (max>allMax)
                allMax = max;
            reportDataList.add(new ReportData(allHeart/sum,max,min,time+i*24*60*60));
        }
        if (iHeartView!=null){
            iHeartView.updateTable(reportDataList,allMax);
        }

    }

    @Override
    public void register(IHeartView iHeartView) {
        this.iHeartView = iHeartView;
    }

    @Override
    public void unRegister() {
        this.iHeartView = null;
    }
}
