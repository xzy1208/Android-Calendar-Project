package com.calendar.db;

import android.content.ContentValues;
import android.database.Cursor;


import com.calendar.bean.Schedule;

import java.sql.Timestamp;

/**
 * Created by Gkuma on 2022/5/24.
 */

public final class ScheduleTable {
    public static final String TABLE = "schedule";

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_PLACE = "place";
    public static final String KEY_ISALLDAY = "isAllDay";
    public static final String KEY_STARTTIME = "startTime";
    public static final String KEY_ENDTIME = "endTime";
    public static final String KEY_REPEATINTERVAL="repeatInterval";
    public static final String KEY_REPEATCYCLE="repeatCycle";//0：不重复；1：每天；2：每周；3：每月；4：每年
    public static final String KEY_REMINDTIME="remindTime";
    public static final String KEY_ISIMPORTANT="isImportant";
    public static final String KEY_SUPPLEMENT="supplement";
    /*待修改，
       未添加*字段
       此处开始时间、结束时间、提醒时间可能从前端获取默认值
    */
    public static final String CREATE_TABLE_SCHEDULE = "create table " +
            TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
            KEY_TITLE+ " text not null, " +
            KEY_STARTTIME+ " DATETIME not null DEFAULT (datetime('now','localtime')) ) " +
            KEY_ENDTIME+ " DATETIME not null DEFAULT (datetime('now','localtime')) ) " +
            KEY_REMINDTIME+ " DATETIME not null DEFAULT (datetime('now','localtime')) ) " +
            KEY_SUPPLEMENT+"text );";

    public static ContentValues CovertToContentValues(Schedule s){
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TITLE, s.title);
        newValues.put(KEY_STARTTIME,s.startTime.toString());
        newValues.put(KEY_ENDTIME,s.endTime.toString());
        newValues.put(KEY_REMINDTIME,s.remindTime.toString());
        newValues.put(KEY_SUPPLEMENT,s.supplement);
        return newValues;
    }
    public static Schedule[] ConvertToSchedule(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        Schedule[] Schedules = new Schedule[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            Schedules[i] = new Schedule();
            Schedules[i].id = cursor.getInt(0);
            Schedules[i].title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
            Schedules[i].startTime = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(KEY_STARTTIME)));
            Schedules[i].endTime = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(KEY_ENDTIME)));
            Schedules[i].remindTime= Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(KEY_REMINDTIME)));
            Schedules[i].supplement = cursor.getString(cursor.getColumnIndex(KEY_SUPPLEMENT));
            cursor.moveToNext();
        }
        return Schedules;
    }
    public static String [] getAllColumnIndex(){
        int num=6;//字段总数
        String[] s =new String[num];
        s[0]=KEY_ID;
        s[1]=KEY_TITLE;
        s[2]=KEY_STARTTIME;
        s[3]=KEY_ENDTIME;
        s[4]=KEY_REMINDTIME;
        s[5]=KEY_SUPPLEMENT;
        return s;
    }
}
