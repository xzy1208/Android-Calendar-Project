package com.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Toast;

import com.calendar.bean.BigDay;
import com.calendar.bean.FindBigDay;
import com.calendar.db.DBAdapter;
import com.calendar.dialog.NumberPickerDialog;
import com.calendar.view.DayManager;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddBigDayActivity extends Activity {

    DBAdapter db;

    private TextView bigDay_title;
    private Button bigDay_date;
    private Switch bigDay_repeat;
    private LinearLayout bigDay_repeat_LL;
    private Button bigDay_repeatInterval;
    private Button bigDay_repeatCycle;
    private Button bigDay_remindTime;
    private TextView bigDay_supplement;

    private Calendar time;// 日期
    private int bigDay_repeatInterval_num;// 重复数字
    private int bigDay_repeatCycle_num;// 重复文字
    private int bigDay_remindTime_num;// 提醒

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bigday);

        db = DBAdapter.setDBAdapter(AddBigDayActivity.this);
        db.open();

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void initView(){
        bigDay_title = (TextView)findViewById(R.id.bigDay_title);
        bigDay_date = (Button)findViewById(R.id.bigDay_date);
        bigDay_repeat = (Switch)findViewById(R.id.bigDay_repeat);
        bigDay_repeat_LL = (LinearLayout)findViewById(R.id.bigDay_repeat_LL);
        bigDay_repeatInterval = (Button)findViewById(R.id.bigDay_repeatInterval);
        bigDay_repeatCycle = (Button)findViewById(R.id.bigDay_repeatCycle);
        bigDay_remindTime = (Button)findViewById(R.id.bigDay_remindTime);
        bigDay_supplement = (TextView)findViewById(R.id.bigDay_supplement);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if(MainActivity.page>=0&&MainActivity.page<=2){
            time= DayManager.getSelectCalendar();
        }else {
            time = Calendar.getInstance();
        }
        String dateStr = sdf.format(time.getTime());
        bigDay_date.setText(dateStr);

        bigDay_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text[]=String.valueOf(bigDay_date.getText()).split(" ");
                String date[]=text[0].split("-");
                //Calendar mcalendar = Calendar.getInstance();
                new DatePickerDialog(AddBigDayActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        time.set(year,month,dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String dateStr = sdf.format(time.getTime());
                        bigDay_date.setText(dateStr);
                    }
                },Integer.parseInt(date[0]),Integer.parseInt(date[1])-1,Integer.parseInt(date[2])).show();
            }
        });

        bigDay_remindTime_num = 0;
        bigDay_remindTime.setOnClickListener(new View.OnClickListener() {
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
                        bigDay_remindTime_num = item.getOrder();
                        bigDay_remindTime.setText(item.getTitle());
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
                        bigDay_repeatInterval.setText(number+"");
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
                        bigDay_repeatCycle_num = item.getOrder();
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

        //注册广播
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("addBigDay")) {
                    Log.e("收到一条广播","addBigDay");
                    String title = bigDay_title.getText().toString();
                    Timestamp date = new Timestamp(time.getTimeInMillis());
                    date.setHours(0);
                    date.setMinutes(0);
                    date.setSeconds(0);
                    date.setNanos(0);
                    Timestamp remindTime = null;
                    int type = 0;
                    String supplement = bigDay_supplement.getText().toString();

                    if(title == null || title.equals("")){
                        Toast.makeText(AddBigDayActivity.this,"标题不可为空",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!bigDay_repeat.isChecked()){ // 不重复
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
                    Timestamp t = new Timestamp(System.currentTimeMillis());
                    t.setHours(0);
                    t.setMinutes(0);
                    t.setSeconds(0);
                    t.setNanos(0);
                    if(date.getTime() - t.getTime() >= 0){ // 未来的时间 （不重复：时间到变正数）
                        type = 1;
                    }else if(bigDay_repeatCycle_num != 0){// 过去的时间+重复
                        type = 1;
                    }

                    BigDay bigDay = new BigDay(title,date,bigDay_repeatInterval_num,bigDay_repeatCycle_num,remindTime,type,supplement);
                    db.insertBigDay(bigDay);
                    Log.e("db.insertBigDay","执行一次");

                    // 更新FindBigDay数据
                    FindBigDay fb = new FindBigDay(db.getAllDataFromBigDay());

                    AddBigDayActivity.this.finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("addBigDay");
        AddBigDayActivity.this.registerReceiver(mReceiver, filter);
    }
}
