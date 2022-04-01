package com.szip.blewatch.Activity.main;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentTabHost;
import com.szip.blewatch.R;
import com.szip.blewatch.View.HostTabView;
import com.szip.blewatch.base.View.BaseActivity;

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

    private long firstTime = 0;

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

    /**
     * 双击退出
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondtime = System.currentTimeMillis();
            if (secondtime - firstTime > 3000) {
                Toast.makeText(this, getString(R.string.touchAgain),
                        Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
