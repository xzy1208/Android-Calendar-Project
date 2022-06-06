package edu.zjut.androiddeveloper_sx.calendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import edu.zjut.androiddeveloper_sx.calendar.LookScheduleActivity;
import edu.zjut.androiddeveloper_sx.calendar.bean.SimpleDate;
import edu.zjut.androiddeveloper_sx.calendar.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Gkuma on 2022/6/1.
 */

public class PopupAdapterForSchedule extends BaseAdapter {

    private Context mContext;
    private List<SimpleDate> mList;
    private LayoutInflater mInflater;

    public PopupAdapterForSchedule(Context mContext, List<SimpleDate> mList) {
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
        PopupViewHolder1 holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.popup_schedule_item, null);
            holder = new PopupViewHolder1();
            holder.popup_item_id = (TextView) convertView.findViewById(R.id.popup_item_id);
            holder.popup_item_startTime = (TextView) convertView.findViewById(R.id.popup_item_startTime);
            holder.popup_item_endTime = (TextView) convertView.findViewById(R.id.popup_item_endTime);
            holder.popup_item_title = (TextView) convertView.findViewById(R.id.popup_item_title);
            convertView.setTag(holder);
        } else {
            holder = (PopupViewHolder1) convertView.getTag();
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
        switch (type) {
            case 0:
                holder.popup_item_startTime.setText("全天");
                holder.popup_item_endTime.setVisibility(View.GONE);
                break;
            case 1:
                calendar.setTime(startTime);
                holder.popup_item_startTime.setText(sdf.format(calendar.getTime()));
                holder.popup_item_endTime.setText("开始");
                holder.popup_item_endTime.setVisibility(View.VISIBLE);
                break;
            case 2:
                calendar.setTime(endTime);
                holder.popup_item_startTime.setText(sdf.format(calendar.getTime()));
                holder.popup_item_endTime.setText("结束");
                holder.popup_item_endTime.setVisibility(View.VISIBLE);
                break;
            case 3:
                calendar.setTime(startTime);
                holder.popup_item_startTime.setText(sdf.format(calendar.getTime()));
                calendar.setTime(endTime);
                holder.popup_item_endTime.setText(sdf.format(calendar.getTime()));
                holder.popup_item_endTime.setVisibility(View.VISIBLE);
        }
        holder.popup_item_title.setText(title);
        holder.popup_item_id.setText(id + "");

        return convertView;
    }

    class PopupViewHolder1 {
        public TextView popup_item_id;
        public TextView popup_item_startTime;
        public TextView popup_item_endTime;
        public TextView popup_item_title;
    }
    public class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            TextView popup_item_id = (TextView)view.findViewById(R.id.popup_item_id);

            Intent intent = new Intent(mContext, LookScheduleActivity.class);;
            intent.putExtra("id",Integer.parseInt(popup_item_id.getText().toString()));
            mContext.startActivity(intent);
        }
    }
}
