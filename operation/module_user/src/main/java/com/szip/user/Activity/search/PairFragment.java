package com.szip.user.Activity.search;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.launcher.ARouter;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.Broadcast.MyHandle;
import com.szip.blewatch.base.Broadcast.ToActivityBroadcast;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.Model.HealthyConfig;
import com.szip.blewatch.base.db.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.blewatch.base.db.dbModel.UserModel;
import com.szip.user.Adapter.DeviceAdapter;
import com.szip.user.R;
import com.szip.user.Utils.HttpMessageUtil;
import com.zhy.http.okhttp.BaseApi;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.JsonGenericsSerializator;

import java.util.ArrayList;

import okhttp3.Call;

import static com.szip.blewatch.base.Const.RouterPathConst.PATH_ACTIVITY_USER_FAQ;

public class PairFragment extends DialogFragment implements MyHandle {
    private View mRootView;
    private ToActivityBroadcast toActivityBroadcast;

    private SportWatchAppFunctionConfigDTO sportWatchAppFunctionConfigDTO;
    private ArrayList<String> mDevices = new ArrayList<>();
    private String mac;

    private TextView stateTv,searchHelpTv,stopTv;
    private ImageView revolveIv;
    private RelativeLayout researchRl;
    private LinearLayout errorLl;
    private ListView deviceList;
    private DeviceAdapter deviceAdapter;


    private RotateAnimation rotateRight = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f);


    private AlphaAnimation alphaAnimation = new AlphaAnimation(0,1f);



    public PairFragment(SportWatchAppFunctionConfigDTO sportWatchAppFunctionConfigDTO) {
        this.sportWatchAppFunctionConfigDTO = sportWatchAppFunctionConfigDTO;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.user_fragment_pair, container, false);
        }
        initAnimation();
        initView();
        initEvent();
        startSearch();
        return mRootView;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if(window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(R.color.bgColor);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
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
        ((TextView)mRootView.findViewById(R.id.titleBigTv)).setText(getString(R.string.user_search));
        stateTv = mRootView.findViewById(R.id.stateTv);
        deviceList = mRootView.findViewById(R.id.deviceList);
        revolveIv = mRootView.findViewById(R.id.revolveIv);
        searchHelpTv = mRootView.findViewById(R.id.searchHelpTv);
        stopTv = mRootView.findViewById(R.id.stopTv);
        researchRl = mRootView.findViewById(R.id.researchRl);
        errorLl = mRootView.findViewById(R.id.errorLl);

        deviceAdapter = new DeviceAdapter(getActivity(),sportWatchAppFunctionConfigDTO.productImg);
        deviceList.setAdapter(deviceAdapter);

    }

    private void initEvent() {

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDevices!=null){
                    stateTv.setText(getString(R.string.user_bind));
                    deviceAdapter.setDataList(new ArrayList<>());
                    bindDevice(mDevices.get(position));
                }
            }
        });

        searchHelpTv.setOnClickListener(listener);
        stopTv.setOnClickListener(listener);
        researchRl.setOnClickListener(listener);
        mRootView.findViewById(R.id.backIv).setOnClickListener(listener);
        mRootView.findViewById(R.id.searchHelpTv).setOnClickListener(listener);

    }

    private void initAnimation() {
        rotateRight.setDuration(2500);//设置动画持续时间
        rotateRight.setRepeatCount(-1);//设置重复次数
        rotateRight.setInterpolator(new LinearInterpolator());
        alphaAnimation.setDuration(1000);
    }

    private void startSearch() {
        revolveIv.startAnimation(rotateRight);
        Intent intent = new Intent(BroadcastConst.START_SEARCH_DEVICE);
        intent.putExtra("search",true);
        intent.putExtra("deviceName",sportWatchAppFunctionConfigDTO.appName);
        getActivity().sendBroadcast(intent);
        stateTv.setText(getString(R.string.user_searching));
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
                revolveIv.clearAnimation();
                mDevices = intent.getStringArrayListExtra("deviceList");
                if (mDevices!=null){
                    if (mDevices.size()==0){
                        stateTv.setText(getString(R.string.user_no_device));
                        errorLl.setVisibility(View.VISIBLE);
                        researchRl.setVisibility(View.VISIBLE);
                        revolveIv.setVisibility(View.GONE);
                        errorLl.startAnimation(alphaAnimation);
                        researchRl.startAnimation(alphaAnimation);
                    }else if (mDevices.size() == 1){
                        stateTv.setText(getString(R.string.user_bind));
                        bindDevice(mDevices.get(0));
                    }else {
                        stateTv.setText(getString(R.string.user_search_much));
                        deviceAdapter.setDataList(mDevices);
                    }
                }
            }
                break;
            case BroadcastConst.UPDATE_BLE_STATE:
                int state = intent.getIntExtra("state",0);
                if (state==3){
                    //连接成功，把手表的配置信息缓存到数据库
                    sportWatchAppFunctionConfigDTO.mac = mac;
                    HealthyConfig healthyConfig = sportWatchAppFunctionConfigDTO.getHealthMonitorConfig();
                    SaveDataUtil.newInstance().saveConfigData(sportWatchAppFunctionConfigDTO);
                    SaveDataUtil.newInstance().saveHealthyConfigData(healthyConfig);

                    //跳转到配对成功提醒页面
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    final Fragment prev = fragmentManager.findFragmentByTag("PAIR_FINISH");
                    if (prev != null){
                        ft.remove(prev).commit();
                        ft = fragmentManager.beginTransaction();
                    }
                    ft.addToBackStack(null);
                    PairFinishFragment finish = new PairFinishFragment(sportWatchAppFunctionConfigDTO.screenType);
                    finish.show(ft, "PAIR_FINISH");
                }else if (state == 2){
                    stateTv.setText(getString(R.string.user_connecting));
                }else {
                    stateTv.setText(getString(R.string.user_disconnect));
                }
                break;
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.researchRl) {
                errorLl.setVisibility(View.GONE);
                researchRl.setVisibility(View.GONE);
                revolveIv.setVisibility(View.VISIBLE);
                startSearch();
            }else if (id == R.id.helpRl){

            }else if (id == R.id.stopTv){
                dismiss();
            }else if (id == R.id.backIv){
                Intent intent = new Intent(BroadcastConst.START_SEARCH_DEVICE);
                intent.putExtra("search",false);
                getActivity().sendBroadcast(intent);
                dismiss();
            }else if (id == R.id.searchHelpTv){
                ARouter.getInstance().build(PATH_ACTIVITY_USER_FAQ)
                        .withString("id","PAIR")
                        .navigation();
            }
        }
    };
}
