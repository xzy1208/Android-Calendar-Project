package com.calendar.view;


import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 日期的管理类
 * Created by xiaozhu on 2016/8/7.
 */
public class DayManager {

    //每周第一位
    private static int selectIndex=-1;
    private static int selectYear=-1;
    private static int selectMonth=-1;
    private static int selectDay=-1;
    private static int realYear=-1;
    private static int realMonth=-1;
    private static int realDay=-1;
    
    public static void setSelectYear(int x){
        selectYear=x;
    }
    public static void setSelectMonth(int x){
        selectMonth=x;
        changeYearForTouch();
    }
    public static void setSelectDay(int x){
        selectDay=x;
        Calendar temp=Calendar.getInstance();
        temp.set(selectYear,selectMonth,selectDay);
        int result=temp.get((Calendar.DAY_OF_MONTH))-temp.get((Calendar.DAY_OF_WEEK))+1;
        if(result<0){
            result=1;
        }
        selectIndex=result;
    }
    public static void setSelectDate(int a,int b,int c){
        selectYear=a;
        selectMonth=b;
        selectDay=c;
        Calendar temp=Calendar.getInstance();
        temp.set(a,b,c);
        int result=temp.get((Calendar.DAY_OF_MONTH))-temp.get((Calendar.DAY_OF_WEEK))+1;
        if(result<0){
            result=1;
        }
        selectIndex=result;
    }
    public static void setSelectCalendar(Calendar calendar){
        selectYear=calendar.get(Calendar.YEAR);
        selectMonth=calendar.get(Calendar.MONTH);
        selectDay=calendar.get(Calendar.DAY_OF_MONTH);
        int result=calendar.get((Calendar.DAY_OF_MONTH))-calendar.get((Calendar.DAY_OF_WEEK))+1;
        Log.d("!weekindex2", String.valueOf(result));
        if(result<0){
            result=1;
        }
        selectIndex=result;
    }
    public static void setRealDate(int a,int b,int c){
        realYear=a;
        realMonth=b;
        realDay=c;
    }
    public static void changeYearForTouch(){
        if(getSelectMonth()==12){
            setSelectYear(getSelectYear()+1);
            setSelectMonth(0);
        }else if(getSelectMonth()==-1){
            setSelectYear(getSelectYear()-1);
            setSelectMonth(11);
        }
    }
    public static void setSelectIndex(int i){
        selectIndex=i;
    }
    public static int getSelectYear(){return selectYear;}
    public static int getSelectMonth(){return selectMonth;}
    public static int getSelectDay(){return selectDay;}
    public static int getRealYear(){return realYear;}
    public static int getRealMonth(){return realMonth;}
    public static int getRealDay(){return realDay;}
    public static int getSelectIndex(){return selectIndex;}
    public  static Calendar getSelectCalendar(){
        Calendar temp =Calendar.getInstance();
        temp.set(selectYear,selectMonth,selectDay);
        return temp;
    }
    public static int getLastMonth(){
        int m=selectMonth-1;
       if(m==0){
            m=11;
        }
        return m;
    }
    public static int getNextMonth(){
        int m=selectMonth+1;
        if(m==11){
            m=0;
        }
        return m;
    }
    static String[] weeks = {"日", "一", "二", "三", "四", "五", "六"};
    static String[] dayArray = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    

    /**
     * 储存正常天数
     */
    static Set<Integer> normalDays = new HashSet<>();

    /**
     * 添加正常天数
     *
     * @param i
     */
    public static void addNomalDays(int i) {
        normalDays.add(i);
    }

    /**
     * 移除正常的天数
     *
     * @param i
     */
    public static void removeNomalDays(int i) {
        if (normalDays.contains(i)) {
            normalDays.remove(i);
        }
    }

    /**
     * 储存异常天数
     */
    static Set<Integer> abnormalDays = new HashSet<>();

