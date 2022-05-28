package com.calendar.db;

import android.content.ContentValues;
import android.database.Cursor;


import com.calendar.bean.BigDay;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gkuma on 2022/5/24.
 */

public final class BigDayTable {
    public static final String TABLE = "bigDay";

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "date";
    public static final String KEY_REPEATINTERVAL="repeatInterval";
    public static final String KEY_REPEATCYCLE="repeatCycle";//0：不重复3：每月；4：每年 与schedule对应避免弄混
    public static final String KEY_REMINDTIME="remindTime";
    public static final String KEY_TYPE="type";//0：正数；1：倒数
    public static final String KEY_SUPPLEMENT="supplement";

    public static final String CREATE_TABLE_BIGDAY = "create table " +
            TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
            KEY_TITLE+ " text not null, " +
            KEY_DATE+ " DATETIME not null DEFAULT (datetime()), "+
            KEY_REPEATINTERVAL+ " integer DEFAULT (0), "+
            KEY_REPEATCYCLE+ " integer DEFAULT (0), "+
            KEY_REMINDTIME+ " DATETIME, "+
            KEY_TYPE+ " int not null, " +
            KEY_SUPPLEMENT+" text );";

    public static ContentValues CovertToContentValues(BigDay b){
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TITLE, b.title);
        newValues.put(KEY_DATE,b.date.toString());
        newValues.put(KEY_REPEATINTERVAL,b.repeatInterval);
        newValues.put(KEY_REPEATCYCLE,b.repeatCycle);
        newValues.put(KEY_REMINDTIME,b.remindTime.toString());
        newValues.put(KEY_TYPE, b.type);
        newValues.put(KEY_SUPPLEMENT,b.supplement);
        return newValues;
    }
    public static List<BigDay> ConvertToBigDay(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<BigDay> bigDays = new ArrayList<>();
        for (int i = 0; i < resultCounts; i++) {
            BigDay bigDay = new BigDay();
            bigDay.id = cursor.getInt(0);
            bigDay.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
            bigDay.date = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            bigDay.repeatInterval = cursor.getInt(3);
            bigDay.repeatCycle = cursor.getInt(4);
            bigDay.remindTime = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(KEY_REMINDTIME)));
            bigDay.type = cursor.getInt(6);
            bigDay.supplement = cursor.getString(cursor.getColumnIndex(KEY_SUPPLEMENT));
            bigDays.add(bigDay);
            cursor.moveToNext();
        }
        return bigDays;
    }
    public static String [] getAllColumnIndex(){
        int num=8;//字段总数
        String[] s =new String[num];
        s[0]=KEY_ID;
        s[1]=KEY_TITLE;
        s[2]=KEY_DATE;
        s[3]=KEY_REPEATINTERVAL;
        s[4]=KEY_REPEATCYCLE;
        s[5]=KEY_REMINDTIME;
        s[6]=KEY_TYPE;
        s[7]=KEY_SUPPLEMENT;
        return s;
    }
}
