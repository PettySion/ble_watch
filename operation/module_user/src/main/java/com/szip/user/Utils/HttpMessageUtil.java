package com.szip.user.Utils;


import com.szip.blewatch.base.Util.http.HttpClientUtils;
import com.szip.blewatch.base.Util.http.TokenInterceptor;
import com.szip.user.HttpModel.DeviceConfigBean;
import com.zhy.http.okhttp.BaseApi;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostJsonBuilder;
import com.zhy.http.okhttp.callback.GenericsCallback;

public class HttpMessageUtil {

    private static HttpMessageUtil httpMessageUtil;
    private HttpMessageUtil(){

    }

    public static HttpMessageUtil newInstance(){                     // 单例模式，双重锁
        if( httpMessageUtil == null ){
            synchronized (HttpMessageUtil.class){
                if( httpMessageUtil == null ){
                    httpMessageUtil = new HttpMessageUtil();
                }
            }
        }
        return httpMessageUtil;
    }

    public void getDeviceConfig(GenericsCallback<DeviceConfigBean> callback){
        GetBuilder getBuilder = OkHttpUtils
                .get()
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(getBuilder,"comm/getAppFunctionConfigs",callback);
    }

    /**
     * 绑定设备
     * */
    public void getBindDevice(String deviceCode,String product, GenericsCallback<BaseApi> callback){
        GetBuilder getBuilder = OkHttpUtils
                .get()
                .addInterceptor(new TokenInterceptor())
                .addParams("deviceCode",deviceCode)
                .addParams("product",product);
        HttpClientUtils.newInstance().buildRequest(getBuilder,"device/bindDevice",callback);
    }

    public void getUnbindDevice(GenericsCallback<BaseApi> callback){
        GetBuilder getBuilder = OkHttpUtils
                .get()
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(getBuilder,"device/unbindDevice",callback);
    };


    public void postForSetUnit(String unit,String temp,GenericsCallback<BaseApi>callback){
        PostJsonBuilder builder = OkHttpUtils
                .jpost()
                .addParams("unit",unit)
                .addParams("tempUnit",temp)
                .addInterceptor(new TokenInterceptor());

        HttpClientUtils.newInstance().buildRequest(builder,"v2/user/setUnit",callback);
    }

}
