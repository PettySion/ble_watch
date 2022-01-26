package com.zhy.http.okhttp.builder;

import com.zhy.http.okhttp.request.PostFormRequest;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;

/**
 * Created by Administrator on 2019/6/20.
 */

public class PostFormBuilder extends OkHttpRequestBuilder<PostFormBuilder> implements HasParamsable{


    public PostFormBuilder addInterceptor(Interceptor interceptor){
        this.interceptor = interceptor;
        return this;
    }

    @Override
    public RequestCall build()
    {
        return new PostFormRequest(url, tag, params, headers,interceptor,id).build();
    }

    @Override
    public PostFormBuilder params(Map<String, String> params)
    {
        this.params = params;
        return this;
    }

    @Override
    public PostFormBuilder addParams(String key, String val)
    {
        if (this.params == null)
        {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }
}
