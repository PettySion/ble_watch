package com.szip.user.Utils;


import com.szip.blewatch.base.Util.ble.ClientManager;
import com.szip.blewatch.base.Util.http.HttpClientUtils;
import com.szip.blewatch.base.Util.http.TokenInterceptor;
import com.szip.user.HttpModel.AvatarBean;
import com.szip.user.HttpModel.DeviceConfigBean;
import com.szip.user.HttpModel.FaqBean;
import com.szip.user.HttpModel.FaqListBean;
import com.zhy.http.okhttp.BaseApi;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostJsonBuilder;
import com.zhy.http.okhttp.builder.PostJsonListBuider;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.File;

import okhttp3.OkHttpClient;

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

    public void postForSetUserInfo(String name,String sex,String birthday,String height,String weight,
                                   String heightBritish,String weightBritish,GenericsCallback<BaseApi> callback){
        PostJsonBuilder builder = OkHttpUtils
                .jpost()
                .addParams("userName",name)
                .addParams("lastName","")
                .addParams("firstName","")
                .addParams("sex",sex)
                .addParams("birthday",birthday)
                .addParams("nation","")
                .addParams("height",height)
                .addParams("weight",weight)
                .addParams("heightBritish",heightBritish)
                .addParams("weightBritish",weightBritish)
                .addParams("blood","")
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(builder,"v2/user/updateUserInfo",callback);
    }

    public void postUploadAvatar(File avatar, GenericsCallback<AvatarBean> callback){
        PostFormBuilder formBuilder = OkHttpUtils
                .fpost()
                .addFile("file","iSmarport_6.jpg",avatar)
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(formBuilder,"user/setProfilePicture",callback);
    }

    public void postForUploadReportData(String data,GenericsCallback<BaseApi> callback){
        PostJsonListBuider jsonBuilder =   OkHttpUtils
                .listpost()
                .addInterceptor(new TokenInterceptor())
                .addParams("data",data);
        HttpClientUtils.newInstance().buildRequest(jsonBuilder,"data/upload",callback);
    }

    public void postSendFeedback(String content,GenericsCallback<BaseApi> callback){
        PostJsonBuilder jsonBuilder = OkHttpUtils
                .jpost()
                .addParams("content",content)
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(jsonBuilder,"user/uploadFeedback",callback);
    }

    public void getFaqList(GenericsCallback<FaqListBean> callback){
        GetBuilder getBuilder = OkHttpUtils
                .get()
                .addParams("pageNum","1")
                .addParams("pageSize","30")
                .addInterceptor(new TokenInterceptor());

        HttpClientUtils.newInstance().buildRequest(getBuilder,"comm/getQuestionAndAnswers",callback);
    }

    public void getFaq(String id,GenericsCallback<FaqBean> callback){
        GetBuilder getBuilder = OkHttpUtils
                .get()
                .addParams("id",id)
                .addInterceptor(new TokenInterceptor());
        HttpClientUtils.newInstance().buildRequest(getBuilder,"comm/getQuestionAndAnswerDetail",callback);
    }

}
