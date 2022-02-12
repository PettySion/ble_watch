package com.szip.user.Search;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.Broadcast.MyHandle;
import com.szip.blewatch.base.Broadcast.ToActivityBroadcast;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.Model.HealthyConfig;
import com.szip.blewatch.base.db.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.blewatch.base.db.dbModel.UserModel;
import com.szip.user.R;
import com.szip.user.Utils.HttpMessageUtil;
import com.zhy.http.okhttp.BaseApi;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.JsonGenericsSerializator;

import java.util.ArrayList;

import okhttp3.Call;

public class PairFragment extends DialogFragment implements MyHandle {
    private View mRootView;
    private SportWatchAppFunctionConfigDTO sportWatchAppFunctionConfigDTO;
    private TextView stateTv;
    private ListView deviceList;
    private DeviceAdapter deviceAdapter;
    private ArrayList<String> mDevices = new ArrayList<>();
    private String mac;

    private ToActivityBroadcast toActivityBroadcast;


    public PairFragment(SportWatchAppFunctionConfigDTO sportWatchAppFunctionConfigDTO) {
        this.sportWatchAppFunctionConfigDTO = sportWatchAppFunctionConfigDTO;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.user_fragment_pair, container, false);
        }
        initView();
        startSearch();
        return mRootView;
    }

    private void startSearch() {
        Intent intent = new Intent(BroadcastConst.START_SEARCH_DEVICE);
        intent.putExtra("deviceName",sportWatchAppFunctionConfigDTO.appName);
        getActivity().sendBroadcast(intent);
        stateTv.setText("正在搜索...");
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if(window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(toActivityBroadcast==null)
        toActivityBroadcast = new ToActivityBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConst.UPDATE_UI_VIEW);
        intentFilter.addAction(BroadcastConst.UPDATE_BLE_STATE);
        toActivityBroadcast.registerReceive(this,getActivity(),intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        toActivityBroadcast.unregister(getActivity());
    }

    private void initView() {
        stateTv = mRootView.findViewById(R.id.stateTv);
        deviceList = mRootView.findViewById(R.id.deviceList);
        deviceAdapter = new DeviceAdapter(getActivity());
        deviceList.setAdapter(deviceAdapter);

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDevices!=null){
                    stateTv.setText("正在绑定...");
                    bindDevice(mDevices.get(position));
                }
            }
        });
    }

    private void bindDevice(String address){
        mac = address;
        HttpMessageUtil.newInstance().getBindDevice(mac, sportWatchAppFunctionConfigDTO.appName, new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(BaseApi response, int id) {
                if (response.getCode()==200){
                    UserModel userModel = LoadDataUtil.newInstance().getUserInfo(MathUtil.newInstance().getUserId(getActivity()));
                    if (userModel==null)
                        return;
                    userModel.deviceCode = mac;
                    userModel.update();
                    Intent intent = new Intent(BroadcastConst.START_CONNECT_DEVICE);
                    intent.putExtra("isConnect",1);
                    getActivity().sendBroadcast(intent);
                }
            }
        });
    }

    @Override
    public void onReceive(Intent intent) {
        switch (intent.getAction()){
            case BroadcastConst.UPDATE_UI_VIEW:{
                mDevices = intent.getStringArrayListExtra("deviceList");
                if (mDevices!=null){
                    if (mDevices.size()==0){
                        Toast.makeText(getActivity(),"连接失败，请确认附近是否有设备",Toast.LENGTH_SHORT).show();
                    }else if (mDevices.size() == 1){
                        stateTv.setText("正在绑定...");
                        bindDevice(mDevices.get(0));
                    }else {
                        deviceAdapter.setDataList(mDevices);
                    }
                }
            }
                break;
            case BroadcastConst.UPDATE_BLE_STATE:
                int state = intent.getIntExtra("state",0);
                if (state==3){
                    sportWatchAppFunctionConfigDTO.mac = mac;
                    HealthyConfig healthyConfig = sportWatchAppFunctionConfigDTO.getHealthMonitorConfig();
                    SaveDataUtil.newInstance().saveConfigData(sportWatchAppFunctionConfigDTO);
                    SaveDataUtil.newInstance().saveHealthyConfigData(healthyConfig);
                    getActivity().finish();
                }else if (state == 2){
                    stateTv.setText("连接中...");
                }else {
                    stateTv.setText("未连接");
                }
                break;
        }
    }
}
