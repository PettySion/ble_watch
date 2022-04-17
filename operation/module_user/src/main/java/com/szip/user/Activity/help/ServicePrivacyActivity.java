package com.szip.user.Activity.help;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.RadioGroup;

import com.szip.blewatch.base.View.BaseActivity;
import com.szip.user.R;

public class ServicePrivacyActivity extends BaseActivity {

    private RadioGroup upRg,downRg;
    private WebView contentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.user_activity_service_privacy);
        setAndroidNativeLightStatusBar(this,true);
        initView();
        initEvent();
    }
    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        upRg.setOnCheckedChangeListener(checkedChangeListener);
        downRg.setOnCheckedChangeListener(checkedChangeListener);
    }

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.huaweiRb) {
                contentWeb.loadUrl("file:///android_asset/HUAWEI.html");
                downRg.clearCheck();
            } else if (checkedId == R.id.oppoRb) {
                contentWeb.loadUrl("file:///android_asset/oppo.html");
                downRg.clearCheck();
            } else if (checkedId == R.id.vivoRb) {
                contentWeb.loadUrl("file:///android_asset/vivo.html");
                downRg.clearCheck();
            } else if (checkedId == R.id.lenovoRb) {
                contentWeb.loadUrl("file:///android_asset/Lenovo.html");
                downRg.clearCheck();
            } else if (checkedId == R.id.sumsunRb) {
                contentWeb.loadUrl("file:///android_asset/Samsun.html");
                upRg.clearCheck();
            } else if (checkedId == R.id.miRb) {
                contentWeb.loadUrl("file:///android_asset/MI.html");
                upRg.clearCheck();
            } else if (checkedId == R.id.onePlusRb) {
                contentWeb.loadUrl("file:///android_asset/onePlus.html");
                upRg.clearCheck();
            } else if (checkedId == R.id.meizuRb) {
                contentWeb.loadUrl("file:///android_asset/MEIZU.html");
                upRg.clearCheck();
            }
            if (group.getCheckedRadioButtonId()!=checkedId)
                group.check(checkedId);
        }
    };

    private void initView() {
        findViewById(R.id.rightIv).setVisibility(View.GONE);
        setTitle(getString(R.string.user_service));
        upRg = findViewById(R.id.upRg);
        downRg = findViewById(R.id.downRg);
        contentWeb = findViewById(R.id.contentWeb);
        contentWeb.loadUrl("file:///android_asset/HUAWEI.html");
        contentWeb.getSettings().setJavaScriptEnabled(true);
    }
}