package com.example.chuny.calendar;

import java.sql.Timestamp;

/**
 * Created by ChunY on 2022/5/24.
 */

public class BigDay {
    public String title;
    public int type;// 1为倒数 0为正数
    public Timestamp date;
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
