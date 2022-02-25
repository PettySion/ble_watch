package com.szip.user.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.dbModel.UserModel;
import com.szip.user.R;

public class SecurityActivity extends BaseActivity {

    private TextView accountTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.user_activity_security);
        setAndroidNativeLightStatusBar(this,true);

        UserModel userModel = LoadDataUtil.newInstance().getUserInfo(MathUtil.newInstance().getUserId(getApplicationContext()));
        if (userModel==null)
            return;

        setTitle(getString(R.string.user_account_safe));

        accountTv = findViewById(R.id.accountTv);
        if (userModel.phoneNumber!=null)
            accountTv.setText(userModel.phoneNumber);
        else
            accountTv.setText(userModel.email);

        findViewById(R.id.changeRl).setOnClickListener(listener);
        findViewById(R.id.deleteRl).setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}