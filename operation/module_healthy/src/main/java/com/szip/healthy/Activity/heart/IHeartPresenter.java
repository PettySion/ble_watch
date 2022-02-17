package com.szip.healthy.Activity.heart;

public interface IHeartPresenter {
    void loadDate(long time);
    void register(IHeartView iHeartView);
    void unRegister();
}
