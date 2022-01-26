package com.szip.blewatch.base.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.szip.blewatch.base.Const.BroadcastConst;

public class ToServiceBroadcast extends BroadcastReceiver {

    private MyHandle handle;


    public void registerReceive(MyHandle handle,Context context){
        this.handle = handle;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConst.SEND_BLE_DATA);
        intentFilter.addAction(BroadcastConst.START_SEARCH_DEVICE);
        intentFilter.addAction(BroadcastConst.START_CONNECT_DEVICE);
        context.registerReceiver(this,intentFilter);
    }

    public void unregister(Context context){
        context.unregisterReceiver(this);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (handle!=null)
            handle.onReceive(intent);
    }

}
