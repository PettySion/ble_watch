package com.szip.user.Search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.szip.blewatch.base.db.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.user.R;
import com.szip.user.HttpModel.DeviceConfigBean;
import com.szip.user.Utils.HttpMessageUtil;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.JsonGenericsSerializator;

import java.util.ArrayList;

import okhttp3.Call;

public class DeviceActivity extends AppCompatActivity {


    private ListView deviceList;
    private ProductAdapter productAdapter;
    private ArrayList<SportWatchAppFunctionConfigDTO> dtoArrayList;
    private DialogFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_device);
        checkPermission();
        initView();
        initData();
    }

    private void initData() {
        HttpMessageUtil.newInstance().getDeviceConfig(new GenericsCallback<DeviceConfigBean>(new JsonGenericsSerializator()) {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(DeviceConfigBean response, int id) {
                if(response.getCode()==200){
                    dtoArrayList = response.getData();
                    productAdapter.setDataList(dtoArrayList);
                }
            }
        });
    }

    private void initView() {
        deviceList = findViewById(R.id.deviceList);
        productAdapter = new ProductAdapter(this);
        deviceList.setAdapter(productAdapter);

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                final Fragment prev = fragmentManager.findFragmentByTag("PAIR");
                if (prev != null){
                    ft.remove(prev).commit();
                    ft = fragmentManager.beginTransaction();
                }
                ft.addToBackStack(null);
                mapFragment = new PairFragment(dtoArrayList.get(position));
                mapFragment.show(ft, "PAIR");
            }
        });
    }

    private void checkPermission() {
        /**
         * 获取权限·
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        100);
            }
        }
    }
}