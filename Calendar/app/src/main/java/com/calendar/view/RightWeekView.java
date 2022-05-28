package com.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import java.util.List;

/**
 * Created by Gkuma on 2022/5/28.
 */

public class RightWeekView extends CalendarView {
    public RightWeekView(Context context) {
        super(context);
    }

    public RightWeekView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public RightWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        correctTime();
        //获取day集合并绘制
        Log.d("!calendar", "in_week_draw_today"+getCalendar().getTime());
        List<Day> days = DayManager.createDayForNextWeek(getMeasuredWidth(), getMeasuredHeight(), drawOtherDays);//获取到天
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

}

