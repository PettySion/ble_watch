package com.szip.healthy.Activity.sport;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.szip.blewatch.base.View.BaseActivity;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.healthy.Adapter.SportListAdapter;
import com.szip.healthy.R;

import java.util.ArrayList;

import static com.szip.blewatch.base.Const.RouterPathConst.PATH_ACTIVITY_SPORT_RESULT;

public class SportListActivity extends BaseActivity implements ISportListView {

    private ExpandableListView sportList;
    private SportListAdapter sportListAdapter;
    private ArrayList<String> groupList;
    private ArrayList<ArrayList<SportData>> childList;

    private int page = 0;
    private ISportListPresenter iSportListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.healthy_activity_sport_list);
        setAndroidNativeLightStatusBar(this,true);
        iSportListPresenter = new SportListPresenterImpl(getApplicationContext(),this);
        initView();
        iSportListPresenter.initList();
    }

    private void initView() {
        setTitle(getString(R.string.healthy_sport_list));
        sportList = findViewById(R.id.sportList);
        sportListAdapter = new SportListAdapter(getApplicationContext());
        sportList.setAdapter(sportListAdapter);
        sportList.setGroupIndicator(null);
        sportList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("sportData",childList.get(groupPosition).get(childPosition));
                ARouter.getInstance().build(PATH_ACTIVITY_SPORT_RESULT)
                        .withBundle("bundle",bundle)
                        .navigation();
                return false;
            }
        });
        sportList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        sportList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            //TODO
                            if (iSportListPresenter!=null)
                                iSportListPresenter.getList(page);
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public void updateList(ArrayList<String> groupList, ArrayList<ArrayList<SportData>> childList) {
        this.groupList = groupList;
        this.childList = childList;
        sportListAdapter.setDataList(groupList,childList);
        for (int i = 0; i <groupList.size(); i++) {
            sportList.expandGroup(i);
        }
        page++;
    }
}