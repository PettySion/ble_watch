package com.szip.user.fragment;

import com.szip.blewatch.base.db.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.blewatch.base.db.dbModel.UserModel;

public interface IMineView {
    void initUserInfo(boolean userVisible);
    void updateUserView(UserModel userModel);
    void updateDeviceView(int step, int sleep, int calorie, SportWatchAppFunctionConfigDTO device);
}
