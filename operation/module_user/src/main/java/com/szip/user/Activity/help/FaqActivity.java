package com.szip.user.Activity.help;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.szip.blewatch.base.View.BaseActivity;
import com.szip.user.Adapter.FaqAdapter;
import com.szip.user.HttpModel.FaqListBean;
import com.szip.user.HttpModel.FaqModel;
import com.szip.user.R;
import com.szip.user.Utils.HttpMessageUtil;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.JsonGenericsSerializator;

import java.util.ArrayList;

import okhttp3.Call;

public class FaqActivity extends BaseActivity {
    private ListView faqList;
    private FaqAdapter faqAdapter;
    private ArrayList<FaqModel> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.user_activity_faq);
        setTitle(getString(R.string.user_faq));
        setAndroidNativeLightStatusBar(this,true);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        faqList = findViewById(R.id.faqList);
        faqAdapter = new FaqAdapter(getApplicationContext());
        faqList.setAdapter(faqAdapter);
    }

    private void initEvent() {
        faqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              if (position==list.size()+1){
//                    startActivity(new Intent(FaqActivity.this,ServicePrivacyActivity.class));
                }else if (position==list.size()){
//                    startActivity(new Intent(FaqActivity.this,GuideActivity.class));
                }else {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    final Fragment prev = getSupportFragmentManager().findFragmentByTag("FAQ");
                    if (prev != null){
                        ft.remove(prev).commit();
                        ft = getSupportFragmentManager().beginTransaction();
                    }
                    ft.addToBackStack(null);
                    FaqFragment faqFragment = new FaqFragment(String.valueOf(list.get(position).getReqId()),list.get(position).getTitle());
                    faqFragment.show(ft, "FAQ");
                }
            }
        });

        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {

        HttpMessageUtil.newInstance().getFaqList(new GenericsCallback<FaqListBean>(new JsonGenericsSerializator()) {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(FaqListBean response, int id) {
                if (response.getCode()==200){
                    list = response.getData().getList();
                    faqAdapter.setList(response.getData().getList());
                }
            }
        });

    }
}