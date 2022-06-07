package edu.zjut.androiddeveloper_sx.calendar;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.zjut.androiddeveloper_sx.calendar.adapter.DayPageAdapter;
import edu.zjut.androiddeveloper_sx.calendar.adapter.LVAdapter2;
import edu.zjut.androiddeveloper_sx.calendar.adapter.MonthPageAdapter;
import edu.zjut.androiddeveloper_sx.calendar.adapter.PopupAdapterForBigDay;
import edu.zjut.androiddeveloper_sx.calendar.adapter.PopupAdapterForSchedule;
import edu.zjut.androiddeveloper_sx.calendar.adapter.ScheduleAdapter1;
import edu.zjut.androiddeveloper_sx.calendar.adapter.WeekPageAdapter;
import edu.zjut.androiddeveloper_sx.calendar.bean.BigDay;
import edu.zjut.androiddeveloper_sx.calendar.bean.FindBigDay;
import edu.zjut.androiddeveloper_sx.calendar.bean.FindSchedule;
import edu.zjut.androiddeveloper_sx.calendar.bean.Schedule;
import edu.zjut.androiddeveloper_sx.calendar.bean.ScheduleDate;
import edu.zjut.androiddeveloper_sx.calendar.bean.SimpleDate;
import edu.zjut.androiddeveloper_sx.calendar.db.DBAdapter;
import edu.zjut.androiddeveloper_sx.calendar.service.ClockService;
import edu.zjut.androiddeveloper_sx.calendar.sms.SmsObserver;
import edu.zjut.androiddeveloper_sx.calendar.utils.PrefUtils;
import edu.zjut.androiddeveloper_sx.calendar.view.CalendarView;
import edu.zjut.androiddeveloper_sx.calendar.view.Day;
import edu.zjut.androiddeveloper_sx.calendar.view.DayManager;
import edu.zjut.androiddeveloper_sx.calendar.view.LeftMonthView;
import edu.zjut.androiddeveloper_sx.calendar.view.LeftWeekView;
import edu.zjut.androiddeveloper_sx.calendar.view.MonthView;
import edu.zjut.androiddeveloper_sx.calendar.view.RightMonthView;
import edu.zjut.androiddeveloper_sx.calendar.view.RightWeekView;
import edu.zjut.androiddeveloper_sx.calendar.view.WeekView;

public class MainActivity extends TabActivity {
    public static int page=0;//默认第一页
    public DBAdapter db;

    private FloatingActionButton floatMenu_add;
    private FloatingActionButton floatMenu_today;

    private ImageButton searchBtn;
    private LinearLayout titleBar;
    private LinearLayout searchTitle;
    private LinearLayout tabHost_LL;
    private LinearLayout search_listView_LL;
    private LinearLayout floatMenu_LL;
    private SearchView searchView;
    private ListView search_listView;
    List<ScheduleDate> search_scheduleDateList;
    private static TextView titleSelectText;

    private LinearLayout menu_del;
    private Button cancel_del_btn;
    private Button finish_del_btn;
    private Button check_all_btn;

    private ImageButton searchBackBtn;
    private ImageButton searchDelBtn;
    boolean isSearch=false;

    private ViewPager monthViewPager;
    private ListView main_month_scheduleListView;
    private ListView main_month_bigDayListView;
    private List<View> monthViews;
    private ViewPager weekViewPager;
    private List<View> weekViews;

    private ViewPager dayViewPager;
    private List<View> dayViews;

    DatePickerDialog datePickerDialog;

    ScrollView weekScheduleView;
    ScrollView dayScheduleView;

    public static Handler handler;

    public static int Finding = 1;
    SmsObserver smsObserver;
    private Handler mHandler;

    public List<BigDay> bigDays;//用于点击不同日期，改变显示的倒数/正数数字

    private boolean isAllowAlert = false;
    private boolean isAllowNotify = false;
    final int[] course_bj = {R.drawable.coursetable1, R.drawable.coursetable2,
            R.drawable.coursetable3, R.drawable.coursetable4, R.drawable.coursetable5,
            R.drawable.coursetable6, R.drawable.coursetable7, R.drawable.coursetable8,
            R.drawable.coursetable9, R.drawable.coursetable10};
    final String[] dayOfWeekArray={"星期天","星期一","星期二","星期三","星期三","星期五","星期六"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar today = Calendar.getInstance();
        DayManager.setSelectDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        DayManager.setRealDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        setContentView(R.layout.activity_main);

        // 申请权限
        getPermissions();

        handler = new Handler(){
            public void handleMessage(Message msg) {
                //Bundle data = msg.getData();
                switch (msg.what)
                {
                    //点击切换日期
                    case 1:
                        updateTitleAndView();
                        break;
                    case 2:
                        createMonthScheduleView();
                        createWeekScheduleView();
                        createDayScheduleView();
                        break;
                }
            }
        };

        db = DBAdapter.setDBAdapter(MainActivity.this);
        db.open();
        initView();
        initCalendar();
        createMonthScheduleView();
        createWeekScheduleBar();
        createWeekScheduleView();
        createDayScheduleBar();
        createDayScheduleView();
        tabPaging();
        sending();
        askAlertPermission();
    }


    @Override
    protected void onStart() {
        super.onStart();
        setClock();
        //updateTitleAndView();
    }

