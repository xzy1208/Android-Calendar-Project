package com.calendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.calendar.R;
import com.calendar.bean.Place;

import java.util.List;

/**
 * Created by ChunY on 2022/6/1.
 */

public class PlaceAdapter extends BaseAdapter {

    private Context mContext;
    private List<Place> mList;
    private LayoutInflater mInflater;

    public PlaceAdapter(Context mContext, List<Place> mList){
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
            convertView = mInflater.inflate(R.layout.place_listview_item, null);
            holder = new ViewHolder();
            holder.item_title = (TextView)convertView.findViewById(R.id.place_item_title);
            holder.item_snippet = (TextView)convertView.findViewById(R.id.place_item_snippet);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item_title.setText(mList.get(position).title);
        holder.item_snippet.setText(mList.get(position).snippet);

        return convertView;
    }

    class ViewHolder {
        public TextView item_title;
        public TextView item_snippet;
    }

}