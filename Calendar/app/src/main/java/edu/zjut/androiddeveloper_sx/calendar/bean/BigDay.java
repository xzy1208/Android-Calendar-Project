package edu.zjut.androiddeveloper_sx.calendar.bean;


import java.sql.Timestamp;

/**
 * Created by Gkuma on 2022/5/24.
 */

public class BigDay {
    //数据库字段
    public int id=-1;
    public String title;
    public Timestamp date;
    public int repeatInterval;
    public int repeatCycle;//0：不重复3：每月；4：每年 与schedule对应避免弄混
    public Timestamp remindTime;
    public int type;//0：正数，1：倒数
    public String supplement;

    public BigDay(){}
    public BigDay(String title, Timestamp date, int repeatInterval, int repeatCycle, Timestamp remindTime, int type, String supplement){
        this.title = title;
        this.date = date;
        this.repeatInterval = repeatInterval;
        this.repeatCycle = repeatCycle;
        this.remindTime = remindTime;
        this.type = type;
        this.supplement = supplement;
    }
    public BigDay(int id,String title, Timestamp date, int repeatInterval, int repeatCycle, Timestamp remindTime, int type, String supplement){
        this.id = id;
        this.title = title;
        this.date = date;
        this.repeatInterval = repeatInterval;
        this.repeatCycle = repeatCycle;
        this.remindTime = remindTime;
        this.type = type;
        this.supplement = supplement;
    }
}
