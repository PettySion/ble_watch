package com.szip.sport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.szip.blewatch.base.Const.SportConst;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.db.dbModel.SportData;
import com.szip.blewatch.base.interfere.OnItemClickListener;
import com.szip.blewatch.base.model.SportTypeModel;
import com.szip.sport.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LastSportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<SportData> list = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private Context mContext;


    public LastSportAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setList(List<SportData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sport_adapter_last_sport, null);
        final SportHolder holder = new SportHolder(view);
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
        SportData sportData = list.get(position);
        SportHolder sportHolder = (SportHolder) holder;
        SportTypeModel sportTypeModel = MathUtil.newInstance().getSportType(sportData.type,mContext);
        if (sportTypeModel==null)
            return;
        sportHolder.sportIv.setImageResource(sportTypeModel.getType());
        sportHolder.sportTv.setText(sportTypeModel.getSportStr());
        sportHolder.timeTv.setText(String.format("%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        sportHolder.calorieTv.setText(String.format(Locale.ENGLISH,"%.1fkcal",((sportData.calorie+55)/100)/10f));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SportHolder extends RecyclerView.ViewHolder {

        private View fruitView;  //表示我们自定义的控件的视图
        private ImageView sportIv;
        private TextView sportTv,timeTv,calorieTv;
        public SportHolder(View itemView) {
            super(itemView);
            fruitView = itemView;
            sportIv = itemView.findViewById(R.id.sportIv);
            sportTv = itemView.findViewById(R.id.sportTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            calorieTv = itemView.findViewById(R.id.calorieTv);
        }
    }
}
