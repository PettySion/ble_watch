package com.szip.healthy.Activity.heart;

import android.view.View;

import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.View.BaseLazyLoadingFragment;
import com.szip.healthy.Activity.sleep.SleepMonthPresenterImpl;
import com.szip.healthy.Model.ReportData;
import com.szip.healthy.R;

import java.util.List;

public class HeartFragment extends BaseLazyLoadingFragment implements IHeartView{

    private int type;//0 日报 1周报 2月报
    private IHeartPresenter iHeartPresenter;

    public HeartFragment(int type) {
        this.type = type;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.healthy_fragment_heart;
    }

    @Override
    protected void initView(View root) {

    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        if (type == 1)
            iHeartPresenter = new HeartWeekPresenterImpl(getActivity().getApplicationContext(),this);
        else
            iHeartPresenter = new HeartMonthPresenterImpl(getActivity().getApplicationContext(),this);
        iHeartPresenter.loadDate(DateUtil.getTimeOfToday());
    }

    @Override
    protected void onFragmentResume() {
        super.onFragmentResume();
        iHeartPresenter.register(this);
    }

    @Override
    protected void onFragmentPause() {
        super.onFragmentPause();
        iHeartPresenter.unRegister();
    }

    @Override
    public void updateTable(List<ReportData> reportDataList, int max) {

    }
}
