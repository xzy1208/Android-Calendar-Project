package com.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Gkuma on 2022/5/26.
 */

public class WeekView extends CalendarView {
    public WeekView(Context context) {
        super(context);
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        correctTime();
        //获取day集合并绘制
        Log.d("!calendar", "in_week_draw_today"+getCalendar().getTime());
        List<Day> days = DayManager.createDayForWeek(getMeasuredWidth(), getMeasuredHeight(), drawOtherDays);//获取到天
        for (Day day : days) {
            canvas.save();
            canvas.translate(day.location_x * day.width, day.location_y * day.height);
            if (this.onDrawDays == null || !onDrawDays.drawDay(day, canvas, context, paint)) {
                day.drawDays(canvas, context, paint);
            }

            if (this.onDrawDays != null) {
                onDrawDays.drawDayAbove(day, canvas, context, paint);
            }
            canvas.restore();
        }
        correctTime();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            //判断点击的是哪个日期
            float x = event.getX();
            float y = event.getY();
            //计算点击的是哪个日期
            int locationX = (int) (x * 7 / getMeasuredWidth());
            int locationY = (int) (y*2 / getMeasuredHeight());

            Calendar temp=Calendar.getInstance();
            temp.set(DayManager.getSelectYear(), DayManager.getSelectMonth(), DayManager.getSelectDay());
            Log.d("!calendar", "touch_week_temp"+temp.getTime());
            //条件需要修改
            int beforeDay= DayManager.getSelectDay();
            int beforeDayOfWeek=temp.get(Calendar.DAY_OF_WEEK)-1;
            if (temp.getActualMaximum(Calendar.DAY_OF_MONTH)-beforeDay<7) {//可能点击了下一月 本月最后一天-选中天数
                temp.set(Calendar.DAY_OF_MONTH,temp.getActualMaximum(Calendar.DAY_OF_MONTH));
                int maxDayOfWeek=temp.get(Calendar.DAY_OF_WEEK)-1;
                int maxDay=temp.getActualMaximum(Calendar.DAY_OF_MONTH);
                if(beforeDayOfWeek<=maxDayOfWeek&&maxDayOfWeek<locationX) {//1:保证该行有下个月日期，2:保证选的是本月最后一天之后的日期  点击下个月的日期
                    DayManager.setSelectMonth(DayManager.getSelectMonth()+1);
                    DayManager.setSelectDay(DayManager.getSelectIndex()+locationX-maxDay);
                    Log.d("!calendar", "changeSelectDay"+ DayManager.getSelectDay());
                    setCalendar(Calendar.DAY_OF_MONTH, DayManager.getSelectDay());
                    setCalendar(Calendar.MONTH, DayManager.getSelectMonth());
                    Log.i("!calendar", "touch_week: " + getCalendar().getTime());
                }else {
                    setCalendar(Calendar.DAY_OF_WEEK, (int) (DayManager.getSelectIndex() + locationX));
                    Log.i("!calendar", "touch_week: " + getCalendar().getTime());
                    DayManager.setSelectDay((int) (DayManager.getSelectIndex() + locationX));
                }
            }else if(beforeDay<7){//可能点击了上个月
                temp.set(Calendar.DAY_OF_MONTH,1);
                int minDayOfWeek=temp.get(Calendar.DAY_OF_WEEK)-1;
                if(beforeDayOfWeek>=minDayOfWeek&&minDayOfWeek>locationX) {//1:保证该行有上个月日期，2:保证选的是本月第一天之前的日期  点击上个月
                    DayManager.setSelectMonth(DayManager.getSelectMonth() - 1);
                    setCalendar(Calendar.MONTH, DayManager.getSelectMonth());//设为上个月
                    int maxDay=getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
                    setCalendar(Calendar.DAY_OF_MONTH,maxDay);
                    Log.d("!calendar", "maxDay" + maxDay);
                    int maxDayOfWeek=getCalendar().get(Calendar.DAY_OF_WEEK)-1;
                    DayManager.setSelectDay(maxDay-maxDayOfWeek+locationX);
                    Log.d("!calendar", "last month" + getCalendar().getTime());
                    //DayManager.setSelectDay(getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH) - beforeDayOfWeek + 1 + locationX);
                    Log.d("!calendar", "changeSelectDay" + DayManager.getSelectDay());
                    setCalendar(Calendar.DAY_OF_MONTH, DayManager.getSelectDay());
                    Log.d("!calendar", "result" + getCalendar().getTime());
                }else if(beforeDayOfWeek>beforeDay) {//没有点击上个月但是点击的是包含上个月的行
                    temp.set(Calendar.MONTH, DayManager.getSelectMonth()-1);
                    Log.d("!calendar", "!lastmonth" + temp.get(Calendar.MONTH));
                    Log.d("!calendar", "!lasttime" + temp.getTime());
                    temp.set(Calendar.DAY_OF_MONTH,1);
                    Log.d("!calendar", "!lastmonthday" + temp.getActualMaximum(Calendar.DAY_OF_MONTH));
                    temp.set(Calendar.DAY_OF_MONTH, temp.getActualMaximum(Calendar.DAY_OF_MONTH));
                    int maxDayOfWeek = temp.get(Calendar.DAY_OF_WEEK) - 1;
                    Log.d("!calendar", "!!!maxdayofweek" + maxDayOfWeek);
                    Log.d("!calendar", "!!!day" + ((int) (locationX) - maxDayOfWeek));
                    DayManager.setSelectDay((int) (locationX) - maxDayOfWeek);
                    setCalendar(Calendar.MONTH, DayManager.getSelectMonth());
                    setCalendar(Calendar.DAY_OF_MONTH, (DayManager.getSelectDay()));
                }
                else{
                    DayManager.setSelectDay((int) (DayManager.getSelectIndex() + locationX));
                    setCalendar(Calendar.DAY_OF_WEEK, (int) (DayManager.getSelectIndex() + locationX));
                }
            }else {
                //calendar.set(Calendar.WEEK_OF_MONTH, (int) locationY);
                Log.i("!calendar", "touch_week_Index: " + DayManager.getSelectIndex());
                Log.i("!calendar", "touch_week_Indexplus: " + DayManager.getSelectIndex() + locationX);
                DayManager.setSelectDay((int) (DayManager.getSelectIndex() + locationX));
                setCalendar(Calendar.DAY_OF_WEEK, (int) (DayManager.getSelectIndex() + locationX));
                Log.i("!calendar", "touch_week: " + getCalendar().getTime());

            }
            if (listener != null) {
                listener.selectChange(this, getCalendar().getTime());
            }
            Log.d("!calendar", "!!!!"+ DayManager.getSelectMonth());
            invalidate();

        }
        return true;
    }
}
