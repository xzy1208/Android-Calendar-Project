package com.calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.calendar.bean.BigDay;
import com.calendar.db.DBAdapter;
import com.calendar.dialog.DelDialog;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LookBigDayActivity extends Activity {

    public DBAdapter db;

    Button cancel_look_date;
    TextView look_bigDay_title;
    TextView look_bigDay_num;
    TextView look_bigDay_date;
    LinearLayout look_bigDay_supplement_LL;
    TextView look_bigDay_supplement;
    TextView look_bigDay_repeatTime;
    TextView look_bigDay_remindTime;
    Button edit_bigDay_date;
    Button del_bigDay_date;

    public int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_big_day);

        db = DBAdapter.setDBAdapter(LookBigDayActivity.this);
        db.open();

        Intent intent = getIntent();
        id = intent.getIntExtra("id",-1);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        display();
    }

    private void initView(){
        cancel_look_date = (Button)findViewById(R.id.cancel_look_date);
        look_bigDay_title = (TextView)findViewById(R.id.look_bigDay_title);
        look_bigDay_num = (TextView)findViewById(R.id.look_bigDay_num);
        look_bigDay_date = (TextView)findViewById(R.id.look_bigDay_date);
        look_bigDay_supplement_LL = (LinearLayout)findViewById(R.id.look_bigDay_supplement_LL);
        look_bigDay_supplement = (TextView)findViewById(R.id.look_bigDay_supplement);
        look_bigDay_repeatTime = (TextView)findViewById(R.id.look_bigDay_repeatTime);
        look_bigDay_remindTime = (TextView)findViewById(R.id.look_bigDay_remindTime);
        edit_bigDay_date = (Button)findViewById(R.id.edit_bigDay_date);
        del_bigDay_date = (Button)findViewById(R.id.del_bigDay_date);

        cancel_look_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LookBigDayActivity.this.finish();
            }
        });

        edit_bigDay_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LookBigDayActivity.this,EditBigDayActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        del_bigDay_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DelDialog.show(LookBigDayActivity.this, "确定删除该重要日吗?", new DelDialog.OnConfirmListener() {
                    @Override
                    public void onConfirmClick() {
                        db.deleteOneDataFromBigDay(id);
                        LookBigDayActivity.this.finish();
                    }
                });
            }
        });
    }

    public void display(){
        BigDay bigDay = db.getOneDataFromBigDay(id).get(0);

        Timestamp t = new Timestamp(System.currentTimeMillis());
        t.setHours(0);
        t.setMinutes(0);
        t.setSeconds(0);
        t.setNanos(0);
        if(bigDay.type == 1 && bigDay.repeatCycle != 0){// 重复 修改前端显示date
            while(bigDay.date.getTime() < t.getTime()){// 一直加重复周期，直到日期大于今天
                if(bigDay.repeatCycle == 1){
                    bigDay.date.setDate(bigDay.date.getDate()+bigDay.repeatInterval);
                }else if(bigDay.repeatCycle == 2){
                    bigDay.date.setDate(bigDay.date.getDate()+7*bigDay.repeatInterval);
                }else if(bigDay.repeatCycle == 3){
                    bigDay.date.setMonth(bigDay.date.getMonth()+bigDay.repeatInterval);
                }else if(bigDay.repeatCycle == 4){
                    bigDay.date.setYear(bigDay.date.getYear()+bigDay.repeatInterval);
                }
            }
        }

        if(bigDay.type==1){
            look_bigDay_title.setText(bigDay.title+"还有");
            look_bigDay_title.setBackgroundColor(Color.parseColor("#3399cc"));
        } else{
            look_bigDay_title.setText(bigDay.title+"已经");
            look_bigDay_title.setBackgroundColor(Color.parseColor("#ff6600"));
        }

        long num = Math.abs(bigDay.date.getTime() - t.getTime())/(24*60*60*1000);
        look_bigDay_num.setText(num+"");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(bigDay.date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(calendar.getTime());
        String[] weekDays = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
        look_bigDay_date.setText("目标日："+dateStr+" "+ weekDays[calendar.get(Calendar.DAY_OF_WEEK)-1]);

        if(bigDay.supplement == null || bigDay.supplement.equals("")){
            look_bigDay_supplement_LL.setVisibility(View.GONE);
        }else{
            look_bigDay_supplement.setText(bigDay.supplement);
            look_bigDay_supplement_LL.setVisibility(View.VISIBLE);
        }

        if(bigDay.repeatCycle == 0){
            look_bigDay_repeatTime.setText("不重复");
        }else{
            String repeatInterval = "";
            if(bigDay.repeatCycle == 1){
                repeatInterval = "天";
            }else if(bigDay.repeatCycle == 2){
                repeatInterval = "周";
            }else if(bigDay.repeatCycle == 3){
                repeatInterval = "月";
            }else if(bigDay.repeatCycle == 4){
                repeatInterval = "年";
            }
            look_bigDay_repeatTime.setText(bigDay.repeatInterval+repeatInterval);
        }

        if(bigDay.remindTime.getTime() != 0){
            calendar.setTime(bigDay.remindTime);
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            dateStr = sdf.format(calendar.getTime());
            look_bigDay_remindTime.setText(dateStr);
        }
    }
}
