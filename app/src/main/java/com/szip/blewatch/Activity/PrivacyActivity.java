package com.szip.blewatch.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.szip.blewatch.R;
import com.szip.blewatch.base.View.BaseActivity;

import static com.szip.blewatch.base.Const.RouterPathConst.PATH_ACTIVITY_PRIVACY;

@Route(path = PATH_ACTIVITY_PRIVACY)
public class PrivacyActivity extends BaseActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        initView();
    }

    private void initView() {
//        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        webView = findViewById(R.id.webview);
        if(getResources().getConfiguration().locale.getLanguage().equals("zh"))
            webView.loadUrl("https://cloud.znsdkj.com:8443/file/contract/iSmarport/statement-zh.html");
        else
            webView.loadUrl("https://cloud.znsdkj.com:8443/file/contract/iSmarport/statement-en.html");
        webView.getSettings().setJavaScriptEnabled(true);
    }
}