package com.szip.healthy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.blewatch.base.Model.SportTypeModel;
import com.szip.healthy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SportListAdapter extends BaseExpandableListAdapter {
    private Context mContext;

    private ArrayList<String> groupList = new ArrayList<>();
    private ArrayList<ArrayList<SportData>> childList = new ArrayList<>();

    public SportListAdapter(Context context) {
        mContext = context;
    }

    public void setDataList(ArrayList<String> groupList,ArrayList<ArrayList<SportData>> childList) {
        this.groupList = groupList;
        this.childList = childList;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.healthy_adapter_sport_time_list, null, false);
            holder = new GroupHolder();
            holder.timeTv = convertView.findViewById(R.id.timeTv);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.timeTv.setText(groupList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.healthy_adapter_sport_list, null, false);
            holder = new ViewHolder();
            holder.timeTv = convertView.findViewById(R.id.timeTv);
            holder.typeTv = convertView.findViewById(R.id.typeTv);
            holder.dataTv = convertView.findViewById(R.id.dataTv);
            holder.typeIv = convertView.findViewById(R.id.typeIv);
            holder.bottomLl = convertView.findViewById(R.id.bottomLl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SportData result = childList.get(groupPosition).get(childPosition);
        SportTypeModel sportTypeModel = MathUtil.newInstance().getSportType(result.type,mContext);

        holder.timeTv.setText(DateUtil.getStringDateFromSecond(result.time,"MM/dd"));
        holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f kcal,%d min",((result.calorie+55)/100)/10f,result.sportTime/60));
        if (sportTypeModel==null)
            return convertView;
        holder.typeTv.setText(sportTypeModel.getSportStr());
        holder.typeIv.setImageResource(sportTypeModel.getType());

        if (groupPosition==groupList.size()-1&&childPosition == childList.get(groupPosition).size()-1)
            holder.bottomLl.setVisibility(View.VISIBLE);
        else
            holder.bottomLl.setVisibility(View.GONE);
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolder {
        ImageView typeIv;
        TextView timeTv,typeTv,dataTv;
        LinearLayout bottomLl;
    }

    private static class GroupHolder{
        TextView timeTv;
    }

}
