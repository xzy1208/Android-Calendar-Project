package com.calendar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.calendar.R;
import com.calendar.bean.BigDay;
import com.calendar.db.DBAdapter;

import java.sql.Timestamp;
import java.util.List;

public class LVAdapter extends BaseAdapter {
    private DBAdapter db;

    private Context mContext;
    private List<BigDay> mList;
    private LayoutInflater mInflater;

    public LVAdapter(Context mContext, List<BigDay> mList){
        db = DBAdapter.setDBAdapter(mContext);
        db.open();

        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.bigdaylistview_item, null);
            holder = new ViewHolder();
            holder.item_title = (TextView)convertView.findViewById(R.id.bigDay_item_title);
            holder.item_num = (TextView)convertView.findViewById(R.id.bigDay_item_num);
            holder.item_day = (TextView)convertView.findViewById(R.id.bigDay_item_day);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final BigDay bigDay = mList.get(position);

        Timestamp t = new Timestamp(System.currentTimeMillis());
        t.setHours(0);
        t.setMinutes(0);
        t.setSeconds(0);
        t.setNanos(0);

        if(bigDay.type == 1){
            if(bigDay.repeatCycle == 0 && bigDay.date.getTime() - t.getTime() < 0){// 不重复、倒数时间到了 修改数据库type
                bigDay.type = 0;
                db.updateOneDataFromBigDay(bigDay.id,bigDay);
            }
            if(bigDay.repeatCycle != 0){// 重复 修改前端显示date
                while(bigDay.date.getTime() < t.getTime()){// 一直加重复周期，直到日期大于今天
                    if(bigDay.repeatCycle == 1){
                        bigDay.date.setDate(bigDay.date.getDate()+bigDay.repeatInterval);
                    }else if(bigDay.repeatCycle == 2){
                        bigDay.date.setDate(bigDay.date.getDate()+7*bigDay.repeatInterval);
                    }else if(bigDay.repeatCycle == 3){
                        bigDay.date.setMonth(bigDay.date.getMonth()+bigDay.repeatInterval);
                    }else if(bigDay.repeatCycle == 4){
                        bigDay.date.setYear(bigDay.date.getYear()+bigDay.repeatInterval);
                    }
                }
            }
        }

        if(bigDay.type == 1){
            holder.item_title.setText(bigDay.title+"还有");
            holder.item_num.setBackgroundColor(Color.parseColor("#6699ff"));
            holder.item_day.setBackgroundResource(R.drawable.shape_corner_right_blue);
        } else{
            holder.item_title.setText(bigDay.title+"已经");
            holder.item_num.setBackgroundColor(Color.parseColor("#ff9933"));
            holder.item_day.setBackgroundResource(R.drawable.shape_corner_right_yellow);
        }

        long num = Math.abs(bigDay.date.getTime() - t.getTime())/(24*60*60*1000);
        holder.item_num.setText(num+"");
        Log.e("LVAdapter",bigDay.title+":"+num);

        return convertView;
    }

    class ViewHolder {
        public TextView item_title;
        public TextView item_num;
        public TextView item_day;
    }
}
