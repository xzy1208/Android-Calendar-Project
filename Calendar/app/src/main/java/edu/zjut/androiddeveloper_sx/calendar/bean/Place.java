package edu.zjut.androiddeveloper_sx.calendar.bean;

/**
 * Created by ChunY on 2022/6/1.
 */

public class Place {
    public double longitude;// 经度
    public double latitude;// 纬度
    public String title;// 名称
    public String snippet;// 地址

    public Place(double longitude, double latitude, String title, String snippet){
        this.longitude = longitude;
        this.latitude = latitude;
        this.title = title;
        this.snippet = snippet;
    }

}
