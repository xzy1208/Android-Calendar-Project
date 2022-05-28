package com.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.calendar.adapter.ScheduleAdapter1;
import com.calendar.bean.FindSchedule;
import com.calendar.bean.Schedule;
import com.calendar.bean.ScheduleDate;
import com.calendar.db.DBAdapter;

import java.util.List;

public class ScheduleActivity extends Activity {

    public DBAdapter db;

    private ListView scheduleListView;

    private List<ScheduleDate> scheduleDateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        db = DBAdapter.setDBAdapter(ScheduleActivity.this);
        db.open();

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        displaySchedule();
    }

    private void initView(){
        scheduleListView = (ListView)findViewById(R.id.scheduleListView);
    }

    private void displaySchedule(){
        scheduleDateList = FindSchedule.scheduleDateList;
        scheduleListView.setAdapter(new ScheduleAdapter1(ScheduleActivity.this,scheduleDateList));
    }
}
