package com.szip.healthy.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.healthy.Model.ReportData;
import com.szip.healthy.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportTableView extends View {
    private int mWidth,mHeight;//本页面宽，高
    private List<ReportData> stepDayList;
    private List<ReportData> stepWeekList;
    private List<ReportData> stepMonthList;
    private List<ReportData> sleepDayList;
    private List<ReportData> sleepWeekList;
    private List<ReportData> sleepMonthList;
    private float maxValue = 60;
    private int startTime,allTime;
    private float dpValue;
    private boolean isTouchAble = false;
    private int index;
    private float barWidth = 16f;

    private Paint textYPaint = new Paint();//Y坐标画笔
    private Paint textXPaint = new Paint();//Y坐标画笔
    private Paint pointPaint,squareBackPaint, touchDataTextPaint,timeTextPaint;
    private List<ReportData> touchDataList;

    public ReportTableView(Context context) {
        super(context);
        initView();
    }

    public ReportTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ReportTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private void initView() {
        dpValue = MathUtil.newInstance().dip2Px(1,getContext());
        textYPaint.setColor(getContext().getResources().getColor(R.color.healthy_gray_text));
        textYPaint.setTextSize(dpValue*7);

        textXPaint.setColor(getContext().getResources().getColor(R.color.healthy_gray_text));
        textXPaint.setTextSize(dpValue*7);

        squareBackPaint = new Paint();
        squareBackPaint.setColor(Color.BLACK);
        touchDataTextPaint = new Paint();
        touchDataTextPaint.setColor(Color.WHITE);
        touchDataTextPaint.setTextSize(dpValue*16);
        timeTextPaint = new Paint();
        timeTextPaint.setTextSize(dpValue*10);
        timeTextPaint.setColor(Color.WHITE);
        pointPaint = new Paint();
        pointPaint.setColor(getContext().getResources().getColor(R.color.healthy_gray_text));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (stepDayList!=null){
            DrawYView(canvas);
            DrawDXMsg(canvas);
            DrawDayStep(canvas);
            if (isTouchAble)
                DrawTouchRect(canvas,true);
        }

        if (stepWeekList!=null){
            DrawYView(canvas);
            DrawWXMsg(canvas);
            DrawWeekStep(canvas);
            if (isTouchAble)
                DrawTouchRect(canvas,false);
        }

        if (stepMonthList!=null){
            DrawYView(canvas);
            DrawMXMsg(canvas);
            DrawMonthStep(canvas);
            if (isTouchAble)
                DrawTouchRect(canvas,false);
        }

        if (sleepDayList!=null){
            DrawSleepXMsg(canvas);
            DrawDaySleep(canvas);
        }

        if (sleepWeekList!=null){
            DrawYView(canvas);
            DrawWXMsg(canvas);
            DrawWeepSleep(canvas);
            if (isTouchAble)
                DrawTouchRect(canvas,false);
        }

        if (sleepMonthList!=null){
            DrawYView(canvas);
            DrawMXMsg(canvas);
            DrawMonthSleep(canvas);
            if (isTouchAble)
                DrawTouchRect(canvas,false);
        }
    }

    /**
     * 画纵坐标
     * */
    private void DrawYView(Canvas canvas){

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getContext().getResources().getColor(R.color.healthy_gray_text));
        paint.setStrokeWidth(0.5f);
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{8,8},0);
        paint.setPathEffect(dashPathEffect);
        textYPaint.setTextAlign(Paint.Align.LEFT);
        float diffCoordinate = (mHeight-dpValue*15)*2f/3f/5f;
        String[] yMsg = getYMsg();
        for(int i = 0; i<6; i++) {
            float levelCoordinate = mHeight-dpValue*15-diffCoordinate*i;
            Path dashPath = new Path();
            dashPath.moveTo(dpValue*15, levelCoordinate);
            dashPath.lineTo(mWidth-dpValue*15, levelCoordinate);
            canvas.drawText(yMsg[i], dpValue*3,
                    levelCoordinate+dpValue*4, textYPaint);
            canvas.drawPath(dashPath, paint);
        }
    }


    private String[] getYMsg(){
        String[] yMsg= new String[6];
        int interval = (int) (maxValue/5);
        for (int i = 0;i<6;i++){
            if (sleepWeekList!=null||sleepMonthList!=null){
                yMsg[i] = String.format(Locale.ENGLISH,"%d",interval*i/60);
            }else {
                if (interval*i>1000)
                    yMsg[i] = String.format(Locale.ENGLISH,"%.1fk",interval*i/1000f);
                else
                    yMsg[i] = String.format(Locale.ENGLISH,"%d",interval*i);
            }

        }
        return yMsg;
    }

    private void DrawDXMsg(Canvas canvas){
        String[] xMsg= new String[2];
        xMsg[0] = "00:00";
        xMsg[1] = "23:59";
        textXPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(xMsg[0],dpValue*15,mHeight-5*dpValue,textXPaint);
        canvas.drawText(xMsg[1],mWidth-dpValue*15-textXPaint.measureText(xMsg[1]),mHeight-5*dpValue,textXPaint);
    }

    private void DrawDayStep(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(getContext().getResources().getColor(R.color.healthy_green));
        paint.setStrokeWidth(barWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float interval = (mWidth-30*dpValue-24*barWidth)/23;
        for (int i = 0;i<stepDayList.size();i++){
            ReportData data = stepDayList.get(i);
            if (data.getAverageData()==0)
                continue;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data.getTime()*1000);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            float startX = 15*dpValue+barWidth / 2 + (interval + barWidth) * hour;
            float startY = mHeight-15*dpValue-barWidth/2;
            float stopY =mHeight-dpValue*15-(mHeight-dpValue*15)*2f/3f*data.getAverageData()/maxValue+barWidth/2;
            if (stopY>startY)
                continue;
            canvas.drawLine(startX,startY,startX,
                    stopY, paint);
        }
    }

    private void DrawWXMsg(Canvas canvas){
        String[] xMsg= new String[2];
        xMsg[0] = getContext().getString(R.string.healthy_sun);
        xMsg[1] = getContext().getString(R.string.healthy_mon);
        textXPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(xMsg[0],dpValue*15,mHeight-5*dpValue,textXPaint);
        canvas.drawText(xMsg[1],mWidth-dpValue*15-textXPaint.measureText(xMsg[1]),mHeight-5*dpValue,textXPaint);
    }

    private void DrawWeekStep(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(getContext().getResources().getColor(R.color.healthy_green));
        paint.setStrokeWidth(barWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float interval = (mWidth-30*dpValue-7*barWidth)/6f;
        for (int i = 0;i<stepWeekList.size();i++){
            ReportData data = stepWeekList.get(i);
            if (data.getAverageData()==0)
                continue;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data.getTime()*1000);
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            float startX = 15*dpValue+barWidth/2 + (interval + barWidth) * (day-1);
            float startY = mHeight-15*dpValue-barWidth/2;
            float stopY =mHeight-dpValue*15-(mHeight-dpValue*15)*2f/3f*data.getAverageData()/maxValue+barWidth/2;
            if (stopY>startY)
                continue;
            canvas.drawLine(startX,startY,startX,
                    stopY, paint);
        }
    }

    private void DrawMXMsg(Canvas canvas){
        String[] xMsg= new String[2];
        xMsg[0] = "1";
        xMsg[1] = String.format("%d",touchDataList.size());
        textXPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(xMsg[0],dpValue*15,mHeight-5*dpValue,textXPaint);
        canvas.drawText(xMsg[1],mWidth-dpValue*15-textXPaint.measureText(xMsg[1]),mHeight-5*dpValue,textXPaint);
    }

    private void DrawMonthStep(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(getContext().getResources().getColor(R.color.healthy_green));
        paint.setStrokeWidth(barWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float interval = (mWidth-30*dpValue-touchDataList.size()*barWidth)/(touchDataList.size()-1);
        for (int i = 0;i<stepMonthList.size();i++){
            ReportData data = stepMonthList.get(i);
            if (data.getAverageData()==0)
                continue;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data.getTime()*1000);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            float startX = 15*dpValue+barWidth/2 + (interval + barWidth) * (day-1);
            float startY = mHeight-15*dpValue-barWidth/2;
            float stopY =mHeight-dpValue*15-(mHeight-dpValue*15)*2f/3f*data.getAverageData()/maxValue+barWidth/2;
            if (stopY>startY)
                continue;
            canvas.drawLine(startX,startY,startX,
                    stopY, paint);
        }
    }

    private void DrawSleepXMsg(Canvas canvas){
        String[] xMsg= new String[2];
        xMsg[0] = String.format("%02d:%02d",startTime/60,startTime%60);
        xMsg[1] = String.format("%02d:%02d",(startTime+allTime)%1440/60,(startTime+allTime)%1440%60);
        textXPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(xMsg[0],dpValue*15,mHeight,textXPaint);
        canvas.drawText(xMsg[1],mWidth-dpValue*15-textXPaint.measureText(xMsg[1]),mHeight,textXPaint);
    }

    private void DrawDaySleep(Canvas canvas){
        Paint paint = new Paint();
        float start = dpValue*15;
        float width;
        float top,bottom;
        for (ReportData reportData:sleepDayList){
            width = (reportData.getTime()/(float)allTime)*(mWidth-30*dpValue);
            if (reportData.getAverageData()==2){//深睡
                top = mHeight-15*dpValue-(mHeight-15*dpValue)/2f;
                bottom = mHeight-15*dpValue;
                paint.setColor(getContext().getResources().getColor(R.color.healthy_ray));
            }else {
                top = 0;
                bottom = mHeight-15*dpValue-(mHeight-15*dpValue)/2f;
                paint.setColor(getContext().getResources().getColor(R.color.healthy_yellow));
            }
            RectF rectF = new RectF(start,top,start+width,bottom);
            canvas.drawRect(rectF,paint);
            start+=width;
        }
    }

    private void DrawWeepSleep(Canvas canvas){
        Paint paint = new Paint();

        paint.setStrokeWidth(barWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        float interval = (mWidth-30*dpValue-7*barWidth)/6f;
        for (int i = 0;i<sleepWeekList.size();i++){
            ReportData data = sleepWeekList.get(i);
            if (data.getAverageData()==0)
                continue;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data.getTime()*1000);
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            float startX = 15*dpValue+barWidth/2 + (interval + barWidth) * (day-1);
            float startY = mHeight-15*dpValue-barWidth/2;
            float stopY =mHeight-dpValue*15-(mHeight-dpValue*15)*2f/3f*data.getAverageData()/maxValue+barWidth/2;
            if (stopY>startY)
                continue;
            paint.setColor(getContext().getResources().getColor(R.color.healthy_yellow));
            canvas.drawLine(startX,startY,startX,
                    stopY, paint);

            startX = 15*dpValue+barWidth/2 + (interval + barWidth) * (day-1);
            startY = mHeight-15*dpValue-barWidth/2;
            stopY =mHeight-dpValue*15-(mHeight-dpValue*15)*2f/3f*data.getMinData()/maxValue+barWidth/2;
            if (stopY>startY)
                continue;
            paint.setColor(getContext().getResources().getColor(R.color.healthy_ray));
            canvas.drawLine(startX,startY,startX,
                    stopY, paint);
            canvas.drawRect(startX,stopY,startX,stopY, paint);
        }
    }

    private void DrawMonthSleep(Canvas canvas){
        Paint paint = new Paint();

        paint.setStrokeWidth(barWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        float interval = (mWidth-30*dpValue-touchDataList.size()*barWidth)/(touchDataList.size()-1);
        for (int i = 0;i<sleepMonthList.size();i++){
            ReportData data = sleepMonthList.get(i);
            if (data.getAverageData()==0)
                continue;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data.getTime()*1000);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            float startX = 15*dpValue+barWidth/2 + (interval + barWidth) * (day-1);
            float startY = mHeight-15*dpValue-barWidth/2;
            float stopY =mHeight-dpValue*15-(mHeight-dpValue*15)*2f/3f*data.getAverageData()/maxValue+barWidth/2;
            if (stopY>startY)
                continue;
            paint.setColor(getContext().getResources().getColor(R.color.healthy_yellow));
            canvas.drawLine(startX,startY,startX,
                    stopY, paint);

            startX = 15*dpValue+barWidth/2 + (interval + barWidth) * (day-1);
            startY = mHeight-15*dpValue-barWidth/2;
            stopY =mHeight-dpValue*15-(mHeight-dpValue*15)*2f/3f*data.getMinData()/maxValue+barWidth/2;
            if (stopY>startY)
                continue;
            paint.setColor(getContext().getResources().getColor(R.color.healthy_ray));
            canvas.drawLine(startX,startY,startX,
                    stopY, paint);
            canvas.drawRect(startX,stopY,startX,stopY, paint);
        }
    }

    private void DrawTouchRect(Canvas canvas,boolean isHour){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getContext().getResources().getColor(R.color.healthy_gray_text));
        paint.setStrokeWidth(0.5f);
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{8,8},0);
        paint.setPathEffect(dashPathEffect);
        float interval = (mWidth-30*dpValue-touchDataList.size()*barWidth)/(touchDataList.size()-1);
        float x = 15*dpValue+barWidth/2 + (interval + barWidth) * index;
        Path line = new Path();
        line.moveTo(x,mHeight/3-15*dpValue);
        line.lineTo(x,mHeight-15*dpValue);
        canvas.drawPath(line,paint);

        if (x<40*dpValue)
            x = 40*dpValue;
        if (x>mWidth-40*dpValue)
            x = mWidth-40*dpValue;

        canvas.drawRoundRect(x-40*dpValue,10*dpValue,x+40*dpValue,mHeight/3-15*dpValue,10*dpValue,
                10*dpValue,squareBackPaint);
        String dataStr = "",timeStr = "";
        if (sleepWeekList!=null||sleepMonthList!=null)
            dataStr = String.format("%dh%dmin", touchDataList.get(index).getAverageData()/60,touchDataList.get(index).getAverageData()%60);
        else
            dataStr = String.format("%d", touchDataList.get(index).getAverageData());
        timeStr = DateUtil.getStringDateFromSecond(touchDataList.get(index).getTime(),isHour?"MM-dd HH:mm":"yyyy/MM/dd EE");
        float touchDataTextWidth = touchDataTextPaint.measureText(dataStr);
        float timeTextWidth = timeTextPaint.measureText(timeStr);
        canvas.drawText(dataStr, (x-(touchDataTextWidth)/2),
                mHeight/3-23*dpValue, touchDataTextPaint);
        canvas.drawText(timeStr,(x-timeTextWidth/2),
                10*dpValue+18*dpValue,timeTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchDataList==null||touchDataList.size()==0)
            return super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                isTouchAble = true;
                if (x<dpValue*15){
                    index = 0;
                }else if (x>mWidth-dpValue*15){
                    index = touchDataList.size()-1;
                }else {
                    for (int i = 0, j = i+1; i< touchDataList.size()-1; i++,j++){
                        if ((x-(dpValue*19+(mWidth-19*dpValue*2)/(touchDataList.size()-1)*i))*
                                (x-(dpValue*19+(mWidth-19*dpValue*2)/(touchDataList.size()-1)*j))<0){
                            index = Math.abs((x-(dpValue*19+(mWidth-19*dpValue*2)/(touchDataList.size()-1)*i)))>
                                    Math.abs((x-(dpValue*19+(mWidth-19*dpValue*2)/(touchDataList.size()-1)*j)))?j:i;
                            break;
                        }
                    }
                }
                break;
        }
        postInvalidate();
        return super.onTouchEvent(event);
    }


    public void setStepMonthList(List<ReportData> stepMonthList,int maxValue) {
        this.stepMonthList = stepMonthList;
        this.maxValue = maxValue;
        this.touchDataList = stepMonthList;
        barWidth = 16f;
        postInvalidate();
    }

    public void setStepWeekList(List<ReportData> stepWeekList, int maxValue) {
        this.stepWeekList = stepWeekList;
        this.maxValue = maxValue;
        this.touchDataList = stepWeekList;
        barWidth = 32f;
        postInvalidate();
    }

    public void setStepDayList(List<ReportData> stepDayList, int maxValue) {
        this.stepDayList = stepDayList;
        this.maxValue = maxValue;
        this.touchDataList = stepDayList;
        barWidth = 16f;
        postInvalidate();
    }

    public void setSleepDayList(List<ReportData> sleepDayList,int startTime,int allTime) {
        this.sleepDayList = sleepDayList;
        this.startTime = startTime;
        this.allTime = allTime;
        postInvalidate();
    }

    public void setSleepWeekList(List<ReportData> sleepWeekList,int maxValue) {
        this.sleepWeekList = sleepWeekList;
        this.maxValue = maxValue;
        this.touchDataList = sleepWeekList;
        barWidth = 32f;
        postInvalidate();
    }

    public void setSleepMonthList(List<ReportData> sleepMonthList,int maxValue) {
        this.sleepMonthList = sleepMonthList;
        this.maxValue = maxValue;
        this.touchDataList = sleepMonthList;
        barWidth = 16f;
        postInvalidate();
    }
}
