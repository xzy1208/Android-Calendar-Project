package edu.zjut.androiddeveloper_sx.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.util.List;

/**
 * Created by Gkuma on 2022/5/27.
 */

public class LeftMonthView extends CalendarView {

    public LeftMonthView(Context context) {
        super(context);
    }

    public LeftMonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeftMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        correctTime();
        //获取day集合并绘制
        List<Day> days = DayManager.createDayForLastMonth(getMeasuredWidth(), getMeasuredHeight(), drawOtherDays);//获取到天
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
        //correctTime();

    }
}
