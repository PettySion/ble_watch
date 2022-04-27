package com.szip.user.Activity.search;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.ble.ClientManager;

import java.util.ArrayList;
import java.util.Calendar;

public class SearchPresenter {
    private Context context;
    private ArrayList<String> mDevices;
    private String deviceName;
    private IUpdateSearch iUpdateSearch;

    public SearchPresenter(Context context,IUpdateSearch iUpdateSearch) {
        this.context = context;
        this.iUpdateSearch = iUpdateSearch;
    }

    public void startSearch(String deviceName){
        this.deviceName = deviceName;
        searchDevice();
    }


    public void stopSearch(){
        ClientManager.getClient().stopSearch();
    }

    private void searchDevice(){
        final SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 1).build();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ClientManager.getClient().search(request, mSearchResponse);
            }
        },1000);
    }



    private long subTime = 0;

    //搜索列表
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            mDevices = new ArrayList<>();
            subTime = Calendar.getInstance().getTimeInMillis();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if (!mDevices.contains(device.getAddress())&&device.getName()!=null&&deviceName.equals(device.getName())) {
                mDevices.add(device.getAddress());
            }
        }

        @Override
        public void onSearchStopped() {
            if (Calendar.getInstance().getTimeInMillis()-subTime<4500){
                searchDevice();
            }else if (iUpdateSearch!=null){
                iUpdateSearch.searchStop(mDevices);
            }

        }

        @Override
        public void onSearchCanceled() {

        }
    };
}
