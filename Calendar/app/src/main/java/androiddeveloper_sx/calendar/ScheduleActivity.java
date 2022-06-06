package androiddeveloper_sx.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import androiddeveloper_sx.calendar.adapter.ScheduleAdapter1;
import androiddeveloper_sx.calendar.bean.FindSchedule;
import androiddeveloper_sx.calendar.bean.ScheduleDate;
import androiddeveloper_sx.calendar.db.DBAdapter;
import edu.zjut.androiddeveloper_sx.calendar.R;

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