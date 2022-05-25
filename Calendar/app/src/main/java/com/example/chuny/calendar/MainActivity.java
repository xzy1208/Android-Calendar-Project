package com.example.chuny.calendar;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        tabPaging();
    }

    private void initView(){

    }

    private void tabPaging(){
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        // 进入界面时刷新UI：setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        // tab1 月
        View tab1 = LayoutInflater.from(this).inflate(R.layout.tab1,null);
        Intent intent1 = new Intent().setClass(this, ScheduleActivity.class);// 记得修改 还有布局的content
        spec = tabHost.newTabSpec("1").setIndicator(tab1).setContent(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(spec);

        // tab2 周
        View tab2 = LayoutInflater.from(this).inflate(R.layout.tab2,null);
        Intent intent2 = new Intent().setClass(this, ScheduleActivity.class);// 记得修改 还有布局的content
        spec = tabHost.newTabSpec("2").setIndicator(tab2).setContent(intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(spec);

        // tab3 日
        View tab3 = LayoutInflater.from(this).inflate(R.layout.tab3,null);
        Intent intent3 = new Intent().setClass(this, ScheduleActivity.class);// 记得修改 还有布局的content
        spec = tabHost.newTabSpec("3").setIndicator(tab3).setContent(intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(spec);

        // tab4 日程
        View tab4 = LayoutInflater.from(this).inflate(R.layout.tab4,null);
        Intent intent4 = new Intent().setClass(this, ScheduleActivity.class);
        spec = tabHost.newTabSpec("4").setIndicator(tab4).setContent(intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(spec);

        //tab5 重要日
        View tab5 = LayoutInflater.from(this).inflate(R.layout.tab5,null);
        Intent intent5 = new Intent().setClass(this, BigDayActivity.class);
        spec = tabHost.newTabSpec("5").setIndicator(tab5).setContent(intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(spec);

        // 设置监听器，监听tab切换
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId){

                int tabID = Integer.valueOf(tabId)-1;
                for (int i = 0; i < getTabWidget().getChildCount(); i++)
                {
                    TextView tab_title = (TextView) getTabWidget().getChildAt(i).findViewById(R.id.tab_title);
                    TextView tab_underline = (TextView) getTabWidget().getChildAt(i).findViewById(R.id.tab_underline);
                    if (i == tabID)
                    {
                        tab_title.setTextColor(Color.parseColor("#6699ff"));
                        tab_underline.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        tab_title.setTextColor(Color.parseColor("#000000"));
                        tab_underline.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }
}
