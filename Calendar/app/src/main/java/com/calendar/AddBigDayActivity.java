package com.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddBigDayActivity extends Activity {

    private Button bigDay_time;

    private Calendar time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bigday);

        initView();
    }

    private void initView(){
        bigDay_time = (Button)findViewById(R.id.bigDay_time);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        time = Calendar.getInstance();
        String dateStr = sdf.format(time.getTime());
        bigDay_time.setText(dateStr);

        bigDay_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcalendar = Calendar.getInstance();
                new DatePickerDialog(AddBigDayActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        time.set(year,month,dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String dateStr = sdf.format(time.getTime());
                        bigDay_time.setText(dateStr);
                    }
                },mcalendar.get(Calendar.YEAR),mcalendar.get(Calendar.MONTH),mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}
