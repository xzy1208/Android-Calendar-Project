package com.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.calendar.adapter.LVAdapater;
import com.calendar.bean.BigDay;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BigDayActivity extends Activity {

    private ListView bigDayListView;

    private List<BigDay> bigDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bigday);

        initView();

        displayBigDay();

        bigDayListView.setOnItemClickListener(new MyOnItemClickListener());
        //registerForContextMenu(bigDayListView);//进行注册
    }

    // 初始化控件
    private void initView(){
        bigDayListView = (ListView)findViewById(R.id.bigDayListView);
    }

    // 排序 倒数日在正数日前 时间小的在前
    private List<BigDay> sort(List<BigDay> bigDays){
        for(int i=0;i<bigDays.size();i++){
            for(int j=bigDays.size()-1;j>i;j--){
                if((bigDays.get(j).type == bigDays.get(j-1).type && bigDays.get(j).num < bigDays.get(j-1).num) || (bigDays.get(j).type == 1 && bigDays.get(j-1).type == 0)){
                    BigDay t = bigDays.get(j);
                    bigDays.set(j, bigDays.get(j-1));
                    bigDays.set(j-1, t);
                }
            }
        }
        return bigDays;
    }

    // 动态显示界面日期
    private void displayBigDay(){
        bigDays = new ArrayList<BigDay>();

        // 测试数据 date设置年份要-1900 月份要-1
        Timestamp date = new Timestamp(new Date(2022-1900,6-1,8).getTime());
        bigDays.add(new BigDay("邢丽瑶生日", 1, date));
        date = new Timestamp(new Date(2022-1900,6-1,21).getTime());
        bigDays.add(new BigDay("项阳阳生日", 1, date));
        date = new Timestamp(new Date(2022-1900,8-1,2).getTime());
        bigDays.add(new BigDay("蔡徐坤生日", 1, date));
        date = new Timestamp(new Date(2022-1900,11-1,2).getTime());
        bigDays.add(new BigDay("易思源生日", 1, date));
        date = new Timestamp(new Date(2022-1900,12-1,8).getTime());
        bigDays.add(new BigDay("项紫依生日", 1, date));
        date = new Timestamp(new Date(2020-1900,3-1,12).getTime());
        bigDays.add(new BigDay("初识", 0, date));
        //

        bigDays = sort(bigDays);
        bigDayListView.setAdapter(new LVAdapater(BigDayActivity.this,bigDays));

        LinearLayout bigDayFirstLL = (LinearLayout)findViewById(R.id.bigDayFirstLL);
        if(bigDays.size() > 0){
            BigDay bigDay = bigDays.get(0);
            TextView bigDayFirstTitle = (TextView)findViewById(R.id.bigDayFirstTitle);
            TextView bigDayFirstDate = (TextView)findViewById(R.id.bigDayFirstDate);
            TextView bigDayFirstNum = (TextView)findViewById(R.id.bigDayFirstNum);
            if(bigDay.type == 1){
                bigDayFirstTitle.setText(bigDay.title+"还有");
            }else{
                bigDayFirstTitle.setText(bigDay.title+"已经");
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(bigDay.date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(calendar.getTime());
            String[] weekDays = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};

            bigDayFirstDate.setText("目标日："+dateStr+" "+ weekDays[calendar.get(Calendar.DAY_OF_WEEK)-1]);
            bigDayFirstNum.setText(bigDay.num+"");

            bigDayFirstLL.setVisibility(View.VISIBLE);
        }else{
            bigDayFirstLL.setVisibility(View.GONE);
        }
    }

    // listView点击事件
    class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            BigDay bigDay = bigDays.get(position);

            Intent intent = new Intent();
            intent.setClass(BigDayActivity.this, LookBigDayActivity.class);
            intent.putExtra("id",bigDay.id);
            //
            intent.putExtra("title",bigDay.title);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(bigDay.date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(calendar.getTime());
            String[] weekDays = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
            Log.e("week",calendar.get(Calendar.DAY_OF_WEEK)+"");
            intent.putExtra("date","目标日："+dateStr+" "+ weekDays[calendar.get(Calendar.DAY_OF_WEEK)-1]);

            intent.putExtra("repeatInterval",bigDay.repeatInterval);
            intent.putExtra("repeatCycle",bigDay.repeatCycle);
            intent.putExtra("remindTime",bigDay.remindTime);
            intent.putExtra("type",bigDay.type);
            intent.putExtra("supplement",bigDay.supplement);
            intent.putExtra("num",bigDay.num);
            //
            startActivity(intent);
        }
    }
}
