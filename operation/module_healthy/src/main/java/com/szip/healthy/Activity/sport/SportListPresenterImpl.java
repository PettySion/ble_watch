package com.szip.healthy.Activity.sport;

import android.content.Context;

import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.dbModel.SportData;

import java.util.ArrayList;
import java.util.List;

public class SportListPresenterImpl implements ISportListPresenter{

    private Context mContext;
    private ISportListView iSportListView;

    private ArrayList<String> groupList;
    private ArrayList<ArrayList<SportData>> childList;

    public SportListPresenterImpl(Context mContext, ISportListView iSportListView) {
        this.mContext = mContext;
        this.iSportListView = iSportListView;
    }


    @Override
    public void initList() {
        groupList = new ArrayList<>();
        childList = new ArrayList<>();
        List<SportData> list = LoadDataUtil.newInstance().getSportList(0);
        ArrayList<SportData> child = null;
        for (int i = 0;i<list.size();i++){
            SportData data = list.get(i);
            String timeStr = DateUtil.getStringDateFromSecond(data.time,"yyyy/MM");
            if (!groupList.contains(timeStr)) {
                if (child != null)
                    childList.add(child);
                groupList.add(timeStr);
                child = new ArrayList<>();
            }
            child.add(data);
        }
        if (child!=null&&child.size()!=0)
            childList.add(child);

        if (iSportListView!=null)
            iSportListView.updateList(groupList,childList);
    }

    @Override
    public void getList(int page) {
        if (groupList==null)
            return;
        List<SportData> list = LoadDataUtil.newInstance().getSportList(page);
        ArrayList<SportData> child = childList.get(groupList.size()-1);
        for (int i = 0;i<list.size();i++){
            SportData data = list.get(i);
            String timeStr = DateUtil.getStringDateFromSecond(data.time,"yyyy/MM");
            if (!groupList.contains(timeStr)){//如果groupList已经存在这个时间
                groupList.add(timeStr);
                if (!childList.contains(child))
                    childList.add(child);
                child = new ArrayList<>();
            }
            child.add(data);
        }
        if (child.size()!=0&&!childList.contains(child))
            childList.add(child);

        if (iSportListView!=null)
            iSportListView.updateList(groupList,childList);
    }
}
