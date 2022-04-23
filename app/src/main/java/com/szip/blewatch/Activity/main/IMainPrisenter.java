package com.szip.blewatch.Activity.main;

import androidx.fragment.app.FragmentTabHost;

public interface IMainPrisenter {

    //检查蓝牙状态
    void checkBluetoochState();
    //检查GPS状态
    void checkGPSState();
    //初始化工具栏
    void initHost(FragmentTabHost fragmentTabHost);

    void setViewDestory();
}
