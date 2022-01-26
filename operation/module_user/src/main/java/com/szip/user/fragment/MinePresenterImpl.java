package com.szip.user.fragment;

import android.content.Context;
import android.content.Intent;

import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.db.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.blewatch.base.db.dbModel.UserModel;
import com.szip.user.utils.HttpMessageUtil;
import com.zhy.http.okhttp.BaseApi;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.JsonGenericsSerializator;

import okhttp3.Call;

public class MinePresenterImpl implements IMinePresenter{
    private UserModel userModel;
    private Context context;
    private IMineView iMineView;

    public MinePresenterImpl(Context context, IMineView iMineView) {
        this.context = context;
        this.iMineView = iMineView;
    }

    @Override
    public void initUser() {
        userModel = LoadDataUtil.newInstance().getUserInfo(MathUtil.newInstance().getUserId(context));
        if (userModel!=null){
            if (userModel.deviceCode==null){
                iMineView.initUserInfo(false);
            }else {
                iMineView.initUserInfo(true);
                Intent intent = new Intent(BroadcastConst.START_CONNECT_DEVICE);
                intent.putExtra("isConnect",1);
                context.sendBroadcast(intent);
                iMineView.updateUserView(userModel);
                getDeviceData();
            }
        }else {
            iMineView.initUserInfo(false);
        }
    }

    @Override
    public void unbind() {
        Intent intent = new Intent(BroadcastConst.START_CONNECT_DEVICE);
        intent.putExtra("isConnect",0);
        context.sendBroadcast(intent);
        HttpMessageUtil.newInstance().getUnbindDevice(callback);
    }

    private void getDeviceData(){
        int step = 0;
        int sleep = 0;
        SportWatchAppFunctionConfigDTO sportWatchAppFunctionConfigDTO;
        sportWatchAppFunctionConfigDTO = LoadDataUtil.newInstance().getSportConfig(userModel.id);
        iMineView.updateDeviceView(step,sleep,0,sportWatchAppFunctionConfigDTO);
    }

    private GenericsCallback<BaseApi> callback = new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(BaseApi response, int id) {
            if (response.getCode() == 200){
                SaveDataUtil.newInstance().unbind(MathUtil.newInstance().getUserId(context));
                initUser();
            }
        }
    };
}
