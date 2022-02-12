package com.szip.healthy.Activity.sport;

import com.szip.blewatch.base.db.dbModel.SportData;

import java.util.ArrayList;

public interface ISportListView {
    void updateList(ArrayList<String> groupList, ArrayList<ArrayList<SportData>> childList);
}
