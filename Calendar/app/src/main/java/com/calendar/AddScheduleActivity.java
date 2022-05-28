package com.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.calendar.bean.FindSchedule;
import com.calendar.bean.Schedule;
import com.calendar.db.DBAdapter;
import com.calendar.dialog.NumberPickerDialog;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddScheduleActivity extends Activity {

    DBAdapter db;

    private TextView schedule_title;
    private TextView schedule_place;
    private Switch schedule_allday;
    private Button schedule_start_time;
    private Button schedule_end_time;
    private Switch schedule_repeat;
    private LinearLayout schedule_repeat_LL;
    private Button schedule_repeatInterval;
    private Button schedule_repeatCycle;
    private Button schedule_remindTime;
    private Switch schedule_isImportant;
    private TextView schedule_supplement;

    private int isAllDay;// 全天
    private int startYear,startMonth,startDay,startHour,startMinute;
    private Calendar startTime;// 开始时间
    private int endYear,endMonth,endDay,endHour,endMinute;
    private Calendar endTime;// 结束时间
    private int schedule_repeatInterval_num;// 重复数字
    private int schedule_repeatCycle_num;// 重复文字
    private int schedule_remindTime_num;// 提醒

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        db = DBAdapter.setDBAdapter(AddScheduleActivity.this);
        db.open();

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void initView(){
        schedule_title = (TextView)findViewById(R.id.schedule_title);
        schedule_place = (TextView)findViewById(R.id.schedule_place);
        schedule_allday = (Switch)findViewById(R.id.schedule_allday);
        schedule_start_time = (Button)findViewById(R.id.schedule_start_time);
        schedule_end_time = (Button)findViewById(R.id.schedule_end_time);
        schedule_repeat = (Switch)findViewById(R.id.schedule_repeat);
        schedule_repeat_LL = (LinearLayout)findViewById(R.id.schedule_repeat_LL);
        schedule_repeatInterval = (Button)findViewById(R.id.schedule_repeatInterval);
        schedule_repeatCycle = (Button)findViewById(R.id.schedule_repeatCycle);
        schedule_isImportant = (Switch)findViewById(R.id.schedule_isImportant);
        schedule_remindTime = (Button)findViewById(R.id.schedule_remindTime);
        schedule_supplement = (TextView)findViewById(R.id.schedule_supplement);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        startTime = Calendar.getInstance();
        String dateStr1 = sdf.format(startTime.getTime());
        schedule_start_time.setText(dateStr1);

        endTime = Calendar.getInstance();
        String dateStr2 = sdf.format(endTime.getTime());
        schedule_end_time.setText(dateStr2);

        schedule_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcalendar = Calendar.getInstance();
                new DatePickerDialog(AddScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        startYear = year;
                        startMonth = month;
                        startDay = dayOfMonth;

                        if(schedule_allday.isChecked()){
                            startTime.set(year,month,dayOfMonth);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(startTime.getTime());
                            schedule_start_time.setText(dateStr);
                        }else{
                            Calendar mcalendar = Calendar.getInstance();
                            new TimePickerDialog(AddScheduleActivity.this, 0,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            startHour = hourOfDay;
                                            startMinute = minute;
                                            startTime.set(startYear,startMonth,startDay,startHour,startMinute);

                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                            String dateStr = sdf.format(startTime.getTime());
                                            schedule_start_time.setText(dateStr);
                                        }
                                    }, mcalendar.get(Calendar.HOUR_OF_DAY), mcalendar.get(Calendar.MINUTE), false).show();
                        }
                    }
                },mcalendar.get(Calendar.YEAR),mcalendar.get(Calendar.MONTH),mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        schedule_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcalendar = Calendar.getInstance();
                new DatePickerDialog(AddScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        endYear = year;
                        endMonth = month;
                        endDay = dayOfMonth;

                        if(schedule_allday.isChecked()){
                            endTime.set(year,month,dayOfMonth);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(endTime.getTime());
                            schedule_end_time.setText(dateStr);
                        }else{
                            Calendar mcalendar = Calendar.getInstance();
                            new TimePickerDialog(AddScheduleActivity.this, 0,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            endHour = hourOfDay;
                                            endMinute = minute;
                                            endTime.set(endYear,endMonth,endDay,endHour,endMinute);

                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                            String dateStr = sdf.format(endTime.getTime());
                                            schedule_end_time.setText(dateStr);
                                        }
                                    }, mcalendar.get(Calendar.HOUR_OF_DAY), mcalendar.get(Calendar.MINUTE), false).show();
                        }
                    }
                },mcalendar.get(Calendar.YEAR),mcalendar.get(Calendar.MONTH),mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        isAllDay = 1;
        schedule_allday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SimpleDateFormat sdf;
                if (isChecked)
                {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    isAllDay = 1;
                }else{
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    isAllDay = 0;
                }
                String dateStr1 = sdf.format(startTime.getTime());
                schedule_start_time.setText(dateStr1);
                String dateStr2 = sdf.format(endTime.getTime());
                schedule_end_time.setText(dateStr2);
            }
        });

        schedule_remindTime_num = 0;
        schedule_remindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(AddScheduleActivity.this, view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.ring, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        schedule_remindTime_num = item.getOrder();
                        schedule_remindTime.setText(item.getTitle());
                        return false;
                    }
                });
                // PopupMenu关闭事件
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                    }
                });
                popupMenu.show();
            }
        });

        // 监听 是否重复按钮的改变
        schedule_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    schedule_repeat_LL.setVisibility(View.VISIBLE);
                }else{
                    schedule_repeat_LL.setVisibility(View.GONE);
                }
            }
        });

        schedule_repeatInterval_num = 1;
        schedule_repeatInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NumberPickerDialog(AddScheduleActivity.this, new NumberPickerDialog.OnNumberSelectedListener() {
                    @Override
                    public void onNumberSelected(NumberPicker view, int number) {
                        schedule_repeatInterval_num = number;
                        schedule_repeatInterval.setText(number+"");
                    }
                }, schedule_repeatInterval_num).show();
            }
        });

        schedule_repeatCycle_num = 4;
        schedule_repeatCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(AddScheduleActivity.this, view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.repeat_cycle, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        schedule_repeatCycle_num = item.getOrder();
                        schedule_repeatCycle.setText(item.getTitle());
                        return false;
                    }
                });
                // PopupMenu关闭事件
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                    }
                });
                popupMenu.show();
            }
        });

        //注册广播
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("addSchedule")) {
                    Log.e("收到一条广播","addSchedule");
                    String title = schedule_title.getText().toString();
                    String place = schedule_place.getText().toString();
                    Timestamp date1 = new Timestamp(startTime.getTimeInMillis());
                    Timestamp date2 = new Timestamp(endTime.getTimeInMillis());
                    Timestamp remindTime = null;
                    int important = 0;
                    String supplement = schedule_supplement.getText().toString();

                    if(title == null || title.equals("")){
                        Toast.makeText(AddScheduleActivity.this,"标题不可为空",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(isAllDay == 1){
                        date1.setHours(0);
                        date1.setMinutes(0);
                        date1.setSeconds(0);
                        date1.setNanos(0);
                        date2.setHours(0);
                        date2.setMinutes(0);
                        date2.setSeconds(0);
                        date2.setNanos(0);
                    }

                    if(date1.getTime()>date2.getTime()){
                        Toast.makeText(AddScheduleActivity.this,"开始时间不可晚于结束时间",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!schedule_repeat.isChecked()){ // 不重复
                        schedule_repeatCycle_num = 0;
                    }

                    if(schedule_remindTime_num == 0){
                        remindTime = new Timestamp(0);
                    }else if(schedule_remindTime_num == 1){
                        remindTime = new Timestamp(date1.getTime()-10*60*1000);
                    }else if(schedule_remindTime_num == 2){
                        remindTime = new Timestamp(date1.getTime()-1*60*60*1000);
                    }else if(schedule_remindTime_num == 3){
                        remindTime = new Timestamp(date1.getTime()-1*24*60*60*1000);
                    }

                    if(schedule_isImportant.isChecked()){
                        important = 1;
                    }

                    Schedule schedule = new Schedule(title, place, isAllDay, date1, date2, schedule_repeatInterval_num, schedule_repeatCycle_num, remindTime, important, supplement);
                    db.insertSchedule(schedule);
                    Log.e("db.insertSchedule","执行一次");

                    // 更新FindSchedule数据
                    FindSchedule fs = new FindSchedule(db.getAllDataFromSchedule());

                    AddScheduleActivity.this.finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("addSchedule");
        AddScheduleActivity.this.registerReceiver(mReceiver, filter);
    }
}
