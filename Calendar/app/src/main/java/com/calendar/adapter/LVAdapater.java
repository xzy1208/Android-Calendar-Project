package com.calendar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.calendar.bean.BigDay;
import com.calendar.R;

import java.util.List;

public class LVAdapater extends BaseAdapter {
    private Context mContext;
    private List<BigDay> mList;
    private LayoutInflater mInflater;

    public LVAdapater(Context mContext, List<BigDay> mList){
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
            holder.item_title = (TextView)convertView.findViewById(R.id.item_title);
            holder.item_num = (TextView)convertView.findViewById(R.id.item_num);
            holder.item_day = (TextView)convertView.findViewById(R.id.item_day);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final BigDay bigDay = mList.get(position);
        if(bigDay.type == 1){
            holder.item_title.setText(bigDay.title+"还有");
            holder.item_num.setBackgroundColor(Color.parseColor("#6699ff"));
            holder.item_day.setBackgroundResource(R.drawable.shape_corner_right_blue);
        } else{
            holder.item_title.setText(bigDay.title+"已经");
            holder.item_num.setBackgroundColor(Color.parseColor("#ff9933"));
            holder.item_day.setBackgroundResource(R.drawable.shape_corner_right_yellow);
        }
        holder.item_num.setText(bigDay.num+"");

        return convertView;
    }

    class ViewHolder {
        public TextView item_title;
        public TextView item_num;
        public TextView item_day;
    }
}
