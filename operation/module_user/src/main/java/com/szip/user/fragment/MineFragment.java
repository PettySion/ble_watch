package com.szip.user.fragment;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.BaseFragment;
import com.szip.blewatch.base.View.CircularImageView;
import com.szip.blewatch.base.View.MyAlerDialog;
import com.szip.blewatch.base.broadcast.MyHandle;
import com.szip.blewatch.base.broadcast.ToActivityBroadcast;
import com.szip.blewatch.base.db.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.blewatch.base.db.dbModel.UserModel;
import com.szip.blewatch.base.interfere.OnItemClickListener;
import com.szip.user.NotificationActivity;
import com.szip.user.R;
import com.szip.user.UnitSelectActivity;
import com.szip.user.adapter.DeviceManagementAdapter;
import com.szip.user.search.DeviceActivity;

/**
 * Created by Administrator on 2019/12/1.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener, MyHandle ,IMineView{

    private CircularImageView iconIv,dialIv,watchIv;
    private TextView nameTv,stepTv,stepDataTv,stepRateTv,sleepTv,sleepDataTv,sleepRateTv,calorieTv,calorieDataTv,calorieRateTv,stateTv,watchTv;
    private SeekBar stepSb,sleepSb,calorieSb;
    private UserModel userModel;
    private RecyclerView menuList;
    private RelativeLayout addDeviceRl;
    private LinearLayout deviceLl;
    private ToActivityBroadcast toActivityBroadcast;

    private IMinePresenter iMinePresenter;
    @Override
    protected int getLayoutId() {
        return R.layout.user_fragment_mine;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        iMinePresenter = new MinePresenterImpl(getActivity().getApplicationContext(),this);
        initView();
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        iMinePresenter.initUser();
        if (toActivityBroadcast == null)
            toActivityBroadcast = new ToActivityBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConst.UPDATE_UI_VIEW);
        intentFilter.addAction(BroadcastConst.UPDATE_BLE_STATE);
        toActivityBroadcast.registerReceive(this,getActivity().getApplicationContext(),intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        toActivityBroadcast.unregister(getActivity().getApplicationContext());
    }

    private void initEvent() {
        nameTv.setOnClickListener(this);
        iconIv.setOnClickListener(this);
        addDeviceRl.setOnClickListener(this);
        getView().findViewById(R.id.setIv).setOnClickListener(this);
        getView().findViewById(R.id.unbindIv).setOnClickListener(this);
        getView().findViewById(R.id.updateIv).setOnClickListener(this);
        getView().findViewById(R.id.editPlanTv).setOnClickListener(this);
        getView().findViewById(R.id.dialLl).setOnClickListener(this);
    }
    private void initView() {
        nameTv = getView().findViewById(R.id.nameTv);
        stepTv = getView().findViewById(R.id.stepTv);
        stepDataTv = getView().findViewById(R.id.stepDataTv);
        stepRateTv = getView().findViewById(R.id.stepRateTv);
        sleepTv = getView().findViewById(R.id.sleepTv);
        sleepDataTv = getView().findViewById(R.id.sleepDataTv);
        sleepRateTv = getView().findViewById(R.id.sleepRateTv);
        calorieTv = getView().findViewById(R.id.calorieTv);
        calorieDataTv = getView().findViewById(R.id.calorieDataTv);
        calorieRateTv = getView().findViewById(R.id.calorieRateTv);
        stepSb = getView().findViewById(R.id.stepSb);
        sleepSb = getView().findViewById(R.id.sleepSb);
        calorieSb = getView().findViewById(R.id.calorieSb);
        dialIv = getView().findViewById(R.id.dialIv);
        iconIv = getView().findViewById(R.id.iconIv);
        watchIv = getView().findViewById(R.id.watchIv);
        watchTv = getView().findViewById(R.id.watchTv);
        stateTv = getView().findViewById(R.id.stateTv);
        menuList = getView().findViewById(R.id.menuList);
        addDeviceRl = getView().findViewById(R.id.addDeviceRl);
        deviceLl = getView().findViewById(R.id.deviceLl);
        menuList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        DeviceManagementAdapter deviceManagementAdapter = new DeviceManagementAdapter(getActivity().getApplicationContext());
        deviceManagementAdapter.setOnItemClickListener(listener);
        menuList.setAdapter(deviceManagementAdapter);
        menuList.setHasFixedSize(true);
        menuList.setNestedScrollingEnabled(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iconIv || id == R.id.nameTv) {
            if (!MathUtil.newInstance().needLogin(getActivity())) {

            }
        } else if (id == R.id.addDeviceRl) {
            if (!MathUtil.newInstance().needLogin(getActivity())) {
                startActivity(new Intent(getActivity(), DeviceActivity.class));
            }
        }else if (id == R.id.unbindIv){
            MyAlerDialog.getSingle().showAlerDialog(getString(R.string.user_unbind_tip), getString(R.string.user_unbind_message),
                    getString(R.string.user_confirm), getString(R.string.user_cancel),
                    true, flag -> {
                        if (flag){
                            iMinePresenter.unbind();
                        }
                    }, getActivity());

        }
    }

    private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            switch (position){
                case 0:
                    Intent intent = new Intent(BroadcastConst.SEND_BLE_DATA);
                    intent.putExtra("command","findWatch");
                    getActivity().sendBroadcast(intent);
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    startActivity(new Intent(getActivity(), UnitSelectActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(getActivity(), NotificationActivity.class));
                    break;
                case 5:
                    break;

            }
        }
    };

    @Override
    public void onReceive(Intent intent) {
        switch (intent.getAction()){
            case BroadcastConst.UPDATE_BLE_STATE:
                int state = intent.getIntExtra("state",0);
                if (state==3){
                    stateTv.setText("已连接");
                }else if (state == 2){
                    stateTv.setText("连接中...");
                }else {
                    stateTv.setText("未连接");
                }
                break;
            case BroadcastConst.UPDATE_UI_VIEW:
                LogUtil.getInstance().logd("data******","刷新UI");
                break;
        }
    }

    @Override
    public void initUserInfo(boolean userVisible) {
        if (userVisible){
            deviceLl.setVisibility(View.VISIBLE);
            addDeviceRl.setVisibility(View.GONE);
        }else {
            deviceLl.setVisibility(View.GONE);
            addDeviceRl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateUserView(UserModel userModel) {
        nameTv.setText(userModel.userName);
        Glide.with(getActivity()).load(userModel.avatar)
                .error(R.mipmap.cp_icon_empty);
        stepTv.setText(String.format("%d",userModel.stepsPlan));
        sleepTv.setText(String.format("%d",userModel.sleepPlan));
        stepSb.setMax(userModel.stepsPlan);
        sleepSb.setMax(userModel.sleepPlan);
    }

    @Override
    public void updateDeviceView(int step, int sleep, int calorie, SportWatchAppFunctionConfigDTO device) {
        stepSb.setProgress(step);
        sleepSb.setProgress(sleep);
        watchTv.setText(device.appName);
    }
}
