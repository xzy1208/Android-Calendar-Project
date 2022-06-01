package com.calendar.bean;

import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ChunY on 2022/5/31.
 */

public class FindBigDay {
    public static List<BigDay> allBigDayList = new ArrayList<>();

    public FindBigDay(List<BigDay> bigDayList){
        allBigDayList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,c.get(Calendar.YEAR)+2);// 2年后
        Timestamp futureTime = new Timestamp(c.getTimeInMillis());
        futureTime.setHours(0);
        futureTime.setMinutes(0);
        futureTime.setSeconds(0);
        futureTime.setNanos(0);
        Log.e("未来2年内",futureTime.getTime()+"");

        for(int i=0;i<bigDayList.size();i++){
            BigDay b = bigDayList.get(i);
            do{
                BigDay bigDay = new BigDay(b.id, b.title, new Timestamp(b.date.getTime()), b.repeatInterval, b.repeatCycle, b.remindTime, b.type, b.supplement);
                allBigDayList.add(bigDay);
                // 判断循环
                if(b.repeatCycle == 0){
                    break;
                } else if(b.repeatCycle == 1){
                    b.date.setDate(b.date.getDate()+b.repeatInterval);
                }else if(b.repeatCycle == 2){
                    b.date.setDate(b.date.getDate()+7*b.repeatInterval);
                }else if(b.repeatCycle == 3){
                    b.date.setMonth(b.date.getMonth()+b.repeatInterval);
                }else if(b.repeatCycle == 4){
                    b.date.setYear(b.date.getYear()+b.repeatInterval);
                }
            }while(b.date.getTime() < futureTime.getTime());
        }

        //sort();
    }

    // 排序
    public void sort(){
        for(int i=0;i<allBigDayList.size();i++){
            for(int j=allBigDayList.size()-1;j>i;j--){
                BigDay s1 = allBigDayList.get(j);
                BigDay s2 = allBigDayList.get(j-1);
                if(s1.date.getTime()<s2.date.getTime()){
                    allBigDayList.set(j,s2);
                    allBigDayList.set(j-1,s1);
                }
            }
        }
    }
}
