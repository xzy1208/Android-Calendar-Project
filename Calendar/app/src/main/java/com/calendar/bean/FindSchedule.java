package com.calendar.bean;

import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ChunY on 2022/5/28.
 */

public class FindSchedule {
    public static List<ScheduleDate> scheduleDateList = new ArrayList<>();

    public FindSchedule(List<Schedule> scheduleList){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,c.get(Calendar.YEAR)+5);// 5年后
        Timestamp futureTime = new Timestamp(c.getTimeInMillis());
        Log.e("未来5年内",futureTime.getYear()+"");

        SimpleDate simpleDate;
        ScheduleDate scheduleDate;
        scheduleDateList = new ArrayList<>();
        for(int i=0;i<scheduleList.size();i++){
            Schedule s = scheduleList.get(i);

            do{
                Timestamp startTime = new Timestamp(s.startTime.getTime());
                Timestamp endTime = new Timestamp(s.endTime.getTime());
                startTime.setHours(0);
                startTime.setMinutes(0);
                startTime.setSeconds(0);
                startTime.setNanos(0);
                endTime.setHours(0);
                endTime.setMinutes(0);
                endTime.setSeconds(0);
                endTime.setNanos(0);

                if(startTime.getTime() == endTime.getTime()){ // 一天
                    if(s.isAllDay == 1){
                        simpleDate = new SimpleDate(s.id, 0,s.startTime, s.endTime,s.title);
                    }else{
                        simpleDate = new SimpleDate(s.id, 3,s.startTime, s.endTime,s.title);
                    }
                    int t;
                    //Log.e("一天",startTime.getTime()+"");
                    for(t=0;t<scheduleDateList.size();t++){
                        //Log.e(t+"",scheduleDateList.get(t).date.getTime()+"");
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
                        //Log.e("跨天",t+"");
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
                            //Log.e(z+"",scheduleDateList.get(z).date.getTime()+"");
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

                // 判断循环
                if(s.repeatCycle == 0){
                    break;
                } else if(s.repeatCycle == 1){
                    s.startTime.setDate(s.startTime.getDate()+s.repeatInterval);
                    s.endTime.setDate(s.endTime.getDate()+s.repeatInterval);
                }else if(s.repeatCycle == 2){
                    s.startTime.setDate(s.startTime.getDate()+7*s.repeatInterval);
                    s.endTime.setDate(s.endTime.getDate()+7*s.repeatInterval);
                }else if(s.repeatCycle == 3){
                    s.startTime.setMonth(s.startTime.getMonth()+s.repeatInterval);
                    s.endTime.setMonth(s.endTime.getMonth()+s.repeatInterval);
                }else if(s.repeatCycle == 4){
                    Log.e("循环年",s.startTime.getYear()+"");
                    s.startTime.setYear(s.startTime.getYear()+s.repeatInterval);
                    s.endTime.setYear(s.endTime.getYear()+s.repeatInterval);
                }
            }while(s.startTime.getTime() < futureTime.getTime());
        }

        sort();
    }

    // 排序
    public void sort(){
        for(int i=0;i<scheduleDateList.size();i++){
            for(int j=scheduleDateList.size()-1;j>i;j--){
                ScheduleDate s1 = scheduleDateList.get(j);
                ScheduleDate s2 = scheduleDateList.get(j-1);
                if(s1.date.getTime()<s2.date.getTime()){
                    scheduleDateList.set(j,s2);
                    scheduleDateList.set(j-1,s1);
                }
            }
        }
    }
}
