package edu.zjut.androiddeveloper_sx.calendar.bean;


import java.sql.Timestamp;

/**
 * Created by Gkuma on 2022/5/24.
 * sqlite无boolean数据类型，则改为int 0
 */

public class Schedule {
    public int id =-1;
    public String title;
    public String place;//可能为其他类型，根据GPS定位
    public int isAllDay;//0：不是全天 1：是全天
    public Timestamp startTime;
    public Timestamp endTime;
    public int repeatInterval;
    public int repeatCycle;//0：不重复；1：每天；2：每周；3：每月；4：每年
    public Timestamp remindTime;
    public int isImportant;
    public String supplement;

    public Schedule(){}

    public Schedule(String title, String place, int isAllDay, Timestamp startTime, Timestamp endTime, int repeatInterval, int repeatCycle, Timestamp remindTime, int isImportant, String supplement){
        this.title = title;
        this.place = place;
        this.isAllDay = isAllDay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatInterval = repeatInterval;
        this.repeatCycle = repeatCycle;
        this.remindTime = remindTime;
        this.isImportant = isImportant;
        this.supplement = supplement;
    }

}
