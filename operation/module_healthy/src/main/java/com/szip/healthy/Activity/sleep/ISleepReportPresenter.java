package com.szip.healthy.Activity.sleep;

public interface ISleepReportPresenter {
    void loadSleep(long time);
    void register(ISleepReportView iSleepReportView);
    void unRegister();
}
