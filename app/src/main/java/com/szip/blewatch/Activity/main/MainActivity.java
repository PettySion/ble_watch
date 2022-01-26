package com.szip.blewatch.Activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentTabHost;
import com.szip.blewatch.R;
import com.szip.blewatch.View.HostTabView;
import com.szip.blewatch.base.Util.BroadcastConst;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.db.LoadDataUtil;

import java.util.ArrayList;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class MainActivity extends BaseActivity implements IMainView{

    private ArrayList<HostTabView> mTableItemList;
    private RelativeLayout layout;
    private FragmentTabHost fragmentTabHost;
    private IMainPrisenter iMainPrisenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.aactivty_main);
        setAndroidNativeLightStatusBar(this,true);
        iMainPrisenter = new MainPresenterImpl(this,this);
        iMainPrisenter.checkBluetoochState();
        initView();
        initHost();
    }

    private void initView() {
        layout = findViewById(R.id.layout);
        fragmentTabHost = findViewById(android.R.id.tabhost);
    }


    @Override
    public void initHostFinish(ArrayList<HostTabView> hostTabViews) {
        mTableItemList =hostTabViews;
    }

    /**
     * 初始化选项卡视图
     * */
    private void initHost() {
        //实例化FragmentTabHost对象
        fragmentTabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);
        iMainPrisenter.initHost(fragmentTabHost);
    }
}
