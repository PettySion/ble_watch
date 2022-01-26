package com.szip.sport.gpsSport;

import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.sport.R;

import static com.szip.blewatch.base.Util.RouterPathConst.PATH_ACTIVITY_SPORT_RESULT;


@Route(path = PATH_ACTIVITY_SPORT_RESULT)
public class SportResultActivity extends BaseActivity {

    private SportData sportData;
    private TextView type,time,step,sportTime,distance,kcal,speedPerHour,speed,stride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.sport_activity_sport_result);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        sportData = (SportData) bundle.getSerializable("sportData");
        ARouter.getInstance().inject(this);
        initView();
        initData();
    }

    private void initData() {
        if (sportData == null)
            return;
        type.setText(String.format("运动类型 = %d",sportData.type));
        time.setText("运动时间 = "+DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        step.setText(String.format("运动步数 = %d",sportData.step));
        sportTime.setText(String.format("运动时长 = %02d:%02d:%02d",sportData.sportTime/60/60,sportData.sportTime/60,sportData.sportTime/60%60));
        distance.setText(String.format("运动里程 = %d",sportData.distance));
        kcal.setText(String.format("运动消耗 = %d",sportData.calorie));
        speedPerHour.setText(String.format("运动时速 = %d,时速数组 = ",sportData.speedPerHour)+sportData.speedPerHourArray);
        speed.setText(String.format("运动配速 = %02d'%02d''",sportData.speed/60,sportData.speed%60));
        stride.setText(String.format("运动步频 = %d,步频数组 = ",sportData.stride)+sportData.strideArray);
    }

    private void initView() {
        type = findViewById(R.id.type);
        time = findViewById(R.id.time);
        step = findViewById(R.id.step);
        sportTime = findViewById(R.id.sportTime);
        distance = findViewById(R.id.distance);
        kcal = findViewById(R.id.kcal);
        speedPerHour = findViewById(R.id.speedPerHour);
        speed = findViewById(R.id.speed);
        stride = findViewById(R.id.stride);
    }
}