package debug;

import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.szip.blewatch.base.BaseApplication;
import com.szip.blewatch.base.Util.LogUtil;

public class MainApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this,true);
    }
}