    public void askAlertPermission(){
        //弹窗权限验证
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isAllowAlert = PrefUtils.getBoolean(this,"isAllowAlert",false);
            if(!isAllowAlert){
                showPermissionDialog();
            }
        }else {//已有权限
            //SendAlarmBroadcast.startAlarmService(this);
        }
    }
    public void askNotifyPermission(){
        //通知权限验证
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isAllowNotify = PrefUtils.getBoolean(this,"isAllowNotify",false);
            if(!isAllowNotify){
                showNotifyPermissionDialog();
            }
        }else {//已有权限
            //SendAlarmBroadcast.startAlarmService(this);
        }
    }
    //权限申请相关方法
    //@TargetApi(Build.VERSION_CODES.M)
    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1);
    }

    /**
     * 弹窗权限申请
     */
    private void showPermissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提醒功能需要开启悬浮窗权限哦(1/2)")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        requestAlertWindowPermission();
                    }
                }).setNegativeButton("取消",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    /**
     * 通知权限申请
     */
    private void showNotifyPermissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("开启横幅通知可以获得更好的体验哦(2/2)")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        requestNotifyWindowPermission();
                    }
                }).setNegativeButton("取消",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }


    //TargetApi(Build.VERSION_CODES.M)
    private void requestNotifyWindowPermission() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android 8.0引导，引导到CHANNEL_ID对应的渠道设置下
            intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, this.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //android 5.0-7.0,引导到所有渠道设置下（单个渠道没有具体的设置）
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        } else {
            //其他
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        }
        startActivityForResult(intent, 2);
    }

    private void initView(){
        //悬浮球
        floatMenu_LL = (LinearLayout)findViewById(R.id.floatMenu_LL);
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
                updateTitleAndView();
            }
        });

        //搜索和标题栏
        titleSelectText=findViewById(R.id.title_select_text);
        createTitle();
        titleBar=findViewById(R.id.title_bar);
        datePickerDialog = new DatePickerDialog(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                //int参数分别为年月日
                DayManager.setSelectYear(i);
                DayManager.setSelectMonth(i1);
                DayManager.setSelectDay(i2);
                MonthPageAdapter.monthView.invalidate();
                WeekPageAdapter.weekView.invalidate();
                DayPageAdapter.dayView.invalidate();
                updateTitleAndView();

            }
        }, DayManager.getSelectYear(), DayManager.getSelectMonth(), DayManager.getSelectDay());

        titleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        searchBtn=findViewById(R.id.search_btn);
        searchBtn.getBackground().setAlpha(0);
        searchTitle=findViewById(R.id.search_title);
        searchTitle.setVisibility(View.GONE);
        searchBackBtn=findViewById(R.id.search_back);
        searchBackBtn.getBackground().setAlpha(0);
        searchDelBtn=findViewById(R.id.search_del);
        searchDelBtn.getBackground().setAlpha(0);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSearch){
                    searchTitle.setVisibility(View.VISIBLE);
                    titleBar.setVisibility(View.GONE);
                    isSearch=true;
                }
                //startActivity(new Intent(MainActivity.this,SearchActivity.class));

                search_scheduleDateList = FindSchedule.scheduleDateList;
                search_listView.setAdapter(new ScheduleAdapter1(MainActivity.this,search_scheduleDateList));
                menu_del.setVisibility(View.GONE);
                search_listView_LL.setVisibility(View.VISIBLE);
                tabHost_LL.setVisibility(View.GONE);
                floatMenu_LL.setVisibility(View.GONE);
            }
        });
        searchBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSearch){
                    searchTitle.setVisibility(View.GONE);
                    titleBar.setVisibility(View.VISIBLE);
                    isSearch=false;

                    search_listView_LL.setVisibility(View.GONE);
                    tabHost_LL.setVisibility(View.VISIBLE);
                    searchView.setQuery("",true);
                    menu_del.setVisibility(View.GONE);
                    floatMenu_LL.setVisibility(View.VISIBLE);
                }
            }
        });

        menu_del = (LinearLayout)findViewById(R.id.menu_del);
        searchDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_listView.setAdapter(new ScheduleAdapter1(MainActivity.this,search_scheduleDateList,1));
                menu_del.setVisibility(View.VISIBLE);
            }
        });

        check_all_btn = (Button)findViewById(R.id.check_all_btn);
        check_all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_all_btn.getText().equals("全选")){
                    for(int i=0;i<search_scheduleDateList.size();i++){
                        for(int j=0;j<search_scheduleDateList.get(i).simpleDateList.size();j++){
                            SimpleDate simpleDate = search_scheduleDateList.get(i).simpleDateList.get(j);
                            simpleDate.isChecked = true;
                            search_scheduleDateList.get(i).simpleDateList.set(j,simpleDate);
                        }
                    }
                    check_all_btn.setText("全不选");
                }else{
                    for(int i=0;i<search_scheduleDateList.size();i++){
                        for(int j=0;j<search_scheduleDateList.get(i).simpleDateList.size();j++){
                            SimpleDate simpleDate = search_scheduleDateList.get(i).simpleDateList.get(j);
                            simpleDate.isChecked = false;
                            search_scheduleDateList.get(i).simpleDateList.set(j,simpleDate);
                        }
                    }
                    check_all_btn.setText("全选");
                }
                search_listView.setAdapter(new ScheduleAdapter1(MainActivity.this,search_scheduleDateList,1));
            }
        });

        cancel_del_btn = (Button)findViewById(R.id.cancel_del_btn);
        cancel_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_listView.setAdapter(new ScheduleAdapter1(MainActivity.this,search_scheduleDateList));
                menu_del.setVisibility(View.GONE);
            }
        });

        finish_del_btn = (Button)findViewById(R.id.finish_del_btn);
        finish_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int del_num = 0;
                for(int i=0;i<search_scheduleDateList.size();i++){
                    for(int j=0;j<search_scheduleDateList.get(i).simpleDateList.size();j++){
                        SimpleDate simpleDate = search_scheduleDateList.get(i).simpleDateList.get(j);
                        if(simpleDate.isChecked){
                            if(db.getOneDataFromSchedule(simpleDate.id)!=null){
                                db.deleteOneDataFromSchedule(simpleDate.id);
                                Log.e("delSchedule",simpleDate.id+"");
                                del_num++;
                            }
                        }
                    }
                }
                Toast.makeText(MainActivity.this,"成功删除"+del_num+"条日程",Toast.LENGTH_SHORT).show();

                // 更新list
                List<Schedule> scheduleList = db.getAllDataFromSchedule();
                FindSchedule fs = new FindSchedule(scheduleList);
                search(searchView.getQuery().toString());
                search_listView.setAdapter(new ScheduleAdapter1(MainActivity.this,search_scheduleDateList));
                menu_del.setVisibility(View.GONE);
            }
        });

        tabHost_LL = (LinearLayout)findViewById(R.id.tabHost_LL);
        search_listView_LL = (LinearLayout)findViewById(R.id.search_listView_LL);
        searchView = (SearchView)findViewById(R.id.search_view);
        search_listView = (ListView)findViewById(R.id.search_listView);

        search_scheduleDateList = new ArrayList<>();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //点击软键盘搜索的时候执行
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //搜索框文本发生改变的时候执行
                if(s != null && !"".equals(s.trim())){
                    search(s);
                } else{
                    search_scheduleDateList = FindSchedule.scheduleDateList;
                }

                // 显示查询后日程
                search_listView.setAdapter(new ScheduleAdapter1(MainActivity.this,search_scheduleDateList));
                menu_del.setVisibility(View.GONE);
                Log.e("search schedule",s+search_scheduleDateList.size()+"");

                return false;
            }
        });

        // 查询日程 存在FindSchedule里 后续可以直接用FindSchedule.scheduleDateList拿 增删改后也这样操作修改变量
        List<Schedule> scheduleList = db.getAllDataFromSchedule();
        if(scheduleList != null){
            FindSchedule fs = new FindSchedule(scheduleList);
        }

        // 查询重要日 存在FindBigDay里 后续可以直接用FindBigDay.allBigDayList拿 增删改后也这样操作修改变量
        List<BigDay> bigDayList = db.getAllDataFromBigDay();
        if(bigDayList != null){
            FindBigDay fb = new FindBigDay(bigDayList);
        }

    }
    public void initCalendar() {
        //日历
        monthViewPager = ((ViewPager) findViewById(R.id.month_pager));
        LayoutInflater inflater = LayoutInflater.from(this);
        monthViews = new ArrayList<View>();
        main_month_scheduleListView = (ListView)findViewById(R.id.main_month_scheduleListView); // 日程
        main_month_bigDayListView = (ListView)findViewById(R.id.main_month_bigDayListView);// 重要日
        //添加布局
        monthViews.add(inflater.inflate(R.layout.month_view_left, null));
        monthViews.add(inflater.inflate(R.layout.month_view, null));
        monthViews.add(inflater.inflate(R.layout.month_view_right, null));


        monthViewPager.setAdapter(new MonthPageAdapter(this, monthViews));
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
                MonthView monthView = monthViewPager.findViewWithTag(1);
                LeftMonthView leftMonthView = monthViewPager.findViewWithTag(0);
                RightMonthView rightMonthView = monthViewPager.findViewWithTag(2);

                // ViewPager.SCROLL_STATE_IDLE 标识的状态是当前页面完全展现，并且没有动画正在进行中，如果不
                // 是此状态下执行 setCurrentItem 方法回在首位替换的时候会出现跳动！
                if (state != ViewPager.SCROLL_STATE_IDLE) return;

                // 当视图在第一个时，将页面号设置为图片的最后一张。
                if (currentPosition == 0) {
                    Calendar temp = DayManager.getSelectCalendar();
                    temp.add(Calendar.MONTH, -1);
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    DayManager.setSelectCalendar(temp);
                    monthView.setCalendar(Calendar.MONTH, DayManager.getSelectMonth());
                    monthView.invalidate();
                    monthViewPager.setCurrentItem(1, false);
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
                    if (leftMonthView != null && rightMonthView != null) {
                        leftMonthView.setVisibility(View.GONE);
                        rightMonthView.setVisibility(View.GONE);
                        leftMonthView.invalidate();
                        rightMonthView.invalidate();
                        leftMonthView.setVisibility(View.VISIBLE);
                        rightMonthView.setVisibility(View.VISIBLE);
                    }
                } else if (currentPosition == 2) {
                    // 当视图在最后一个是,将页面号设置为图片的第一张。
                    Calendar temp = DayManager.getSelectCalendar();
                    temp.add(Calendar.MONTH, 1);
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    DayManager.setSelectCalendar(temp);
                    //monthView.setCalendar(Calendar.MONTH,DayManager.getSelectMonth());
                    monthView.invalidate();
                    //monthView.setCalendar(Calendar.MONTH,DayManager.getSelectMonth());
                    monthViewPager.setCurrentItem(1, false);
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
                    if (leftMonthView != null && rightMonthView != null) {
                        leftMonthView.setVisibility(View.GONE);
                        rightMonthView.setVisibility(View.GONE);
                        leftMonthView.invalidate();
                        rightMonthView.invalidate();
                        leftMonthView.setVisibility(View.VISIBLE);
                        rightMonthView.setVisibility(View.VISIBLE);
                    }
                }
                updateTitleAndView();
            }
        });
        weekViewPager = findViewById(R.id.week_pager);
        weekViews = new ArrayList<View>();
        //添加布局
        weekViews.add(inflater.inflate(R.layout.week_view_left, null));
        weekViews.add(inflater.inflate(R.layout.week_view, null));
        weekViews.add(inflater.inflate(R.layout.week_view_right, null));

        weekViewPager.setAdapter(new WeekPageAdapter(this, weekViews));
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

                WeekView weekView = weekViewPager.findViewWithTag(1);
                LeftWeekView leftWeekView = weekViewPager.findViewWithTag(0);
                RightWeekView rightWeekView = weekViewPager.findViewWithTag(2);
                Calendar temp = Calendar.getInstance();
                temp.set(DayManager.getSelectYear(), DayManager.getSelectMonth(), DayManager.getSelectDay());

                if (state != ViewPager.SCROLL_STATE_IDLE) return;

                // 当视图在第一个时，将页面号设置为图片的最后一张。
                if (currentPosition == 0) {
                    //Log.d("!week", String.valueOf(temp.getTime()));
                    temp.add(Calendar.DAY_OF_MONTH, -7);
                    //Log.d("!weekafter", String.valueOf(temp.getTime()));
                    DayManager.setSelectCalendar(temp);
                    //Log.d("!weekafterget", String.valueOf(DayManager.getSelectCalendar().getTime()));
                    //Log.d("!weekIndex", String.valueOf(DayManager.getSelectIndex()));
                    weekView.setCalendar(temp);
                    weekView.invalidate();
                    weekViewPager.setCurrentItem(1, false);
                    if (leftWeekView != null && rightWeekView != null) {
                        leftWeekView.setVisibility(View.GONE);
                        rightWeekView.setVisibility(View.GONE);
                        leftWeekView.invalidate();
                        rightWeekView.invalidate();
                        leftWeekView.setVisibility(View.VISIBLE);
                        rightWeekView.setVisibility(View.VISIBLE);
                    }
                } else if (currentPosition == 2) {
                    temp.add(Calendar.DAY_OF_MONTH, +7);
                    DayManager.setSelectCalendar(temp);
                    weekView.invalidate();
                    weekViewPager.setCurrentItem(1, false);
                    if (leftWeekView != null && rightWeekView != null) {
                        leftWeekView.setVisibility(View.GONE);
                        rightWeekView.setVisibility(View.GONE);
                        leftWeekView.invalidate();
                        rightWeekView.invalidate();
                        leftWeekView.setVisibility(View.VISIBLE);
                        rightWeekView.setVisibility(View.VISIBLE);
                    }
                }
                updateCalendar();
            }
        });
        //日
        dayViewPager = findViewById(R.id.day_pager);
        dayViews = new ArrayList<View>();
        //添加布局
        dayViews.add(inflater.inflate(R.layout.day_view_left, null));
        dayViews.add(inflater.inflate(R.layout.day_view, null));
        dayViews.add(inflater.inflate(R.layout.day_view_right, null));

        dayViewPager.setAdapter(new DayPageAdapter(this, dayViews));
        dayViewPager.setCurrentItem(1);
        dayViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

                WeekView dayView = dayViewPager.findViewWithTag(1);
                LeftWeekView leftWeekDayView = dayViewPager.findViewWithTag(0);
                RightWeekView rightWeekDayView = dayViewPager.findViewWithTag(2);
                Calendar temp = Calendar.getInstance();
                temp.set(DayManager.getSelectYear(), DayManager.getSelectMonth(), DayManager.getSelectDay());

                if (state != ViewPager.SCROLL_STATE_IDLE) return;

                // 当视图在第一个时，将页面号设置为图片的最后一张。
                if (currentPosition == 0) {
                    //Log.d("!day", String.valueOf(temp.getTime()));
                    temp.add(Calendar.DAY_OF_MONTH, -7);
                    //Log.d("!dayafter", String.valueOf(temp.getTime()));
                    DayManager.setSelectCalendar(temp);
                    dayView.setCalendar(temp);
                    dayView.invalidate();
                    dayViewPager.setCurrentItem(1, false);
                    if (leftWeekDayView != null && rightWeekDayView != null) {
                        leftWeekDayView.setVisibility(View.GONE);
                        rightWeekDayView.setVisibility(View.GONE);
                        leftWeekDayView.invalidate();
                        rightWeekDayView.invalidate();
                        leftWeekDayView.setVisibility(View.VISIBLE);
                        rightWeekDayView.setVisibility(View.VISIBLE);
                    }

                } else if (currentPosition == 2) {
                    temp.add(Calendar.DAY_OF_MONTH, +7);
                    DayManager.setSelectCalendar(temp);
                    dayView.invalidate();
                    dayViewPager.setCurrentItem(1, false);
                    if (leftWeekDayView != null && rightWeekDayView != null) {
                        leftWeekDayView.setVisibility(View.GONE);
                        rightWeekDayView.setVisibility(View.GONE);
                        leftWeekDayView.invalidate();
                        rightWeekDayView.invalidate();
                        leftWeekDayView.setVisibility(View.VISIBLE);
                        rightWeekDayView.setVisibility(View.VISIBLE);
                    }
                }
                updateTitleAndView();
            }
        });
        DayPageAdapter.dayView.setOnDrawDays(new CalendarView.OnDrawDays() {
            @Override
            public boolean drawDay(Day day, Canvas canvas, Context context, Paint paint) {

                return false;
            }

            @Override
            public void drawDayAbove(Day day, Canvas canvas, Context context, Paint paint) {
                String s[] = day.dateText.split("-");
                // Log.d("!Day",String.valueOf(DayManager.getSelectDay()));
                // Log.d("!Day_dateText",day.dateText);
                //dateText带0
                if (s.length == 3 && !(s[2].equals(String.valueOf(DayManager.getSelectDay())) || s[2].equals("0" + String.valueOf(DayManager.getSelectDay())))) {
                    paint.setARGB(60, 80, 130, 255);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawRect(0, 0, day.width, day.height, paint);
                    //canvas.draw
                    //canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.yazi), 20, 20, paint);
                }
            }
        });
        CalendarView.OnDrawDays otherDays = new CalendarView.OnDrawDays() {
            @Override
            public boolean drawDay(Day day, Canvas canvas, Context context, Paint paint) {

                return false;
            }

            @Override
            public void drawDayAbove(Day day, Canvas canvas, Context context, Paint paint) {
                {
                    String s[] = day.dateText.split("-");
                    if (s.length == 3) {
                        paint.setARGB(60, 80, 130, 255);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawRect(0, 0, day.width, day.height, paint);
                    }
                    //canvas.draw
                    //canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.yazi), 20, 20, paint);
                }
            }
        };
        DayPageAdapter.lastWeekDayView.setOnDrawDays(otherDays);
        DayPageAdapter.nextWeekDayView.setOnDrawDays(otherDays);

    }


    private void tabPaging(){
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        // 进入界面时刷新UI：setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        // tab1 月
        View tab1 = LayoutInflater.from(this).inflate(R.layout.tab1, null);
        spec = tabHost.newTabSpec("1").setIndicator(tab1).setContent(R.id.main_month_view);//若传递layout必须传递id
        tabHost.addTab(spec);

        // tab2 周
        View tab2 = LayoutInflater.from(this).inflate(R.layout.tab2, null);
        spec = tabHost.newTabSpec("2").setIndicator(tab2).setContent(R.id.main_week_view);
        tabHost.addTab(spec);

        // tab3 日
        View tab3 = LayoutInflater.from(this).inflate(R.layout.tab3, null);
        spec = tabHost.newTabSpec("3").setIndicator(tab3).setContent(R.id.main_day_view);
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
                page = Integer.valueOf(tabId)-1;
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

    public void setClock(){
        Intent intent = new Intent(MainActivity.this, ClockService.class);
        //startForegroundService(intent);
        startService(intent);
    }

    public void sending(){
        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                if (msg.what == Finding) {
                    JSONObject obj = (JSONObject)msg.obj;
                    try{
                        String phone = obj.getString("address");
                        String dateStr = obj.getString("code");// 日期
                        int year = Integer.parseInt(dateStr.substring(0,4));
                        int month = Integer.parseInt(dateStr.substring(4,6));
                        int day = Integer.parseInt(dateStr.substring(6,8));
                        Calendar c = Calendar.getInstance();
                        c.set(year,month-1,day);
                        Timestamp date = new Timestamp(c.getTimeInMillis());
                        date.setHours(0);
                        date.setMinutes(0);
                        date.setSeconds(0);
                        date.setNanos(0);
                        Log.e("查询日期",date.getTime()+"");

                        // 查询数据
                        List<String> scheduleStrList = new ArrayList<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String scheduleStr;
                        List<ScheduleDate> scheduleDateList = FindSchedule.scheduleDateList;
                        int i;
                        for(i=0;i<scheduleDateList.size();i++){
                            Log.e("日期",scheduleDateList.get(i).date.getTime()+"");
                            if(date.getTime() == scheduleDateList.get(i).date.getTime()){
                                List<SimpleDate> simpleDateList = scheduleDateList.get(i).simpleDateList;
                                for(int j=0;j<simpleDateList.size();j++){
                                    if(simpleDateList.get(j).type == 0){
                                        scheduleStr = (j+1)+"."+simpleDateList.get(j).title+"：全天";
                                    }else if(simpleDateList.get(j).type == 1){
                                        scheduleStr = (j+1)+"."+simpleDateList.get(j).title+"："+sdf.format(simpleDateList.get(j).startTime)+"开始";
                                    }else if(simpleDateList.get(j).type == 2){
                                        scheduleStr = (j+1)+"."+simpleDateList.get(j).title+"："+sdf.format(simpleDateList.get(j).endTime)+"结束";
                                    }else{
                                        scheduleStr = (j+1)+"."+simpleDateList.get(j).title+"："+sdf.format(simpleDateList.get(j).startTime)+"至"+sdf.format(simpleDateList.get(j).endTime);
                                    }
                                    scheduleStrList.add(scheduleStr);
                                }
                                break;
                            }
                        }
                        if(i == scheduleDateList.size()){
                            scheduleStr = "无日程安排";
                            scheduleStrList.add(scheduleStr);
                        }

                        // 发送短信
                        SmsManager smsManager = SmsManager.getDefault();
                        for(int x=0;x<scheduleStrList.size();x++){
                            smsManager.sendTextMessage(phone, null, scheduleStrList.get(x), null, null);
                            Log.e("发送短信",scheduleStrList.get(x));
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 发送短信权限、访问短信权限
                }
            };
        };

        //创建内容观察者的对象
        smsObserver = new SmsObserver(MainActivity.this, mHandler);
        //短信的uri为content://sms
        Uri uri = Uri.parse("content://sms");
        //注册内容观察者
        this.getContentResolver().registerContentObserver(uri, true, smsObserver);
    }

    public void search(String s){
        // 全部日程
        List<ScheduleDate> scheduleDateList = FindSchedule.scheduleDateList;
        search_scheduleDateList = new ArrayList<>();

        // 处理数据 存储查询的日程
        if(scheduleDateList != null){
            // 查询每一天
            for(int i=0;i<scheduleDateList.size();i++){
                Timestamp date = scheduleDateList.get(i).date;
                List<SimpleDate> simpleDateList = new ArrayList<>();
                // 查询每一个时间段
                for(int j=0;j<scheduleDateList.get(i).simpleDateList.size();j++){
                    // 该时间段的日程是否包含搜索串
                    if(scheduleDateList.get(i).simpleDateList.get(j).title.indexOf(s) != -1){
                        simpleDateList.add(scheduleDateList.get(i).simpleDateList.get(j));
                    }
                }
                if(simpleDateList != null && simpleDateList.size()!=0){
                    ScheduleDate scheduleDate = new ScheduleDate(date,simpleDateList);
                    search_scheduleDateList.add(scheduleDate);
                }
            }
        }
    }
    public void createTitle(){
        int year= DayManager.getSelectYear();
        int month=DayManager.getSelectMonth()+1;
        titleSelectText.setText(year+"年"+month+"月");
    }
    public void updateTitleAndView(){
        int year= DayManager.getSelectYear();
        int month=DayManager.getSelectMonth()+1;
        titleSelectText.setText(year+"年"+month+"月");
        updateCalendar();

    }

    public void createMonthScheduleView(){
        int year = DayManager.getSelectYear();
        int month = DayManager.getSelectMonth()+1;
        int day = DayManager.getSelectDay();
        Timestamp date = new Timestamp(year-1900,month-1,day,0,0,0,0);
        List<ScheduleDate> scheduleDate = new ArrayList<>();
        for(int i=0;i<FindSchedule.scheduleDateList.size();i++){
            Log.e("FindScheduleDate",FindSchedule.scheduleDateList.get(i).date.getTime()+"");
            if(date.getTime() == FindSchedule.scheduleDateList.get(i).date.getTime()){
                scheduleDate.add(FindSchedule.scheduleDateList.get(i));
                break;
            }
        }
        main_month_scheduleListView.setAdapter(new ScheduleAdapter1(MainActivity.this,scheduleDate));
    }
    private void createWeekScheduleBar() {
        weekScheduleView = findViewById(R.id.week_schedule_view);
        for (int i = 1; i <= 24; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.left_time_view, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 180);
            view.setLayoutParams(params);
            TextView text = (TextView) view.findViewById(R.id.time_number_text);
            text.setText(String.valueOf(i));
            LinearLayout leftViewLayout = (LinearLayout) findViewById(R.id.week_time_left_view_layout);
            leftViewLayout.addView(view);
        }

    }

    public void createWeekScheduleView() {
        weekRemoveView();
        int[] haveBigday=createWeekBigDayView();
        Log.d("!WeekView", "scheduleDateSize" + FindSchedule.getWeekScheduleDate().size());
        for (ScheduleDate schedule : FindSchedule.getWeekScheduleDate()) {
            createWeekTableView(schedule,haveBigday);
        }
    }

    private int[] createWeekBigDayView() {
        int[] haveBigDay = {0, 0, 0, 0, 0, 0, 0};
        final List<List<BigDay>> bigDays = new ArrayList<List<BigDay>>(7);
        for (int i = 0; i < 7; i++)//初始化
        {
            bigDays.add(new ArrayList<BigDay>());
        }
        for (BigDay bigDay : FindBigDay.getWeekBigDay()) {
            Timestamp temp = bigDay.date;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(temp);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            haveBigDay[dayOfWeek] = 1;
            bigDays.get(dayOfWeek).add(bigDay);
        }
        for (int i=0;i<7;i++) {
            GridLayout dayView = null;
            switch (i) {
                case 0:
                    dayView = findViewById(R.id.week_all_day_0);
                    break;
                case 1:
                    dayView = findViewById(R.id.week_all_day_1);
                    break;
                case 2:
                    dayView = findViewById(R.id.week_all_day_2);
                    break;
                case 3:
                    dayView = findViewById(R.id.week_all_day_3);
                    break;
                case 4:
                    dayView = findViewById(R.id.week_all_day_4);
                    break;
                case 5:
                    dayView = findViewById(R.id.week_all_day_5);
                    break;
                case 6:
                    dayView = findViewById(R.id.week_all_day_6);
                    break;
            }
            if (bigDays.get(i).size() > 0) {
                final BigDay bigDay = bigDays.get(i).get(0);
                final View v = LayoutInflater.from(this).inflate(R.layout.schedule_small_card, null);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(0),
                        GridLayout.spec(0,1.0f));
                v.setLayoutParams(params);
                TextView text = (TextView) v.findViewById(R.id.schedule_text_view);
                text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                if (bigDays.get(i).size() > 1) {
                    String title=bigDay.title;
                    if(title.length()>=3)
                        title=title.substring(0,1)+"..";
                    text.setText("★" + title + " " + "+");
                    final List<BigDay> list = bigDays.get(i);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initPopWindowForBigDay(bigDay.date, list);
                        }
                    });
                } else {
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, LookBigDayActivity.class);
                            intent.putExtra("id", bigDay.id);
                            startActivity(intent);
                        }
                    });
                    String title=bigDay.title;
                    if(title.length()>3)
                        title=title.substring(0,2)+"..";
                    text.setText("★" + title); //显示日程名
                }
                dayView.addView(v);//加载单个日程布局

            }
        }

        return haveBigDay;
    }
    private void createWeekTableView(final ScheduleDate schedule,int[] haveBigDay) {
        //每次处理当天日程的视图
        Log.d("!WeekView", "in week view");

        int height = 3;//180/60
        Timestamp temp = schedule.date;
        Log.d("!WeekView", "Day" + String.valueOf(temp.getTime()));
        Calendar date = Calendar.getInstance();
        date.setTime(temp);
        // /Calendar date=tsCovertToCal(temp);
        Log.d("!WeekView", "Calendar" + date.getTime());
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK) - 1;
        RelativeLayout dayView = null;
        GridLayout allDayView=null;
        Log.d("!WeekView", "DayofWeek" + String.valueOf(dayOfWeek));
        List<SimpleDate> simpleDateList = schedule.simpleDateList;
        final List<SimpleDate> allDayList = new ArrayList<SimpleDate>();
        List<SimpleDate> daytimeList = new ArrayList<SimpleDate>();
        final List<List<SimpleDate>> alterList = new ArrayList<List<SimpleDate>>();
        List<int[]> timeList = new ArrayList<int[]>();//存放时间节点

        //对全天和非全天分类处理
        for (SimpleDate simpleDate : simpleDateList) {
            if (simpleDate.type == 0) {
                allDayList.add(simpleDate);
            } else {
                int startTime = simpleDate.startTime.getHours() * 60 + simpleDate.startTime.getMinutes();
                int endTime = simpleDate.endTime.getHours() * 60 + simpleDate.endTime.getMinutes();
                if (simpleDate.type == 1) {
                    endTime = 1440;
                } else if (simpleDate.type == 2) {
                    startTime = 0;
                } else if (startTime == endTime) {
                    endTime = startTime + 5;
                }

                int i=0;
                if (timeList.size() == 0) {
                    timeList.add(new int[]{startTime, endTime});
                    List<SimpleDate> first = new ArrayList<SimpleDate>();
                    first.add(simpleDate);
                    alterList.add(first);
                } else {
                    boolean isCover=false;
                    for (i = 0; i < timeList.size(); i++) {
                        int t1=timeList.get(i)[0];
                        int t2=timeList.get(i)[1];
                        if (startTime >= timeList.get(i)[0] && endTime <= timeList.get(i)[1]) {//包含关系
                            alterList.get(i).add(simpleDate);
                            isCover=true;
                        } else if (startTime >= timeList.get(i)[0]&&startTime<timeList.get(i)[1]&& endTime > timeList.get(i)[1]) {//前面包含，后面大于
                            alterList.get(i).add(simpleDate);
                            timeList.get(i)[1] = endTime;
                            isCover=true;
                        } else if (startTime < timeList.get(i)[0] &&endTime>timeList.get(i)[0]&& endTime <= timeList.get(i)[1]) {//前面大于，后面包含
                            alterList.get(i).add(simpleDate);
                            timeList.get(i)[0] = startTime;
                            isCover=true;
                        } else if (startTime < timeList.get(i)[0] && endTime > timeList.get(i)[1]) {//全大于
                            alterList.get(i).add(simpleDate);
                            timeList.get(i)[0] = startTime;
                            timeList.get(i)[1] = endTime;
                            isCover=true;
                        }

                    }
                    if(i==timeList.size()&&isCover==false){
                        //时间无交集
                        timeList.add(new int[]{startTime, endTime});
                        List<SimpleDate> newOne = new ArrayList<SimpleDate>();
                        newOne.add(simpleDate);
                        alterList.add(newOne);
                    }
                }
            }
        }
        //全天
        switch (dayOfWeek) {
            case 0:
                allDayView =  findViewById(R.id.week_all_day_0);
                break;
            case 1:
                allDayView = findViewById(R.id.week_all_day_1);
                break;
            case 2:
                allDayView =  findViewById(R.id.week_all_day_2);
                break;
            case 3:
                allDayView = findViewById(R.id.week_all_day_3);
                break;
            case 4:
                allDayView = findViewById(R.id.week_all_day_4);
                break;
            case 5:
                allDayView =  findViewById(R.id.week_all_day_5);
                break;
            case 6:
                allDayView = findViewById(R.id.week_all_day_6);
                break;
        }
        if (allDayList.size() > 0) {
            final SimpleDate simpleDate = allDayList.get(0);
            final View v1 = LayoutInflater.from(this).inflate(R.layout.schedule_small_card, null);
            final View v2 = LayoutInflater.from(this).inflate(R.layout.schedule_small_card, null);
            GridLayout.LayoutParams params1 = new GridLayout.LayoutParams(
                    GridLayout.spec(0),//位置  weight
                    GridLayout.spec(0,1.0f));
            GridLayout.LayoutParams params2 = new GridLayout.LayoutParams(
                    GridLayout.spec(1),//位置 weight
                    GridLayout.spec(0,1.0f));
            v1.setLayoutParams(params1);
            TextView text1 = (TextView) v1.findViewById(R.id.schedule_text_view);
            text1.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
            v2.setLayoutParams(params2);
            TextView text2 = (TextView) v2.findViewById(R.id.schedule_text_view);
            text2.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);

            if (haveBigDay[dayOfWeek] == 1) {//已有一个重要日
                if (allDayList.size() > 1) {
                    String title=simpleDate.title;
                    if(title.length()>=3)
                        title=title.substring(0,2)+"..";
                    text2.setText(title+ " " + "+");
                    v2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initPopWindowForSchedule(schedule.date, allDayList);
                        }
                    });
                } else {
                    String title=simpleDate.title;
                    if(title.length()>=3)
                        title=title.substring(0,2)+"..";
                    text2.setText(title); //显示日程名
                    v2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v1) {
                            Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);
                            intent.putExtra("id", simpleDate.id);
                            startActivity(intent);
                        }
                    });

                }
                allDayView.addView(v2);//加载单个日程布局
            } else {//没有重要日，则显示两个模块的日程
                String title=simpleDate.title;
                if(title.length()>3)
                    title=title.substring(0,2)+"..";
                    text1.setText(title);
                    v1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v1) {
                            Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);
                            intent.putExtra("id", simpleDate.id);
                            startActivity(intent);
                        }
                    });
                allDayView.addView(v1);
                    if (allDayList.size() > 2) {
                        String title2=allDayList.get(1).title;
                        if(title2.length()>3)
                            title2=title2.substring(0,2)+"..";
                        text2.setText(title2 + " " + "+");
                        v2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                initPopWindowForSchedule(schedule.date, allDayList);
                            }
                        });
                        allDayView.addView(v2);
                    } else if(allDayList.size()==2){
                        String title2=allDayList.get(1).title;
                        if(title2.length()>3)
                            title2=title2.substring(0,2)+"..";
                        text2.setText(title2);
                        v2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v1) {
                                Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);
                                intent.putExtra("id", allDayList.get(1).id);
                                startActivity(intent);
                            }
                        });
                        allDayView.addView(v2);
                    }
                }
            }
            //非全天
            switch (dayOfWeek) {
                case 0:
                    dayView = (RelativeLayout) findViewById(R.id.week_0);
                    break;
                case 1:
                    dayView = (RelativeLayout) findViewById(R.id.week_1);
                    break;
                case 2:
                    dayView = (RelativeLayout) findViewById(R.id.week_2);
                    break;
                case 3:
                    dayView = (RelativeLayout) findViewById(R.id.week_3);
                    break;
                case 4:
                    dayView = (RelativeLayout) findViewById(R.id.week_4);
                    break;
                case 5:
                    dayView = (RelativeLayout) findViewById(R.id.week_5);
                    break;
                case 6:
                    dayView = (RelativeLayout) findViewById(R.id.week_6);
                    break;
            }
            for (int j = 0; j < timeList.size(); j++) {
                int[] time = timeList.get(j);
                SimpleDate simpleDate = alterList.get(j).get(0);
                final View v = LayoutInflater.from(this).inflate(R.layout.schedule_card, null); //加载单个课程布局
                v.setY(height * time[0]); //设置开始高度,即第几节课开始
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, (time[1] - time[0]) * height); //设置布局高度,即跨多少节课
                v.setLayoutParams(params);
                TextView text = (TextView) v.findViewById(R.id.schedule_text_view);
                text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                if (alterList.get(j).size() > 1) {
                    if(time[1] - time[0]>=30) {
                    text.setText(simpleDate.title + "\n\n" + "+");
                    }else {
                        text.setText("+");
                    }
                    final List<SimpleDate> list = alterList.get(j);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initPopWindowForSchedule(schedule.date, list);
                        }
                    });
                } else {
                    if(time[1] - time[0]>=30) {
                        text.setText(simpleDate.title);
                    }else {
                        text.setText("");
                    }
                    final int id = simpleDate.id;
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    });
                }
                dayView.addView(v);
            }
        }

    public void weekRemoveView() {
        RelativeLayout view = (RelativeLayout) findViewById(R.id.week_0);
        GridLayout view2=null;
        view.removeAllViews();
        view = (RelativeLayout) findViewById(R.id.week_1);
        view.removeAllViews();
        view = (RelativeLayout) findViewById(R.id.week_2);
        view.removeAllViews();
        view = (RelativeLayout) findViewById(R.id.week_3);
        view.removeAllViews();
        view = (RelativeLayout) findViewById(R.id.week_4);
        view.removeAllViews();
        view = (RelativeLayout) findViewById(R.id.week_5);
        view.removeAllViews();
        view = (RelativeLayout) findViewById(R.id.week_6);
        view.removeAllViews();
        view2= findViewById(R.id.week_all_day_0);
        view2.removeAllViews();
        view2 =  findViewById(R.id.week_all_day_1);
        view2.removeAllViews();
        view2 =  findViewById(R.id.week_all_day_2);
        view2.removeAllViews();
        view2 =  findViewById(R.id.week_all_day_3);
        view2.removeAllViews();
        view2 =  findViewById(R.id.week_all_day_4);
        view2.removeAllViews();
        view2 =  findViewById(R.id.week_all_day_5);
        view2.removeAllViews();
        view2 =  findViewById(R.id.week_all_day_6);
        view2.removeAllViews();
    }

    private void createDayScheduleBar() {
        dayScheduleView = findViewById(R.id.day_schedule_view);
        Log.d("!DayView", "bar");
        for (int i = 1; i <= 24; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.left_time_view, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 180);
            view.setLayoutParams(params);
            TextView text = (TextView) view.findViewById(R.id.time_number_text);
            text.setText(String.valueOf(i));
            LinearLayout leftViewLayout = (LinearLayout) findViewById(R.id.day_time_left_view_layout);
            leftViewLayout.addView(view);
        }
    }

    public void createDayScheduleView() {
        final View[] allDayColumn = {findViewById(R.id.day_all_day0),findViewById(R.id.day_all_day1),findViewById(R.id.day_all_day2),
                findViewById(R.id.day_all_day3),findViewById(R.id.day_all_day4)};
        dayRemoveView();
        int count=createDayBigDayView();
        //Log.d("!DayView", "scheduleDateSize" + FindSchedule.getDayScheduleDate().size());
        if(FindSchedule.getDayScheduleDate().size()!=0) {
            createDayTableView(FindSchedule.getDayScheduleDate().get(0), count);
        }else {
            for (int i = count; i < 5; i++) {
                RelativeLayout temp = (RelativeLayout) allDayColumn[i];
                temp.setVisibility(View.GONE);
            }
        }

    }

    public int createDayBigDayView() {
        Log.d("!Day","inDaybigday");
        final View[] allDayColumn = {findViewById(R.id.day_all_day0),findViewById(R.id.day_all_day1),findViewById(R.id.day_all_day2),
                findViewById(R.id.day_all_day3),findViewById(R.id.day_all_day4)};
        int count = 0;
        RelativeLayout layout = null;
        List<BigDay> bigDays = FindBigDay.getTodayBigDay();
        if (bigDays.size() > 0) {
            BigDay bigDay = FindBigDay.getTodayBigDay().get(0);
            layout = (RelativeLayout) allDayColumn[count];
            final View v = LayoutInflater.from(this).inflate(R.layout.schedule_card, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); //设置布局高度,即跨多少节课
            v.setLayoutParams(params);
            TextView text = (TextView) v.findViewById(R.id.schedule_text_view);
            text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
            text.setText("★" + bigDay.title); //显示日程名
            final int id = bigDay.id;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LookBigDayActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });
            layout.addView(v);
            count++;
            if (bigDays.size() > 1) {
                bigDay = FindBigDay.getTodayBigDay().get(1);
                layout = (RelativeLayout) allDayColumn[count];
                final View v1 = LayoutInflater.from(this).inflate(R.layout.schedule_card, null);
                v1.setLayoutParams(params);
                TextView text1 = (TextView) v1.findViewById(R.id.schedule_text_view);
                text1.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                if (bigDays.size() == 2) {
                    text1.setText("★" + bigDay.title); //显示日程名
                    final int id1 = bigDay.id;
                    v1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, LookBigDayActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    });

                } else {
                    text1.setText("★" + bigDay.title + " +"); //显示日程名
                    final int id1 = bigDay.id;
                    final List<BigDay>list=bigDays;
                    list.remove(0);
                    v1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initPopWindowForBigDay(list.get(0).date,list);
                        }
                    });
                }
                layout.addView(v1);
                count++;
            }
            Log.d("!Day","count");
        }
        return count;
    }
    private void createDayTableView(final ScheduleDate schedule, int count) {
        int maxnum=5-count;
        //每次处理当天日程的视图
        //Log.d("!DayView", "in week view");
        int height = 3;//180/60
        //Log.d("!DayView", "Calendar" + schedule.date.getDate());
        final View[] timeDayColumn = {findViewById(R.id.day_layout0),findViewById(R.id.day_layout1),findViewById(R.id.day_layout2),
                findViewById(R.id.day_layout3),findViewById(R.id.day_layout4)};
        final View[] allDayColumn = {findViewById(R.id.day_all_day0),findViewById(R.id.day_all_day1),findViewById(R.id.day_all_day2),
                findViewById(R.id.day_all_day3),findViewById(R.id.day_all_day4)};
        List<SimpleDate> simpleDateList = schedule.simpleDateList;
        //初步分类
        List<SimpleDate> allDayList = new ArrayList<SimpleDate>();
        List<SimpleDate> daytimeList = new ArrayList<SimpleDate>();
        //非全天细分
        List<dayScheduleOnView> alterList = new ArrayList<dayScheduleOnView>();
        List<List<int[]>> timeList = new ArrayList<List<int[]>>();//存放每一列的时间节点
        //对全天和非全天分类处理
        for (SimpleDate simpleDate : simpleDateList) {
            if (simpleDate.type == 0) {
                allDayList.add(simpleDate);
            } else {
                int startTime = simpleDate.startTime.getHours() * 60 + simpleDate.startTime.getMinutes();
                int endTime = simpleDate.endTime.getHours() * 60 + simpleDate.endTime.getMinutes();
                if (simpleDate.type == 1) {
                    endTime = 1440;
                } else if (simpleDate.type == 2) {
                    startTime = 0;
                }else if(startTime==endTime)
                {
                    endTime=startTime+1;
                }
                if (alterList.size() == 0) {
                    alterList.add(new dayScheduleOnView(0, simpleDate, startTime, endTime));
                    List<int[]> firstTime = new ArrayList<int[]>();
                    firstTime.add(new int[]{startTime, endTime});
                    timeList.add(firstTime);
                } else {
                    int i = 0;
                    int j = 0;
                    boolean isCover=false;
                    for (i = 0; i < timeList.size(); i++) {//timeList数量表示当前有几列
                        List<int[]> list = timeList.get(i);
                        isCover=false;
                        for (j = 0; j < list.size(); j++) {
                            if (!(startTime >= list.get(j)[1] && endTime > list.get(j)[1] || startTime < list.get(j)[0] && endTime <= list.get(j)[0])) {//有包含关系
                                {
                                    //只要有一个包含就直接开始判断下一列
                                    isCover=true;
                                    break;
                                }
                            }
                        }
                        if (j == list.size()&&isCover==false)//无包含关系，加入该列
                        {
                            alterList.add(new dayScheduleOnView(i, simpleDate, startTime, endTime));
                            timeList.get(i).add(new int[]{startTime, endTime});
                            break;
                        }
                    }
                    if (i == timeList.size()) {//有包含关系，开新列，若为第五列（4），即合并
                        if (timeList.size() != 5) {
                            List<int[]> list = new ArrayList<int[]>();
                            list.add(new int[]{startTime, endTime});
                            timeList.add(list);
                            alterList.add(new dayScheduleOnView(i, simpleDate, startTime, endTime));
                        } else {
                            timeList.get(4).add(new int[]{startTime, endTime});
                            alterList.add(new dayScheduleOnView(4, simpleDate, startTime, endTime));
                        }
                    }
                }
            }
        }

        //全天
        Log.d("!Day","size??"+allDayList.size());
        if (allDayList.size() > 0) {
            int num=0;
            if(allDayList.size()<maxnum) {//不足五列
                num = allDayList.size();
                for(int i=0;i<num;i++)
                {
                    RelativeLayout layout=(RelativeLayout) allDayColumn[count+i];
                    SimpleDate simpleDate = allDayList.get(i);
                    final View v = LayoutInflater.from(this).inflate(R.layout.schedule_card, null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); //设置布局高度,即跨多少节课
                    v.setLayoutParams(params);
                    TextView text = (TextView) v.findViewById(R.id.schedule_text_view);
                    text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                    text.setText(simpleDate.title); //显示日程名
                    final int id=simpleDate.id;
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    });
                    layout.addView(v);
                }
                if(num+count>=0) {//隐藏多余行
                    for (int i = num+count; i < 5; i++) {
                        RelativeLayout temp = (RelativeLayout) allDayColumn[i];
                        temp.setVisibility(View.GONE);
                    }
                }
            }else{//满5列
                for(int i=0;i<maxnum-1;i++)//前几列
                {
                    RelativeLayout layout=(RelativeLayout) allDayColumn[count+i];
                    SimpleDate simpleDate = allDayList.get(i);
                    final View v = LayoutInflater.from(this).inflate(R.layout.schedule_card, null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); //设置布局高度,即跨多少节课
                    v.setLayoutParams(params);
                    TextView text = (TextView) v.findViewById(R.id.schedule_text_view);
                    text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                    text.setText(simpleDate.title); //显示日程名
                    layout.addView(v);
                    final int id=simpleDate.id;
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    });

                }
                //最后一列
                RelativeLayout layout=(RelativeLayout) allDayColumn[4];
                SimpleDate simpleDate = allDayList.get(maxnum-1);
                final View v = LayoutInflater.from(this).inflate(R.layout.schedule_card, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); //设置布局高度,即跨多少节课
                v.setLayoutParams(params);
                TextView text = (TextView) v.findViewById(R.id.schedule_text_view);
                text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                if (allDayList.size() > maxnum) {
                    text.setText(simpleDate.title + " " + "+");
                    final List<SimpleDate> list=new ArrayList<>();
                    for(int i=maxnum-1;i<allDayList.size();i++)
                    {
                        list.add(allDayList.get(i));
                    }
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initPopWindowForSchedule(schedule.date,list);
                        }
                    });
                } else {
                    text.setText(simpleDate.title); //显示日程名
                    final int id=simpleDate.id;
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    });
                }
                layout.addView(v);//加载单个日程布局
            }
        }
        //非全天
        if(timeList.size()<5){
            for (int j = 0; j < timeList.size(); j++) {
                RelativeLayout layout = (RelativeLayout)timeDayColumn[j];
                //List<dayScheduleOnView> thisColumn=new ArrayList<dayScheduleOnView>();
                for (dayScheduleOnView d : alterList) {
                    if (d.column == j) {
                        final View v = LayoutInflater.from(this).inflate(R.layout.schedule_card, null); //加载单个课程布局
                        v.setY(height * d.startTime); //设置开始高度,即第几节课开始
                        Log.d("!Day",d.startTime+" "+d.endTime);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                (ViewGroup.LayoutParams.MATCH_PARENT, (d.endTime - d.startTime) * height); //设置布局高度,即跨多少节课
                        v.setLayoutParams(params);
                        TextView text = (TextView) v.findViewById(R.id.schedule_text_view);
                        text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                        if(d.endTime-d.startTime>=30) {
                            text.setText(d.simpleDate.title);
                        }else {
                            text.setText("");
                        }
                        final int id=d.simpleDate.id;
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        });
                        layout.addView(v);
                    }
                }


            }
            RelativeLayout temp;
            if(timeList.size()-1>=0) {
                for (int i = timeList.size(); i < 5; i++) {
                    temp = (RelativeLayout) timeDayColumn[i];
                    temp.setVisibility(View.GONE);
                }
            }

        }else{//==5
            for (int j = 0; j <5; j++) {
                RelativeLayout layout = (RelativeLayout) timeDayColumn[j];
                if (j != 4) {//非最后一列
                    for (dayScheduleOnView d : alterList) {
                        if (d.column == j) {
                            View v = LayoutInflater.from(this).inflate(R.layout.schedule_card, null,false); //加载单个课程布局
                            v.setY(height * d.startTime); //设置开始高度,即第几节课开始
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                    (ViewGroup.LayoutParams.MATCH_PARENT, (d.endTime - d.startTime) * height); //设置布局高度,即跨多少节课
                            v.setLayoutParams(params);
                            TextView text = (TextView) v.findViewById(R.id.schedule_text_view);
                            text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                            if(d.endTime-d.startTime>=30) {
                                text.setText(d.simpleDate.title);
                            }else {
                                text.setText("");
                            }//显示日程名
                            final int id=d.simpleDate.id;
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);
                                    intent.putExtra("id", id);
                                    startActivity(intent);
                                }
                            });
                            layout.addView(v);

                        }
                    }
                }else {//第五列
                    int start = 1440;
                    int end = 0;
                    for (int[] a : timeList.get(4)) {
                        if (start >= a[0])
                            start = a[0];
                        if (end < a[1])
                            end = a[1];
                    }
                    layout = (RelativeLayout) timeDayColumn[4];
                    final List<SimpleDate> list=new ArrayList<>();
                    for (dayScheduleOnView d : alterList) {
                        if (d.column == 4) {//获取到第一个5列的日程,剩余日程待处理
                            list.add(d.simpleDate);
                        }
                    }
                    final View v = LayoutInflater.from(this).inflate(R.layout.schedule_card, null); //加载单个课程布局
                    v.setY(height * start); //设置开始高度,即第几节课开始
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT, (end - start) * height); //设置布局高度,即跨多少节课
                    v.setLayoutParams(params);
                    TextView text = (TextView) v.findViewById(R.id.schedule_text_view);
                    text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                    if (list.size() > 1) {
                        if(end-start>=30) {
                            text.setText(list.get(0).title+ "\n\n" + "+"); //显示日程名
                        }else  text.setText("+");
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                initPopWindowForSchedule(schedule.date,list);
                            }
                        });
                    } else {
                        if(end-start>=30) {
                            text.setText(list.get(0).title); //显示日程名
                        }else  text.setText("");
                        final int id=list.get(0).id;
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        });
                    }
                    layout.addView(v);
                }
            }
        }
    }

    public void dayRemoveView() {
        final View[] timeDayColumn = {findViewById(R.id.day_layout0),findViewById(R.id.day_layout1),findViewById(R.id.day_layout2),
                findViewById(R.id.day_layout3),findViewById(R.id.day_layout4)};
        final View[] allDayColumn = {findViewById(R.id.day_all_day0),findViewById(R.id.day_all_day1),findViewById(R.id.day_all_day2),
                findViewById(R.id.day_all_day3),findViewById(R.id.day_all_day4)};
        RelativeLayout temp;
        for(int i=0;i<5;i++){
            temp=(RelativeLayout)timeDayColumn[i];
            temp.removeAllViews();
            temp.setVisibility(View.VISIBLE);
        }
        for(int i=0;i<5;i++){
            temp=(RelativeLayout)allDayColumn[i];
            temp.removeAllViews();
            temp.setVisibility(View.VISIBLE);
        }
    }

    /*public List<dayScheduleOnView> getByColumn(int i){

    }*/

    class dayScheduleOnView {
        int column = -1;
        SimpleDate simpleDate;
        int startTime = -1;
        int endTime = -1;

        dayScheduleOnView(int column, SimpleDate simpleDate, int st, int et) {
            this.column = column;
            this.simpleDate = simpleDate;
            this.startTime = st;
            this.endTime = et;
        }
    };
    //日程悬浮框
    private void initPopWindowForSchedule(Timestamp date,List<SimpleDate>list) {
        View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_view, null, false);
        TextView textView=view.findViewById(R.id.popup_item_date);
        String dateStr=date.getYear()+1900+"年"+(date.getMonth()+1)+"月"+date.getDate()+"日 "+dayOfWeekArray[date.getDay()];
        textView.setText(dateStr);
        ListView listView= view.findViewById(R.id.popup_item_listview);
        listView.setAdapter(new PopupAdapterForSchedule(MainActivity.this,list));
        listView.setOnItemClickListener(new MyOnItemClickListenerForSchedule());
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);


        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(getDrawable(R.drawable.shape_all_radius_and_border));    //要为popWindow设置一个背景才有效

        View onPut=findViewById(R.id.week_schedule_view);//定位而非在那个位置
        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAtLocation(onPut, Gravity.CENTER_VERTICAL,25,0);

    }
    //重要日悬浮框
    private void initPopWindowForBigDay(Timestamp date,List<BigDay>list) {
        View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_view, null, false);
        TextView textView=view.findViewById(R.id.popup_item_date);
        String dateStr=date.getYear()+1900+"年"+(date.getMonth()+1)+"月"+date.getDate()+"日 "+dayOfWeekArray[date.getDay()];
        textView.setText(dateStr);
        ListView listView= view.findViewById(R.id.popup_item_listview);
        listView.setAdapter(new PopupAdapterForBigDay(MainActivity.this,list));
        listView.setOnItemClickListener(new MyOnItemClickListenerForBigDay());
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);


        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(getDrawable(R.drawable.shape_all_radius_and_border));    //要为popWindow设置一个背景才有效

        View onPut=findViewById(R.id.week_schedule_view);//定位而非在那个位置
        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAtLocation(onPut, Gravity.CENTER_VERTICAL,25,0);

    }

    public class MyOnItemClickListenerForSchedule implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            TextView popup_item_id = (TextView)view.findViewById(R.id.popup_item_id);

            Intent intent = new Intent(MainActivity.this, LookScheduleActivity.class);;
            intent.putExtra("id",Integer.parseInt(popup_item_id.getText().toString()));
            startActivity(intent);
        }
    }
    public class MyOnItemClickListenerForBigDay implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            TextView popup_item_id = (TextView)view.findViewById(R.id.popup_item_id);

            Intent intent = new Intent(MainActivity.this, LookBigDayActivity.class);;
            intent.putExtra("id",Integer.parseInt(popup_item_id.getText().toString()));
            startActivity(intent);
        }
    }
    public void updateCalendar(){
        MonthPageAdapter.monthView.invalidate();
        MonthPageAdapter.lastMonthView.invalidate();
        MonthPageAdapter.nextMonthView.invalidate();
        WeekPageAdapter.weekView.invalidate();
        WeekPageAdapter.lastWeekView.invalidate();
        WeekPageAdapter.nextWeekView.invalidate();
        DayPageAdapter.dayView.invalidate();
        DayPageAdapter.nextWeekDayView.invalidate();
        DayPageAdapter.lastWeekDayView.invalidate();
        createMonthScheduleView();
        createWeekScheduleView();
        createDayScheduleView();
        updateMonthSchedule();
        updateMonthBigDay();
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateCalendar();
    }

    public void updateMonthSchedule(){
        int year = DayManager.getSelectYear();
        int month = DayManager.getSelectMonth()+1;
        int day = DayManager.getSelectDay();
        Timestamp date = new Timestamp(year-1900,month-1,day,0,0,0,0);
        List<ScheduleDate> scheduleDate = new ArrayList<>();
        for(int i=0;i<FindSchedule.scheduleDateList.size();i++){
            if(date.getTime() == FindSchedule.scheduleDateList.get(i).date.getTime()){
                scheduleDate.add(FindSchedule.scheduleDateList.get(i));
                break;
            }
        }
        main_month_scheduleListView.setAdapter(new ScheduleAdapter1(MainActivity.this,scheduleDate));
    }

    public void updateMonthBigDay(){
        int year = DayManager.getSelectYear();
        int month = DayManager.getSelectMonth()+1;
        int day = DayManager.getSelectDay();
        Timestamp date = new Timestamp(year-1900,month-1,day,0,0,0,0);
        bigDays = new ArrayList<>();
        Log.e("now",date.getTime()+"");
        Log.e("allBigDayList.size",FindBigDay.allBigDayList.size()+"");
        for(int i=0;i<FindBigDay.allBigDayList.size();i++){
            Log.e("allBigDayList",FindBigDay.allBigDayList.get(i).date.getTime()+"");
        }
        for(int i=0;i<FindBigDay.allBigDayList.size();i++){
            Log.e("findbigday",FindBigDay.allBigDayList.get(i).date.getTime()+"");
            if(date.getTime() == FindBigDay.allBigDayList.get(i).date.getTime()){
                bigDays.add(FindBigDay.allBigDayList.get(i));
            }
        }
        Log.e("MainActivity",bigDays.size()+"");
        main_month_bigDayListView.setAdapter(new LVAdapter2(MainActivity.this,bigDays));
        main_month_bigDayListView.setOnItemClickListener(new MyOnItemClickListener2());
    }

    // main_month_bigDayListView点击事件
    class MyOnItemClickListener2 implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            BigDay bigDay = bigDays.get(position);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LookBigDayActivity.class);
            intent.putExtra("id",bigDay.id);
            startActivity(intent);
        }
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    | ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS) | ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//是否请求过该权限
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                                    Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.READ_PHONE_STATE,
                            }, 10001);
                } else {//没有则请求获取权限
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                                    Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.READ_PHONE_STATE,}, 10001);
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this,"悬浮窗权限开启！",Toast.LENGTH_SHORT).show();
                PrefUtils.setBoolean(MainActivity.this, "isAllowAlert", true);
            }else {
                PrefUtils.setBoolean(MainActivity.this, "isAllowAlert", false);
            }
            askNotifyPermission();
        }else if (requestCode == 2) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this,"通知权限开启！",Toast.LENGTH_SHORT).show();
                PrefUtils.setBoolean(MainActivity.this, "isAllowNotify", true);
            }else {
                PrefUtils.setBoolean(MainActivity.this, "isAllowNotify", false);
            }
        }
    }
}
