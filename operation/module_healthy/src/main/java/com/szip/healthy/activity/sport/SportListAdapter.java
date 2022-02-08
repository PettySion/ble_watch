package com.szip.healthy.activity.sport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.blewatch.base.model.SportTypeModel;
import com.szip.healthy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SportListAdapter extends BaseAdapter {
    private Context mContext;

    private List<SportData> mDataList = new ArrayList<>();

    public SportListAdapter(Context context) {
        mContext = context;
    }

    public void setDataList(List<SportData> datas) {
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
        ImageView typeIv;
        TextView timeTv,typeTv,dataTv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.healthy_adapter_sport_list, null, false);
            holder = new ViewHolder();
            holder.timeTv = convertView.findViewById(R.id.timeTv);
            holder.typeTv = convertView.findViewById(R.id.typeTv);
            holder.dataTv = convertView.findViewById(R.id.dataTv);
            holder.typeIv = convertView.findViewById(R.id.typeIv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SportData result = (SportData) getItem(position);
        SportTypeModel sportTypeModel = MathUtil.newInstance().getSportType(result.type,mContext);

        holder.timeTv.setText(DateUtil.getStringDateFromSecond(result.time,"MM/dd"));
        holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f kcal,%d min",((result.calorie+55)/100)/10f,result.sportTime/60));
        if (sportTypeModel==null)
            return convertView;
        holder.typeTv.setText(sportTypeModel.getSportStr());
        holder.typeIv.setImageResource(sportTypeModel.getType());
        return convertView;
    }
}
