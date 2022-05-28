package com.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Gkuma on 2022/5/25.
 */

public class MonthView extends CalendarView {
    private Context context;
    private TextView titleSelect;
    private TextView tv_pre;
    private TextView tv_month;
    private TextView tv_next;
    private GestureDetector g;
    /**
     * 日历控件
     */
    //private CalendarView calendar;
    /**
     * 日历对象
     */
    private Calendar cal;
    /**
     * 格式化工具
     */
    private SimpleDateFormat formatter;
    /**
     * 日期
     */
    private Date curDate;

    /*public void initGestureDetector(){
        g=new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                float x = motionEvent1.getX() - motionEvent.getX();
                float y = motionEvent1.getY() - motionEvent.getY();

                if (x > 0) {
                    doResult(0);//0右
                } else if (x < 0) {
                    doResult(1);//1左
                }
                return true;
            }
        });
    }*/
    public MonthView(Context context) {
        super(context);
        this.context = context;
        //initGestureDetector();
        //初始化控件
    }


    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //initGestureDetector();
        //初始化控件
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        //initGestureDetector();
        //初始化控件
    }

    @Override
    protected void onDraw(Canvas canvas) {
        correctTime();
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
        Log.d("!calendar_center",String.valueOf(DayManager.getSelectMonth()));
    }




    /**
     * 初始化界面
     */
    private void init() {
        formatter = new SimpleDateFormat("yyyy年MM月");
        //获取当前时间
        curDate = cal.getTime();
        String str = formatter.format(curDate);
        tv_month.setText(str);
        String strPre = (cal.get(Calendar.MONTH)) + "月";
        if (strPre.equals("0月")) {
            strPre = "12月";
        }
        tv_pre.setText(strPre);
        String strNext = (cal.get(Calendar.MONTH) + 2) + "月";
        if (strNext.equals("13月")) {
            strNext = "1月";
        }
        //titleSelect.setText(strNext);

    }
    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("touched");
        if(MotionEvent.ACTION_MOVE == event.getAction()){
            return g.onTouchEvent(event);
        }
        super.onTouchEvent(event);
        return true;
    }
    public void doResult(int action) {

        switch (action) {
            case 0://1
                System.out.println("go right");
                break;

            case 1:
                System.out.println("go left");
                break;

        }
    }*/


}
