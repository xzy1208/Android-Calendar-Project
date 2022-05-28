package com.calendar.Observer;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.calendar.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ChunY on 2022/5/28.
 */

public class SmsObserver extends ContentObserver {
    private Context mContext;
    private Handler mHandler;
    private static int id=0; //这里必须用静态的，防止程序多次意外初始化情况

    public SmsObserver(Context context,Handler handler){
        super(handler);
        this.mContext = context;
        this.mHandler = handler;
    }

    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        String code = "";

        //过滤可能界面调用初始化两次的情况
        if (uri.toString().contains("content://sms/raw")) {
            return;
        }

        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor cursor = mContext.getContentResolver().query(inboxUri, null, null, null, "date desc");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String _id = cursor.getString(cursor.getColumnIndex("_id"));
                //比较id 解决重复问题
                if (id < Integer.parseInt(_id)) {
                    id = Integer.parseInt(_id);//将获取到的当前id记录，防止重复
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    Log.i("Info", body);
                    //正则表达式d{6}的意思是连续6位是数字的就提取出来
                    Pattern pattern = Pattern.compile("(\\d{8})");
                    //对短信的内容进行匹配
                    Matcher matcher = pattern.matcher(body);
                    if (body.contains("查询") && matcher.find()) {
                        code = matcher.group(0);
                        Log.i("Info", code);

                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("address",address);
                            obj.put("code",code);

                            // 发送到主线程
                            Message msMessage = new Message();
                            msMessage.what = MainActivity.Finding;
                            msMessage.obj = obj;
                            mHandler.sendMessage(msMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            cursor.close();
        }
    }
}