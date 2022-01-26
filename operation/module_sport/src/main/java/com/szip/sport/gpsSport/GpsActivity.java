package com.szip.sport.gpsSport;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.View.MyAlerDialog;
import com.szip.blewatch.base.View.PulldownUpdateView;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.sport.R;

import static com.szip.blewatch.base.Const.RouterPathConst.PATH_ACTIVITY_SPORT_RESULT;


public class GpsActivity extends BaseActivity implements IGpsView{

    private TextView distanceTv,speedTv,timeTv,calorieTv,countDownTv;
    private View switchView;
    private ImageView lockIv,mapIv,switchIv,gpsIv;
    private RelativeLayout switchRl,finishRl,updateRl;
    private FrameLayout lockFl,startTimeFl;
    private PulldownUpdateView updateView;
    private IGpsPresenter iSportPresenter;
    private long countDownTime = 3;
    private int sportType = 0;

    private ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1f, 1f,
            1f, 50f, 50f);
    private ScaleAnimation touchAnimation = new ScaleAnimation(1f, 0.9f, 1f,
            0.9f, 50f, 50f);

    private long firstime = 0;
    private boolean started = false;

    private Sensor stepCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.sport_activity_gps);
        initView();
        initEvent();
        initAnimation();
        sportType = getIntent().getIntExtra("sportType",2);
        stepCounter = ((SensorManager)getSystemService(SENSOR_SERVICE)).getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(sportType==-1){
//            isportPresenter = new DevicePresenterImpl(getApplicationContext(),this);
        }else {
            if (stepCounter==null)
                iSportPresenter = new GpsPresenterImpl(getApplicationContext(),this, sportType);
            else
                iSportPresenter = new StepPresenterImpl(getApplicationContext(),stepCounter,this,sportType);
        }
        iSportPresenter.startLocationService();
    }

    private void initView() {
        updateView = findViewById(R.id.updateView);
        distanceTv = findViewById(R.id.distanceTv);
        speedTv = findViewById(R.id.speedTv);
        timeTv = findViewById(R.id.timeTv);
        calorieTv = findViewById(R.id.calorieTv);
        lockIv = findViewById(R.id.lockIv);
        mapIv = findViewById(R.id.mapIv);
        switchIv = findViewById(R.id.switchIv);
        switchRl = findViewById(R.id.switchRl);
        finishRl = findViewById(R.id.finishRl);
        updateRl = findViewById(R.id.updateRl);
        lockFl = findViewById(R.id.lockFl);
        startTimeFl = findViewById(R.id.startTimeFl);
        countDownTv = findViewById(R.id.countDownTv);
        switchView = findViewById(R.id.switchView);
        gpsIv = findViewById(R.id.gpsIv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iSportPresenter.finishLocationService(false);
        iSportPresenter.setViewDestory();
    }



    private void initEvent() {
        updateView.setListener(pulldownListener);
        lockIv.setOnClickListener(onClickListener);
        mapIv.setOnClickListener(onClickListener);
        switchRl.setOnClickListener(onClickListener);
        finishRl.setOnClickListener(onClickListener);
    }

    /**
     * 初始化动画
     * */
    private void initAnimation() {
        touchAnimation.setDuration(50);//设置动画持续时间
        touchAnimation.setRepeatCount(0);//设置重复次数
        touchAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setDuration(1000);//设置动画持续时间
        scaleAnimation.setRepeatCount(3);//设置重复次数
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startTimeFl.setVisibility(View.GONE);
                if (iSportPresenter !=null)
                    iSportPresenter.startLocationService();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (countDownTime==1)
                    countDownTv.setText("GO!");
                else
                    countDownTv.setText(String.valueOf(--countDownTime));
            }
        });
    }

    /**
     * 控件下拉监听
     * */
    private PulldownUpdateView.PulldownListener pulldownListener = new PulldownUpdateView.PulldownListener() {
        @Override
        public void updateNow() {
            lockFl.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.lockIv) {
                updateRl.setVisibility(View.VISIBLE);
                lockFl.setVisibility(View.VISIBLE);
            } else if (id == R.id.mapIv) {
                if (iSportPresenter != null && started)
                    iSportPresenter.openMap(getSupportFragmentManager());
                else
                    showToast("请先开启运动");
            } else if (id == R.id.switchRl) {
                switchRl.startAnimation(touchAnimation);
                if (switchRl.getTag().equals("start")) {
                    if (iSportPresenter != null)
                        iSportPresenter.startLocationService();
                } else {
                    if (iSportPresenter != null)
                        iSportPresenter.stopLocationService();
                }
            } else if (id == R.id.finishRl) {
                MyAlerDialog.getSingle().showAlerDialog("提醒", "确定要结束运动吗？",
                        "确定", "取消", false,
                        new MyAlerDialog.AlerDialogOnclickListener() {
                            @Override
                            public void onDialogTouch(boolean flag) {
                                if (flag) {
                                    switchRl.startAnimation(touchAnimation);
                                    if (iSportPresenter != null)
                                        iSportPresenter.finishLocationService(true);
                                }
                            }
                        }, GpsActivity.this);
            }
        }
    };

    @Override
    public void startCountDown() {
        started = true;
        startTimeFl.setVisibility(View.VISIBLE);
        updateRl.setVisibility(View.GONE);
        countDownTv.startAnimation(scaleAnimation);
    }

    @Override
    public void startRun() {
        switchView.setBackgroundResource(R.drawable.sport_bg_circle_white);
        switchIv.setImageResource(R.mipmap.sport_icon_stop);
        switchRl.setTag("");
        finishRl.setVisibility(View.GONE);
    }

    @Override
    public void stopRun() {
        switchView.setBackgroundResource(R.drawable.sport_bg_circle_green);
        switchIv.setImageResource(R.mipmap.sport_icon_continue);
        switchRl.setTag("start");
        finishRl.setVisibility(View.VISIBLE);
    }


    @Override
    public void saveRun(final SportData sportData) {
        LogUtil.getInstance().logd("data******","sport save = "+sportData);
        if (sportData!=null){
            if (sportData.time>30){
                Bundle bundle = new Bundle();
                bundle.putSerializable("sportData",sportData);
                SaveDataUtil.newInstance().saveSportData(sportData);
                ARouter.getInstance().build(PATH_ACTIVITY_SPORT_RESULT)
                        .withBundle("bundle",bundle)
                        .navigation();
            }else {
                showToast("运动时长跟距离太短，无法生成有效的运动报告");
            }
        }
        finish();
    }

    @Override
    public void upDateTime(final int time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeTv.setText(String.format("%02d:%02d:%02d",time/60/60,time/60%60,time%60));
            }
        });
    }

    @Override
    public void upDateRunData(int speed, float distance, float calorie,float acc) {
        speedTv.setText(String.format("%d'%d''",speed/60,speed%60));
        distanceTv.setText(String.format("%.2f",distance/1000));
        calorieTv.setText(String.format("%.1f",calorie));
        if (acc>=29){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_1);
        }else if (acc>=15){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_2);
        }else {
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_3);
        }
    }

    /**
     * 双击退出
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondtime = System.currentTimeMillis();
            if (secondtime - firstime > 3000) {
                Toast.makeText(this, "再次点击退出本次运动",
                        Toast.LENGTH_SHORT).show();
                firstime = System.currentTimeMillis();
                return true;
            } else {
                //todo 发送数据结束到手表端
//                if(MyApplication.getInstance().isMtk()&& MainService.getInstance().getState()==3)
//                    EXCDController.getInstance().writeForControlSport(3);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}