package com.calendar.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.calendar.MainActivity;
import com.calendar.R;
import com.calendar.view.CalendarView;
import com.calendar.view.Day;
import com.calendar.view.LeftMonthView;
import com.calendar.view.MonthView;
import com.calendar.view.RightMonthView;

import java.util.Date;
import java.util.List;

/**
 * Created by Gkuma on 2022/5/27.
 */

public class MonthPageAdapter extends PagerAdapter {
    private Context mContext;
    private List<View> monthViews;
    public static MonthView monthView;
    public static LeftMonthView lastMonthView;
    public static RightMonthView nextMonthView;
    private addClickListener listener;

    public MonthPageAdapter(Context context ,List<View> list) {
        mContext = context;
        monthViews = list;
        monthView=list.get(1).findViewById(R.id.month_view);
        lastMonthView=list.get(0).findViewById(R.id.left_month_view);
        nextMonthView=list.get(2).findViewById(R.id.right_month_view);
        monthView.setOnDrawDays(new CalendarView.OnDrawDays() {
            @Override
            public boolean drawDay(Day day, Canvas canvas, Context context, Paint paint) {
                Log.d("in_monthAdapter","inlistener");
                return false;//true为原画消失
            }

            @Override
            public void drawDayAbove(Day day, Canvas canvas, Context context, Paint paint) {

            }
        });
        monthView.setOnSelectChangeListener(new CalendarView.OnSelectChangeListener() {
            @Override
            public void selectChange(CalendarView calendarView, Date date) {

                Message msg = new Message();
                msg.what = 1;
                MainActivity.handler.sendMessage(msg);
                lastMonthView.invalidate();
                nextMonthView.invalidate();
                //Log.d("!calendar","inadalpter listen"+String.valueOf(DayManager.getSelectDay()));
            }
        });
    }


    @Override
    public int getCount() {
        return monthViews.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view =monthViews.get(position);
        if(position==2)
        {
            RightMonthView monthView=view.findViewById(R.id.right_month_view);
            monthView.setTag(2);

        }else if(position==1) {
            MonthView monthView=view.findViewById(R.id.month_view);
            monthView.setTag(1);
        }else if(position==0){

            LeftMonthView monthView=view.findViewById(R.id.left_month_view);
            monthView.setTag(0);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(monthViews.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private static interface addClickListener{

        public void addClick();  //自行配置参数  需要传递到activity的值

    }
    public void setCusClickListener(addClickListener cusClickListener) {

        this.listener = cusClickListener;
    }

    /*@Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }*/

}

