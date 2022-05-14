package com.szip.user.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.View.character.OnOptionChangedListener;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.user.R;
import com.szip.user.View.CharacterPickerWindow;

import java.util.List;

public class AutoMeasureActivity extends BaseActivity {

    private Switch heartSw,bpSw,spoSw,tempSw;
    private LinearLayout bpLl;
    private TextView heartFrequencyTv,bpFrequencyTv,spoFrequencyTv,tempFrequencyTv;
    private TextView heartStartTv,bpStartTv,spoStartTv,tempStartTv;
    private TextView heartEndTv,bpEndTv,spoEndTv,tempEndTv;
    private CharacterPickerWindow window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.user_activity_auto_measure);
        setAndroidNativeLightStatusBar(this,true);
        initView();
        initEvent();
    }

    private void initView() {
        setTitle(getString(R.string.user_auto));
        heartSw = findViewById(R.id.heartSw);
        bpSw = findViewById(R.id.bpSw);
        spoSw = findViewById(R.id.spoSw);
        tempSw = findViewById(R.id.tempSw);

        heartFrequencyTv = findViewById(R.id.heartFrequencyTv);
        bpFrequencyTv = findViewById(R.id.bpFrequencyTv);
        spoFrequencyTv = findViewById(R.id.spoFrequencyTv);
        tempFrequencyTv = findViewById(R.id.tempFrequencyTv);

        heartStartTv = findViewById(R.id.heartStartTv);
        bpStartTv = findViewById(R.id.bpStartTv);
        spoStartTv = findViewById(R.id.spoStartTv);
        tempStartTv = findViewById(R.id.tempStartTv);

        heartEndTv = findViewById(R.id.heartEndTv);
        bpEndTv = findViewById(R.id.bpEndTv);
        spoEndTv = findViewById(R.id.spoEndTv);
        tempEndTv = findViewById(R.id.tempEndTv);

        bpLl = findViewById(R.id.bpLl);

        if(LoadDataUtil.newInstance().isSupportBp())
            bpLl.setVisibility(View.VISIBLE);

    }

    private void initEvent() {
        findViewById(R.id.heartStartLl).setOnClickListener(onClickListener);
        findViewById(R.id.heartEndLl).setOnClickListener(onClickListener);
        findViewById(R.id.heartFrequencyLl).setOnClickListener(onClickListener);
        findViewById(R.id.bpStartLl).setOnClickListener(onClickListener);
        findViewById(R.id.bpEndLl).setOnClickListener(onClickListener);
        findViewById(R.id.bpFrequencyLl).setOnClickListener(onClickListener);
        findViewById(R.id.spoStartLl).setOnClickListener(onClickListener);
        findViewById(R.id.spoEndLl).setOnClickListener(onClickListener);
        findViewById(R.id.spoFrequencyLl).setOnClickListener(onClickListener);
        findViewById(R.id.tempStartLl).setOnClickListener(onClickListener);
        findViewById(R.id.tempEndLl).setOnClickListener(onClickListener);
        findViewById(R.id.tempFrequencyLl).setOnClickListener(onClickListener);
        findViewById(R.id.saveTv).setOnClickListener(onClickListener);
    }

    /**
     * 初始化选择器
     * */
    private void initWindow(int title,List<String> first,List<String> second,List<String> third,
                          int current1,int current2,int current3,OnOptionChangedListener listener) {
        //步行计划选择器
        window = new CharacterPickerWindow(this,getString(title));
        //初始化选项数据
        window.getPickerView().setPickerWithoutLink(first,second,third);
        //设置默认选中的三级项目
        window.setCurrentPositions(current1,current2,current3);
        //监听确定选择按钮
        window.setOnoptionsSelectListener(listener);
        window.setCyclic(true);
    }

    private OnOptionChangedListener heartFrequency = (option1, option2, option3) -> {

    };
    private OnOptionChangedListener heartStart = (option1, option2, option3) -> {
        heartStartTv.setText(String.format("%02d:%02d",option1,option2));
    };
    private OnOptionChangedListener heartEnd = (option1, option2, option3) -> {
        heartEndTv.setText(String.format("%02d:%02d",option1,option2));
    };

    private OnOptionChangedListener bpFrequency = (option1, option2, option3) -> {

    };
    private OnOptionChangedListener bpStart = (option1, option2, option3) -> {
        bpStartTv.setText(String.format("%02d:%02d",option1,option2));
    };
    private OnOptionChangedListener bpEnd = (option1, option2, option3) -> {
        bpEndTv.setText(String.format("%02d:%02d",option1,option2));
    };

    private OnOptionChangedListener spoFrequency = (option1, option2, option3) -> {

    };
    private OnOptionChangedListener spoStart = (option1, option2, option3) -> {
        spoStartTv.setText(String.format("%02d:%02d",option1,option2));
    };
    private OnOptionChangedListener spoEnd = (option1, option2, option3) -> {
        spoEndTv.setText(String.format("%02d:%02d",option1,option2));
    };

    private OnOptionChangedListener tempFrequency = (option1, option2, option3) -> {

    };
    private OnOptionChangedListener tempStart = (option1, option2, option3) -> {
        tempStartTv.setText(String.format("%02d:%02d",option1,option2));
    };
    private OnOptionChangedListener tempEnd = (option1, option2, option3) -> {
        tempEndTv.setText(String.format("%02d:%02d",option1,option2));
    };

    private View.OnClickListener onClickListener = v -> {
        int id = v.getId();
        if (id == R.id.heartFrequencyLl){
            final List<String> frequencyList = MathUtil.newInstance().getFrequencyList(6);
            String str = heartFrequencyTv.getText().toString();
            str.substring(0,str.length()-3);
            int current = Integer.valueOf(str)/30-1;
            initWindow(R.string.user_start_time,frequencyList,null,null,current,0,0,heartFrequency);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.heartStartLl){
            final List<String> hourList = MathUtil.newInstance().getNumberList(24);
            final List<String> minList = MathUtil.newInstance().getNumberList(60);
            String str[] = heartStartTv.getText().toString().split(":");
            int hour = Integer.valueOf(str[0]);
            int min = Integer.valueOf(str[1]);
            initWindow(R.string.user_start_time,hourList,minList,null,hour,min,0,heartStart);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.heartEndLl){
            final List<String> hourList = MathUtil.newInstance().getNumberList(24);
            final List<String> minList = MathUtil.newInstance().getNumberList(60);
            String str[] = heartEndTv.getText().toString().split(":");
            int hour = Integer.valueOf(str[0]);
            int min = Integer.valueOf(str[1]);
            initWindow(R.string.user_start_time,hourList,minList,null,hour,min,0,heartEnd);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.bpFrequencyLl){
            final List<String> frequencyList = MathUtil.newInstance().getFrequencyList(6);
            String str = bpFrequencyTv.getText().toString();
            str.substring(0,str.length()-3);
            int current = Integer.valueOf(str)/30-1;
            initWindow(R.string.user_start_time,frequencyList,null,null,current,0,0,bpFrequency);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.bpStartLl){
            final List<String> hourList = MathUtil.newInstance().getNumberList(24);
            final List<String> minList = MathUtil.newInstance().getNumberList(60);
            String str[] = bpStartTv.getText().toString().split(":");
            int hour = Integer.valueOf(str[0]);
            int min = Integer.valueOf(str[1]);
            initWindow(R.string.user_start_time,hourList,minList,null,hour,min,0,bpStart);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.bpEndLl){
            final List<String> hourList = MathUtil.newInstance().getNumberList(24);
            final List<String> minList = MathUtil.newInstance().getNumberList(60);
            String str[] = bpEndTv.getText().toString().split(":");
            int hour = Integer.valueOf(str[0]);
            int min = Integer.valueOf(str[1]);
            initWindow(R.string.user_start_time,hourList,minList,null,hour,min,0,bpEnd);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.spoFrequencyLl){
            final List<String> frequencyList = MathUtil.newInstance().getFrequencyList(6);
            String str = heartFrequencyTv.getText().toString();
            str.substring(0,str.length()-3);
            int current = Integer.valueOf(str)/30-1;
            initWindow(R.string.user_start_time,frequencyList,null,null,current,0,0,heartFrequency);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.spoStartLl){
            final List<String> hourList = MathUtil.newInstance().getNumberList(24);
            final List<String> minList = MathUtil.newInstance().getNumberList(60);
            String str[] = spoFrequencyTv.getText().toString().split(":");
            int hour = Integer.valueOf(str[0]);
            int min = Integer.valueOf(str[1]);
            initWindow(R.string.user_start_time,hourList,minList,null,hour,min,0,spoFrequency);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.spoEndLl){
            final List<String> hourList = MathUtil.newInstance().getNumberList(24);
            final List<String> minList = MathUtil.newInstance().getNumberList(60);
            String str[] = spoEndTv.getText().toString().split(":");
            int hour = Integer.valueOf(str[0]);
            int min = Integer.valueOf(str[1]);
            initWindow(R.string.user_start_time,hourList,minList,null,hour,min,0,spoEnd);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.tempFrequencyLl){
            final List<String> frequencyList = MathUtil.newInstance().getFrequencyList(6);
            String str = tempFrequencyTv.getText().toString();
            str.substring(0,str.length()-3);
            int current = Integer.valueOf(str)/30-1;
            initWindow(R.string.user_start_time,frequencyList,null,null,current,0,0,tempFrequency);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.tempStartLl){
            final List<String> hourList = MathUtil.newInstance().getNumberList(24);
            final List<String> minList = MathUtil.newInstance().getNumberList(60);
            String str[] = tempStartTv.getText().toString().split(":");
            int hour = Integer.valueOf(str[0]);
            int min = Integer.valueOf(str[1]);
            initWindow(R.string.user_start_time,hourList,minList,null,hour,min,0,tempStart);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.tempEndLl){
            final List<String> hourList = MathUtil.newInstance().getNumberList(24);
            final List<String> minList = MathUtil.newInstance().getNumberList(60);
            String str[] = tempEndTv.getText().toString().split(":");
            int hour = Integer.valueOf(str[0]);
            int min = Integer.valueOf(str[1]);
            initWindow(R.string.user_start_time,hourList,minList,null,hour,min,0,tempEnd);
            window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        }else if (id == R.id.saveTv){

        }
    };
}