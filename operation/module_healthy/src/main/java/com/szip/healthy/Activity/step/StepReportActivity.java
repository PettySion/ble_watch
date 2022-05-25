package com.szip.healthy.Activity.step;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.tabs.TabLayout;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Const.ReportConst;
import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.healthy.Adapter.MyPagerAdapter;
import com.szip.healthy.R;
import com.szip.healthy.View.CalendarPicker;

import java.util.ArrayList;
import java.util.Calendar;

import static com.szip.blewatch.base.Const.RouterPathConst.PATH_ACTIVITY_REPORT_STEP;

@Route(path = PATH_ACTIVITY_REPORT_STEP)
public class StepReportActivity extends BaseActivity {

    private String[] tabs;
    private TabLayout mTab;
    private ViewPager mPager;

    private long reportDate;
    private TextView timeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.healthy_activity_report);
        ARouter.getInstance().inject(this);
        setAndroidNativeLightStatusBar(this,true);
        tabs = new String[]{getString(R.string.healthy_day),getString(R.string.healthy_week),getString(R.string.healthy_month)};
        reportDate = DateUtil.getTimeOfToday();
        LoadDataUtil.newInstance().initCalendarPoint("step");
        initView();
        initPage();
    }

    private void initView() {
        setTitle(getString(R.string.healthy_step));
        mTab = findViewById(R.id.reportTl);
        mPager = findViewById(R.id.reportVp);

        timeTv = findViewById(R.id.timeTv);
        setTimeText();

        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarPicker.getInstance()
                        .enableAnimation(true)
                        .setFragmentManager(getSupportFragmentManager())
                        .setAnimationStyle(R.style.CustomAnim)
                        .setDate(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM-dd"))
                        .setFlag(0)
                        .setCalendarListener(new CalendarPicker.CalendarListener() {
                            @Override
                            public void onClickDate(String date) {
                                if (DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd")>DateUtil.getTimeOfToday()){
                                    showToast(getString(R.string.healthy_tomorrow));
                                }else {
                                    reportDate = DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd");
                                    setTimeText();
                                    Intent intent = new Intent(BroadcastConst.UPDATE_REPORT_TIME);
                                    sendBroadcast(intent);
                                }
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initPage() {

        ArrayList<Fragment> fragments = new ArrayList<>();
        // 创建一个集合,装填Fragment
        StepFragment dayFragment =  new StepFragment(ReportConst.REPORT_DAY);
        StepFragment weekFragment =  new StepFragment(ReportConst.REPORT_WEEK);
        StepFragment monthFragment =  new StepFragment(ReportConst.REPORT_MONTH);
        // 装填
        fragments.add(dayFragment);
        fragments.add(weekFragment);
        fragments.add(monthFragment);

        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragmentArrayList(fragments);

        mPager.setAdapter(myPagerAdapter);
        // 使用 TabLayout 和 ViewPager 相关联
        mTab.setupWithViewPager(mPager);

        // TabLayout 指示器 (记得自己手动创建3个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        for (int i = 0; i < myPagerAdapter.getCount(); i++) {
            TabLayout.Tab tab = mTab.getTabAt(i);//获得每一个tab
            tab.setCustomView(R.layout.healthy_main_top_layout);//给每一个tab设置view
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(true);//第一个tab被选中
            }
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.main_tv);
            textView.setText(tabs[i]);//设置tab上的文字
        }
        mTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(true);
                mPager.setCurrentItem(tab.getPosition());
                setTimeText();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setTimeText(){
        if (mTab.getSelectedTabPosition()==2){
            timeTv.setText(DateUtil.getStringDateFromSecond(reportDate,"yyyy/MM"));
        }else if (mTab.getSelectedTabPosition() == 1){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(reportDate*1000);
            calendar.set(Calendar.DAY_OF_WEEK,1);
            timeTv.setText(DateUtil.getStringDateFromSecond(calendar.getTimeInMillis()/1000,"yyyy/MM/dd")
                    +"~"+DateUtil.getStringDateFromSecond(calendar.getTimeInMillis()/1000+6*24*60*60,"yyyy/MM/dd"));
        }else {
            timeTv.setText(DateUtil.getStringDateFromSecond(reportDate,"yyyy/MM/dd"));
        }
    }

    public long getReportDate() {
        return reportDate;
    }
}