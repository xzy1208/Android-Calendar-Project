package edu.zjut.androiddeveloper_sx.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.zjut.androiddeveloper_sx.calendar.bean.FindSchedule;
import edu.zjut.androiddeveloper_sx.calendar.bean.Schedule;
import edu.zjut.androiddeveloper_sx.calendar.db.DBAdapter;
import edu.zjut.androiddeveloper_sx.calendar.dialog.DelDialog;

public class LookScheduleActivity extends Activity {

    DBAdapter db;

    private TextView look_schedule_title;
    private TextView look_schedule_place;
    private TextView look_schedule_startTime;
    private TextView look_schedule_endTime;
    private TextView look_schedule_supplement;
    private TextView look_schedule_repeatTime;
    private TextView look_schedule_remindTime;
    private Button cancel_look_date;
    private Button edit_Schedule_date;
    private Button del_schedule_date;
    private LinearLayout look_schedule_supplement_LL;

    public int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_schedule);

        db = DBAdapter.setDBAdapter(LookScheduleActivity.this);
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
        look_schedule_title = (TextView)findViewById(R.id.look_schedule_title);
        look_schedule_place = (TextView)findViewById(R.id.look_schedule_place);
        look_schedule_startTime = (TextView)findViewById(R.id.look_schedule_start_time);
        look_schedule_endTime = (TextView)findViewById(R.id.look_schedule_end_time);
        look_schedule_supplement = (TextView)findViewById(R.id.look_schedule_supplement);
        look_schedule_repeatTime = (TextView)findViewById(R.id.look_schedule_repeatTime);
        look_schedule_remindTime = (TextView)findViewById(R.id.look_schedule_remindTime);
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

        edit_Schedule_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LookScheduleActivity.this,EditScheduleActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        del_schedule_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DelDialog.show(LookScheduleActivity.this, "确定删除该日程吗?", new DelDialog.OnConfirmListener() {
                    @Override
                    public void onConfirmClick() {
                        db.deleteOneDataFromSchedule(id);
                        // 更新FindSchedule数据
                        FindSchedule fs = new FindSchedule(db.getAllDataFromSchedule());
                        LookScheduleActivity.this.finish();
                    }
                });
            }
        });
    }

    public void display(){
        Schedule schedule = db.getOneDataFromSchedule(id).get(0);

        look_schedule_title.setText(schedule.title);

        if(schedule.place == null || schedule.place.equals("") || schedule.place.equals("选择地点")){
            look_schedule_place.setVisibility(View.GONE);
        }else{
            look_schedule_place.setText(schedule.place);
            look_schedule_place.setVisibility(View.VISIBLE);
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        String[] weekDays = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};

        calendar.setTime(schedule.startTime);
        String dateStr1 = sdf1.format(calendar.getTime());
        String dateStr2 = "";
        if(schedule.isAllDay == 0){
            dateStr2 = sdf2.format(calendar.getTime());
        }
        look_schedule_startTime.setText("开始："+dateStr1+" "+ weekDays[calendar.get(Calendar.DAY_OF_WEEK)-1]+" "+dateStr2);

        calendar = Calendar.getInstance();
        calendar.setTime(schedule.endTime);
        dateStr1 = sdf1.format(calendar.getTime());
        dateStr2 = "";
        if(schedule.isAllDay == 0){
            dateStr2 = sdf2.format(calendar.getTime());
        }
        look_schedule_endTime.setText("结束："+dateStr1+" "+ weekDays[calendar.get(Calendar.DAY_OF_WEEK)-1]+" "+dateStr2);

        if(schedule.supplement == null || schedule.supplement.equals("")){
            look_schedule_supplement_LL.setVisibility(View.GONE);
        }else{
            look_schedule_supplement.setText(schedule.supplement);
            look_schedule_supplement_LL.setVisibility(View.VISIBLE);
        }

        if(schedule.repeatCycle == 0){
            look_schedule_repeatTime.setText("不重复");
        }else{
            String repeatInterval = "";
            if(schedule.repeatCycle == 1){
                repeatInterval = "天";
            }else if(schedule.repeatCycle == 2){
                repeatInterval = "周";
            }else if(schedule.repeatCycle == 3){
                repeatInterval = "月";
            }else if(schedule.repeatCycle == 4){
                repeatInterval = "年";
            }
            look_schedule_repeatTime.setText(schedule.repeatInterval+repeatInterval);
        }

        if(schedule.remindTime.getTime() != 0){
            calendar.setTime(schedule.remindTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String dateStr = sdf.format(calendar.getTime());
            look_schedule_remindTime.setText(dateStr);
        }
    }
}
