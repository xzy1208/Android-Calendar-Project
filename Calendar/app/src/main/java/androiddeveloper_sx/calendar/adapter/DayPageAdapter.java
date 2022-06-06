package androiddeveloper_sx.calendar.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import androiddeveloper_sx.calendar.MainActivity;
import androiddeveloper_sx.calendar.view.CalendarView;
import androiddeveloper_sx.calendar.view.LeftWeekView;
import androiddeveloper_sx.calendar.view.RightWeekView;
import androiddeveloper_sx.calendar.view.WeekView;
import edu.zjut.androiddeveloper_sx.calendar.R;


/**
 * Created by Gkuma on 2022/5/28.
 */

public class DayPageAdapter extends PagerAdapter {
    private Context mContext;
    private List<View> dayViews;
    public static WeekView dayView;
    public static LeftWeekView lastWeekDayView;
    public static RightWeekView nextWeekDayView;
    private addClickListener listener;

    public DayPageAdapter(Context context , List<View> list) {
        mContext = context;
        dayViews = list;
        dayView=list.get(1).findViewById(R.id.day_view);
        lastWeekDayView=list.get(0).findViewById(R.id.left_day_view);
        nextWeekDayView=list.get(2).findViewById(R.id.right_day_view);



        dayView.setOnSelectChangeListener(new CalendarView.OnSelectChangeListener() {
            @Override
            public void selectChange(CalendarView calendarView, Date date) {

                Message msg = new Message();
                msg.what = 1;
                MainActivity.handler.sendMessage(msg);
                lastWeekDayView.invalidate();
                nextWeekDayView.invalidate();
                //Log.d("!calendar","inadalpter listen"+String.valueOf(DayManager.getSelectDay()));
            }
        });
    }


    @Override
    public int getCount() {
        return dayViews.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view =dayViews.get(position);
        if(position==2)
        {
            RightWeekView dayView=view.findViewById(R.id.right_day_view);
            dayView.setTag(2);

        }else if(position==1) {
            WeekView dayView=view.findViewById(R.id.day_view);
            dayView.setTag(1);
        }else if(position==0){

            LeftWeekView dayView=view.findViewById(R.id.left_day_view);
            dayView.setTag(0);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(dayViews.get(position));
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