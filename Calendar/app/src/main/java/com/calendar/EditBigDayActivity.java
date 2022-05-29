package com.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Toast;

import com.calendar.bean.BigDay;
import com.calendar.db.DBAdapter;
import com.calendar.dialog.NumberPickerDialog;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditBigDayActivity extends Activity {

    DBAdapter db;

    private Button cancel_edit_bigDay;
    private Button finish_edit_bigDay;

    private TextView edit_bigDay_title;
    private Button edit_bigDay_date;
    private Switch edit_bigDay_repeat;
    private LinearLayout edit_bigDay_repeat_LL;
    private Button edit_bigDay_repeatInterval;
    private Button edit_bigDay_repeatCycle;
    private Button edit_bigDay_remindTime;
    private TextView edit_bigDay_supplement;

    private Calendar time;// 日期
    private int bigDay_repeatInterval_num;// 重复数字
    private int bigDay_repeatCycle_num;// 重复文字
    private int bigDay_remindTime_num;// 提醒

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_big_day);

        db = DBAdapter.setDBAdapter(EditBigDayActivity.this);
        db.open();

        initView();
    }

    private void initView(){
        cancel_edit_bigDay = (Button)findViewById(R.id.cancel_edit_bigDay);
        finish_edit_bigDay = (Button)findViewById(R.id.finish_edit_bigDay);

        edit_bigDay_title = (TextView)findViewById(R.id.edit_bigDay_title);
        edit_bigDay_date = (Button)findViewById(R.id.edit_bigDay_date);
        edit_bigDay_repeat = (Switch)findViewById(R.id.edit_bigDay_repeat);
        edit_bigDay_repeat_LL = (LinearLayout)findViewById(R.id.edit_bigDay_repeat_LL);
        edit_bigDay_repeatInterval = (Button)findViewById(R.id.edit_bigDay_repeatInterval);
        edit_bigDay_repeatCycle = (Button)findViewById(R.id.edit_bigDay_repeatCycle);
        edit_bigDay_remindTime = (Button)findViewById(R.id.edit_bigDay_remindTime);
        edit_bigDay_supplement = (TextView)findViewById(R.id.edit_bigDay_supplement);

        cancel_edit_bigDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditBigDayActivity.this.finish();
            }
        });

        finish_edit_bigDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edit_bigDay_title.getText().toString();
                Timestamp date = new Timestamp(time.getTimeInMillis());
                Timestamp remindTime = null;
                int type = 0;
                String supplement = edit_bigDay_supplement.getText().toString();

                if(title == null || title.equals("")){
                    Toast.makeText(EditBigDayActivity.this,"标题不可为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!edit_bigDay_repeat.isChecked()){ // 不重复
                    bigDay_repeatCycle_num = 0;
                }

                Log.e("remindTime_num",bigDay_remindTime_num+"");
                if(bigDay_remindTime_num == 0){
                    remindTime = new Timestamp(0);
                }else if(bigDay_remindTime_num == 1){
                    remindTime = new Timestamp(date.getTime()-10*60*1000);
                }else if(bigDay_remindTime_num == 2){
                    remindTime = new Timestamp(date.getTime()-1*60*60*1000);
                }else if(bigDay_remindTime_num == 3){
                    remindTime = new Timestamp(date.getTime()-1*24*60*60*1000);
                }
                Log.e("remindTime",remindTime.getTime()+"");

                // 倒数
                if(date.getTime() - new Timestamp(System.currentTimeMillis()).getTime() >= 0){ // 未来的时间 （不重复：时间到变正数）
                    type = 1;
                }else if(bigDay_repeatCycle_num != 0){// 过去的时间+重复
                    type = 1;
                }

                Log.e("repeatCycle2",bigDay_repeatCycle_num+"");
                BigDay bigDay = new BigDay(title,date,bigDay_repeatInterval_num,bigDay_repeatCycle_num,remindTime,type,supplement);
                db.updateOneDataFromBigDay(id,bigDay);
                Log.e("db.editBigDay","执行一次");

                EditBigDayActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        BigDay bigDay = db.getOneDataFromBigDay(id).get(0);

        edit_bigDay_title.setText(bigDay.title);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        time = Calendar.getInstance();
        time.setTimeInMillis(bigDay.date.getTime());
        String dateStr = sdf.format(time.getTime());
        edit_bigDay_date.setText(dateStr);

        edit_bigDay_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcalendar = Calendar.getInstance();
                new DatePickerDialog(EditBigDayActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        time.set(year,month,dayOfMonth,0,0,0);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String dateStr = sdf.format(time.getTime());
                        edit_bigDay_date.setText(dateStr);
                    }
                },mcalendar.get(Calendar.YEAR),mcalendar.get(Calendar.MONTH),mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if(bigDay.date.getTime() == bigDay.remindTime.getTime()){
            bigDay_remindTime_num = 0;
            edit_bigDay_remindTime.setText("无");
        }else if(bigDay.date.getTime()-bigDay.remindTime.getTime() == 10*60*1000){
            bigDay_remindTime_num = 1;
            edit_bigDay_remindTime.setText("提前10分钟提醒");
        }else if(bigDay.date.getTime()-bigDay.remindTime.getTime() == 1*60*60*1000){
            bigDay_remindTime_num = 2;
            edit_bigDay_remindTime.setText("提前1小时提醒");
        }else if(bigDay.date.getTime()-bigDay.remindTime.getTime() == 1*24*60*60*1000){
            bigDay_remindTime_num = 3;
            edit_bigDay_remindTime.setText("提前1天提醒");
        }else{
            Log.e("EditBigDayActivity","提醒时间有误");
        }
        edit_bigDay_remindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(EditBigDayActivity.this, view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.ring, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        bigDay_remindTime_num = item.getOrder();
                        edit_bigDay_remindTime.setText(item.getTitle());
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

        Log.e("repeatCycle1",bigDay.repeatCycle+"");
        if(bigDay.repeatCycle == 0){
            edit_bigDay_repeat.setChecked(false);
            edit_bigDay_repeat_LL.setVisibility(View.GONE);
        }else{
            edit_bigDay_repeat.setChecked(true);
            edit_bigDay_repeat_LL.setVisibility(View.VISIBLE);
        }
        // 监听 是否重复按钮的改变
        edit_bigDay_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    edit_bigDay_repeat_LL.setVisibility(View.VISIBLE);
                    bigDay_repeatInterval_num = 1;
                    edit_bigDay_repeatInterval.setText(bigDay_repeatInterval_num+"");
                    bigDay_repeatCycle_num = 4;
                    edit_bigDay_repeatCycle.setText("年");
                }else{
                    edit_bigDay_repeat_LL.setVisibility(View.GONE);
                }
            }
        });

        bigDay_repeatInterval_num = bigDay.repeatInterval;
        edit_bigDay_repeatInterval.setText(bigDay_repeatInterval_num+"");
        edit_bigDay_repeatInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NumberPickerDialog(EditBigDayActivity.this, new NumberPickerDialog.OnNumberSelectedListener() {
                    @Override
                    public void onNumberSelected(NumberPicker view, int number) {
                        bigDay_repeatInterval_num = number;
                        edit_bigDay_repeatInterval.setText(number+"");
                    }
                }, bigDay_repeatInterval_num).show();
            }
        });

        bigDay_repeatCycle_num = bigDay.repeatCycle;
        if(bigDay_repeatCycle_num == 1){
            edit_bigDay_repeatCycle.setText("天");
        }else if(bigDay_repeatCycle_num == 2){
            edit_bigDay_repeatCycle.setText("周");
        }else if(bigDay_repeatCycle_num == 3){
            edit_bigDay_repeatCycle.setText("月");
        }else if(bigDay_repeatCycle_num == 4){
            edit_bigDay_repeatCycle.setText("年");
        }
        edit_bigDay_repeatCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(EditBigDayActivity.this, view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.repeat_cycle, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        bigDay_repeatCycle_num = item.getOrder();
                        edit_bigDay_repeatCycle.setText(item.getTitle());
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

        edit_bigDay_supplement.setText(bigDay.supplement);
    }
}
