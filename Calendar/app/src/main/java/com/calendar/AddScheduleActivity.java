package com.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddScheduleActivity extends Activity {

    private Button schedule_start_time;
    private Button schedule_end_time;
    private Switch schedule_allday;

    private int startYear,startMonth,startDay,startHour,startMinute;
    private Calendar startTime;
    private int endYear,endMonth,endDay,endHour,endMinute;
    private Calendar endTime;

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
    }
}
