package com.szip.login.Utils;


import com.szip.blewatch.base.Util.http.TokenInterceptor;
import com.szip.login.HttpModel.CheckVerificationBean;
import com.szip.login.HttpModel.DeviceConfigBean;
import com.zhy.http.okhttp.BaseApi;
import com.szip.login.HttpModel.LoginBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostJsonBuilder;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.szip.blewatch.base.Util.http.HttpClientUtils;

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
    public void postLogin(String type, String areaCode, String phoneNumber, String email, String password, String phoneId,
                          String phoneSystem, GenericsCallback<LoginBean> callback) {
        PostJsonBuilder postJsonBuilder = OkHttpUtils
                .jpost()
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("password",password)
                .addParams("phoneId",phoneId)
                .addParams("phoneSystem",phoneSystem)
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(postJsonBuilder,"v2/user/login",callback);
    }

    public void getVerificationCode(String type,String areaCode,String phoneNumber,String email,GenericsCallback<BaseApi> callback){
        PostJsonBuilder postJsonBuilder = OkHttpUtils
                .jpost()
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(postJsonBuilder,"user/sendVerifyCode",callback);
    }

    /**
     * 验证验证码接口
     * @param type                  验证码类型1：手机 2：邮箱
     * @param areaCode              区号
     * @param phoneNumber           手机号码
     * @param email                 邮箱
     * @param verifyCode            验证码
     * */
    public void postCheckVerifyCode(String type,String areaCode,String phoneNumber,String email,String verifyCode,
                                     GenericsCallback<CheckVerificationBean> callback) {
        PostJsonBuilder builder = OkHttpUtils
                .jpost()
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("verifyCode",verifyCode)
                .addInterceptor(new TokenInterceptor());

        HttpClientUtils.newInstance().buildRequest(builder,"user/checkVerifyCode",callback);
    }


    public void postRegister(String type,String areaCode,String phoneNumber,String email,String verifyCode,String password,
                               String phoneId,String phoneSystem,GenericsCallback<BaseApi> callback){
        PostJsonBuilder postJsonBuilder = OkHttpUtils
                .jpost()
                .id(100)
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("verifyCode",verifyCode)
                .addParams("password",password)
                .addParams("phoneId",phoneId)
                .addParams("phoneSystem",phoneSystem)
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(postJsonBuilder,"user/signUp",callback);
    }

    /**
     * 忘记密码接口
     * @param type               找回类型1：手机  2：邮箱
     * @param areaCode           区号
     * @param phoneNumber        手机号码
     * @param email              邮箱
     * @param verifyCode         验证码
     * @param password           密码
     * */
    public void postForgotPassword(String type,String areaCode,String phoneNumber,String email,String verifyCode,
                                     String password,GenericsCallback<BaseApi> callback){
        PostJsonBuilder builder = OkHttpUtils
                .jpost()
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("verifyCode",verifyCode)
                .addParams("password",password)
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(builder,"user/retrievePassword",callback);
    }

    public void unBindDevice(GenericsCallback<BaseApi> callback){
        GetBuilder getBuilder = OkHttpUtils
                .get()
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(getBuilder,"device/unbindDevice",callback);

    }

    public void getDeviceConfig(GenericsCallback<DeviceConfigBean> callback){
        GetBuilder getBuilder = OkHttpUtils
                .get()
                .addParams("appName","newApp")
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(getBuilder,"comm/getAppFunctionConfigs",callback);
    }
}
