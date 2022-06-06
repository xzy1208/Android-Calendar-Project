package edu.zjut.androiddeveloper_sx.calendar.bean;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ChunY on 2022/5/26.
 */

public class ScheduleDate {
    public Timestamp date;// 日期
    public List<SimpleDate> simpleDateList;// 当天每个小时间段和对应的事件

    public ScheduleDate(){}
    public ScheduleDate(Timestamp date, List<SimpleDate> simpleDateList){
        this.date = date;
        this.simpleDateList = simpleDateList;
    }

}
