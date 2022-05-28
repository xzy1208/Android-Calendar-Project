package com.calendar;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.calendar.db.DBAdapter;

public class AddActivity extends TabActivity {

    public DBAdapter db;

    private Button cancel_add_date;
    private Button finish_add_date;
    private TextView add_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        db = DBAdapter.setDBAdapter(AddActivity.this);
        db.open();

        tabPaging();

        initView();
    }

    private void initView(){
        cancel_add_date = (Button)findViewById(R.id.cancel_add_date);
        finish_add_date = (Button)findViewById(R.id.finish_add_date);
        add_title = (TextView)findViewById(R.id.add_title);

        cancel_add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddActivity.this.finish();
            }
        });

        finish_add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getTabHost().getCurrentTab() == 0){ // 日程
                    Intent intent = new Intent();
                    intent.setAction("addSchedule");
                    sendOrderedBroadcast(intent, null);
                    Log.e("发送一条广播","addSchedule");
                }else if(getTabHost().getCurrentTab() == 1){ // 重要日
                    Intent intent = new Intent();
                    intent.setAction("addBigDay");
                    sendOrderedBroadcast(intent, null);
                }
            }
        });
    }

    private void tabPaging() {
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        // 进入界面时刷新UI：setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        // 日程
        View tab1 = LayoutInflater.from(this).inflate(R.layout.tab_schedule, null);
        Intent intent1 = new Intent().setClass(this, AddScheduleActivity.class);
        spec = tabHost.newTabSpec("1").setIndicator(tab1).setContent(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(spec);

        // 重要日
        View tab2 = LayoutInflater.from(this).inflate(R.layout.tab_bigday, null);
        Intent intent2 = new Intent().setClass(this, AddBigDayActivity.class);
        spec = tabHost.newTabSpec("2").setIndicator(tab2).setContent(intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(spec);

        // 设置监听器，监听tab切换
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                int tabID = Integer.valueOf(tabId) - 1;
                for (int i = 0; i < getTabWidget().getChildCount(); i++) {
                    TextView tab_title = (TextView) getTabWidget().getChildAt(i).findViewById(R.id.tab_title);
                    TextView tab_underline = (TextView) getTabWidget().getChildAt(i).findViewById(R.id.tab_underline);
                    if (i == tabID) {
                        tab_title.setTextColor(Color.parseColor("#6699ff"));
                        tab_underline.setVisibility(View.VISIBLE);
                        if(i== 0){
                            add_title.setText("新建日程");
                        }else{
                            add_title.setText("新建重要日");
                        }
                    } else {
                        tab_title.setTextColor(Color.parseColor("#000000"));
                        tab_underline.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

}
