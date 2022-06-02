package com.calendar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.calendar.adapter.PlaceAdapter;
import com.calendar.bean.FindSchedule;
import com.calendar.bean.Place;
import com.calendar.bean.Schedule;
import com.calendar.db.DBAdapter;
import com.calendar.dialog.NumberPickerDialog;
import com.calendar.view.DayManager;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddScheduleActivity extends Activity implements LocationSource, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener,AMap.OnMapClickListener{

    DBAdapter db;

    private TextView schedule_title;
    private Button schedule_place;
    private RelativeLayout schedule_place_RL;
    private Switch schedule_allday;
    private Button schedule_start_time;
    private Button schedule_end_time;
    private Switch schedule_repeat;
    private LinearLayout schedule_repeat_LL;
    private Button schedule_repeatInterval;
    private Button schedule_repeatCycle;
    private Button schedule_remindTime;
    private Switch schedule_isImportant;
    private TextView schedule_supplement;

    private int isAllDay;// 全天
    private int startYear,startMonth,startDay,startHour,startMinute;
    private Calendar startTime;// 开始时间
    private int endYear,endMonth,endDay,endHour,endMinute;
    private Calendar endTime;// 结束时间
    private int schedule_repeatInterval_num;// 重复数字
    private int schedule_repeatCycle_num;// 重复文字
    private int schedule_remindTime_num;// 提醒

    private BroadcastReceiver mReceiver;

    // 地图对象
    private AMap aMap;
    private MapView mapView = null;
    private UiSettings uiSettings;
    // 定位服务
    private LocationSource.OnLocationChangedListener onLocationChangedListener;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;
    //地理编码
    private GeocodeSearch geocodeSearch;
    //回显位置信息的TextView
    private TextView locationCoordinate;
    private TextView locationInfo;
    private ListView place_listView;
    //当前地图上的marker
    private Marker marker;
    // 权限
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int PERMISSON_REQUESTCODE = 0;
    private boolean isNeedCheck = true;
    // 搜索框
    private SearchView searchView;
    private Button cancel_choose_place;
    private Button finish_choose_place;
    // 地址数据
    private PoiSearch poiSearch;
    private List<Place> placeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        db = DBAdapter.setDBAdapter(AddScheduleActivity.this);
        db.open();

        initView();

        // 隐私合规校验
        AMapLocationClient.updatePrivacyShow(this,true,true);
        AMapLocationClient.updatePrivacyAgree(this,true);
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);

        place_listView = findViewById(R.id.place_listView);
        place_listView.setOnItemClickListener(new MyOnItemClickListener());

        //获取地图控件引用
        mapView = (MapView)findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        if(aMap == null){
            aMap = mapView.getMap();
            uiSettings = aMap.getUiSettings();
            // 设置地图属性
            setMapAttribute();
        }

        initSearchView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mapView.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected  void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void initView(){
        schedule_title = (TextView)findViewById(R.id.schedule_title);
        schedule_place = (Button)findViewById(R.id.schedule_place);
        schedule_place_RL = (RelativeLayout)findViewById(R.id.schedule_place_RL);
        schedule_allday = (Switch)findViewById(R.id.schedule_allday);
        schedule_start_time = (Button)findViewById(R.id.schedule_start_time);
        schedule_end_time = (Button)findViewById(R.id.schedule_end_time);
        schedule_repeat = (Switch)findViewById(R.id.schedule_repeat);
        schedule_repeat_LL = (LinearLayout)findViewById(R.id.schedule_repeat_LL);
        schedule_repeatInterval = (Button)findViewById(R.id.schedule_repeatInterval);
        schedule_repeatCycle = (Button)findViewById(R.id.schedule_repeatCycle);
        schedule_isImportant = (Switch)findViewById(R.id.schedule_isImportant);
        schedule_remindTime = (Button)findViewById(R.id.schedule_remindTime);
        schedule_supplement = (TextView)findViewById(R.id.schedule_supplement);

        schedule_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedule_place_RL.setVisibility(View.VISIBLE);
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if(MainActivity.page>=0&&MainActivity.page<=2){
            startTime= DayManager.getSelectCalendar();
        }else{
            startTime = Calendar.getInstance();
        }
        String dateStr1 = sdf.format(startTime.getTime());
        schedule_start_time.setText(dateStr1);
        if(MainActivity.page>=0&&MainActivity.page<=2){
            endTime= DayManager.getSelectCalendar();
        }else {
            endTime = Calendar.getInstance();
        }
        String dateStr2 = sdf.format(endTime.getTime());
        schedule_end_time.setText(dateStr2);

        schedule_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcalendar = Calendar.getInstance();
                new DatePickerDialog(AddScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        startYear = year;
                        startMonth = month;
                        startDay = dayOfMonth;

                        if(schedule_allday.isChecked()){
                            startTime.set(year,month,dayOfMonth);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(startTime.getTime());
                            schedule_start_time.setText(dateStr);
                        }else{
                            Calendar mcalendar = Calendar.getInstance();
                            new TimePickerDialog(AddScheduleActivity.this, 0,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            startHour = hourOfDay;
                                            startMinute = minute;
                                            startTime.set(startYear,startMonth,startDay,startHour,startMinute);

                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                            String dateStr = sdf.format(startTime.getTime());
                                            schedule_start_time.setText(dateStr);
                                        }
                                    }, mcalendar.get(Calendar.HOUR_OF_DAY), mcalendar.get(Calendar.MINUTE), false).show();
                        }
                    }
                },mcalendar.get(Calendar.YEAR),mcalendar.get(Calendar.MONTH),mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        schedule_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcalendar = Calendar.getInstance();
                new DatePickerDialog(AddScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                        endYear = year;
                        endMonth = month;
                        endDay = dayOfMonth;

                        if(schedule_allday.isChecked()){
                            endTime.set(year,month,dayOfMonth);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(endTime.getTime());
                            schedule_end_time.setText(dateStr);
                        }else{
                            Calendar mcalendar = Calendar.getInstance();
                            new TimePickerDialog(AddScheduleActivity.this, 0,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            endHour = hourOfDay;
                                            endMinute = minute;
                                            endTime.set(endYear,endMonth,endDay,endHour,endMinute);

                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                            String dateStr = sdf.format(endTime.getTime());
                                            schedule_end_time.setText(dateStr);
                                        }
                                    }, mcalendar.get(Calendar.HOUR_OF_DAY), mcalendar.get(Calendar.MINUTE), false).show();
                        }
                    }
                },mcalendar.get(Calendar.YEAR),mcalendar.get(Calendar.MONTH),mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        isAllDay = 1;
        schedule_allday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SimpleDateFormat sdf;
                if (isChecked)
                {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    isAllDay = 1;
                }else{
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    isAllDay = 0;
                }
                String dateStr1 = sdf.format(startTime.getTime());
                schedule_start_time.setText(dateStr1);
                String dateStr2 = sdf.format(endTime.getTime());
                schedule_end_time.setText(dateStr2);
            }
        });

        schedule_remindTime_num = 0;
        schedule_remindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(AddScheduleActivity.this, view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.ring, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        schedule_remindTime_num = item.getOrder();
                        schedule_remindTime.setText(item.getTitle());
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
        schedule_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    schedule_repeat_LL.setVisibility(View.VISIBLE);
                }else{
                    schedule_repeat_LL.setVisibility(View.GONE);
                }
            }
        });

        schedule_repeatInterval_num = 1;
        schedule_repeatInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NumberPickerDialog(AddScheduleActivity.this, new NumberPickerDialog.OnNumberSelectedListener() {
                    @Override
                    public void onNumberSelected(NumberPicker view, int number) {
                        schedule_repeatInterval_num = number;
                        schedule_repeatInterval.setText(number+"");
                    }
                }, schedule_repeatInterval_num).show();
            }
        });

        schedule_repeatCycle_num = 4;
        schedule_repeatCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(AddScheduleActivity.this, view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.repeat_cycle, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        schedule_repeatCycle_num = item.getOrder();
                        schedule_repeatCycle.setText(item.getTitle());
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
                if (action.equals("addSchedule")) {
                    Log.e("收到一条广播","addSchedule");
                    String title = schedule_title.getText().toString();
                    String place = schedule_place.getText().toString();
                    Timestamp date1 = new Timestamp(startTime.getTimeInMillis());
                    Timestamp date2 = new Timestamp(endTime.getTimeInMillis());
                    Timestamp remindTime = null;
                    int important = 0;
                    String supplement = schedule_supplement.getText().toString();

                    if(title == null || title.equals("")){
                        Toast.makeText(AddScheduleActivity.this,"标题不可为空",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(isAllDay == 1){
                        date1.setHours(0);
                        date1.setMinutes(0);
                        date1.setSeconds(0);
                        date1.setNanos(0);
                        date2.setHours(0);
                        date2.setMinutes(0);
                        date2.setSeconds(0);
                        date2.setNanos(0);
                    }

                    if(date1.getTime()>date2.getTime()){
                        Toast.makeText(AddScheduleActivity.this,"开始时间不可晚于结束时间",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!schedule_repeat.isChecked()){ // 不重复
                        schedule_repeatCycle_num = 0;
                    }

                    if(schedule_remindTime_num == 0){
                        remindTime = new Timestamp(0);
                    }else if(schedule_remindTime_num == 1){
                        remindTime = new Timestamp(date1.getTime()-10*60*1000);
                    }else if(schedule_remindTime_num == 2){
                        remindTime = new Timestamp(date1.getTime()-1*60*60*1000);
                    }else if(schedule_remindTime_num == 3){
                        remindTime = new Timestamp(date1.getTime()-1*24*60*60*1000);
                    }

                    if(schedule_isImportant.isChecked()){
                        important = 1;
                    }

                    Schedule schedule = new Schedule(title, place, isAllDay, date1, date2, schedule_repeatInterval_num, schedule_repeatCycle_num, remindTime, important, supplement);
                    db.insertSchedule(schedule);
                    Log.e("db.insertSchedule","执行一次");

                    // 更新FindSchedule数据
                    FindSchedule fs = new FindSchedule(db.getAllDataFromSchedule());

                    AddScheduleActivity.this.finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("addSchedule");
        AddScheduleActivity.this.registerReceiver(mReceiver, filter);
    }

    private void initSearchView(){
        searchView = (SearchView)findViewById(R.id.place_searchView);
        cancel_choose_place = (Button)findViewById(R.id.cancel_choose_place);
        finish_choose_place = (Button)findViewById(R.id.finish_choose_place);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //点击软键盘搜索的时候执行
                query(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //搜索框文本发生改变的时候执行
                if(s == null || "".equals(s.trim())){
                    placeList = new ArrayList<>();
                    place_listView.setAdapter(new PlaceAdapter(AddScheduleActivity.this, placeList));
                }
                return false;
            }
        });

        cancel_choose_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedule_place_RL.setVisibility(View.GONE);
            }
        });

        finish_choose_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(marker != null){
                    //根据当前经纬度查询地址
                    double latitude = marker.getPosition().latitude;
                    double longitude = marker.getPosition().longitude;
                    LatLonPoint latLonPoint = new LatLonPoint(latitude, longitude);
                    RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
                    geocodeSearch.getFromLocationAsyn(query);
                }

                schedule_place_RL.setVisibility(View.GONE);
            }
        });

    }

    private void query(String s){
        //通过Query设置搜索条件：第一个参数为搜索内容，第二个参数为搜索类型，第三个参数为搜索范围（空字符串代表全国）。
        PoiSearch.Query query = new PoiSearch.Query(s, "", "");
        try{
            poiSearch = new PoiSearch(AddScheduleActivity.this, query);
            poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                @Override
                public void onPoiSearched(PoiResult poiResult, int errcode) {
                    //判断搜索成功
                    if (errcode == 1000) {
                        if (null != poiResult && poiResult.getPois().size() > 0) {
                            placeList = new ArrayList<Place>();
                            for (int i = 0; i < poiResult.getPois().size(); i++) {
                                double longitude = poiResult.getPois().get(i).getLatLonPoint().getLongitude();
                                double latitude = poiResult.getPois().get(i).getLatLonPoint().getLatitude();
                                String title = poiResult.getPois().get(i).getTitle();
                                String snippet = poiResult.getPois().get(i).getProvinceName()+poiResult.getPois().get(i).getCityName()+poiResult.getPois().get(i).getAdName()+poiResult.getPois().get(i).getSnippet();
                                Place place = new Place(longitude, latitude, title, snippet);
                                placeList.add(place);
                            }
                            place_listView.setAdapter(new PlaceAdapter(AddScheduleActivity.this, placeList));
                        }
                    }
                }
                @Override
                public void onPoiItemSearched(PoiItem poiItem, int i) {}
            });
            poiSearch.searchPOIAsyn();
        }catch (AMapException e){
            Log.e("GPS",e.getErrorMessage());
        }
    }

    private void setMapAttribute() {
        //设置默认缩放级别
        aMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //隐藏的右下角缩放按钮
        uiSettings.setZoomControlsEnabled(false);
        //显示右上角定位按钮
        uiSettings.setMyLocationButtonEnabled(true);
        //设置定位监听
        aMap.setLocationSource(this);
        //可触发定位并显示当前位置
        aMap.setMyLocationEnabled(true);
        //定位一次，且将视角移动到地图中心点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        //隐藏定位点外圈圆的颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        aMap.setMyLocationStyle(myLocationStyle);
        //设置地理编码查询
        try {
            geocodeSearch = new GeocodeSearch(this);
            geocodeSearch.setOnGeocodeSearchListener(this);
        }catch (AMapException e){
            Log.e("GPS","地理编码查询失败");
        }
        //设置地图点击事件
        aMap.setOnMapClickListener(this);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation){
        if (onLocationChangedListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //显示定位圆点
                onLocationChangedListener.onLocationChanged(aMapLocation);
            } else {
                Log.e("GPS","定位失败, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener){
        ServiceSettings.updatePrivacyShow(this,true,true);
        ServiceSettings.updatePrivacyAgree(this,true);

        this.onLocationChangedListener = onLocationChangedListener;
        if (locationClient == null) {
            //初始化定位
            try {
                locationClient = new AMapLocationClient(this);
            }catch(Exception e){
                Log.e("GPS","LocationClient初始化错误");
            }
            //初始化定位参数
            locationClientOption = new AMapLocationClientOption();
            //设置定位回调监听
            locationClient.setLocationListener(this);
            //高精度定位模式
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //单定位模式
            locationClientOption.setOnceLocation(true);
            //设置定位参数
            locationClient.setLocationOption(locationClientOption);
            //启动定位
            locationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        onLocationChangedListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
    }

    // 根据坐标转换地址信息
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (regeocodeResult != null) {
            schedule_place.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress());
        }
    }

    // 地址转坐标
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    // 地图点击事件
    @Override
    public void onMapClick(LatLng latLng) {
        // 根据点击地图的点位画出标记图
        drawMarker(latLng.latitude, latLng.longitude);
    }

    // 移动到指定经纬度
    private void initAMap(double latitude, double longitude) {
        AMap mAMap = mapView.getMap();
        CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude, longitude), 17f, 0, 30);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mAMap.moveCamera(cameraUpdate);
    }

    // 画定位标记图
    public void drawMarker(double latitude, double longitude) {
        if (marker != null) {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_icon));
        markerOptions.position(new LatLng(latitude, longitude));
        marker = aMap.addMarker(markerOptions);
    }

    // 检查权限
    private void checkPermissions(String[] permissions) {
        //获取权限列表
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
            //list.toarray将集合转化为数组
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    // 获取权限集中需要申请权限的列表
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    // 检测是否说有的权限都已经授权
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {      //没有授权
                showMissingPermissionDialog();              //显示提示信息
                isNeedCheck = false;
            }
        }
    }

    // 显示提示信息
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton(R.string.setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    // listView点击事件
    class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Place place = placeList.get(position);
            initAMap(place.latitude, place.longitude);
            drawMarker(place.latitude, place.longitude);
            Log.e("GPS",place.title);
        }
    }
}
