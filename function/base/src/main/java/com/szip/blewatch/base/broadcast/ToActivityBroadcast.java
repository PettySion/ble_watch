package com.szip.blewatch.base.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.szip.blewatch.base.Util.BroadcastConst;

public class ToActivityBroadcast extends BroadcastReceiver {
    private MyHandle handle;

    public void registerReceive(MyHandle handle,Context context,IntentFilter intentFilter){
        this.handle = handle;
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
