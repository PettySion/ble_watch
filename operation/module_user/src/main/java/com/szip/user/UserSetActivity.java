package com.szip.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.View.CircularImageView;

public class UserSetActivity extends BaseActivity implements View.OnClickListener {

    private CircularImageView iconIv;
    private TextView versionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_user_set);
        initView();
        initEvent();
    }

    private void initView() {
        iconIv = findViewById(R.id.iconIv);
        versionTv = findViewById(R.id.versionTv);
    }

    private void initEvent() {
        findViewById(R.id.userInfoRl).setOnClickListener(this);
        findViewById(R.id.securityRl).setOnClickListener(this);
        findViewById(R.id.helpRl).setOnClickListener(this);
        findViewById(R.id.privacyRl).setOnClickListener(this);
        findViewById(R.id.logoutTv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}