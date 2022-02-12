package com.szip.healthy.Fragment;

import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.healthy.Model.HealthyData;

import java.util.List;

public interface IHealthyView {
    void updateSportData(int step, int calorie, int distance);
    void updateLastSport(SportData sportData);
    void updateHealthyCard(List<HealthyData> healthyDataList);
}