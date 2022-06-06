package edu.zjut.androiddeveloper_sx.calendar.service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import edu.zjut.androiddeveloper_sx.calendar.LookBigDayActivity;
import edu.zjut.androiddeveloper_sx.calendar.bean.BigDay;
import edu.zjut.androiddeveloper_sx.calendar.LookScheduleActivity;
import edu.zjut.androiddeveloper_sx.calendar.R;
import edu.zjut.androiddeveloper_sx.calendar.bean.Schedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ChunY on 2022/5/28.
 */

public class TaskService extends Service {
    private int count=0;
    private MediaPlayer mediaPlayer;
    private Thread taskThread;

    @Override
    public void onCreate() {
        Log.e("TaskService","onCreate");
        //taskThread=new Thread(null,task(),"showRemind");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TaskService","onStartCommand");

        //taskThread.start();
        task();

        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public Runnable task1(){
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
                        taskThread.stop();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)); // 表示该dialog是一个系统的dialog
        dialog.setCanceledOnTouchOutside(false);// 失去焦点不会消失
        dialog.show();
        // 打开手机悬浮窗权限、后台弹出界面权限
        return null;
    }
    public void task(){
        String title,message;
        int type,id;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if(ClockService.tasks.get(0).type == 0) {
            type=0;
            BigDay bigDay = ClockService.tasks.get(0).bigDay;
            id=bigDay.id;
            title = bigDay.title;
            calendar.setTime(bigDay.date);
            String dateStr = sdf1.format(calendar.getTime());
            message = bigDay.title+" "+dateStr;
        }else{
            type=1;
            Schedule schedule = ClockService.tasks.get(0).schedule;
            id=schedule.id;
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
                        //stopSelf();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)); // 表示该dialog是一个系统的dialog
        dialog.setCanceledOnTouchOutside(false);// 失去焦点不会消失
        dialog.show();
        // 打开手机悬浮窗权限、后台弹出界面权限
        //显示通知栏通知
        sendSimpleNotify(type,title,message,id);
    }
    public void sendSimpleNotify(int type,String title,String message,int id){
        Log.d("!not","send");
        count++;
        if(type==0)
        {
            title="重要日提醒:"+title;
        }else{
            title="日程提醒:"+title;
        }
        //从系统服务中获取通知管理器
        NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //调用后只有8.0以上执行
        createNotifyChannel(notifyMgr,this,"channel_id");
        //创建一个跳转到活动页面的意图
        Intent clickIntent;
        if(type==0) {
            clickIntent = new Intent(this, LookBigDayActivity.class);
            clickIntent.putExtra("id", id);
        }else{
            clickIntent = new Intent(this, LookScheduleActivity.class);
            clickIntent.putExtra("id", id);
        }
        //创建一个用于页面跳转的延迟意图
        PendingIntent contentIntent = PendingIntent.getActivity(this,count,clickIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT);
        //创建一个通知消息的构造器
        Notification.Builder builder = new Notification.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Android8.0开始必须给每个通知分配对应的渠道
            builder = new Notification.Builder(this,"channel_id");
        }
        builder.setContentIntent(contentIntent)//设置内容的点击意图
                .setAutoCancel(true)//设置是否允许自动清除
                .setSmallIcon(R.mipmap.calendar)//设置状态栏里的小图标
                .setTicker("提示消息来啦")//设置状态栏里面的提示文本
                .setWhen(System.currentTimeMillis())//设置推送时间，格式为"小时：分钟"
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.calendar))//设置通知栏里面的大图标
                .setContentTitle(title)//设置通知栏里面的标题文本
                .setContentText(message);//设置通知栏里面的内容文本
        //根据消息构造器创建一个通知对象
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Notification notify = builder.build();
            //使用通知管理器推送通知，然后在手机的通知栏就会看到消息
            notifyMgr.notify(count,notify);
        }
    }

    /**
     * 创建通知渠道，Android8.0开始必须给每个通知分配对应的渠道
     * @param notifyMgr
     * @param ctx
     * @param channelId
     */
    public void createNotifyChannel(NotificationManager notifyMgr,Context ctx,String channelId){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //创建一个默认重要性的通知渠道
            NotificationChannel channel = new NotificationChannel(channelId,"Channel",NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null,null);
            channel.setShowBadge(true);
            channel.canBypassDnd();//可否绕过请勿打扰模式
            channel.enableLights(true);//闪光
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);//锁屏显示通知
            channel.setLightColor(Color.RED);//指定闪光时的灯光颜色
            channel.canShowBadge();//桌面ICON是否可以显示角标
            channel.enableVibration(true);//是否可以震动
            channel.getGroup();//获取通知渠道组
            channel.setVibrationPattern(new long[]{100,100,200});//震动的模式
            channel.shouldShowLights();//是否会闪光
            notifyMgr.createNotificationChannel(channel);
        }
    }
}
