package com.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.calendar.adapter.ScheduleAdapter1;
import com.calendar.bean.Schedule;
import com.calendar.bean.ScheduleDate;
import com.calendar.bean.SimpleDate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleActivity extends Activity {

    private ListView scheduleListView;

    private List<ScheduleDate> scheduleDateList;

    private static List<Schedule> scheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        testData();
        initView();
    }

    private void initView(){
        scheduleListView = (ListView)findViewById(R.id.scheduleListView);
        scheduleListView.setAdapter(new ScheduleAdapter1(ScheduleActivity.this,scheduleDateList));
    }

    private void testData(){
        //数据1
        scheduleList = new ArrayList<>();

        Timestamp startTime = new Timestamp(new Date(2022-1900,5-1,27).getTime());
        Timestamp endTime = new Timestamp(new Date(2022-1900,5-1,27).getTime());
        Schedule s = new Schedule(0, "测试1", 1, startTime, endTime);
        scheduleList.add(s);

        startTime = new Timestamp(new Date(2022-1900,5-1,27).getTime());
        endTime = new Timestamp(new Date(2022-1900,5-1,30).getTime());
        s = new Schedule(1, "测试2", 1, startTime, endTime);
        scheduleList.add(s);

        startTime = new Timestamp(new Date(2022-1900,5-1,27,9,42).getTime());
        endTime = new Timestamp(new Date(2022-1900,5-1,27,19,27).getTime());
        s = new Schedule(2, "测试3", 0, startTime, endTime);
        scheduleList.add(s);

        startTime = new Timestamp(new Date(2022-1900,5-1,27,9,42).getTime());
        endTime = new Timestamp(new Date(2022-1900,5-1,30,19,27).getTime());
        s = new Schedule(3, "测试4", 0, startTime, endTime);
        scheduleList.add(s);

        //数据2
        SimpleDate simpleDate;
        ScheduleDate scheduleDate;
        scheduleDateList = new ArrayList<>();
        for(int i=0;i<scheduleList.size();i++){
            Log.e("i",i+"");
            s = scheduleList.get(i);
            startTime = new Timestamp(s.startTime.getTime());
            endTime = new Timestamp(s.endTime.getTime());
            startTime.setHours(0);
            startTime.setMinutes(0);
            startTime.setSeconds(0);
            endTime.setHours(0);
            endTime.setMinutes(0);
            endTime.setSeconds(0);
            if(startTime.getTime() == endTime.getTime()){ // 一天
                if(s.isAllDay == 1){
                    simpleDate = new SimpleDate(s.id, 0,s.startTime, s.endTime,s.title);
                }else{
                    simpleDate = new SimpleDate(s.id, 3,s.startTime, s.endTime,s.title);
                }
                int t;
                for(t=0;t<scheduleDateList.size();t++){
                    if(startTime.getTime() == scheduleDateList.get(t).date.getTime()){ // 存在这天
                        scheduleDateList.get(t).simpleDateList.add(simpleDate);
                        break;
                    }
                }
                if(t == scheduleDateList.size()){
                    List<SimpleDate> simpleDateList = new ArrayList<>();
                    simpleDateList.add(simpleDate);
                    scheduleDate = new ScheduleDate(startTime,simpleDateList);
                    scheduleDateList.add(scheduleDate);
                }
            }else{ // 跨天
                for(long t=startTime.getTime();t<=endTime.getTime();t+=24*60*60*1000){
                    if(s.isAllDay == 1){
                        simpleDate = new SimpleDate(s.id, 0,s.startTime, s.endTime,s.title);
                    }else {
                        if(t == startTime.getTime()){
                            simpleDate = new SimpleDate(s.id, 1,s.startTime, s.endTime,s.title);
                        }else if(t == endTime.getTime()){
                            simpleDate = new SimpleDate(s.id, 2,s.startTime, s.endTime,s.title);
                        }else{
                            simpleDate = new SimpleDate(s.id, 0,s.startTime, s.endTime,s.title);
                        }
                    }
                    int z;
                    for(z=0;z<scheduleDateList.size();z++){
                        if(t == scheduleDateList.get(z).date.getTime()){ // 存在这天
                            scheduleDateList.get(z).simpleDateList.add(simpleDate);
                            break;
                        }
                    }
                    if(z == scheduleDateList.size()){
                        List<SimpleDate> simpleDateList = new ArrayList<>();
                        simpleDateList.add(simpleDate);
                        scheduleDate = new ScheduleDate(new Timestamp(t),simpleDateList);
                        scheduleDateList.add(scheduleDate);
                    }
                }
            }
        }
    }

    public static Schedule getSchedule(int id){
        return scheduleList.get(id);
    }

}
