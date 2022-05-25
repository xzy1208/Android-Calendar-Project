package com.calendar.bean;


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
    //其他字段
    public long num;

    public BigDay(){}
    public BigDay(String title, int type, Timestamp date){
        this.title = title;
        this.type = type;
        this.date = date;
        if(type == 1){
            this.num = (date.getTime() - new Timestamp(System.currentTimeMillis()).getTime())/(24*60*60*1000);
        }else{
            this.num = (new Timestamp(System.currentTimeMillis()).getTime() - date.getTime())/(24*60*60*1000);
        }
    }

}
