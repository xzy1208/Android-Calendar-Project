package com.calendar.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import com.calendar.R;
import com.calendar.bean.BigDay;
import com.calendar.bean.Schedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ChunY on 2022/5/28.
 */

public class TaskService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        Log.e("TaskService","onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TaskService","onStartCommand");

        String title,message;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if(ClockService.tasks.get(0).type == 0) {
            BigDay bigDay = ClockService.tasks.get(0).bigDay;
            title = bigDay.title;
            calendar.setTime(bigDay.date);
            String dateStr = sdf1.format(calendar.getTime());
            message = bigDay.title+" "+dateStr;
        }else{
            Schedule schedule = ClockService.tasks.get(0).schedule;
            title = schedule.title;
            String startTime, endTime;
            if(schedule.isAllDay == 1){
                calendar.setTime(schedule.startTime);
                startTime = sdf1.format(calendar.getTime());
                calendar.setTime(schedule.endTime);
                endTime = sdf1.format(calendar.getTime());
            }else{
                calendar.setTime(schedule.startTime);
                startTime = sdf2.format(calendar.getTime());
                calendar.setTime(schedule.endTime);
                endTime = sdf2.format(calendar.getTime());
            }
            message = startTime+"至"+endTime;
        }

        mediaPlayer = mediaPlayer.create(this, R.raw.mi);
        mediaPlayer.start();
        //创建一个闹钟提醒的对话框,点击确定关闭铃声与页面
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // 用系统的dialog，全局悬浮
        Log.e("TaskService",title+" "+message);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("关闭提醒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)); // 表示该dialog是一个系统的dialog
        dialog.setCanceledOnTouchOutside(false);// 失去焦点不会消失
        dialog.show();
        // 打开手机悬浮窗权限、后台弹出界面权限

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