    /**
     * 添加异常天数
     *
     * @param i
     */
    public static void addAbnormalDays(int i) {
        abnormalDays.add(i);
    }

    /**
     * 移除异常的天数
     *
     * @param i
     */
    public static void removeAbnormalDays(int i) {
        if (abnormalDays.contains(i)) {
            abnormalDays.remove(i);
        }
    }

    /**
     * 储存外出天数
     */
    static Set<Integer> outDays = new HashSet<>();

    /**
     * 添加外出天数天数
     *
     * @param i
     */
    public static void addOutDays(int i) {
        outDays.add(i);
    }

    /**
     * 移除外出天数的天数
     *
     * @param i
     */
    public static void removeOutDays(int i) {
        if (outDays.contains(i)) {
            outDays.remove(i);
        }
    }

    /**
     * 储存休息天数
     */
    static Set<Integer> restDays = new HashSet<>();


    /**
     * 根据日历对象创建日期集合
     *
     *
     * @param width    控件的宽度
     * @param heigh    控件的高度
     * @param
     * @return 返回的天数的集合
     */
    public static List<Day> createDayByCalendar(int width, int heigh, boolean drawOtherDay) {
        Calendar tempCalendar=Calendar.getInstance();
        tempCalendar.set(selectYear,selectMonth,selectDay);
        Log.i("!calendar", "createThisMonth: "+tempCalendar.getTime());
        //初始化休息的天数
        //initRestDays(tempCalendar);
        //模拟数据
        //imitateData();

        List<Day> days = new ArrayList<>();

        Day day = null;

        int dayWidth = width / 7;
        int dayHeight = heigh / (tempCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH) + 1);//将界面均分为多行
        //添加星期标识，
        for (int i = 0; i < 7; i++) {
            day = new Day(dayWidth, dayHeight);
            //为星期设置位置，为第0行，
            day.location_x = i;
            day.location_y = 0;
            day.text = weeks[i];
            //设置日期颜色
            day.textClor = 0xFF699CF0;
            days.add(day);

        }
        int count = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取本月多少天
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);//将1号作为本月第一天
        int firstWeekCount = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;//获取本月第一天为星期几
        //Log.i("!calendar", "createDayByCalendar: "+firstWeekCount);

        //添加上一个月的天数
        if (drawOtherDay) {
            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) - 1);

            int preCount = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            //补充天数
            for (int i = 0; i < firstWeekCount; i++) {
                day = new Day(dayWidth, dayHeight);
                day.text = dayArray[preCount - firstWeekCount + i];
                day.location_x = i;
                day.location_y = 1;
                day.textClor=0xffaaaaaa;
                day.isCurrent=false;
                day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+1)+"-"+day.text;
                days.add(day);
            }
            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) + 1);
        }

        //生成每一天的对象，其中第i次创建的是第i+1天
        for (int i = 0; i < count; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];

            tempCalendar.set(Calendar.DAY_OF_MONTH, i + 1);
            //设置每个天数的位置
            day.location_y = tempCalendar.get(Calendar.WEEK_OF_MONTH);

            day.location_x = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;
            day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+1)+"-"+day.text;

            //设置日期选择状态
            if (i == realDay - 1&&selectMonth==realMonth&&selectYear==realYear&&selectDay==realDay) {
                day.backgroundStyle = 3;
                day.textClor = 0xFF4384ED;

            } else if (i == selectDay - 1) {
                day.backgroundStyle = 2;

                day.textClor = 0xFFFAFBFE;

            } else {
                day.backgroundStyle = 1;
                day.textClor = 0xFF8696A5;
            }
            //设置工作状态
            if (restDays.contains(1 + i)) {
                day.workState = 0;
            } else if (abnormalDays.contains(i + 1)) {

                day.workState = 2;
            } else if (outDays.contains(i + 1)) {
                day.workState = 3;
            } else {
                day.workState = 1;
            }
            days.add(day);
        }

        //添加下一个月的天数
        int lastCount = tempCalendar.get(Calendar.DAY_OF_WEEK);//本月最后一周有几天
       // Log.i("!Calendar", "lastCount: "+lastCount);

        for (int i = 0; i < 7 - lastCount; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];

            //设置每个天数的位置
            day.location_y = tempCalendar.get(Calendar.WEEK_OF_MONTH);

            day.location_x = lastCount+i;
            day.isCurrent=false;
            day.textClor=0xffaaaaaa;
            day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+2)+"-"+day.text;
            days.add(day);
        }
        //Log.i("!calendar", "createDayByCalendar: "+tempCalendar.getTime());
        return days;
    }

    public static List<Day> createDayForLastMonth(int width, int heigh, boolean drawOtherDay) {
        Calendar tempCalendar=Calendar.getInstance();
        tempCalendar.set(selectYear,selectMonth,selectDay);
        tempCalendar.add(Calendar.MONTH,-1);
        /*if(tempCalendar.get(Calendar.MONTH)==0){
            tempCalendar.set(Calendar.MONTH,11);
            tempCalendar.set(Calendar.YEAR,tempCalendar.get(Calendar.YEAR)-1);
        }else{
            tempCalendar.set(Calendar.MONTH,tempCalendar.get(Calendar.MONTH)-1);
        }*/
        Log.i("!calendar", "createLastMonth: "+tempCalendar.getTime());
        //初始化休息的天数
        //initRestDays(tempCalendar);
        //模拟数据
        //imitateData();

        List<Day> days = new ArrayList<>();

        Day day = null;

        int dayWidth = width / 7;
        int dayHeight = heigh / (tempCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH) + 1);//将界面均分为多行
        //添加星期标识，
        for (int i = 0; i < 7; i++) {
            day = new Day(dayWidth, dayHeight);
            //为星期设置位置，为第0行，
            day.location_x = i;
            day.location_y = 0;
            day.text = weeks[i];
            //设置日期颜色
            day.textClor = 0xFF699CF0;
            days.add(day);

        }
        int count = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取本月多少天
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);//将1号作为本月第一天
        int firstWeekCount = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;//获取本月第一天为星期几
        //Log.i("!calendar", "createDayByCalendar: "+firstWeekCount);

        //添加上一个月的天数
        if (drawOtherDay) {
            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) - 1);

            int preCount = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            //补充天数
            for (int i = 0; i < firstWeekCount; i++) {
                day = new Day(dayWidth, dayHeight);
                day.text = dayArray[preCount - firstWeekCount + i];
                day.location_x = i;
                day.location_y = 1;
                day.textClor=0xffaaaaaa;
                day.isCurrent=false;
                day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+1)+"-"+day.text;
                days.add(day);
            }
            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) + 1);
        }

        //生成每一天的对象，其中第i次创建的是第i+1天
        for (int i = 0; i < count; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];

            tempCalendar.set(Calendar.DAY_OF_MONTH, i + 1);
            //设置每个天数的位置
            day.location_y = tempCalendar.get(Calendar.WEEK_OF_MONTH);

            day.location_x = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;
            day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+1)+"-"+day.text;

            //设置日期选择状态
            /*if (i == realDay - 1&&selectMonth==realMonth&&selectYear==realYear&&selectDay==realDay) {
                day.backgroundStyle = 3;
                day.textClor = 0xFF4384ED;

            } else if (i == selectDay - 1) {
                day.backgroundStyle = 2;

                day.textClor = 0xFFFAFBFE;

            } else {*/
                day.backgroundStyle = 1;
                day.textClor = 0xFF8696A5;
            //}
            //设置工作状态
            if (restDays.contains(1 + i)) {
                day.workState = 0;
            } else if (abnormalDays.contains(i + 1)) {

                day.workState = 2;
            } else if (outDays.contains(i + 1)) {
                day.workState = 3;
            } else {
                day.workState = 1;
            }
            days.add(day);
        }

        //添加下一个月的天数
        int lastCount = tempCalendar.get(Calendar.DAY_OF_WEEK);//本月最后一周有几天
        // Log.i("!Calendar", "lastCount: "+lastCount);

        for (int i = 0; i < 7 - lastCount; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];

            //设置每个天数的位置
            day.location_y = tempCalendar.get(Calendar.WEEK_OF_MONTH);

            day.location_x = lastCount+i;
            day.isCurrent=false;
            day.textClor=0xffaaaaaa;
            day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+2)+"-"+day.text;
            days.add(day);
        }
        //Log.i("!calendar", "createDayByCalendar: "+tempCalendar.getTime());
        //setSelectMonth(getSelectMonth()-1);
        return days;
    }

    public static List<Day> createDayForNextMonth(int width, int heigh, boolean drawOtherDay) {
        Calendar tempCalendar=Calendar.getInstance();
        tempCalendar.set(selectYear,selectMonth,selectDay);
        tempCalendar.add(Calendar.MONTH,+1);
        /*if(tempCalendar.get(Calendar.MONTH)==11){
            tempCalendar.set(Calendar.MONTH,0);
            tempCalendar.set(Calendar.YEAR,tempCalendar.get(Calendar.YEAR)+1);
        }else{
            tempCalendar.set(Calendar.MONTH,tempCalendar.get(Calendar.MONTH)+1);
        }*/
        Log.i("!calendar", "createnextMonth: "+tempCalendar.getTime());
        //初始化休息的天数
        //initRestDays(tempCalendar);
        //模拟数据
        //imitateData();

        List<Day> days = new ArrayList<>();

        Day day = null;

        int dayWidth = width / 7;
        int dayHeight = heigh / (tempCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH) + 1);//将界面均分为多行
        //添加星期标识，
        for (int i = 0; i < 7; i++) {
            day = new Day(dayWidth, dayHeight);
            //为星期设置位置，为第0行，
            day.location_x = i;
            day.location_y = 0;
            day.text = weeks[i];
            //设置日期颜色
            day.textClor = 0xFF699CF0;
            days.add(day);

        }
        int count = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取本月多少天
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);//将1号作为本月第一天
        int firstWeekCount = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;//获取本月第一天为星期几
        //Log.i("!calendar", "createDayByCalendar: "+firstWeekCount);

        //添加上一个月的天数
        if (drawOtherDay) {
            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) - 1);

            int preCount = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            //补充天数
            for (int i = 0; i < firstWeekCount; i++) {
                day = new Day(dayWidth, dayHeight);
                day.text = dayArray[preCount - firstWeekCount + i];
                day.location_x = i;
                day.location_y = 1;
                day.textClor=0xffaaaaaa;
                day.isCurrent=false;
                day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+1)+"-"+day.text;
                days.add(day);
            }
            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) + 1);
        }

        //生成每一天的对象，其中第i次创建的是第i+1天
        for (int i = 0; i < count; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];

            tempCalendar.set(Calendar.DAY_OF_MONTH, i + 1);
            //设置每个天数的位置
            day.location_y = tempCalendar.get(Calendar.WEEK_OF_MONTH);

            day.location_x = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;
            day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+1)+"-"+day.text;

            //设置日期选择状态
            /* (i == realDay - 1&&selectMonth==realMonth&&selectYear==realYear&&selectDay==realDay) {
                day.backgroundStyle = 3;
                day.textClor = 0xFF4384ED;

            } else if (i == selectDay - 1) {
                day.backgroundStyle = 2;

                day.textClor = 0xFFFAFBFE;

            } else {*/
                day.backgroundStyle = 1;
                day.textClor = 0xFF8696A5;
           // }
            //设置工作状态
            if (restDays.contains(1 + i)) {
                day.workState = 0;
            } else if (abnormalDays.contains(i + 1)) {

                day.workState = 2;
            } else if (outDays.contains(i + 1)) {
                day.workState = 3;
            } else {
                day.workState = 1;
            }
            days.add(day);
        }

        //添加下一个月的天数
        int lastCount = tempCalendar.get(Calendar.DAY_OF_WEEK);//本月最后一周有几天
        // Log.i("!Calendar", "lastCount: "+lastCount);

        for (int i = 0; i < 7 - lastCount; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];

            //设置每个天数的位置
            day.location_y = tempCalendar.get(Calendar.WEEK_OF_MONTH);

            day.location_x = lastCount+i;
            day.isCurrent=false;
            day.textClor=0xffaaaaaa;
            day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+2)+"-"+day.text;
            days.add(day);
        }
       // Log.i("!calendar", "createDayByCalendar: "+tempCalendar.getTime());
       // setSelectMonth(getSelectMonth()-1);
        return days;
    }
    /**
     * 模拟数据
     */
    private static void imitateData() {
        abnormalDays.add(2);
        abnormalDays.add(11);
        abnormalDays.add(16);
        abnormalDays.add(17);
        abnormalDays.add(23);

        outDays.add(8);
        outDays.add(9);
        outDays.add(18);
        outDays.add(22);

    }

    /**
     * 初始化休息的天数  计算休息的天数
     *
     * @param calendar
     */
    private static void initRestDays(Calendar calendar) {
        Calendar tempCalendar=calendar;
        int total = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < total; i++) {
            tempCalendar.set(Calendar.DAY_OF_MONTH, i + 1);
            if (tempCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || tempCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                restDays.add(i + 1);
            }
        }
    }

    //获取当前日期的那一周的天
    public static List<Day> createDayForWeek(int width, int heigh, boolean drawOtherDay) {
        Calendar tempCalendar=Calendar.getInstance();
        tempCalendar.set(selectYear,selectMonth,selectDay);
        Calendar today=tempCalendar;
        int todayDay=today.get(Calendar.DAY_OF_MONTH);
        int todayOfWeek=today.get(Calendar.DAY_OF_WEEK)-1;
        Log.d("!calendar", "today"+today.getTime());
        //初始化休息的天数
        //initRestDays(calendar);
        //模拟数据
        //imitateData();

        List<Day> days = new ArrayList<>();

        Day day = null;
        int dayWidth = width / 7;
        int dayHeight = heigh /2;
        //添加星期标识，
        for (int i = 0; i < 7; i++) {
            day = new Day(dayWidth, dayHeight);
            //为星期设置位置，为第0行，
            day.location_x = i;
            day.location_y = 0;
            day.text = weeks[i];
            //设置日期颜色
            day.textClor = 0xFF699CF0;
            days.add(day);
        }

        int count = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取本月天数
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);//设置1号为本月第一天
        int firstWeekCount = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;//获取该日是星期几
        Log.d("!calendar", "todayDay"+todayDay);
        Log.d("!calendar", "todayofWeek"+todayOfWeek);
        Log.d("!calendar", "firstWeekCount"+firstWeekCount);
        //当天为前七天则需考虑添加上一个月天数,若正好显示则星期和日正好对应
        //添加上一个月的天数
        if (todayDay<=7&&todayOfWeek+1>todayDay&&drawOtherDay) {
            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) - 1);//设置为上个月

            int preCount = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);//上个月多少天

            for (int i = 0; i <firstWeekCount; i++) {
                day = new Day(dayWidth, dayHeight);
                day.text = dayArray[preCount - firstWeekCount + i];
                day.location_x = i;
                day.location_y = 1;
                day.textClor=0xffaaaaaa;
                day.isCurrent=false;
                day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+1)+"-"+day.text;
                days.add(day);
            }

            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) + 1);//日期设置回本月
        }

        int currentIndex=-1;int afterdays=0;int xpos=0;
        if(todayDay<=7&&todayOfWeek+1>todayDay){
            currentIndex=0;
            xpos=todayOfWeek-todayDay+1;
        }else{
            currentIndex=todayDay-todayOfWeek-1;
        }

        if(todayDay+6-todayOfWeek>tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)-1) {
            afterdays=tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)-1;
        }else{
            afterdays=todayDay+5-todayOfWeek;
        }

        //生成每一天的对象，其中第i次创建的是第i+1天
        for (int i = currentIndex; i <=afterdays ; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];

            tempCalendar.set(Calendar.DAY_OF_MONTH, i + 1);
            //设置每个天数的位置
            day.location_y = 1;
            day.location_x = xpos ;
            day.dateText=today.get(Calendar.YEAR)+"-"+(today.get(Calendar.MONTH)+1)+"-"+day.text;
            xpos++;

            //设置日期选择状态
            if (i == realDay - 1&&selectMonth==realMonth&&selectYear==realYear&&selectDay==realDay) {
                day.backgroundStyle = 3;
                day.textClor = 0xFF4384ED;

            } else if (i == selectDay - 1) {
                day.backgroundStyle = 2;

                day.textClor = 0xFFFAFBFE;

            } else {
                day.backgroundStyle = 1;
                day.textClor = 0xFF8696A5;
            }
            //设置工作状态
            if (restDays.contains(1 + i)) {
                day.workState = 0;
            } else if (abnormalDays.contains(i + 1)) {

                day.workState = 2;
            } else if (outDays.contains(i + 1)) {
                day.workState = 3;
            } else {
                day.workState = 1;
            }
            days.add(day);
        }

        //添加下一个月的天数
        int lastCount = today.get(Calendar.DAY_OF_WEEK);//本月最后一周有几天

       if(today.getActualMaximum(Calendar.DAY_OF_MONTH)-todayDay<=7&&today.getActualMaximum(Calendar.DAY_OF_MONTH)-todayDay-7!=todayOfWeek){
            for (int i = 0; i < 7 - lastCount; i++) {
                day = new Day(dayWidth, dayHeight);
                day.text = dayArray[i];

                //设置每个天数的位置
                day.location_y = 1;
                day.location_x = lastCount + i;
                day.isCurrent = false;
                day.textClor = 0xffaaaaaa;
                day.dateText = tempCalendar.get(Calendar.YEAR) + "-" + (tempCalendar.get(Calendar.MONTH) + 2) + "-" + day.text;
                days.add(day);
            }
        }
        return days;
    }

    public static List<Day> createDayForLastWeek(int width, int heigh, boolean drawOtherDay) {
        Calendar tempCalendar=Calendar.getInstance();
        tempCalendar.set(selectYear,selectMonth,selectDay);
        tempCalendar.add(Calendar.DAY_OF_MONTH,-7);
        Calendar today=tempCalendar;
        int todayDay=today.get(Calendar.DAY_OF_MONTH);
        int todayOfWeek=today.get(Calendar.DAY_OF_WEEK)-1;
        Log.d("!calendar", "today"+today.getTime());
        //初始化休息的天数
        //initRestDays(calendar);
        //模拟数据
        //imitateData();

        List<Day> days = new ArrayList<>();

        Day day = null;
        int dayWidth = width / 7;
        int dayHeight = heigh /2;
        //添加星期标识，
        for (int i = 0; i < 7; i++) {
            day = new Day(dayWidth, dayHeight);
            //为星期设置位置，为第0行，
            day.location_x = i;
            day.location_y = 0;
            day.text = weeks[i];
            //设置日期颜色
            day.textClor = 0xFF699CF0;
            days.add(day);
        }

        int count = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取本月天数
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);//设置1号为本月第一天
        int firstWeekCount = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;//获取该日是星期几
        Log.d("!calendar", "todayDay"+todayDay);
        Log.d("!calendar", "todayofWeek"+todayOfWeek);
        Log.d("!calendar", "firstWeekCount"+firstWeekCount);
        //当天为前七天则需考虑添加上一个月天数,若正好显示则星期和日正好对应
        //添加上一个月的天数
        if (todayDay<=7&&todayOfWeek+1>todayDay&&drawOtherDay) {
            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) - 1);//设置为上个月

            int preCount = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);//上个月多少天

            for (int i = 0; i <firstWeekCount; i++) {
                day = new Day(dayWidth, dayHeight);
                day.text = dayArray[preCount - firstWeekCount + i];
                day.location_x = i;
                day.location_y = 1;
                day.textClor=0xffaaaaaa;
                day.isCurrent=false;
                day.dateText=tempCalendar.get(Calendar.YEAR)+"-"+(tempCalendar.get(Calendar.MONTH)+1)+"-"+day.text;
                days.add(day);
            }

            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) + 1);//日期设置回本月
        }

        int currentIndex=-1;int afterdays=0;int xpos=0;
        if(todayDay<=7&&todayOfWeek+1>todayDay){
            currentIndex=0;
            xpos=todayOfWeek-todayDay+1;
        }else{
            currentIndex=todayDay-todayOfWeek-1;
        }

        if(todayDay+6-todayOfWeek>tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)-1) {
            afterdays=tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)-1;
        }else{
            afterdays=todayDay+5-todayOfWeek;
        }

        //生成每一天的对象，其中第i次创建的是第i+1天
        for (int i = currentIndex; i <=afterdays ; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];

            tempCalendar.set(Calendar.DAY_OF_MONTH, i + 1);
            //设置每个天数的位置
            day.location_y = 1;
            day.location_x = xpos ;
            day.dateText=today.get(Calendar.YEAR)+"-"+(today.get(Calendar.MONTH)+1)+"-"+day.text;
            xpos++;

            //设置日期选择状态
            day.backgroundStyle = 1;
            day.textClor = 0xFF8696A5;

            //设置工作状态
            if (restDays.contains(1 + i)) {
                day.workState = 0;
            } else if (abnormalDays.contains(i + 1)) {

                day.workState = 2;
            } else if (outDays.contains(i + 1)) {
                day.workState = 3;
            } else {
                day.workState = 1;
            }
            days.add(day);
        }

        //添加下一个月的天数
        int lastCount = today.get(Calendar.DAY_OF_WEEK);//本月最后一周有几天

        if(today.getActualMaximum(Calendar.DAY_OF_MONTH)-todayDay<=7&&today.getActualMaximum(Calendar.DAY_OF_MONTH)-todayDay-7!=todayOfWeek){
            for (int i = 0; i < 7 - lastCount; i++) {
                day = new Day(dayWidth, dayHeight);
                day.text = dayArray[i];

                //设置每个天数的位置
                day.location_y = 1;
                day.location_x = lastCount + i;
                day.isCurrent = false;
                day.textClor = 0xffaaaaaa;
                day.dateText = tempCalendar.get(Calendar.YEAR) + "-" + (tempCalendar.get(Calendar.MONTH) + 2) + "-" + day.text;
                days.add(day);
            }
        }
        return days;
    }
    public static List<Day> createDayForNextWeek(int width, int heigh, boolean drawOtherDay) {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(selectYear, selectMonth, selectDay);
        tempCalendar.add(Calendar.DAY_OF_MONTH,+7);
        Calendar today = tempCalendar;
        int todayDay = today.get(Calendar.DAY_OF_MONTH);
        int todayOfWeek = today.get(Calendar.DAY_OF_WEEK) - 1;
        Log.d("!calendar", "today" + today.getTime());
        //初始化休息的天数
        //initRestDays(calendar);
        //模拟数据
        //imitateData();

        List<Day> days = new ArrayList<>();

        Day day = null;
        int dayWidth = width / 7;
        int dayHeight = heigh / 2;
        //添加星期标识，
        for (int i = 0; i < 7; i++) {
            day = new Day(dayWidth, dayHeight);
            //为星期设置位置，为第0行，
            day.location_x = i;
            day.location_y = 0;
            day.text = weeks[i];
            //设置日期颜色
            day.textClor = 0xFF699CF0;
            days.add(day);
        }

        int count = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取本月天数
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);//设置1号为本月第一天
        int firstWeekCount = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;//获取该日是星期几
        Log.d("!calendar", "todayDay" + todayDay);
        Log.d("!calendar", "todayofWeek" + todayOfWeek);
        Log.d("!calendar", "firstWeekCount" + firstWeekCount);
        //当天为前七天则需考虑添加上一个月天数,若正好显示则星期和日正好对应
        //添加上一个月的天数
        if (todayDay <= 7 && todayOfWeek + 1 > todayDay && drawOtherDay) {
            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) - 1);//设置为上个月

            int preCount = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);//上个月多少天

            for (int i = 0; i < firstWeekCount; i++) {
                day = new Day(dayWidth, dayHeight);
                day.text = dayArray[preCount - firstWeekCount + i];
                day.location_x = i;
                day.location_y = 1;
                day.textClor = 0xffaaaaaa;
                day.isCurrent = false;
                day.dateText = tempCalendar.get(Calendar.YEAR) + "-" + (tempCalendar.get(Calendar.MONTH) + 1) + "-" + day.text;
                days.add(day);
            }

            tempCalendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH) + 1);//日期设置回本月
        }

        int currentIndex = -1;
        int afterdays = 0;
        int xpos = 0;
        if (todayDay <= 7 && todayOfWeek + 1 > todayDay) {
            currentIndex = 0;
            xpos = todayOfWeek - todayDay + 1;
        } else {
            currentIndex = todayDay - todayOfWeek - 1;
        }

        if (todayDay + 6 - todayOfWeek > tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1) {
            afterdays = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1;
        } else {
            afterdays = todayDay + 5 - todayOfWeek;
        }

        //生成每一天的对象，其中第i次创建的是第i+1天
        for (int i = currentIndex; i <= afterdays; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];

            tempCalendar.set(Calendar.DAY_OF_MONTH, i + 1);
            //设置每个天数的位置
            day.location_y = 1;
            day.location_x = xpos;
            day.dateText = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + day.text;
            xpos++;

            //设置日期选择状态
            if (i == realDay - 1 && selectMonth == realMonth && selectYear == realYear && selectDay == realDay) {
                day.backgroundStyle = 3;
                day.textClor = 0xFF4384ED;

            } else if (i == selectDay - 1) {
                day.backgroundStyle = 2;

                day.textClor = 0xFFFAFBFE;

            } else {
                day.backgroundStyle = 1;
                day.textClor = 0xFF8696A5;
            }
            //设置工作状态
            if (restDays.contains(1 + i)) {
                day.workState = 0;
            } else if (abnormalDays.contains(i + 1)) {

                day.workState = 2;
            } else if (outDays.contains(i + 1)) {
                day.workState = 3;
            } else {
                day.workState = 1;
            }
            days.add(day);
        }

        //添加下一个月的天数
        int lastCount = today.get(Calendar.DAY_OF_WEEK);//本月最后一周有几天

        if (today.getActualMaximum(Calendar.DAY_OF_MONTH) - todayDay <= 7 && today.getActualMaximum(Calendar.DAY_OF_MONTH) - todayDay - 7 != todayOfWeek) {
            for (int i = 0; i < 7 - lastCount; i++) {
                day = new Day(dayWidth, dayHeight);
                day.text = dayArray[i];

                //设置每个天数的位置
                day.location_y = 1;
                day.location_x = lastCount + i;
                day.isCurrent = false;
                day.textClor = 0xffaaaaaa;
                day.dateText = tempCalendar.get(Calendar.YEAR) + "-" + (tempCalendar.get(Calendar.MONTH) + 2) + "-" + day.text;
                days.add(day);
            }
        }
        return days;
    }
}
