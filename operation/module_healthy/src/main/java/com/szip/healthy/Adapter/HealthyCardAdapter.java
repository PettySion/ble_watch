package com.szip.healthy.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.Const.HealthyConst;
import com.szip.blewatch.base.interfere.OnItemClickListener;
import com.szip.healthy.R;
import com.szip.healthy.model.HealthyData;
import com.szip.healthy.view.HealthyTableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HealthyCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<HealthyData> healthyDataList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public HealthyCardAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setHealthyDataList(List<HealthyData> healthyDataList) {
        this.healthyDataList = healthyDataList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.healthy_adapter_healthy_card, null);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.healthy_adapter_healthy_edit, null);
        final Holder holder = new Holder(view);
        //对加载的子项注册监听事件
        holder.fruitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener!=null)
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0&&position!=healthyDataList.size()){
            HealthyData healthyData = healthyDataList.get(position);
            ((Holder) holder).healthyTableView.setHealthyData(healthyData);

            if (healthyData.getType() == HealthyConst.HEART){
                ((Holder) holder).typeIv.setImageResource(R.mipmap.state_hr);
                ((Holder) holder).typeTv.setText(mContext.getString(R.string.healthy_heart));
                if (healthyData.getTime() == 0)
                    return;
                ((Holder) holder).dataTv.setText(Html.fromHtml(String.format(Locale.ENGLISH,"<big>%d</big> Bpm",healthyData.getData())));
                ((Holder) holder).timeTv.setText(DateUtil.getStringDateFromSecond(healthyData.getTime(),"yyyy/MM/dd"));
            }else if (healthyData.getType() == HealthyConst.STEP){
                ((Holder) holder).typeIv.setImageResource(R.mipmap.state_steps_32);
                ((Holder) holder).typeTv.setText(mContext.getString(R.string.healthy_step));
                if (healthyData.getTime() == 0)
                    return;
                ((Holder) holder).dataTv.setText(Html.fromHtml(String.format(Locale.ENGLISH,"<big>%d</big> steps", healthyData.getData())));
                ((Holder) holder).timeTv.setText(DateUtil.getStringDateFromSecond(healthyData.getTime(),"yyyy/MM/dd"));
            }else if (healthyData.getType() == HealthyConst.SLEEP){
                ((Holder) holder).typeIv.setImageResource(R.mipmap.state_sleep);
                ((Holder) holder).typeTv.setText(mContext.getString(R.string.healthy_sleep));
                if (healthyData.getTime() == 0)
                    return;
                ((Holder) holder).dataTv.setText(Html.fromHtml(String.format(Locale.ENGLISH,"<big>%02d</big>h<big>%02d</big>min",
                        healthyData.getData()/60,healthyData.getData()%60)));
                ((Holder) holder).timeTv.setText(DateUtil.getStringDateFromSecond(healthyData.getTime(),"yyyy/MM/dd"));
            }else if (healthyData.getType() == HealthyConst.BLOOD_OXYGEN){
                ((Holder) holder).typeIv.setImageResource(R.mipmap.state_spo2);
                ((Holder) holder).typeTv.setText(mContext.getString(R.string.healthy_blood_oxygen));
                if (healthyData.getTime() == 0)
                    return;
                ((Holder) holder).dataTv.setText(Html.fromHtml(String.format(Locale.ENGLISH,"<big>%d</big>%%",healthyData.getData())));
                ((Holder) holder).timeTv.setText(DateUtil.getStringDateFromSecond(healthyData.getTime(),"yyyy/MM/dd"));
            }else if (healthyData.getType() == HealthyConst.BLOOD_PRESSURE){
                ((Holder) holder).typeIv.setImageResource(R.mipmap.state_bp);
                ((Holder) holder).typeTv.setText(mContext.getString(R.string.healthy_blood_pressure));
                if (healthyData.getTime() == 0)
                    return;
                ((Holder) holder).dataTv.setText(Html.fromHtml(String.format(Locale.ENGLISH,"<big>%d/%d</big>mmhg",healthyData.getData(),healthyData.getData1())));
                ((Holder) holder).timeTv.setText(DateUtil.getStringDateFromSecond(healthyData.getTime(),"yyyy/MM/dd"));
            }else if (healthyData.getType() == HealthyConst.TEMPERATURE){
                ((Holder) holder).typeIv.setImageResource(R.mipmap.state_temperature);
                ((Holder) holder).typeTv.setText(mContext.getString(R.string.healthy_temp));
                if (healthyData.getTime() == 0)
                    return;
                ((Holder) holder).dataTv.setText(Html.fromHtml(String.format(Locale.ENGLISH,"<big>%.1f</big> ℃",healthyData.getData()/10f)));
                ((Holder) holder).timeTv.setText(DateUtil.getStringDateFromSecond(healthyData.getTime(),"yyyy/MM/dd"));
            }

        }
    }

    @Override
    public int getItemCount() {
        return healthyDataList.size()%2==0?healthyDataList.size():healthyDataList.size()+1;
    }

    class Holder extends RecyclerView.ViewHolder {

        private View fruitView;  //表示我们自定义的控件的视图
        private ImageView typeIv;
        private TextView typeTv,dataTv,timeTv;
        private HealthyTableView healthyTableView;
        public Holder(View itemView) {
            super(itemView);
            fruitView = itemView;
            typeIv = itemView.findViewById(R.id.typeIv);
            typeTv = itemView.findViewById(R.id.typeTv);
            dataTv = itemView.findViewById(R.id.dataTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            healthyTableView = itemView.findViewById(R.id.healthyTableView);
        }
    }

    class EditHolder extends RecyclerView.ViewHolder {

        private View fruitView;  //表示我们自定义的控件的视图
        private TextView editTv;
        public EditHolder(View itemView) {
            super(itemView);
            fruitView = itemView;
            editTv = itemView.findViewById(R.id.editTv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (healthyDataList.size()%2==1&&position == healthyDataList.size())
            return 1;
        return 0;
    }
}
