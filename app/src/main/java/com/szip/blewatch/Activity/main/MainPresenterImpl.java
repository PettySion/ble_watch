package com.szip.blewatch.Activity.main;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.TabHost;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTabHost;

import com.szip.blewatch.R;
import com.szip.blewatch.View.HostTabView;

import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.MyAlerDialog;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.consult.fragment.ConsultFragment;
import com.szip.healthy.Fragment.HealthyFragment;
import com.szip.sport.Fragment.SportFragment;
import com.szip.user.Fragment.MineFragment;

import java.util.ArrayList;


public class MainPresenterImpl implements IMainPrisenter{

    private IMainView iMainView;
    private Handler handler;
    private Context context;

    public MainPresenterImpl(IMainView iMainView, Context context) {
        this.iMainView = iMainView;
        handler = new Handler(Looper.getMainLooper());
        this.context = context;
    }

    @Override
    public void checkBluetoochState() {
        //判断蓝牙状态
        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        if (!blueadapter.isEnabled()) {
            Intent bleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(bleIntent);
        }else {
            checkGPSState();
        }
    }


    private boolean isInstalled(@NonNull String packageName, Context context) {
        if ("".equals(packageName) || packageName.length() <= 0) {
            return false;
        }

        PackageInfo packageInfo;

        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void checkGPSState() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!(gps || network)) {
            MyAlerDialog.getSingle().showAlerDialog(context.getString(R.string.tip), context.getString(R.string.checkGPS),
                    context.getString(R.string.confirm), context.getString(R.string.cancel),
                    false, new MyAlerDialog.AlerDialogOnclickListener() {
                        @Override
                        public void onDialogTouch(boolean flag) {
                            if (flag){
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(intent);
                            }
                        }
                    },context);
        }
        if (BluetoothAdapter.getDefaultAdapter().isEnabled())
            initBle();

    }
    //todo 02手表连接前需要做的初始化操作
//                    BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
//                    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
//                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(MyApplication.getInstance().getUserInfo().getDeviceCode());
//                    WearableManager.getInstance().setRemoteDevice(device);
    @Override
    public void initBle() {
        String mac = LoadDataUtil.newInstance().getMacAddress(MathUtil.newInstance().getUserId(context));
        if (mac!=null){//已绑定
           Intent intent = new Intent(BroadcastConst.START_CONNECT_DEVICE);
           intent.putExtra("isConnect",1);
           context.sendBroadcast(intent);
        }
    }

    @Override
    public void setViewDestory() {
        iMainView = null;
    }




    @Override
    public void initHost(FragmentTabHost fragmentTabHost) {

        final ArrayList<HostTabView> mTableItemList = new ArrayList<>();
        //添加tab
        mTableItemList.add(new HostTabView(R.mipmap.nav_state,R.mipmap.nav_state_pre,R.string.home, HealthyFragment.class,context));
        mTableItemList.add(new HostTabView(R.mipmap.nav_sport,R.mipmap.nav_sport_pre,R.string.sport, SportFragment.class,context));
//        mTableItemList.add(new HostTabView(R.mipmap.a_tabbar_tab3,R.mipmap.a_tabbar_market_p,R.string.consult, ConsultFragment.class,context));
        mTableItemList.add(new HostTabView(R.mipmap.nav_my,R.mipmap.nav_my_pre,R.string.mine, MineFragment.class,context));
        //去掉分割线
        fragmentTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0; i<mTableItemList.size(); i++) {
            HostTabView tabItem = mTableItemList.get(i);
            //实例化一个TabSpec,设置tab的名称和视图
            TabHost.TabSpec tabSpec = fragmentTabHost.newTabSpec(tabItem.getTitleString()).setIndicator(tabItem.getView());
            fragmentTabHost.addTab(tabSpec,tabItem.getFragmentClass(),null);

            //给Tab按钮设置背景
            fragmentTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#00ffffff"));

            //默认选中第一个tab
            if(i == 0) {
                tabItem.setChecked(true);
            }
        }

        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                //重置Tab样式
                for (int i = 0; i< mTableItemList.size(); i++) {
                    HostTabView tabitem = mTableItemList.get(i);
                    if (tabId.equals(tabitem.getTitleString())) {
                        tabitem.setChecked(true);
                    }else {
                        tabitem.setChecked(false);
                    }
                }
            }
        });

        if (iMainView!=null)
            iMainView.initHostFinish(mTableItemList);
    }
}
