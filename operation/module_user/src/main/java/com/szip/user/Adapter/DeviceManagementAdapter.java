package com.szip.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Interfere.OnItemClickListener;
import com.szip.user.R;

import java.util.Arrays;
import java.util.List;

public class DeviceManagementAdapter extends RecyclerView.Adapter<DeviceManagementAdapter.Holder>{

    private int imageList[];
    private List<String> nameList;

    public DeviceManagementAdapter(Context context) {
        nameList = Arrays.asList(context.getString(R.string.user_find_watch),context.getString(R.string.user_ble_call),
                context.getString(R.string.user_ble_camera),context.getString(R.string.user_unit)
                ,context.getString(R.string.user_notification),context.getString(R.string.user_about));
        imageList = new int[6];
        imageList[0] = R.mipmap.cp_icon_empty;
        imageList[1] = R.mipmap.cp_icon_empty;
        imageList[2] = R.mipmap.cp_icon_empty;
        imageList[3] = R.mipmap.cp_icon_empty;
        imageList[4] = R.mipmap.cp_icon_empty;
        imageList[5] = R.mipmap.cp_icon_empty;
        LogUtil.getInstance().logd("data******","size = "+nameList.size());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_adapter_menu, null);
        final Holder holder = new Holder(view);
        //对加载的子项注册监听事件
        holder.fruitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.menuTv.setText(nameList.get(position));
        holder.menuIv.setImageResource(imageList[position]);
    }

    @Override
    public int getItemCount() {
        LogUtil.getInstance().logd("data******","size = "+(nameList == null ? 0 : nameList.size()));
        return nameList == null ? 0 : nameList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView menuTv;
        private ImageView menuIv;
        private View fruitView;  //表示我们自定义的控件的视图

        public Holder(View itemView) {
            super(itemView);
            fruitView = itemView;
            menuTv = itemView.findViewById(R.id.menuTv);
            menuIv = itemView.findViewById(R.id.menuIv);
        }
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

}
