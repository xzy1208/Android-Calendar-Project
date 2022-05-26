package com.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddScheduleActivity extends Activity {

    private Button schedule_start_time;
    private Button schedule_end_time;
    private Switch schedule_allday;
    private Button schedule_ring_time;
    private Switch schedule_repeat;
    private LinearLayout schedule_repeat_LL;
    private Button schedule_repeatInterval;
    private Button schedule_repeatCycle;

    private int startYear,startMonth,startDay,startHour,startMinute;
    private Calendar startTime;// 开始时间
    private int endYear,endMonth,endDay,endHour,endMinute;
    private Calendar endTime;// 结束时间
    private AlertDialog schedule_ring_dialog;
    private int schedule_ring_time_type; // 记录选择的提醒时间类型
    private int schedule_repeatInterval_num;// 重复数字
    private int schedule_repeatCycle_num;// 重复文字 1天 2周 3月 4年

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        initView();
    }

    private void initView(){
        schedule_start_time = (Button)findViewById(R.id.schedule_start_time);
        schedule_end_time = (Button)findViewById(R.id.schedule_end_time);
        schedule_allday = (Switch)findViewById(R.id.schedule_allday);
        schedule_ring_time = (Button)findViewById(R.id.schedule_ring_time);
        schedule_repeat = (Switch)findViewById(R.id.schedule_repeat);
        schedule_repeat_LL = (LinearLayout)findViewById(R.id.schedule_repeat_LL);
        schedule_repeatInterval = (Button)findViewById(R.id.schedule_repeatInterval);
        schedule_repeatCycle = (Button)findViewById(R.id.schedule_repeatCycle);

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

        schedule_allday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SimpleDateFormat sdf;
                if (isChecked)
                {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                }else{
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                }
                String dateStr1 = sdf.format(startTime.getTime());
                schedule_start_time.setText(dateStr1);
                String dateStr2 = sdf.format(endTime.getTime());
                schedule_end_time.setText(dateStr2);
            }
        });

        schedule_ring_time_type = 0;
        schedule_ring_time.setOnClickListener(new View.OnClickListener() {
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
                        schedule_ring_time_type = item.getItemId();
                        schedule_ring_time.setText(item.getTitle());
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
                        schedule_repeatInterval.setText(schedule_repeatInterval_num+"");
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
                        schedule_repeatCycle_num = item.getItemId()+1;
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
    }
}
