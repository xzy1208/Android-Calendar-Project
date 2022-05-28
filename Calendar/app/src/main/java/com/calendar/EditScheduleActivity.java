package com.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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

public class EditScheduleActivity extends Activity {

    public DBAdapter db;

    private Button cancel_edit_schedule;
    private Button finish_edit_schedule;

    private TextView edit_schedule_title;
    private TextView edit_schedule_place;
    private Switch edit_schedule_allday;
    private Button edit_schedule_start_time;
    private Button edit_schedule_end_time;
    private Switch edit_schedule_repeat;
    private LinearLayout edit_schedule_repeat_LL;
    private Button edit_schedule_repeatInterval;
    private Button edit_schedule_repeatCycle;
    private Button edit_schedule_remindTime;
    private Switch edit_schedule_isImportant;
    private TextView edit_schedule_supplement;

    private int isAllDay;// 全天
    private int startYear,startMonth,startDay,startHour,startMinute;
    private Calendar startTime;// 开始时间
    private int endYear,endMonth,endDay,endHour,endMinute;
    private Calendar endTime;// 结束时间
    private int schedule_repeatInterval_num;// 重复数字
    private int schedule_repeatCycle_num;// 重复文字
    private int schedule_remindTime_num;// 提醒

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        db = DBAdapter.setDBAdapter(EditScheduleActivity.this);
        db.open();

