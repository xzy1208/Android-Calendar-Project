package com.calendar.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.calendar.MainActivity;
import com.calendar.R;
import com.calendar.view.CalendarView;
import com.calendar.view.Day;
import com.calendar.view.DayManager;
import com.calendar.view.MonthView;

import java.util.Date;
import java.util.List;

/**
 * Created by Gkuma on 2022/5/27.
 * 废用
 */

public class MonthPageAdapter2 extends PagerAdapter {
    private Context mContext;
    private List<View> monthViews;
    private MonthView monthView;
    private TextView lastMonthView;
    private TextView nextMonthView;
    private addClickListener listener;
    public static Handler handler;
    //private Handler handler;

    public MonthPageAdapter2(Context context , List<View> list) {

        mContext = context;
        monthViews = list;
        monthView=list.get(1).findViewById(R.id.month_view);
        lastMonthView=list.get(0).findViewById(R.id.left_month_text);

        lastMonthView.setText(DayManager.getLastMonth()+1+"月");
        nextMonthView=list.get(2).findViewById(R.id.right_month_text);
        nextMonthView.setText(DayManager.getNextMonth()+1+"月");
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
            nextMonthView.setTag(2);
        }else if(position==1) {
            monthView.setTag(1);
        }else if(position==0){
           lastMonthView.setTag(0);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((MonthView)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public static interface addClickListener{

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

