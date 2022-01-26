package com.szip.healthy.activity.sport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.szip.blewatch.base.Util.DateUtil;
import com.szip.blewatch.base.db.dbModel.SportData;
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
        TextView timeTv,typeTv,sportTimeTv;
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
            holder.sportTimeTv = convertView.findViewById(R.id.sportTimeTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SportData result = (SportData) getItem(position);

        holder.timeTv.setText(DateUtil.getStringDateFromSecond(result.time,"yyyy/MM/dd HH:mm:ss"));
        holder.sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",result.sportTime/3600,
                result.sportTime%3600/60,result.sportTime%3600%60));
        switch (result.type){
            case 1:{//徒步
                holder.typeTv.setText("徒步");
            }
            break;
            case 2:{//跑步
                holder.typeTv.setText("室外跑");
            }
            break;
            case 5:{//马拉松
                holder.typeTv.setText("马拉松");
            }
            break;
            case 6:{
                holder.typeTv.setText("计划跑");
            }
            break;
            case 7:
            case 3:{//室内跑步
                holder.typeTv.setText("室内跑步");
            }
            break;
            case 4:{//登山
                holder.typeTv.setText("登山");
            }
            break;
            case 8:{//跳绳
                holder.typeTv.setText("跳绳");
            }
            break;
            case 9:{//羽毛球
                holder.typeTv.setText("羽毛球");
            }
            break;
            case 10:{//篮球
                holder.typeTv.setText("篮球");
            }
            break;
            case 11:{//骑行
                holder.typeTv.setText("骑行");
            }
            break;
            case 12:{//滑冰
                holder.typeTv.setText("滑冰");
            }
            break;
            case 13:{//健身房
                holder.typeTv.setText("跑步机");
            }
            break;
            case 14:{//瑜伽
                holder.typeTv.setText("瑜伽");
            }
            break;
            case 15:{//网球
                holder.typeTv.setText("网球");
            }
            break;
            case 16:{//乒乓球
                holder.typeTv.setText("乒乓球");
            }
            break;
            case 17:{//足球
                holder.typeTv.setText("足球");
            }
            break;
            case 18:{//游泳
                holder.typeTv.setText("游泳");
            }
            break;
            case 19:{//攀岩
                holder.typeTv.setText("攀岩");
            }
            break;
            case 20:{//划船
                holder.typeTv.setText("划船");
            }
            break;
            case 22:{//冲浪
              holder.typeTv.setText("冲浪");
            }
            break;
        }
        return convertView;
    }
}
