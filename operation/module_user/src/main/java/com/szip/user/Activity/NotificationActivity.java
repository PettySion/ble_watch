package com.szip.user.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.db.dbModel.NotificationData;
import com.szip.user.Adapter.NotificationAdapter;
import com.szip.user.R;

import java.util.ArrayList;
import java.util.List;

import static com.szip.blewatch.base.Util.MathUtil.FILE;

public class NotificationActivity extends BaseActivity {

    private Switch allSw;
    private ListView switchList;
    private NotificationAdapter notificationAdapter;

    private boolean notificationState = false;
    private SharedPreferences sharedPreferences;

    private boolean openService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.user_activity_notification);
        setAndroidNativeLightStatusBar(this,true);
        sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
        notificationState = sharedPreferences.getBoolean("notificationState",false);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (openService){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isNotificationListenerActived()){
                        List<NotificationData> list = new ArrayList<>();
                        list.add(new NotificationData("massage", R.mipmap.cp_icon_empty, "短信", true));
                        list.add(new NotificationData("com.tencent.mm", R.mipmap.cp_icon_empty, "微信", true));
                        list.add(new NotificationData("com.tencent.mobileqq", R.mipmap.cp_icon_empty, "QQ", true));
                        list.add(new NotificationData("com.facebook.katana", R.mipmap.cp_icon_empty, "facebook", true));
                        list.add(new NotificationData("com.facebook.orca", R.mipmap.cp_icon_empty, "facebook massage", true));
                        list.add(new NotificationData("com.twitter.android", R.mipmap.cp_icon_empty, "twitter", true));
                        list.add(new NotificationData("com.whatsapp", R.mipmap.cp_icon_empty, "WhatsApp", true));
                        list.add(new NotificationData("com.instagram.android", R.mipmap.cp_icon_empty, "instagram", true));
                        SaveDataUtil.newInstance().saveNotificationList(list);
                        notificationState = true;
                        initData();
                    }else {
                        allSw.setChecked(false);
                    }
                    openService = false;
                }
            },500);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.edit().putBoolean("notificationState",notificationState).commit();
    }

    private void initData() {
        if (allSw.isChecked()){
            List<NotificationData> list = LoadDataUtil.newInstance().getNotificationList();
            notificationAdapter.setNotificationDatas(list);
        }
    }

    private void initView() {
        setTitle(getString(R.string.user_notification));
        allSw = findViewById(R.id.allSw);
        switchList = findViewById(R.id.switchList);
        notificationAdapter = new NotificationAdapter(getApplicationContext());
        switchList.setAdapter(notificationAdapter);

        allSw.setChecked(notificationState);
        allSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (isNotificationListenerActived()){
                        notificationState = true;
                        initData();
                    }else {
                        showNotifiListnerPrompt();
                    }
                }else {
                    notificationState = false;
                    notificationAdapter.setNotificationDatas(null);
                }
            }
        });
    }

//    private void checkPermission() {
//        /**
//         * 获取权限·
//         * */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED){
//                requestPermissions(new String[]{Manifest.permission.READ_SMS},
//                        100);
//            }else {
//                MainService.getInstance().startSmsService();
//            }
//        }else {
//            MainService.getInstance().startSmsService();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 100){
//            int code = grantResults[0];
//            if (code == PackageManager.PERMISSION_GRANTED){
//                MainService.getInstance().startSmsService();
//            }else {
//                showToast(getString(R.string.user_camera_permission_error));
////                mPersonalAppAdapter.notifyDataSetChanged();
//            }
//        }
//    }


    private boolean isNotificationListenerActived() {
        String packageName = getPackageName();
        String strListener = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        return strListener != null
                && strListener
                .contains(packageName);
    }

    private void showNotifiListnerPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.user_notify_title);
        builder.setMessage(R.string.user_notify_msg);

        builder.setNegativeButton(R.string.user_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                allSw.setChecked(false);
                dialog.dismiss();
            }
        });
        // Go to notification listener settings
        builder.setPositiveButton(R.string.user_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openService = true;
                dialog.dismiss();
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });
        builder.create().show();
    }
}