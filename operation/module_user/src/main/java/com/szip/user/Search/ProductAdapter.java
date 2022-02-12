package com.szip.user.Search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.szip.blewatch.base.db.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.user.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context mContext;

    private List<SportWatchAppFunctionConfigDTO> mDataList = new ArrayList<>();

    public ProductAdapter(Context context) {
        mContext = context;
    }

    public void setDataList(List<SportWatchAppFunctionConfigDTO> datas) {
        mDataList.clear();
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.user_adapter_device_list, null, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.nameTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SportWatchAppFunctionConfigDTO result = (SportWatchAppFunctionConfigDTO) getItem(position);

        holder.name.setText(result.getAppName());

        return convertView;
    }
}