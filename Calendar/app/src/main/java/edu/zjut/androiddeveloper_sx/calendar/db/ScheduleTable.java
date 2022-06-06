package edu.zjut.androiddeveloper_sx.calendar.db;

import android.content.ContentValues;
import android.database.Cursor;


import edu.zjut.androiddeveloper_sx.calendar.bean.Schedule;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    public static final String CREATE_TABLE_SCHEDULE = "create table " +
            TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
            KEY_TITLE+ " text not null, " +
            KEY_PLACE+ " text, " +
            KEY_ISALLDAY+ " integer, "+
            KEY_STARTTIME+ " DATETIME not null DEFAULT (datetime('now','localtime')), " +
            KEY_ENDTIME+ " DATETIME not null DEFAULT (datetime('now','localtime')), " +
            KEY_REPEATINTERVAL+ " integer DEFAULT (0), "+
            KEY_REPEATCYCLE+ " integer DEFAULT (0), "+
            KEY_REMINDTIME+ " DATETIME, "+
            KEY_ISIMPORTANT+ " integer DEFAULT (0), "+
            KEY_SUPPLEMENT+" text );";

    public static ContentValues CovertToContentValues(Schedule s){
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TITLE, s.title);
        newValues.put(KEY_PLACE, s.place);
        newValues.put(KEY_ISALLDAY, s.isAllDay);
        newValues.put(KEY_STARTTIME,s.startTime.toString());
        newValues.put(KEY_ENDTIME,s.endTime.toString());
        newValues.put(KEY_REPEATINTERVAL,s.repeatInterval);
        newValues.put(KEY_REPEATCYCLE,s.repeatCycle);
        newValues.put(KEY_REMINDTIME,s.remindTime.toString());
        newValues.put(KEY_ISIMPORTANT,s.isImportant);
        newValues.put(KEY_SUPPLEMENT,s.supplement);
        return newValues;
    }
    public static List<Schedule> ConvertToSchedule(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 0 ; i<resultCounts; i++){
            Schedule schedule = new Schedule();
            schedule.id = cursor.getInt(0);
            schedule.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
            schedule.place = cursor.getString(cursor.getColumnIndex(KEY_PLACE));
            schedule.isAllDay = cursor.getInt(3);
            schedule.startTime = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(KEY_STARTTIME)));
            schedule.endTime = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(KEY_ENDTIME)));
            schedule.repeatInterval = cursor.getInt(6);
            schedule.repeatCycle = cursor.getInt(7);
            schedule.remindTime= Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(KEY_REMINDTIME)));
            schedule.isImportant = cursor.getInt(9);
            schedule.supplement = cursor.getString(cursor.getColumnIndex(KEY_SUPPLEMENT));
            schedules.add(schedule);
            cursor.moveToNext();
        }
        return schedules;
    }
    public static String [] getAllColumnIndex(){
        int num=11;//字段总数
        String[] s =new String[num];
        s[0]=KEY_ID;
        s[1]=KEY_TITLE;
        s[2]=KEY_PLACE;
        s[3]=KEY_ISALLDAY;
        s[4]=KEY_STARTTIME;
        s[5]=KEY_ENDTIME;
        s[6]=KEY_REPEATINTERVAL;
        s[7]=KEY_REPEATCYCLE;
        s[8]=KEY_REMINDTIME;
        s[9]=KEY_ISIMPORTANT;
        s[10]=KEY_SUPPLEMENT;
        return s;
    }
}
