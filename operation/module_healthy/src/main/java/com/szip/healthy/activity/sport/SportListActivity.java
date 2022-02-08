package com.szip.healthy.activity.sport;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.broadcast.MyHandle;
import com.szip.blewatch.base.broadcast.ToActivityBroadcast;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.healthy.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.szip.blewatch.base.Const.RouterPathConst.PATH_ACTIVITY_SPORT_RESULT;

public class SportListActivity extends BaseActivity implements MyHandle {

    private ListView sportList;
    private SportListAdapter sportListAdapter;

    private List<SportData> sportData = new ArrayList<>();

    private ToActivityBroadcast toActivityBroadcast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.healthy_activity_sport_list);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(toActivityBroadcast==null)
        toActivityBroadcast = new ToActivityBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConst.UPDATE_UI_VIEW);
        toActivityBroadcast.registerReceive(this,this,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        toActivityBroadcast.unregister(this);
    }

    private void initView() {
        sportList = findViewById(R.id.sportList);
        sportListAdapter = new SportListAdapter(getApplicationContext());
        sportList.setAdapter(sportListAdapter);

        sportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("sportData",sportData.get(position));
                ARouter.getInstance().build(PATH_ACTIVITY_SPORT_RESULT)
                        .withBundle("bundle",bundle)
                        .navigation();
            }
        });
    }

    private void initData() {
        setTitle(getString(R.string.healthy_sport_list));
        sportData = LoadDataUtil.newInstance().getSportList();
        LogUtil.getInstance().logd("data******","list = "+sportData.size());
        sportListAdapter.setDataList(sportData);
    }

    @Override
    public void onReceive(Intent intent) {
        initData();
    }
}