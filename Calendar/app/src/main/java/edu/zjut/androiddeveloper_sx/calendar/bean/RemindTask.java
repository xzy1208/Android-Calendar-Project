package edu.zjut.androiddeveloper_sx.calendar.bean;

/**
 * Created by ChunY on 2022/5/28.
 */

public class RemindTask {
    public int type;// 0：bigDay 1：Schedule
    public BigDay bigDay;
    public Schedule schedule;

    public RemindTask(BigDay bigDay){
        type = 0;
        this.bigDay = bigDay;
    }

    public RemindTask(Schedule schedule){
        type = 1;
        this.schedule = schedule;
    }

}
