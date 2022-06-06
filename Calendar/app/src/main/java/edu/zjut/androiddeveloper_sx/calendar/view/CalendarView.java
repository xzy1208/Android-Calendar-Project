package edu.zjut.androiddeveloper_sx.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 自定义的日历控件
 */
public class CalendarView extends View {
    public Context context;
    /**
     * 画笔
     */
    public Paint paint;
    /***
     * 当前的时间
     */
    private Calendar calendar;
    /**
     * 选中监听
     */
    public OnSelectChangeListener listener;
    /**
     * 是否在本月里画其他月的日子
     */
    public boolean drawOtherDays = true;

    public OnDrawDays onDrawDays;

    /**
     * 改变日期，并更改当前状态，由于绘图是在calendar基础上进行绘制的，所以改变calendar就可以改变图片
     *
     * @param calendar
     */

    public void setCalendar(Calendar calendar) {//当前日期
        this.calendar = calendar;
        /*if ((calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.YEAR)).equals(DayManager.getRealMonth()+" "+DayManager.getRealYear())) {
            DayManager.setSelectDay(DayManager.getRealDay());
        } else {
            DayManager.setSelectDay(-1);
        }*/
        invalidate();
    }

    public void setCalendar(int a,int b) {
        this.calendar.set(a,b);
        /*if ((calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.YEAR)).equals(DayManager.getCurrentTime())) {
            DayManager.setCurrent(DayManager.getTempcurrent());
        } else {
            DayManager.setCurrent(-1);
        }*/
        invalidate();
    }
    public Calendar getCalendar(){
        return this.calendar;
    }

    public CalendarView(Context context) {
        super(context);
        this.context = context;
        //初始化控件
        initView();
    }


    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //初始化控件
        initView();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        //初始化控件
        initView();
    }

    /***
     * 初始化控件
     */
    private void initView() {
        paint = new Paint();
        paint.setAntiAlias(true);
        calendar = Calendar.getInstance();
        DayManager.setRealDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        DayManager.setSelectDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        Log.i("!calendar", calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.YEAR));


    }

    public void correctTime(){
        calendar.set(DayManager.getSelectYear(),DayManager.getSelectMonth(),DayManager.getSelectDay());
    }
    @Override
    protected void onDraw(Canvas canvas) {
        //correctTime();
        //super.onDraw(canvas);
        //获取day集合并绘制
        List<Day> days = DayManager.createDayByCalendar(getMeasuredWidth(), getMeasuredHeight(), drawOtherDays);//获取到天
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



    /**
     * 设置日期选择改变监听
     *
     * @param listener
     */
    public void setOnSelectChangeListener(OnSelectChangeListener listener) {

        this.listener = listener;
    }

    /**
     * 是否画本月外其他日子
     *
     * @param drawOtherDays true 表示画，false表示不画 ，默认为true
     */
    public void setDrawOtherDays(boolean drawOtherDays) {
        this.drawOtherDays = drawOtherDays;
        invalidate();
    }

    /**
     * 日期选择改变监听的接口
     */
    public interface OnSelectChangeListener {
        void selectChange(CalendarView calendarView, Date date);
    }

    /**
     * 画天数回调
     */
    public interface OnDrawDays {
        /**
         * 层次在原画下
         * 画天的回调，返回true 则覆盖默认的画面，返回
         * interface OnSelectChangeListener {
         * @return
         */
        boolean drawDay(Day day, Canvas canvas, Context context, Paint paint);

        /**
         * 层次在原画上
         */
        void drawDayAbove(Day day, Canvas canvas, Context context, Paint paint);
    }

    public void setOnDrawDays(OnDrawDays onDrawDays) {
        this.onDrawDays = onDrawDays;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            //判断点击的是哪个日期
            float x = event.getX();
            float y = event.getY();
            //计算点击的是哪个日期
            int locationX = (int) (x * 7 / getMeasuredWidth());
            int locationY = (int) ((calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) + 1) * y / getMeasuredHeight());
            if (locationY == 0) {
                return super.onTouchEvent(event);
            } else if (locationY == 1) {//点击第一行
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                int thisMonthfirstDayOfWeek=calendar.get(Calendar.DAY_OF_WEEK) - 1;
                Log.d("!calendar", "month_thisMonthfirstDayOfWeek"+thisMonthfirstDayOfWeek);
                if (locationX < thisMonthfirstDayOfWeek) {//点击上个月
                    DayManager.setSelectMonth(DayManager.getSelectMonth()-1);
                    setCalendar(Calendar.MONTH, DayManager.getSelectMonth());
                    DayManager.setSelectDay(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-thisMonthfirstDayOfWeek+1+locationX);
                    setCalendar(Calendar.DAY_OF_MONTH, DayManager.getSelectDay());

                    Log.d("!calendar", "month_SelectIndex"+ DayManager.getSelectIndex());
                    Log.d("!calendar", "month_changeSelectDay"+ DayManager.getSelectDay());
                    Log.i("!calendar", "month_touch_week: " + getCalendar().getTime());

                }else{
                    calendar.set(Calendar.WEEK_OF_MONTH, (int) locationY);
                    calendar.set(Calendar.DAY_OF_WEEK, (int) (locationX + 1));
                    DayManager.setSelectDay(calendar.get(Calendar.DAY_OF_MONTH));
                }
            } else if (locationY == calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)) {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                int beforeSelectIndex=calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-calendar.get(Calendar.DAY_OF_WEEK)+1;//由当前月切换到下一个月需要提前记录
                //DayManager.setSelectIndex(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-calendar.get(Calendar.DAY_OF_WEEK)+1);
                if (locationX > calendar.get(Calendar.DAY_OF_WEEK)-1) {//若点击下个月的日期
                    DayManager.setSelectMonth(DayManager.getSelectMonth()+1);//DayManager中已经处理跨月的情况
                    Log.d("!calendar", "month_SelectIndex"+beforeSelectIndex);
                    DayManager.setSelectDay(beforeSelectIndex+locationX-getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH));
                    Log.d("!calendar", "month_changeSelectDay"+ DayManager.getSelectDay());
                    setCalendar(Calendar.DAY_OF_MONTH, DayManager.getSelectDay());
                    setCalendar(Calendar.MONTH, DayManager.getSelectMonth());
                    Log.i("!calendar", "month_touch_week: " + getCalendar().getTime());
                }else {
                    calendar.set(Calendar.WEEK_OF_MONTH, (int) locationY);
                    calendar.set(Calendar.DAY_OF_WEEK, (int) (locationX + 1));
                    DayManager.setSelectDay(calendar.get(Calendar.DAY_OF_MONTH));

                    Log.i("!calendar", "month_touch_week: " + getCalendar().getTime());
                }
            }else{
                calendar.set(Calendar.WEEK_OF_MONTH, (int) locationY);
                calendar.set(Calendar.DAY_OF_WEEK, (int) (locationX + 1));
                DayManager.setSelectDay(calendar.get(Calendar.DAY_OF_MONTH));
            }


            if (listener != null) {
                listener.selectChange(this, calendar.getTime());
            }
            Log.d("!calendar", "!!!!"+ DayManager.getSelectMonth());
            invalidate();
        }
        return true;
    }
}
