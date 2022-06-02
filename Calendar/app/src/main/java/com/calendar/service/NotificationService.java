package com.calendar.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.calendar.LookScheduleActivity;
import com.calendar.R;

/**
 * Created by Gkuma on 2022/6/1.
 */

public class NotificationService extends Service {
    private final IBinder mBinder=new LocalBinder();
    public static int count=0;
    public class LocalBinder extends Binder {
        public NotificationService getService(){
            return NotificationService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("!not","onbind");
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent){
        return false;
    }
    @Override
    public void onCreate(){
        sendSimpleNotify("通知","日程提醒");
    }
    public void sendSimpleNotify(String title,String message){
        Log.d("!not","send");
        count++;
        int id=-1;
        //从系统服务中获取通知管理器
        NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //调用后只有8.0以上执行
        createNotifyChannel(notifyMgr,this,"channel_id");
        //创建一个跳转到活动页面的意图
        Intent clickIntent = new Intent(this,LookScheduleActivity.class);
        clickIntent.putExtra("id",id);
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
                .setSmallIcon(R.mipmap.ic_launcher)//设置状态栏里的小图标
                .setTicker("提示消息来啦")//设置状态栏里面的提示文本
                .setWhen(System.currentTimeMillis())//设置推送时间，格式为"小时：分钟"
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))//设置通知栏里面的大图标
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
