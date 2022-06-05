package com.calendar.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.calendar.bean.BigDay;
import com.calendar.bean.RemindTask;
import com.calendar.bean.Schedule;
import com.calendar.db.DBAdapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ChunY on 2022/5/28.
 */

public class ClockService extends Service {

    public static DBAdapter db;
    public Thread clockThread;
    public static AlarmManager alarmManager;
    public static  PendingIntent pi;

    public static Intent intent;

    public static  List<RemindTask> tasks;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ClockService","onCreate");

        //this.startService(new Intent(this,ClockService.class));// 通过这句话在第一次进入oncreate方法就开启了单独进程的服务

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); // 获得系统提供的AlarmManager服务的对象
        intent = new Intent(ClockService.this, TaskService.class);
        pi = PendingIntent.getService(ClockService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); // PendingIntent是对Intent的描述，主要用来处理即将发生的事情，这个Intent会由其他程序进行调用，这里是由闹钟调用
        // 最后一个参数设为PendingIntent.FLAG_UPDATE_CURRENT才能传递intent里的extras

        db = DBAdapter.setDBAdapter(ClockService.this);
        db.open();
       // clockThread=new Thread(null,addTask(),"clockThread");

        /*Log.d("!clock",Process.myPid()+" "+Process.myTid());
        Toast.makeText(this, Process.myPid()+" "+Process.myTid(),Toast.LENGTH_LONG);*/

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("ClockService","onStartCommand");
        // 加任务
        addTask();
        //clockThread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("ClockService","onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Runnable addTask1(){
        tasks = new ArrayList<>();
        List<BigDay> bigDayList = db.getAllDataFromBigDay();
        List<Schedule> scheduleList = db.getAllDataFromSchedule();
        Calendar c = Calendar.getInstance();
        //Toast.makeText(getApplicationContext(), "进程号=" + Process.myPid() + ", 线程号="+ Process.myTid(),Toast.LENGTH_LONG).show();
       /* if(bigDayList != null){
            for(int i=0;i<bigDayList.size();i++){
                BigDay bigDay = bigDayList.get(i);
                if(bigDay.remindTime.getTime() > 0){// 有开启提醒
                    if(bigDay.repeatCycle != 0){// 不重复
                        if(bigDay.remindTime.getTime() > c.getTimeInMillis()){
                            RemindTask task = new RemindTask(bigDay);
                            tasks.add(task);
                        }
                    }else{// 重复
                        while(bigDay.remindTime.getTime() < c.getTimeInMillis()){
                            if(bigDay.repeatCycle == 1){
                                bigDay.remindTime.setDate(bigDay.remindTime.getDate()+bigDay.repeatInterval);
                            }else if(bigDay.repeatCycle == 2){
                                bigDay.remindTime.setDate(bigDay.remindTime.getDate()+7*bigDay.repeatInterval);
                            }else if(bigDay.repeatCycle == 3){
                                bigDay.remindTime.setMonth(bigDay.remindTime.getMonth()+bigDay.repeatInterval);
                            }else if(bigDay.repeatCycle == 4){
                                bigDay.remindTime.setYear(bigDay.remindTime.getYear()+bigDay.repeatInterval);
                            }
                        }
                        RemindTask task = new RemindTask(bigDay);
                        tasks.add(task);
                    }
                }
            }
        }

        if(scheduleList != null){
            for(int i=0;i<scheduleList.size();i++){
                Schedule schedule = scheduleList.get(i);
                if(schedule.remindTime.getTime() > 0){// 有开启提醒
                    if(schedule.repeatCycle != 0){// 不重复
                        if(schedule.remindTime.getTime() > c.getTimeInMillis()){
                            RemindTask task = new RemindTask(schedule);
                            tasks.add(task);
                        }
                    }else{// 重复
                        while(schedule.remindTime.getTime() < c.getTimeInMillis()){
                            if(schedule.repeatCycle == 1){
                                schedule.remindTime.setDate(schedule.remindTime.getDate()+schedule.repeatInterval);
                            }else if(schedule.repeatCycle == 2){
                                schedule.remindTime.setDate(schedule.remindTime.getDate()+7*schedule.repeatInterval);
                            }else if(schedule.repeatCycle == 3){
                                schedule.remindTime.setMonth(schedule.remindTime.getMonth()+schedule.repeatInterval);
                            }else if(schedule.repeatCycle == 4){
                                schedule.remindTime.setYear(schedule.remindTime.getYear()+schedule.repeatInterval);
                            }
                        }
                        RemindTask task = new RemindTask(schedule);
                        tasks.add(task);
                    }
                }
            }
        }

        if(tasks != null && tasks.size()!=0){
            sort();
        }
*/
        // 提示闹钟设置完毕
        //Toast.makeText(ClockService.this, hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
        //Toast.makeText(ClockService.this, "闹钟设置完毕 "+c.get(Calendar.YEAR)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND), Toast.LENGTH_SHORT).show();

        return null;
    }

    public void addTask(){
        tasks = new ArrayList<>();
        List<BigDay> bigDayList = db.getAllDataFromBigDay();
        List<Schedule> scheduleList = db.getAllDataFromSchedule();
        Calendar c = Calendar.getInstance();
        //Toast.makeText(getApplicationContext(), "进程号=" + Process.myPid() + ", 线程号="+ Process.myTid(),Toast.LENGTH_LONG).show();
       if(bigDayList != null){
            for(int i=0;i<bigDayList.size();i++){
                BigDay bigDay = bigDayList.get(i);
                if(bigDay.remindTime.getTime() > 0){// 有开启提醒
                    if(bigDay.repeatCycle != 0){// 不重复
                        if(bigDay.remindTime.getTime() > c.getTimeInMillis()){
                            RemindTask task = new RemindTask(bigDay);
                            tasks.add(task);
                        }
                    }else{// 重复
                        while(bigDay.remindTime.getTime() < c.getTimeInMillis()){
                            if(bigDay.repeatCycle == 1){
                                bigDay.remindTime.setDate(bigDay.remindTime.getDate()+bigDay.repeatInterval);
                            }else if(bigDay.repeatCycle == 2){
                                bigDay.remindTime.setDate(bigDay.remindTime.getDate()+7*bigDay.repeatInterval);
                            }else if(bigDay.repeatCycle == 3){
                                bigDay.remindTime.setMonth(bigDay.remindTime.getMonth()+bigDay.repeatInterval);
                            }else if(bigDay.repeatCycle == 4){
                                bigDay.remindTime.setYear(bigDay.remindTime.getYear()+bigDay.repeatInterval);
                            }
                        }
                        RemindTask task = new RemindTask(bigDay);
                        tasks.add(task);
                    }
                }
            }
        }

        if(scheduleList != null){
            for(int i=0;i<scheduleList.size();i++){
                Schedule schedule = scheduleList.get(i);
                if(schedule.remindTime.getTime() > 0){// 有开启提醒
                    if(schedule.repeatCycle == 0){// 不重复
                        if(schedule.remindTime.getTime() > c.getTimeInMillis()){
                            RemindTask task = new RemindTask(schedule);
                            tasks.add(task);
                        }
                    }else{// 重复
                        while(schedule.remindTime.getTime() < c.getTimeInMillis()){
                            if(schedule.repeatCycle == 1){
                                schedule.remindTime.setDate(schedule.remindTime.getDate()+schedule.repeatInterval);
                            }else if(schedule.repeatCycle == 2){
                                schedule.remindTime.setDate(schedule.remindTime.getDate()+7*schedule.repeatInterval);
                            }else if(schedule.repeatCycle == 3){
                                schedule.remindTime.setMonth(schedule.remindTime.getMonth()+schedule.repeatInterval);
                            }else if(schedule.repeatCycle == 4){
                                schedule.remindTime.setYear(schedule.remindTime.getYear()+schedule.repeatInterval);
                            }
                        }
                        RemindTask task = new RemindTask(schedule);
                        tasks.add(task);
                    }
                }
            }
        }

        if(tasks != null && tasks.size()!=0){
            sort();
        }
        // 提示闹钟设置完毕
        //Toast.makeText(ClockService.this, hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
        //Toast.makeText(ClockService.this, "闹钟设置完毕 "+c.get(Calendar.YEAR)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND), Toast.LENGTH_SHORT).show();

    }

    public static void sort(){
        for(int i=0;i<tasks.size();i++){
            for(int j=tasks.size()-1;j>i;j--){
                RemindTask r1 = tasks.get(j);
                RemindTask r2 = tasks.get(j-1);
                Timestamp t1,t2;
                if(r1.type == 0){
                    t1 = r1.bigDay.remindTime;
                }else{
                    t1 = r1.schedule.remindTime;
                }
                if(r2.type == 0){
                    t2 = r2.bigDay.remindTime;
                }else{
                    t2 = r2.schedule.remindTime;
                }
                if(t1.getTime() < t2.getTime()){
                    tasks.set(j-1,r1);
                    tasks.set(j,r2);
                }
            }
        }

        // 设置AlarmManager在Calendar对应的时间启动TaskService
        Long time;
        if(tasks.get(0).type == 0) {
            BigDay bigDay = tasks.get(0).bigDay;
            time = bigDay.remindTime.getTime();
        }else{
            Schedule schedule = tasks.get(0).schedule;
            time = schedule.remindTime.getTime();
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pi); // 用set时间不准
        Log.e("ClockService.sort",time+"");
    }

}
