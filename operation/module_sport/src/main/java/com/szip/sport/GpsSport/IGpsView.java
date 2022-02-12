package com.szip.sport.GpsSport;


import com.szip.blewatch.base.db.dbModel.SportData;

public interface IGpsView {
    void startCountDown();
    void startRun();
    void stopRun();
    void saveRun(final SportData sportData);
    void upDateTime(int time);
    void upDateRunData(int speed,float distance,float calorie,float acc);
}
