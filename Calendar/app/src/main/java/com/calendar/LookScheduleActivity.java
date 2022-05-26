package com.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LookScheduleActivity extends Activity {

    private TextView look_schedule_title;
    private TextView look_schedule_place;
    private TextView look_schedule_start_time;
    private TextView look_schedule_end_time;
    private TextView look_schedule_supplement;
    private Button cancel_look_date;
    private Button edit_Schedule_date;
    private Button del_schedule_date;
    private LinearLayout look_schedule_supplement_LL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_schedule);

        initView();
    }

    private void initView(){
        look_schedule_title = (TextView)findViewById(R.id.look_schedule_title);
        look_schedule_place = (TextView)findViewById(R.id.look_schedule_place);
        look_schedule_start_time = (TextView)findViewById(R.id.look_schedule_start_time);
        look_schedule_end_time = (TextView)findViewById(R.id.look_schedule_end_time);
        look_schedule_supplement = (TextView)findViewById(R.id.look_schedule_supplement);
        edit_Schedule_date = (Button)findViewById(R.id.edit_schedule_date);
        del_schedule_date = (Button)findViewById(R.id.del_schedule_date);
        look_schedule_supplement_LL = (LinearLayout)findViewById(R.id.look_schedule_supplement_LL);

        cancel_look_date = (Button)findViewById(R.id.cancel_look_date);
        cancel_look_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LookScheduleActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        int id = intent.getIntExtra("id",-1);
        //
        String title = intent.getStringExtra("title");
        String place = intent.getStringExtra("place");
        String startTime = intent.getStringExtra("startTime");
        String endTime = intent.getStringExtra("endTime");
        //int repeatInterval = intent.getIntExtra("repeatInterval",0);
        //int repeatCycle = intent.getIntExtra("repeatCycle",0);
        //remindTime
        String supplement = intent.getStringExtra("supplement");
        //

        look_schedule_title.setText(title);

        if(place == null || place.equals("")){
            look_schedule_place.setVisibility(View.GONE);
        }else{
            look_schedule_place.setText(place);
            look_schedule_place.setVisibility(View.VISIBLE);
        }

        look_schedule_start_time.setText("开始："+startTime);
        look_schedule_end_time.setText("结束："+endTime);

        if(supplement == null || supplement.equals("")){
            look_schedule_supplement_LL.setVisibility(View.GONE);
        }else{
            look_schedule_supplement.setText(supplement);
            look_schedule_supplement_LL.setVisibility(View.VISIBLE);
        }

        edit_Schedule_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转编辑界面
            }
        });

        del_schedule_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 删除
            }
        });
    }
}
