package androiddeveloper_sx.calendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androiddeveloper_sx.calendar.LookScheduleActivity;
import androiddeveloper_sx.calendar.bean.ScheduleDate;
import edu.zjut.androiddeveloper_sx.calendar.R;
import androiddeveloper_sx.calendar.bean.SimpleDate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ChunY on 2022/5/26.
 */

public class ScheduleAdapter1 extends BaseAdapter{

    private Context mContext;
    private List<ScheduleDate> mList;
    private LayoutInflater mInflater;
    private int type;

    public ScheduleAdapter1(Context mContext, List<ScheduleDate> mList){
        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(this.mContext);
        type = 0;
    }

    public ScheduleAdapter1(Context mContext, List<ScheduleDate> mList, int type){
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
        ScheduleAdapter1.ViewHolder holder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.schedulelistview_item, null);
            holder = new ScheduleAdapter1.ViewHolder();
            holder.schedule_item_date = (TextView)convertView.findViewById(R.id.schedule_item_date);
            holder.schedule_item_ListView = (ListView) convertView.findViewById(R.id.schedule_item_listview);
            convertView.setTag(holder);
        }else{
            holder = (ScheduleAdapter1.ViewHolder) convertView.getTag();
        }

        final ScheduleDate scheduleDate = mList.get(position);
        final Timestamp date = scheduleDate.date;
        final List<SimpleDate> simpleDateList = scheduleDate.simpleDateList;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String dateStr = sdf.format(calendar.getTime());
        String[] weekDays = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
        holder.schedule_item_date.setText(dateStr+" "+ weekDays[calendar.get(Calendar.DAY_OF_WEEK)-1]);

        holder.schedule_item_ListView.setAdapter(new ScheduleAdapter2(mContext, simpleDateList, type));
        holder.schedule_item_ListView.setOnItemClickListener(new MyOnItemClickListener());

        return convertView;
    }

    class ViewHolder {
        public TextView schedule_item_date;
        public ListView schedule_item_ListView;
    }

    // listView点击事件
    public class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            TextView schedule_item_id = (TextView)view.findViewById(R.id.schedule_item_id);

            Intent intent = new Intent(mContext, LookScheduleActivity.class);;
            intent.putExtra("id",Integer.parseInt(schedule_item_id.getText().toString()));
            mContext.startActivity(intent);
        }
    }

}
