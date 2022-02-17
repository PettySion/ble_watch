package com.szip.sport.Activity.gpsSport;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.szip.blewatch.base.Const.SportConst;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.View.MyAlerDialog;
import com.szip.blewatch.base.View.PulldownUpdateView;
import com.szip.blewatch.base.db.SaveDataUtil;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.sport.R;

import static com.szip.blewatch.base.Const.RouterPathConst.PATH_ACTIVITY_SPORT_RESULT;


public class GpsActivity extends BaseActivity implements IGpsView{

    private TextView distanceTv,speedTv,timeTv,calorieTv,countDownTv,sportTypeTv,sportStateTv,gpsTv;
    private ImageView gpsIv;
    private RelativeLayout updateRl;
    private LinearLayout speedLl,runLl,indoorLl,pauseLl;
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
        initView();
        initEvent();
        initAnimation();
        iSportPresenter.startLocationService();
    }

    private void initView() {
        updateView = findViewById(R.id.updateView);
        distanceTv = findViewById(R.id.distanceTv);
        speedTv = findViewById(R.id.speedTv);
        timeTv = findViewById(R.id.timeTv);
        calorieTv = findViewById(R.id.calorieTv);
        updateRl = findViewById(R.id.updateRl);
        lockFl = findViewById(R.id.lockFl);
        startTimeFl = findViewById(R.id.startTimeFl);
        countDownTv = findViewById(R.id.countDownTv);
        gpsIv = findViewById(R.id.gpsIv);
        gpsTv = findViewById(R.id.gpsTv);
        sportTypeTv = findViewById(R.id.sportTypeTv);
        sportStateTv = findViewById(R.id.sportStateTv);
        runLl = findViewById(R.id.runLl);
        indoorLl = findViewById(R.id.indoorLl);
        pauseLl = findViewById(R.id.pauseLl);
        speedLl = findViewById(R.id.speedLl);
        sportTypeTv.setText(MathUtil.newInstance().getSportType(sportType,getApplicationContext()).getSportStr());
        if (sportType == SportConst.RUN_INDOOR){
            speedLl.setVisibility(View.GONE);
            runLl.setVisibility(View.GONE);
            indoorLl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iSportPresenter.finishLocationService(false);
        iSportPresenter.setViewDestory();
    }



    private void initEvent() {
        updateView.setListener(pulldownListener);
        findViewById(R.id.lockIv).setOnClickListener(onClickListener);
        findViewById(R.id.pauseIv).setOnClickListener(onClickListener);
        findViewById(R.id.mapIv).setOnClickListener(onClickListener);
        findViewById(R.id.lockBigIv).setOnClickListener(onClickListener);
        findViewById(R.id.indoorPauseIv).setOnClickListener(onClickListener);
        findViewById(R.id.startIv).setOnClickListener(onClickListener);
        findViewById(R.id.finishIv).setOnClickListener(onClickListener);
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
            if (id == R.id.lockIv||id == R.id.lockBigIv) {
                updateRl.setVisibility(View.VISIBLE);
                lockFl.setVisibility(View.VISIBLE);
            } else if (id == R.id.mapIv) {
                if (iSportPresenter != null && started)
                    iSportPresenter.openMap(getSupportFragmentManager());
                else
                    showToast(getString(R.string.sport_start_sport));
            } else if (id == R.id.startIv) {
                if (iSportPresenter != null)
                    iSportPresenter.startLocationService();
            } else if (id == R.id.pauseIv || id == R.id.indoorPauseIv){
                if (iSportPresenter != null)
                    iSportPresenter.stopLocationService();
            }else if (id == R.id.finishIv) {
                MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip),
                        getString(R.string.sport_finish_sport), getString(R.string.confirm),
                        getString(R.string.cancel), false,
                        new MyAlerDialog.AlerDialogOnclickListener() {
                            @Override
                            public void onDialogTouch(boolean flag) {
                                if (flag) {
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
        sportStateTv.setText(getString(R.string.sport_running));
        if (sportType== SportConst.RUN_INDOOR){
            runLl.setVisibility(View.GONE);
            indoorLl.setVisibility(View.VISIBLE);
        }else {
            runLl.setVisibility(View.VISIBLE);
            indoorLl.setVisibility(View.GONE);
        }
        pauseLl.setVisibility(View.GONE);
    }

    @Override
    public void stopRun() {
        sportStateTv.setText(getString(R.string.sport_run_pause));
        pauseLl.setVisibility(View.VISIBLE);
        runLl.setVisibility(View.GONE);
        indoorLl.setVisibility(View.GONE);
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
                showToast(getString(R.string.sport_time_short));
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
        if (acc == 0){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_0);
            gpsTv.setText(getString(R.string.sport_gps_low));
        }else if (acc>=29){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_1);
            gpsTv.setText(getString(R.string.sport_gps_low));
        }else if (acc>=15){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_2);
            gpsTv.setText(getString(R.string.sport_gps_middle));
        }else {
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_3);
            gpsTv.setText(getString(R.string.sport_gps_high));
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
                Toast.makeText(this, getString(R.string.sport_destroy),
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