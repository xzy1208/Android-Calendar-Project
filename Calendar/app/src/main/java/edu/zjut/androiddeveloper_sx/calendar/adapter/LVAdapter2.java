package edu.zjut.androiddeveloper_sx.calendar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import edu.zjut.androiddeveloper_sx.calendar.bean.BigDay;
import edu.zjut.androiddeveloper_sx.calendar.db.DBAdapter;
import edu.zjut.androiddeveloper_sx.calendar.R;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ChunY on 2022/5/31.
 */

public class LVAdapter2 extends BaseAdapter {
    private DBAdapter db;

    private Context mContext;
    private List<BigDay> mList;
    private LayoutInflater mInflater;

    public LVAdapter2(Context mContext, List<BigDay> mList){
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

        if(bigDay.date.getTime() >= t.getTime()){
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
        Log.e("LVAdapter2",bigDay.title+":"+num);

        return convertView;
    }

    class ViewHolder {
        public TextView item_title;
        public TextView item_num;
        public TextView item_day;
    }
}
