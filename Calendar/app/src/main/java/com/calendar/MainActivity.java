package com.calendar;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.calendar.adapter.MonthPageAdapter;
import com.calendar.adapter.WeekPageAdapter;
import com.calendar.view.DayManager;
import com.calendar.view.LeftMonthView;
import com.calendar.view.LeftWeekView;
import com.calendar.view.MonthView;
import com.calendar.view.RightMonthView;
import com.calendar.view.RightWeekView;
import com.calendar.view.WeekView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends TabActivity {

    private FloatingActionButton floatMenu_add;
    private FloatingActionButton floatMenu_today;

    private ImageButton searchBtn;
    private LinearLayout titleBar;
    private LinearLayout searchTitle;
    private static TextView titleSelectText;

    private ImageButton searchBackBtn;
    private MonthView monthView;
    private MonthView leftMonthView;
    private MonthView rightMonthView;
    private WeekView weekView;
    boolean isSearch=false;

    private ViewPager monthViewPager;
    private List<View> monthViews;
    //private MonthPageAdapter2 monthPageAdapter2;
    private ViewPager weekViewPager;
    private List<View> weekViews;

    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler(){
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                switch (msg.what)
                {
                    case 1:
                        updateTitle();
                        break;
                }
            }
        };

        initView();

        tabPaging();
    }

    private void initView(){
        //悬浮球
        floatMenu_add = (FloatingActionButton) findViewById(R.id.floatMenu_add);
        floatMenu_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });

        floatMenu_today = (FloatingActionButton) findViewById(R.id.floatMenu_today);
        floatMenu_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 今日
                DayManager.setSelectYear(DayManager.getRealYear());
                DayManager.setSelectMonth(DayManager.getRealMonth());
                DayManager.setSelectDay(DayManager.getRealDay());
                Calendar temp=Calendar.getInstance();
                temp.set(DayManager.getRealYear(),DayManager.getRealMonth(),DayManager.getRealDay());
                monthView.setCalendar(temp);
                weekView.setCalendar(temp);
            }
        });

        //搜索和标题栏
        titleSelectText=findViewById(R.id.title_select_text);
        titleBar=findViewById(R.id.title_bar);
        searchBtn=findViewById(R.id.search_btn);
        searchBtn.getBackground().setAlpha(0);
        searchTitle=findViewById(R.id.search_title);
        searchTitle.setVisibility(View.GONE);
        searchBackBtn=findViewById(R.id.search_back);
        searchBackBtn.getBackground().setAlpha(0);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSearch){
                    searchTitle.setVisibility(View.VISIBLE);
                    titleBar.setVisibility(View.GONE);
                    isSearch=true;
                }
                //startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });
        searchBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSearch){
                    searchTitle.setVisibility(View.GONE);
                    titleBar.setVisibility(View.VISIBLE);
                    isSearch=false;
                }
            }
        });
        //日历
        monthViewPager = ((ViewPager) findViewById(R.id.month_pager));
        LayoutInflater inflater = LayoutInflater.from(this);
        monthViews=new ArrayList<View>();
        //添加布局
        monthViews.add(inflater.inflate(R.layout.month_view_left,null));
        monthViews.add(inflater.inflate(R.layout.month_view,null));
        monthViews.add(inflater.inflate(R.layout.month_view_right,null));
        /*monthViews.add(inflater.inflate(R.layout.month_view_left2,null));
        monthViews.add(inflater.inflate(R.layout.month_view,null));
        monthViews.add(inflater.inflate(R.layout.month_view_right2,null));
        monthPageAdapter2=new MonthPageAdapter2(this,monthViews);*/

        monthViewPager.setAdapter(new MonthPageAdapter(this,monthViews));
       // monthViewPager.setAdapter(new MonthPageAdapter2(this,monthViews));
        monthViewPager.setCurrentItem(1);
        monthViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int currentPosition;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }
            //目前主动调用ini会导致left和right刷新两次，产生闪跳
            //可能由于本身viewpage会加载该页两边的页，但是为非动态形式，-->考虑重写ViewPager->改为设置不可见后刷新再设为可见
            @Override
            public void onPageScrollStateChanged(int state) {
                //TextView leftView=monthViewPager.findViewWithTag(0);
                //TextView rightView=monthViewPager.findViewWithTag(2);
                MonthView monthView=monthViewPager.findViewWithTag(1);
                LeftMonthView leftMonthView=monthViewPager.findViewWithTag(0);
                RightMonthView rightMonthView=monthViewPager.findViewWithTag(2);

                // ViewPager.SCROLL_STATE_IDLE 标识的状态是当前页面完全展现，并且没有动画正在进行中，如果不
                // 是此状态下执行 setCurrentItem 方法回在首位替换的时候会出现跳动！
                if (state != ViewPager.SCROLL_STATE_IDLE) return;

                // 当视图在第一个时，将页面号设置为图片的最后一张。
                if (currentPosition == 0) {
                    DayManager.setSelectMonth(DayManager.getSelectMonth()-1);
                    monthView.setCalendar(Calendar.MONTH,DayManager.getSelectMonth());
                    monthViewPager.setCurrentItem(1,false);
                    //方案1
                    /*Calendar temp=Calendar.getInstance();
                    temp.set(DayManager.getSelectYear(),DayManager.getSelectMonth(),DayManager.getSelectDay());
                    temp.add(Calendar.MONTH,-1);
                    leftMonthView.setCalendar(temp);
                    temp.add(Calendar.MONTH,2);
                    rightMonthView.setCalendar(temp);*/
                    //方案1.5
                    //rightMonthView.invalidate();
                    //leftMonthView.invalidate();
                   // rightMonthView.invalidate();
                    //方案2 ok！
                    /*leftView.setVisibility(View.GONE);
                    rightView.setVisibility(View.GONE);
                    leftView.setText(DayManager.getLastMonth()+1+"月");
                    rightView.setText(DayManager.getNextMonth()+1+"月");
                    leftView.setVisibility(View.VISIBLE);
                    rightView.setVisibility(View.VISIBLE);*/
                    leftMonthView.setVisibility(View.GONE);
                    rightMonthView.setVisibility(View.GONE);
                    leftMonthView.invalidate();
                    rightMonthView.invalidate();
                    leftMonthView.setVisibility(View.VISIBLE);
                    rightMonthView.setVisibility(View.VISIBLE);
                } else if (currentPosition ==2) {
                    // 当视图在最后一个是,将页面号设置为图片的第一张。
                    DayManager.setSelectMonth(DayManager.getSelectMonth()+1);
                    //monthView.setCalendar(Calendar.MONTH,DayManager.getSelectMonth());
                    monthView.invalidate();
                    //monthView.setCalendar(Calendar.MONTH,DayManager.getSelectMonth());

                    monthViewPager.setCurrentItem(1,false);
                   //leftMonthView.invalidate();
                    //rightMonthView.invalidate();
                   // leftView.setText(DayManager.getSelectMonth()-1+"月");
                    //rightView.setText(DayManager.getSelectMonth()+1+"月");
                    //方案2 ok！
                    /*leftView.setVisibility(View.GONE);
                    rightView.setVisibility(View.GONE);
                    leftView.setText(DayManager.getLastMonth()+1+"月");
                    rightView.setText(DayManager.getNextMonth()+1+"月");
                    leftView.setVisibility(View.VISIBLE);
                    rightView.setVisibility(View.VISIBLE);*/
                    leftMonthView.setVisibility(View.GONE);
                    rightMonthView.setVisibility(View.GONE);
                    leftMonthView.invalidate();
                    rightMonthView.invalidate();
                    leftMonthView.setVisibility(View.VISIBLE);
                    rightMonthView.setVisibility(View.VISIBLE);
                }
                updateTitle();
            }
        });
        weekViewPager=findViewById(R.id.week_pager);
        weekViews=new ArrayList<View>();
        //添加布局
        weekViews.add(inflater.inflate(R.layout.week_view_left,null));
        weekViews.add(inflater.inflate(R.layout.week_view,null));
        weekViews.add(inflater.inflate(R.layout.week_view_right,null));

        weekViewPager.setAdapter(new WeekPageAdapter(this,weekViews));
        weekViewPager.setCurrentItem(1);
        weekViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentPosition;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
               
                WeekView weekView=weekViewPager.findViewWithTag(1);
                LeftWeekView leftWeekView=weekViewPager.findViewWithTag(0);
                RightWeekView rightWeekView=weekViewPager.findViewWithTag(2);
                Calendar temp=Calendar.getInstance();
                temp.set(DayManager.getSelectYear(),DayManager.getSelectMonth(),DayManager.getSelectDay());

                if (state != ViewPager.SCROLL_STATE_IDLE) return;

                // 当视图在第一个时，将页面号设置为图片的最后一张。
                if (currentPosition == 0) {
                    Log.d("!week", String.valueOf(temp.getTime()));
                    temp.add(Calendar.DAY_OF_MONTH,-7);
                    Log.d("!weekafter", String.valueOf(temp.getTime()));
                    DayManager.setSelectCalendar(temp);
                    Log.d("!weekafterget", String.valueOf(DayManager.getSelectCalendar().getTime()));
                    Log.d("!weekIndex", String.valueOf(DayManager.getSelectIndex()));
                    weekView.setCalendar(temp);
                    weekView.invalidate();
                    weekViewPager.setCurrentItem(1,false);
                    leftWeekView.setVisibility(View.GONE);
                    rightWeekView.setVisibility(View.GONE);
                    leftWeekView.invalidate();
                    rightWeekView.invalidate();
                    leftWeekView.setVisibility(View.VISIBLE);
                    rightWeekView.setVisibility(View.VISIBLE);
                } else if (currentPosition ==2) {
                    temp.add(Calendar.DAY_OF_MONTH,+7);
                    DayManager.setSelectCalendar(temp);
                    weekView.invalidate();
                    weekViewPager.setCurrentItem(1,false);
                    leftWeekView.setVisibility(View.GONE);
                    rightWeekView.setVisibility(View.GONE);
                    leftWeekView.invalidate();
                    rightWeekView.invalidate();
                    leftWeekView.setVisibility(View.VISIBLE);
                    rightWeekView.setVisibility(View.VISIBLE);
                }
                updateTitle();
            }
        });

    }

    public static void updateTitle(){
        int year= DayManager.getSelectYear();
        int month=DayManager.getSelectMonth()+1;
        titleSelectText.setText(year+"年"+month+"月");
    }

    private void tabPaging(){
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        // 进入界面时刷新UI：setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        // tab1 月
        View tab1 = LayoutInflater.from(this).inflate(R.layout.tab1,null);
        spec = tabHost.newTabSpec("1").setIndicator(tab1).setContent(R.id.main_month_view);//若传递layout必须传递id
        tabHost.addTab(spec);

        // tab2 周
        View tab2 = LayoutInflater.from(this).inflate(R.layout.tab2,null);
        spec = tabHost.newTabSpec("2").setIndicator(tab2).setContent(R.id.main_week_view);
        tabHost.addTab(spec);

        // tab3 日
        View tab3 = LayoutInflater.from(this).inflate(R.layout.tab3,null);
        Intent intent3 = new Intent().setClass(this, ScheduleActivity.class);// 记得修改 还有布局的content
        spec = tabHost.newTabSpec("3").setIndicator(tab3).setContent(intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(spec);

        // tab4 日程
        View tab4 = LayoutInflater.from(this).inflate(R.layout.tab4,null);
        Intent intent4 = new Intent().setClass(this, ScheduleActivity.class);
        spec = tabHost.newTabSpec("4").setIndicator(tab4).setContent(intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(spec);

        //tab5 重要日
        View tab5 = LayoutInflater.from(this).inflate(R.layout.tab5,null);
        Intent intent5 = new Intent().setClass(this, BigDayActivity.class);
        spec = tabHost.newTabSpec("5").setIndicator(tab5).setContent(intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(spec);

        // 设置监听器，监听tab切换
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId){

                int tabID = Integer.valueOf(tabId)-1;
                for (int i = 0; i < getTabWidget().getChildCount(); i++)
                {
                    TextView tab_title = (TextView) getTabWidget().getChildAt(i).findViewById(R.id.tab_title);
                    TextView tab_underline = (TextView) getTabWidget().getChildAt(i).findViewById(R.id.tab_underline);
                    if (i == tabID)
                    {
                        tab_title.setTextColor(Color.parseColor("#6699ff"));
                        tab_underline.setVisibility(View.VISIBLE);

                        // 重要日不显示“今”
                        if(i == 4){
                            floatMenu_today.setVisibility(View.GONE);
                        }else{
                            floatMenu_today.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        tab_title.setTextColor(Color.parseColor("#000000"));
                        tab_underline.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }
}
