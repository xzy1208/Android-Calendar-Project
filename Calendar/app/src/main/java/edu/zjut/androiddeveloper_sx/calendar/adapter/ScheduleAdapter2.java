package edu.zjut.androiddeveloper_sx.calendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import edu.zjut.androiddeveloper_sx.calendar.R;
import edu.zjut.androiddeveloper_sx.calendar.bean.SimpleDate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ChunY on 2022/5/26.
 */

public class ScheduleAdapter2 extends BaseAdapter{

    private Context mContext;
    private List<SimpleDate> mList;
    private LayoutInflater mInflater;
    private int type;

    public ScheduleAdapter2(Context mContext, List<SimpleDate> mList){
        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(this.mContext);
        type = 0;
    }

    public ScheduleAdapter2(Context mContext, List<SimpleDate> mList ,int type){
        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(this.mContext);
        this.type = type;
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
        ScheduleAdapter2.ViewHolder holder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.schedule_item_item, null);
            holder = new ScheduleAdapter2.ViewHolder();
            holder.schedule_item_id = (TextView)convertView.findViewById(R.id.schedule_item_id);
            holder.schedule_item_delCB = (CheckBox)convertView.findViewById(R.id.schedule_item_delCB);
            holder.schedule_item_startTime = (TextView)convertView.findViewById(R.id.schedule_item_startTime);
            holder.schedule_item_endTime = (TextView) convertView.findViewById(R.id.schedule_item_endTime);
            holder.schedule_item_title = (TextView) convertView.findViewById(R.id.schedule_item_title);
            convertView.setTag(holder);
        }else{
            holder = (ScheduleAdapter2.ViewHolder) convertView.getTag();
        }

        final SimpleDate simpleDate = mList.get(position);
        final int id = simpleDate.id;
        final int type = simpleDate.type;
        final Timestamp startTime = simpleDate.startTime;
        final Timestamp endTime = simpleDate.endTime;
        final String title = simpleDate.title;
        final boolean isChecked = simpleDate.isChecked;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        switch(type){
            case 0:
                holder.schedule_item_startTime.setText("全天");
                holder.schedule_item_endTime.setVisibility(View.GONE);
                break;
            case 1:
                calendar.setTime(startTime);
                holder.schedule_item_startTime.setText(sdf.format(calendar.getTime()));
                holder.schedule_item_endTime.setText("开始");
                holder.schedule_item_endTime.setVisibility(View.VISIBLE);
                break;
            case 2:
                calendar.setTime(endTime);
                holder.schedule_item_startTime.setText(sdf.format(calendar.getTime()));
                holder.schedule_item_endTime.setText("结束");
                holder.schedule_item_endTime.setVisibility(View.VISIBLE);
                break;
            case 3:
                calendar.setTime(startTime);
                holder.schedule_item_startTime.setText(sdf.format(calendar.getTime()));
                calendar.setTime(endTime);
                holder.schedule_item_endTime.setText(sdf.format(calendar.getTime()));
                holder.schedule_item_endTime.setVisibility(View.VISIBLE);
        }
        holder.schedule_item_title.setText(title);
        holder.schedule_item_id.setText(id+"");

        // 显示删除复选框
        if(this.type == 1){
            holder.schedule_item_delCB.setVisibility(View.VISIBLE);
        }else{
            holder.schedule_item_delCB.setVisibility(View.GONE);
        }

        holder.schedule_item_delCB.setChecked(simpleDate.isChecked);
        holder.schedule_item_delCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simpleDate.isChecked) {
                    simpleDate.isChecked = false;
                } else {
                    simpleDate.isChecked = true;
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        public TextView schedule_item_id;
        public CheckBox schedule_item_delCB;
        public TextView schedule_item_startTime;
        public TextView schedule_item_endTime;
        public TextView schedule_item_title;
    }

}
