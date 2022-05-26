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
        //reloadCalenlar();
        Log.d("!calendar", "newWeek"+super.getCalendar().getTime());
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //reloadCalenlar();
        Log.d("!calendar", "newWeek"+getCalendar().getTime());
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //reloadCalenlar();
    }
    public void reloadCalenlar(){
        //calendar= Calendar.getInstance();
        //calendar.setTime(today);
        //Log.d("!calendar", "reload2"+calendar.getTime());
    }
    @Override
    protected void onDraw(Canvas canvas) {
        //reloadCalenlar();
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
            Log.d("!calendar", "touch:x"+locationX+"y"+locationY);
            if (locationX+ DayManager.getSelectIndex()>getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH)) {

            }
            else {
                //calendar.set(Calendar.WEEK_OF_MONTH, (int) locationY);
                setCalendar(Calendar.DAY_OF_WEEK, (int) (DayManager.getSelectIndex() + locationX));
                Log.i("!calendar", "touch_week: " + getCalendar().getTime());
                DayManager.setSelectDay((int) (DayManager.getSelectIndex() + locationX));
                if (listener != null) {
                    listener.selectChange(this, getCalendar().getTime());
                }
            }
            invalidate();

        }
        return true;
    }
}
