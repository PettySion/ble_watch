package com.szip.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.szip.blewatch.base.db.dbModel.NotificationData;
import com.szip.blewatch.base.db.dbModel.NotificationData_Table;
import com.szip.user.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    private List<NotificationData> notificationDatas = new ArrayList<>();
    private Context mContext;

    public class ViewHolder {
        public TextView nameTv;

        public ImageView iconIv;

        public Switch stateSw;
    }

    public NotificationAdapter(Context context) {
        mContext = context;
    }

    public void setNotificationDatas(List<NotificationData> notificationDatas) {
        if (notificationDatas==null)
            this.notificationDatas.clear();
        else
            this.notificationDatas = notificationDatas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (notificationDatas.size());
    }

    @Override
    public Object getItem(int position) {
        return notificationDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;
        /*
         * TextView tvAppName; ImageView ivIcon; Switch swPush;
         */

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.user_adaper_notification, null, false);
            viewHolder = new ViewHolder();
            viewHolder.nameTv = view.findViewById(R.id.nameTv);
            viewHolder.iconIv = view.findViewById(R.id.iconIv);
            viewHolder.stateSw = view.findViewById(R.id.stateSw);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        NotificationData packageItem = null;

        viewHolder.stateSw
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        NotificationData data = notificationDatas.get(position);
                        NotificationData sqlData = SQLite.select()
                                .from(NotificationData.class)
                                .where(NotificationData_Table.id.is(data.id))
                                .querySingle();
                        sqlData.state = isChecked;
                        sqlData.update();
                    }
                });

        packageItem = notificationDatas.get(position);
        viewHolder.iconIv.setImageDrawable(mContext.getResources().getDrawable(packageItem.packageImgId));
        viewHolder.nameTv.setText(packageItem.name);
        viewHolder.stateSw.setChecked(packageItem.state);

        return view;
    }
}
