package com.szip.user.Activity.help;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.szip.user.HttpModel.FaqBean;
import com.szip.user.R;
import com.szip.user.Utils.HttpMessageUtil;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.JsonGenericsSerializator;

import java.io.IOException;

import okhttp3.Call;

public class FaqFragment extends DialogFragment {

    private String id;
    private String title;
    private View mRootView;
    private WebView contentWeb;

    public FaqFragment(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.user_fragment_faq, container, false);
        }

        initView();
        initData();
        return mRootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if(window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setWindowAnimations(R.style.CustomAnim);
        }
        setAndroidNativeLightStatusBar(dialog,true);
        return dialog;
    }


    private void initView() {
        ((TextView)mRootView.findViewById(R.id.titleBigTv)).setText(title);
        contentWeb = mRootView.findViewById(R.id.contentWeb);
        mRootView.findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initData() {
        HttpMessageUtil.newInstance().getFaq(id, new GenericsCallback<FaqBean>(new JsonGenericsSerializator()) {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(FaqBean response, int id) {
                if (response.getCode() == 200){
                    contentWeb.loadData(response.getData().getContent(),"text/html","UTF-8");
                }
            }
        });
    }



    private void setAndroidNativeLightStatusBar(Dialog activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

}
