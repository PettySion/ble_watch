package com.szip.healthy.fragment;

import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.healthy.model.HealthyData;

import java.util.List;

public interface IHealthyView {
    void updateSportData(int step, int calorie, int distance);
    void updateLastSport(SportData sportData);
    void updateHealthyCard(List<HealthyData> healthyDataList);
}
