package com.calendar.bean;

import java.sql.Timestamp;

/**
 * Created by ChunY on 2022/5/26.
 */

public class SimpleDate {
    public int id;// 该事件对应的数据库id
    public int type;// 0全天 1开始 2结束 3开始结束
    public Timestamp startTime;// 开始时间
    public Timestamp endTime;// 结束时间
    public String title;// 事件

    public boolean isChecked;// 删除选中

    public SimpleDate(int id, int type, Timestamp startTime, Timestamp endTime, String title){
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;

        isChecked = false;
    }
}
