package com.szip.blewatch.base.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.Util.ble.BluetoothUtilImpl;
import com.szip.blewatch.base.Util.ble.ClientManager;
import com.szip.blewatch.base.Util.ble.IBluetoothState;
import com.szip.blewatch.base.Util.ble.IBluetoothUtil;
import com.szip.blewatch.base.Broadcast.MyHandle;
import com.szip.blewatch.base.Broadcast.ToServiceBroadcast;
import com.szip.blewatch.base.db.LoadDataUtil;

import java.util.ArrayList;

public class BleService extends Service implements MyHandle {

    // Global instance
    private static BleService mSevice = null;

    public static BleService getInstance() {
        return mSevice;
    }

    private int bluetoothState;

    private IBluetoothUtil iBluetoothUtil;

    private ToServiceBroadcast broadcast;
    @Override
    public void onCreate() {
        super.onCreate();
        mSevice = this;
        iBluetoothUtil = new BluetoothUtilImpl(getApplicationContext());
        registerService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcast!=null)
        broadcast.unregister(this);
        mSevice = null;
        Intent intent = new Intent();
        intent.setClass(this,BleService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }else {
            startService(intent);
        }
    }

    private void registerService() {
        // register battery low
        if (broadcast==null){
            broadcast = new ToServiceBroadcast();
            broadcast.registerReceive(this,this);
        }
    }

    /**
     * 连接蓝牙
     * */
    private String mac;
    private void connect(){
        iBluetoothUtil.connect(mac,iBluetoothState);
    }
    private void disConnect(){
        iBluetoothUtil.disconnect();
    }
    private final IBluetoothState iBluetoothState = new IBluetoothState() {
        @Override
        public void updateState(int state) {
            bluetoothState = state;
            Intent intent = new Intent(BroadcastConst.UPDATE_BLE_STATE);
            intent.putExtra("state",bluetoothState);
            sendBroadcast(intent);
            Log.i("data******","state = "+bluetoothState);
        }
    };


    /**
     * 扫描蓝牙
     * */
    private ArrayList<String> mDevices;
    private String deviceName;

    private void searchDevice(){
        final SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 1).build();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ClientManager.getClient().search(request, mSearchResponse);
            }
        },500);
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            LogUtil.getInstance().logd("data******","正在搜索");
            mDevices = new ArrayList<>();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if (!mDevices.contains(device)&&device.getName()!=null&&deviceName.equals(device.getName())){
                mDevices.add(device.getAddress());
            }
        }

        @Override
        public void onSearchStopped() {
            LogUtil.getInstance().logd("data******","搜索结束");
            Intent intent = new Intent(BroadcastConst.UPDATE_UI_VIEW);
            intent.putStringArrayListExtra("deviceList",mDevices);
            sendBroadcast(intent);
            mDevices = null;
        }

        @Override
        public void onSearchCanceled() {

        }
    };


    @Override
    public void onReceive(Intent intent) {
        switch (intent.getAction()){
            case BroadcastConst.SEND_BLE_DATA:{
                String command = intent.getStringExtra("command");
                switch (command){
                    case "setUnit":
                        iBluetoothUtil.writeForSetUnit();
                        break;
                    case "setWeather":
                        iBluetoothUtil.writeForSetWeather();
                        break;
                    case "findWatch":
                        iBluetoothUtil.writeForFindWatch();
                        break;
                    case "setInfo":
                        iBluetoothUtil.writeForUpdateUserInfo();
                        break;
                    case "sendNotify":{
                        String title = intent.getStringExtra("title");
                        String label = intent.getStringExtra("label");
                        int id = intent.getIntExtra("id",0);
                        Log.i("notify******","title = "+title+" ;label = "+label+" ;id ="+id);
                        iBluetoothUtil.writeToSendNotify(title,label,id);
                    }
                        break;
                }
            }
                break;
            case BroadcastConst.START_CONNECT_DEVICE:
                int state = intent.getIntExtra("isConnect",0);
                switch (state){
                    case 0:
                        disConnect();
                        break;
                    case 1:{
                        if (bluetoothState==3||bluetoothState==2){//如果是已经连接的状态，直接把状态用广播丢出去
                            Intent stateIntent = new Intent(BroadcastConst.UPDATE_BLE_STATE);
                            stateIntent.putExtra("state",bluetoothState);
                            sendBroadcast(stateIntent);
                        }else {
                            mac = LoadDataUtil.newInstance().getMacAddress(MathUtil.newInstance().getUserId(mSevice));
                            connect();
                        }
                    }
                    break;
                }

                break;
            case BroadcastConst.START_SEARCH_DEVICE:
                deviceName = intent.getStringExtra("deviceName");
                searchDevice();
                break;
        }
    }
}