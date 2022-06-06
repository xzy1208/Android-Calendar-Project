package edu.zjut.androiddeveloper_sx.calendar.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import edu.zjut.androiddeveloper_sx.calendar.MainActivity;
import edu.zjut.androiddeveloper_sx.calendar.view.CalendarView;
import edu.zjut.androiddeveloper_sx.calendar.view.Day;
import edu.zjut.androiddeveloper_sx.calendar.view.LeftWeekView;
import edu.zjut.androiddeveloper_sx.calendar.view.RightWeekView;
import edu.zjut.androiddeveloper_sx.calendar.view.WeekView;
import edu.zjut.androiddeveloper_sx.calendar.R;

/**
 * Created by Gkuma on 2022/5/28.
 */

public class WeekPageAdapter extends PagerAdapter {
    private Context mContext;
    private List<View> weekViews;
    public static WeekView weekView;
    public static LeftWeekView lastWeekView;
    public static RightWeekView nextWeekView;
    private addClickListener listener;

    public WeekPageAdapter(Context context ,List<View> list) {
        mContext = context;
        weekViews = list;
        weekView=list.get(1).findViewById(R.id.week_view);
        lastWeekView=list.get(0).findViewById(R.id.left_week_view);
        nextWeekView=list.get(2).findViewById(R.id.right_week_view);

        weekView.setOnDrawDays(new CalendarView.OnDrawDays() {
            @Override
            public boolean drawDay(Day day, Canvas canvas, Context context, Paint paint) {
                return false;//true为原画消失
            }

            @Override
            public void drawDayAbove(Day day, Canvas canvas, Context context, Paint paint) {

            }
        });

        weekView.setOnSelectChangeListener(new CalendarView.OnSelectChangeListener() {
            @Override
            public void selectChange(CalendarView calendarView, Date date) {

                Message msg = new Message();
                msg.what = 1;
                MainActivity.handler.sendMessage(msg);
                lastWeekView.invalidate();
                nextWeekView.invalidate();
                //Log.d("!calendar","inadalpter listen"+String.valueOf(DayManager.getSelectDay()));
            }
        });
    }


    @Override
    public int getCount() {
        return weekViews.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view =weekViews.get(position);
        if(position==2)
        {
            RightWeekView weekView=view.findViewById(R.id.right_week_view);
            weekView.setTag(2);

        }else if(position==1) {
            WeekView weekView=view.findViewById(R.id.week_view);
            weekView.setTag(1);
        }else if(position==0){
            LeftWeekView weekView=view.findViewById(R.id.left_week_view);
            weekView.setTag(0);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(weekViews.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private static interface addClickListener{

        public void addClick();  //自行配置参数  需要传递到activity的值

    }
    public void setCusClickListener(WeekPageAdapter.addClickListener cusClickListener) {

        this.listener = cusClickListener;
    }

    /*@Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }*/

}
