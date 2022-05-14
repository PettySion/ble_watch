package com.szip.blewatch.Activity.main;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.szip.blewatch.R;
import com.szip.blewatch.View.HostTabView;

import com.szip.blewatch.base.Broadcast.ToActivityBroadcast;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.MyAlerDialog;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.healthy.ModuleMain.HealthyFragment;
import com.szip.sport.ModuleMain.SportFragment;
import com.szip.user.ModuleMain.MineFragment;

import java.util.ArrayList;


public class MainPresenterImpl implements IMainPrisenter{

    private Context context;

    public MainPresenterImpl( Context context) {
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
    }

    @Override
    public void initPager(ViewPager2 pager, TabLayout tabLayout) {
        pager.setUserInputEnabled(false);
        String[] tabText = new String[]{context.getString(R.string.home),context.getString(R.string.sport),context.getString(R.string.mine)};
        int[] tabBg = new int[]{R.drawable.bg_tab_state,R.drawable.bg_tab_sport,R.drawable.bg_tab_mine};

        new TabLayoutMediator(tabLayout, pager, true, (tab, position) -> {
            tab.setCustomView(R.layout.tab_host_layout);
            TextView textView = tab.getCustomView().findViewById(R.id.tabTv);
            ImageView imageView = tab.getCustomView().findViewById(R.id.tabIv);
            textView.setText(tabText[position]);
            imageView.setBackground(context.getResources().getDrawable(tabBg[position]));
        }).attach();
    }
}
