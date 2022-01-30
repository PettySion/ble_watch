package debug;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.szip.blewatch.base.BaseApplication;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.Util.http.HttpClientUtils;
import com.szip.blewatch.base.Util.http.TokenInterceptor;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.db.dbModel.UserModel;
import com.szip.blewatch.base.model.HealthyConfig;
import com.szip.blewatch.base.service.BleService;
import com.zhy.http.okhttp.BaseApi;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.JsonGenericsSerializator;

import okhttp3.Call;

public class MainApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this,true);


        MathUtil.newInstance().saveStringData(getApplicationContext(),"token","l)a6G7s*TXmwmJ6~eC(9");
        MathUtil.newInstance().saveIntData(getApplicationContext(),"userId",136);
        HttpClientUtils.newInstance().setToken("l)a6G7s*TXmwmJ6~eC(9");
        startService(new Intent(this, BleService.class));


        GetBuilder getBuilder = OkHttpUtils
                .get()
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(getBuilder,"v2/user/getUserInfo",new GenericsCallback<UserInfoBean>(new JsonGenericsSerializator()) {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(UserInfoBean response, int id) {
                if (response.getCode() == 200){
                    SaveDataUtil.newInstance().saveUserInfo(response.getData());

                }else if (response.getCode() == 401){
                    MathUtil.newInstance().saveStringData(MainApplication.this,"token",null);

                }
            }
        });

        if (LoadDataUtil.newInstance().getHealthyCard(true).size()==0){
            HealthyConfig healthyConfig = new HealthyConfig((byte) 1,(byte)1,(byte)1,(byte)1,(byte)1,(byte)1,(byte)1);
            SaveDataUtil.newInstance().saveHealthyConfigData(healthyConfig);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(BroadcastConst.START_CONNECT_DEVICE);
                intent.putExtra("isConnect",1);
                sendBroadcast(intent);
            }
        },2000);

    }

    private class UserInfoBean extends BaseApi {
        private UserModel data;

        public UserModel getData() {
            return data;
        }
    }
}