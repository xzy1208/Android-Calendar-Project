package com.calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LookBigDayActivity extends Activity {

    Button cancel_look_date;
    TextView look_bigDay_title;
    TextView look_bigDay_num;
    TextView look_bigDay_time;
    Button edit_bigDay_date;
    Button del_bigDay_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_big_day);

        initView();
    }

    private void initView(){
        cancel_look_date = (Button)findViewById(R.id.cancel_look_date);
        look_bigDay_title = (TextView)findViewById(R.id.look_bigDay_title);
        look_bigDay_num = (TextView)findViewById(R.id.look_bigDay_num);
        look_bigDay_time = (TextView)findViewById(R.id.look_bigDay_time);
        edit_bigDay_date = (Button)findViewById(R.id.edit_bigDay_date);
        del_bigDay_date = (Button)findViewById(R.id.del_bigDay_date);

        cancel_look_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LookBigDayActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        int id = intent.getIntExtra("id",-1);
        //
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        //int repeatInterval = intent.getIntExtra("repeatInterval",0);
        //int repeatCycle = intent.getIntExtra("repeatCycle",0);
        //Timestamp remindTime
        int type = intent.getIntExtra("type",1);
        String supplement = intent.getStringExtra("supplement");
        long num = intent.getLongExtra("num",0);
        //

        if(type==1){
            look_bigDay_title.setText(title+"还有");
            look_bigDay_title.setBackgroundColor(Color.parseColor("#3399cc"));
        } else{
            look_bigDay_title.setText(title+"已经");
            look_bigDay_title.setBackgroundColor(Color.parseColor("#ff6600"));
        }

        look_bigDay_num.setText(num+"");

        look_bigDay_time.setText(date);

        edit_bigDay_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转编辑界面
            }
        });

        del_bigDay_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 删除
            }
        });
    }
}
