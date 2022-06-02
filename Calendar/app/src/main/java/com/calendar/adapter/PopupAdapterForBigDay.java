package com.calendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.calendar.LookScheduleActivity;
import com.calendar.R;
import com.calendar.bean.BigDay;

import java.util.List;

/**
 * Created by Gkuma on 2022/6/2.
 */

public class PopupAdapterForBigDay extends BaseAdapter {

    private Context mContext;
    private List<BigDay> mList;
    private LayoutInflater mInflater;

    public PopupAdapterForBigDay(Context mContext, List<BigDay> mList) {
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
        PopupViewHolder2 holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.popup_bigday_item, null);
            holder = new PopupViewHolder2();
            holder.popup_item_id = (TextView) convertView.findViewById(R.id.popup_item_id);
            holder.popup_item_title = (TextView) convertView.findViewById(R.id.popup_item_title);
            convertView.setTag(holder);
        } else {
            holder = (PopupViewHolder2) convertView.getTag();
        }

        final BigDay bigDay = mList.get(position);
        final int id = bigDay.id;
        final String title=bigDay.title;
        holder.popup_item_title.setText(title);
        holder.popup_item_id.setText(id + "");

        return convertView;
    }

    class PopupViewHolder2 {
        public TextView popup_item_id;
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