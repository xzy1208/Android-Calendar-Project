package com.calendar.db;

import android.content.ContentValues;
import android.database.Cursor;


import com.calendar.bean.BigDay;

import java.sql.Timestamp;

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
    /*待修改，
          未添加*字段
          此处开始时间、结束时间、提醒时间可能从前端获取默认值
     */
    public static final String CREATE_TABLE_BIGDAY = "create table " +
            TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
            KEY_TITLE+ " text not null, " +
            KEY_DATE+ " DATETIME not null DEFAULT (datetime()) ) "+
            KEY_TYPE+ " int not null, " +
            KEY_SUPPLEMENT+"text );";

    public static ContentValues CovertToContentValues(BigDay b){
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TITLE, b.title);
        newValues.put(KEY_DATE,b.date.toString());
        newValues.put(KEY_TYPE, b.type);
        newValues.put(KEY_SUPPLEMENT,b.supplement);
        return newValues;
    }
    public static BigDay[] ConvertToBigDay(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        BigDay[] BigDays = new BigDay[resultCounts];
        for (int i = 0; i < resultCounts; i++) {
            BigDays[i] = new BigDay();
            BigDays[i].id = cursor.getInt(0);
            BigDays[i].title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
            BigDays[i].date = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            BigDays[i].id = cursor.getInt(3);
            BigDays[i].supplement = cursor.getString(cursor.getColumnIndex(KEY_SUPPLEMENT));
            cursor.moveToNext();
        }
        return BigDays;
    }
    public static String [] getAllColumnIndex(){
        int num=5;//字段总数
        String[] s =new String[num];
        s[0]=KEY_ID;
        s[1]=KEY_TITLE;
        s[2]=KEY_DATE;
        s[3]=KEY_TYPE;
        s[4]=KEY_SUPPLEMENT;
        return s;
    }
}
