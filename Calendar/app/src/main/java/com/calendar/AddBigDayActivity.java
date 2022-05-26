package com.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddBigDayActivity extends Activity {

    private Button bigDay_time;
    private Button bigDay_ring_time;
    private Switch bigDay_repeat;
    private LinearLayout bigDay_repeat_LL;
    private Button bigDay_repeatInterval;
    private Button bigDay_repeatCycle;

    private Calendar time;// 日期
    private int bigDay_ring_time_type; // 记录选择的提醒时间类型 0不提醒 1分钟 2小时 3天
    private int bigDay_repeatInterval_num;// 重复数字
    private int bigDay_repeatCycle_num;// 重复文字 1天 2周 3月 4年

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bigday);

        initView();
    }

    private void initView(){
        bigDay_time = (Button)findViewById(R.id.bigDay_time);
        bigDay_ring_time = (Button)findViewById(R.id.bigDay_ring_time);
        bigDay_repeat = (Switch)findViewById(R.id.bigDay_repeat);
        bigDay_repeat_LL = (LinearLayout)findViewById(R.id.bigDay_repeat_LL);
        bigDay_repeatInterval = (Button)findViewById(R.id.bigDay_repeatInterval);
        bigDay_repeatCycle = (Button)findViewById(R.id.bigDay_repeatCycle);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        time = Calendar.getInstance();
        String dateStr = sdf.format(time.getTime());
        bigDay_time.setText(dateStr);

        bigDay_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcalendar = Calendar.getInstance();
                new DatePickerDialog(AddBigDayActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        time.set(year,month,dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String dateStr = sdf.format(time.getTime());
                        bigDay_time.setText(dateStr);
                    }
                },mcalendar.get(Calendar.YEAR),mcalendar.get(Calendar.MONTH),mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        bigDay_ring_time_type = 0;
        bigDay_ring_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(AddBigDayActivity.this, view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.ring, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        bigDay_ring_time_type = item.getItemId();
                        bigDay_ring_time.setText(item.getTitle());
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
        bigDay_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    bigDay_repeat_LL.setVisibility(View.VISIBLE);

                }else{
                    bigDay_repeat_LL.setVisibility(View.GONE);
                }
            }
        });

        bigDay_repeatInterval_num = 1;
        bigDay_repeatInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NumberPickerDialog(AddBigDayActivity.this, new NumberPickerDialog.OnNumberSelectedListener() {
                    @Override
                    public void onNumberSelected(NumberPicker view, int number) {
                        bigDay_repeatInterval_num = number;
                        bigDay_repeatInterval.setText(bigDay_repeatInterval_num+"");
                    }
                }, bigDay_repeatInterval_num).show();
            }
        });

        bigDay_repeatCycle_num = 4;
        bigDay_repeatCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(AddBigDayActivity.this, view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.repeat_cycle, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        bigDay_repeatCycle_num = item.getItemId()+1;
                        bigDay_repeatCycle.setText(item.getTitle());
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
