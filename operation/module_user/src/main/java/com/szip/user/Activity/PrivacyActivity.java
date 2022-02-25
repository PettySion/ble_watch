package com.szip.user.Activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.szip.blewatch.base.View.BaseActivity;
import com.szip.user.R;

public class PrivacyActivity extends BaseActivity {


    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.user_activity_privacy);
        setAndroidNativeLightStatusBar(this,true);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.user_privacy));
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = findViewById(R.id.webview);
        if(getResources().getConfiguration().locale.getLanguage().equals("zh"))
            webView.loadUrl("https://cloud.znsdkj.com:8443/file/contract/iSmarport/statement-zh.html");
        else
            webView.loadUrl("https://cloud.znsdkj.com:8443/file/contract/iSmarport/statement-en.html");
        webView.getSettings().setJavaScriptEnabled(true);
    }
}