        initView();
    }

    public void initView(){
        cancel_edit_schedule = (Button)findViewById(R.id.cancel_edit_schedule);
        finish_edit_schedule = (Button)findViewById(R.id.finish_edit_schedule);

        edit_schedule_title = (TextView)findViewById(R.id.edit_schedule_title);
        edit_schedule_place = (TextView)findViewById(R.id.edit_schedule_place);
        edit_schedule_allday = (Switch)findViewById(R.id.edit_schedule_allday);
        edit_schedule_start_time = (Button)findViewById(R.id.edit_schedule_start_time);
        edit_schedule_end_time = (Button)findViewById(R.id.edit_schedule_end_time);
        edit_schedule_repeat = (Switch)findViewById(R.id.edit_schedule_repeat);
        edit_schedule_repeat_LL = (LinearLayout)findViewById(R.id.edit_schedule_repeat_LL);
        edit_schedule_repeatInterval = (Button)findViewById(R.id.edit_schedule_repeatInterval);
        edit_schedule_repeatCycle = (Button)findViewById(R.id.edit_schedule_repeatCycle);
        edit_schedule_isImportant = (Switch)findViewById(R.id.edit_schedule_isImportant);
        edit_schedule_remindTime = (Button)findViewById(R.id.edit_schedule_remindTime);
        edit_schedule_supplement = (TextView)findViewById(R.id.edit_schedule_supplement);

        cancel_edit_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditScheduleActivity.this.finish();
            }
        });

        finish_edit_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edit_schedule_title.getText().toString();
                String place = edit_schedule_place.getText().toString();
                Timestamp date1 = new Timestamp(startTime.getTimeInMillis());
                Timestamp date2 = new Timestamp(endTime.getTimeInMillis());
                Timestamp remindTime = null;
                int important = 0;
                String supplement = edit_schedule_supplement.getText().toString();

                if(title == null || title.equals("")){
                    Toast.makeText(EditScheduleActivity.this,"标题不可为空",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditScheduleActivity.this,"开始时间不可晚于结束时间",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!edit_schedule_repeat.isChecked()){ // 不重复
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

                if(edit_schedule_isImportant.isChecked()){
                    important = 1;
                }

                Schedule schedule = new Schedule(title, place, isAllDay, date1, date2, schedule_repeatInterval_num, schedule_repeatCycle_num, remindTime, important, supplement);
                db.updateOneDataFromSchedule(id,schedule);
                Log.e("db.editSchedule","执行一次");

                // 更新FindSchedule数据
                FindSchedule fs = new FindSchedule(db.getAllDataFromSchedule());

                EditScheduleActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        Schedule schedule = db.getOneDataFromSchedule(id).get(0);

        edit_schedule_title.setText(schedule.title);
        edit_schedule_place.setText(schedule.place);

        SimpleDateFormat sdf;
        if(schedule.isAllDay == 1){
            isAllDay = 1;
            edit_schedule_allday.setChecked(true);
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }else{
            isAllDay = 0;
            edit_schedule_allday.setChecked(false);
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }

        startTime = Calendar.getInstance();
        startTime.setTimeInMillis(schedule.startTime.getTime());
        String dateStr1 = sdf.format(startTime.getTime());
        edit_schedule_start_time.setText(dateStr1);

        endTime = Calendar.getInstance();
        endTime.setTimeInMillis(schedule.endTime.getTime());
        String dateStr2 = sdf.format(endTime.getTime());
        edit_schedule_end_time.setText(dateStr2);

        edit_schedule_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcalendar = Calendar.getInstance();
                new DatePickerDialog(EditScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        startYear = year;
                        startMonth = month;
                        startDay = dayOfMonth;

                        if(edit_schedule_allday.isChecked()){
                            startTime.set(year,month,dayOfMonth);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(startTime.getTime());
                            edit_schedule_start_time.setText(dateStr);
                        }else{
                            Calendar mcalendar = Calendar.getInstance();
                            new TimePickerDialog(EditScheduleActivity.this, 0,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            startHour = hourOfDay;
                                            startMinute = minute;
                                            startTime.set(startYear,startMonth,startDay,startHour,startMinute);

                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                            String dateStr = sdf.format(startTime.getTime());
                                            edit_schedule_start_time.setText(dateStr);
                                        }
                                    }, mcalendar.get(Calendar.HOUR_OF_DAY), mcalendar.get(Calendar.MINUTE), false).show();
                        }
                    }
                },mcalendar.get(Calendar.YEAR),mcalendar.get(Calendar.MONTH),mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edit_schedule_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcalendar = Calendar.getInstance();
                new DatePickerDialog(EditScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        endYear = year;
                        endMonth = month;
                        endDay = dayOfMonth;

                        if(edit_schedule_allday.isChecked()){
                            endTime.set(year,month,dayOfMonth);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(endTime.getTime());
                            edit_schedule_end_time.setText(dateStr);
                        }else{
                            Calendar mcalendar = Calendar.getInstance();
                            new TimePickerDialog(EditScheduleActivity.this, 0,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            endHour = hourOfDay;
                                            endMinute = minute;
                                            endTime.set(endYear,endMonth,endDay,endHour,endMinute);

                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                            String dateStr = sdf.format(endTime.getTime());
                                            edit_schedule_end_time.setText(dateStr);
                                        }
                                    }, mcalendar.get(Calendar.HOUR_OF_DAY), mcalendar.get(Calendar.MINUTE), false).show();
                        }
                    }
                },mcalendar.get(Calendar.YEAR),mcalendar.get(Calendar.MONTH),mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edit_schedule_allday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                edit_schedule_start_time.setText(dateStr1);
                String dateStr2 = sdf.format(endTime.getTime());
                edit_schedule_end_time.setText(dateStr2);
            }
        });

        if(schedule.startTime.getTime() == schedule.remindTime.getTime()){
            schedule_remindTime_num = 0;
            edit_schedule_remindTime.setText("无");
        }else if(schedule.startTime.getTime()-schedule.remindTime.getTime() == 10*60*1000){
            schedule_remindTime_num = 1;
            edit_schedule_remindTime.setText("提前10分钟提醒");
        }else if(schedule.startTime.getTime()-schedule.remindTime.getTime() == 1*60*60*1000){
            schedule_remindTime_num = 2;
            edit_schedule_remindTime.setText("提前1小时提醒");
        }else if(schedule.startTime.getTime()-schedule.remindTime.getTime() == 1*24*60*60*1000){
            schedule_remindTime_num = 3;
            edit_schedule_remindTime.setText("提前1天提醒");
        }else{
            Log.e("EditscheduleActivity","提醒时间有误");
        }
        edit_schedule_remindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(EditScheduleActivity.this, view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.ring, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        schedule_remindTime_num = item.getOrder();
                        edit_schedule_remindTime.setText(item.getTitle());
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

        if(schedule.repeatCycle == 0){
            edit_schedule_repeat.setChecked(false);
            edit_schedule_repeat_LL.setVisibility(View.GONE);
        }else{
            edit_schedule_repeat.setChecked(true);
            edit_schedule_repeat_LL.setVisibility(View.VISIBLE);
        }
        // 监听 是否重复按钮的改变
        edit_schedule_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    edit_schedule_repeat_LL.setVisibility(View.VISIBLE);
                    schedule_repeatInterval_num = 1;
                    edit_schedule_repeatInterval.setText(schedule_repeatInterval_num+"");
                    schedule_repeatCycle_num = 4;
                    edit_schedule_repeatCycle.setText("年");

                }else{
                    edit_schedule_repeat_LL.setVisibility(View.GONE);
                }
            }
        });

        schedule_repeatInterval_num = schedule.repeatInterval;
        edit_schedule_repeatInterval.setText(schedule_repeatInterval_num+"");
        edit_schedule_repeatInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NumberPickerDialog(EditScheduleActivity.this, new NumberPickerDialog.OnNumberSelectedListener() {
                    @Override
                    public void onNumberSelected(NumberPicker view, int number) {
                        schedule_repeatInterval_num = number;
                        edit_schedule_repeatInterval.setText(number+"");
                    }
                }, schedule_repeatInterval_num).show();
            }
        });

        schedule_repeatCycle_num = schedule.repeatCycle;
        if(schedule_repeatCycle_num == 1){
            edit_schedule_repeatCycle.setText("天");
        }else if(schedule_repeatCycle_num == 2){
            edit_schedule_repeatCycle.setText("周");
        }else if(schedule_repeatCycle_num == 3){
            edit_schedule_repeatCycle.setText("月");
        }else if(schedule_repeatCycle_num == 4){
            edit_schedule_repeatCycle.setText("年");
        }
        edit_schedule_repeatCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(EditScheduleActivity.this, view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.repeat_cycle, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        schedule_repeatCycle_num = item.getOrder();
                        edit_schedule_repeatCycle.setText(item.getTitle());
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

        if(schedule.isImportant == 1){
            edit_schedule_isImportant.setChecked(true);
        }else{
            edit_schedule_isImportant.setChecked(false);
        }

        edit_schedule_supplement.setText(schedule.supplement);
    }
}
