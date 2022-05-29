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
import com.calendar.db.DBAdapter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;;
import java.util.Calendar;
import java.util.List;

public class BigDayActivity extends Activity {

    public DBAdapter db;

    private ListView bigDayListView;

    private List<BigDay> bigDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bigday);

        db = DBAdapter.setDBAdapter(BigDayActivity.this);
        db.open();

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        displayBigDay();
    }

    // 初始化控件
    private void initView(){
        bigDayListView = (ListView)findViewById(R.id.bigDayListView);
        bigDayListView.setOnItemClickListener(new MyOnItemClickListener());
    }

    // 排序 倒数日在正数日前 时间小的在前
    private List<BigDay> sort(List<BigDay> bigDays){
        for(int i=0;i<bigDays.size();i++){
            for(int j=bigDays.size()-1;j>i;j--){
                long num1 = Math.abs(bigDays.get(j).date.getTime() - new Timestamp(System.currentTimeMillis()).getTime())/(24*60*60*1000);
                long num2 = Math.abs(bigDays.get(j-1).date.getTime() - new Timestamp(System.currentTimeMillis()).getTime())/(24*60*60*1000);
                if((bigDays.get(j).type == bigDays.get(j-1).type && num1 < num2) || (bigDays.get(j).type == 1 && bigDays.get(j-1).type == 0)){
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
        bigDays = db.getAllDataFromBigDay();
        if(bigDays == null) return;

        bigDays = sort(bigDays);
        bigDayListView.setAdapter(new LVAdapater(BigDayActivity.this,bigDays));

        LinearLayout bigDayFirstLL = (LinearLayout)findViewById(R.id.bigDayFirstLL);
        if(bigDays.size() > 0){
            BigDay bigDay = bigDays.get(0);
            TextView bigDayFirstTitle = (TextView)findViewById(R.id.bigDayFirstTitle);
            TextView bigDayFirstDate = (TextView)findViewById(R.id.bigDayFirstDate);
            TextView bigDayFirstNum = (TextView)findViewById(R.id.bigDayFirstNum);

            Timestamp t = new Timestamp(System.currentTimeMillis());
            t.setHours(0);
            t.setMinutes(0);
            t.setSeconds(0);
            t.setNanos(0);
            if(bigDay.type == 1 && bigDay.repeatCycle != 0){// 重复 修改前端显示date
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

            long num = Math.abs(bigDay.date.getTime() - t.getTime())/(24*60*60*1000);
            bigDayFirstNum.setText(num+"");

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
            startActivity(intent);
        }
    }
}
