# Android-Final-Project

## 一、大纲

### 1.主要功能：

*表示待定

#### ①前端交互

| 功能                            | 结果或进度 |
| ------------------------------- | ---------- |
| 日历显示模式切换：月、周、日    | done       |
| 月周日不同模式下日程+重要日显示 | done       |
| 日程搜索                        | done       |
| 日程总体显示                    | done       |
| 同一时间段多个日程              | done       |
| 单个日程详细显示                | done       |
| 单个日程编辑界面                | done       |
| 倒数日总体显示                  | done       |
| 跳转到今日                      | done       |
| *任务清单总体显示               |            |
| *跳转到某一日期                 | done       |



#### ②后台功能

| 功能                 | 结果或进度 |
| -------------------- | ---------- |
| 数据库建立           | 基本实现   |
| 添加日程             | done       |
| 删除日程             | done       |
| 编辑日程             | done       |
| 日程提醒             | done       |
| *日程通知            |            |
| 日程搜索             | done       |
| 添加重要日           | done       |
| 删除重要日           | done       |
| 编辑重要日           | done       |
| 重要日提醒           | done       |
| 短信查询             | done       |
| *添加任务            |            |
| *删除任务            |            |
| *编辑任务            |            |
| *GPS定位（用于地点） |            |



### 2.、具体结构

**UI设计等待一起讨论完善**

#### 日历

##### 月

<img src=".\UI\month.jpg" alt="month" style="zoom:50%;" />

##### 周

<img src=".\UI\week.jpg" alt="week" style="zoom:50%;" />

<img src=".\UI\weekMuchSchedule.jpg" alt="weekMuchSchedule" style="zoom:50%;" />

<img src=".\UI\weekMoreSchedule.jpg" alt="weekMoreSchedule" style="zoom:50%;" />

<img src=".\UI\weekScheduledetail.jpg" alt="weekScheduledetail" style="zoom:50%;" />

##### 日

<img src=".\UI\day.jpg" alt="day" style="zoom:50%;" />

日的详细日程课参考周

#### 日程

<img src=".\UI\addSchedule.jpg" alt="addSchedule" style="zoom:50%;" />

<img src=".\UI\scheduleDetail.jpg" alt="scheduleDetail" style="zoom:50%;" />

#### 重要日

<img src=".\UI\addMatterDay.jpg" alt="addMatterDay" style="zoom:50%;" />

<img src=".\UI\matterDayDetail.jpg" alt="matterDayDetail" style="zoom:50%;" />

### 3、数据库结构

**schedule**：日程表

| 列名            | 字段类型 | 含义                |
| --------------- | -------- | ------------------- |
| _id             | int      | id                  |
| title           | text     | 标题                |
| *place          | text     | *地点               |
| *isAllDay       | int      | *是否全天           |
| startTime       | DATETIME | 开始时间            |
| endTime         | DATETIME | 结束时间            |
| *repeatInterval | int      | *重复时间 数字      |
| *repeatCycle    | int      | *重复频率 文字      |
| remindTime      | DATETIME | 提醒时间/多久前提醒 |
| *isImportant    | int      | *是否重要提醒       |
| supplement      | text     | *补充说明           |

**bigDay**：重要日表

| 列名            | 类型     | 含义                 |
| --------------- | -------- | -------------------- |
| _id             | int      | id                   |
| title           | text     | 标题                 |
| date            | DATETIME | 日期                 |
| *repeatInterval | int      | *重复时间 数字       |
| *repeatCycle    | int      | *重复时间 文字       |
| *remindTime     | DATETIME | *提醒时间/多久前提醒 |
| type            | int      | 正数/倒数            |
| supplement      | text     | *补充说明            |

**task**：任务清单（暂定）



## 二、具体开发说明

### 1.数据库

DBAdapter作为管理数据库和表的工具

创建schedule和bigDay类作为Bean

然后又创建了scheduleTable和bigDayTable用于封装Bean和数据库查询结果间的转换方法等方法，以及分类相应字段，避免两个表的混乱。

### 2.数据转换

#### ①Date和Timestamp

##### a.Date

按：年、月、日

public Date(int year,int month,int date)

按：年、月、日、时、分

public Date(int year,int month,int date,int hrs,int min)

按：年、月、日、时、分、秒

public Date(int year,int month,int date,int hrs,int min,int sec)

按给定的参数创建一日期对象。

year的值为：**需设定的年份-1900**。例如需设定的年份是1997则year的值应为97，即1997-1900的结果。所以Date中可设定的年份最小为1900；

**month的值域为0～11**，0代表1月，11表代表12月；

date的值域在1～31之间；

hrs的值域在0～23之间。从午夜到次日凌晨1点间hrs=0，从中午到下午1点间hrs=12；

min和sec的值域在0～59之间。

[(3条消息) java 设定一个时间Date - CSDN](https://www.csdn.net/tags/NtTacgwsNjY4NTctYmxvZwO0O0OO0O0O.html)

##### b.Date转Timestamp

```java
Date date = new Date();  
Timestamp ts = new Timestamp(date.getTime());

Date date = new Date();    
DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
String dateStr = sdf.format(date);  
Timestamp ts = Timestamp.valueOf(dateStr); //2017-05-06 15:54:21.0
```

[Java java.sql.Timestamp时间戳案例详解_java_脚本之家 (jb51.net)](https://www.jb51.net/article/220840.htm)

### 3.tabhost的使用

#### ①MainActivity

将需要添加选项卡的MainActivity继承TabActivity

注册TabHost，注册选项tab1、tab2、tab3……设置tab1的布局、点击后对应的Activity、tabId用于监听切换

```java
public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tabPaging();
    }

    private void tabPaging(){
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        // 进入界面时刷新UI：setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        View tab1 = LayoutInflater.from(this).inflate(R.layout.tab1,null);// tab1.xml是第选项卡第一个选项的布局
        Intent intent1 = new Intent().setClass(this, ScheduleActivity.class);// 和tab1.xml布局的context对应，绑定了该Activity作为点击该选项跳转的界面
        spec = tabHost.newTabSpec("1").setIndicator(tab1).setContent(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));// “1”是tabId
        tabHost.addTab(spec);

        // 设置监听器，监听tab切换
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId){

                // 切换选项卡的效果
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
```

#### ②main.xml

设置MainActivity对应的main.xml的布局：

设置最外部组件TabHost的tools:context=".MainActivity"和android:id="@android:id/tabhost">，注意tabhost的id不可更改！

设置TabWidget对应选项卡条，id不可更改；设置FrameLayout对应选项卡条下的内容，id不可更改。

```java
<?xml version="1.0" encoding="utf-8"?>
<TabHost xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:id="@android:id/tabhost">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <TabWidget
            android:id="@android:id/tabs"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:paddingTop="15dp"
            android:paddingBottom="15dp"
            android:paddingLeft="50dp"
            android:paddingRight="50dp"/>

        <FrameLayout
            android:id="@android:id/tabcontent"
            android:layout_width="match_parent"
            android:layout_height="match_parent">
        </FrameLayout>

    </LinearLayout>

</TabHost>
```

#### ③tab.xml

设置第一个选项tab1.xml的布局：

tools:context=".ScheduleActivity" 绑定的是点击选项要切换的activity

```java
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    tools:context=".ScheduleActivity"
    android:gravity="center">

    <TextView
        android:id="@+id/tab_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="月"
        android:textColor="#6699ff"
        android:textSize="15sp" />

    <TextView
        android:id="@+id/tab_underline"
        android:layout_width="match_parent"
        android:layout_height="2dp"
        android:layout_marginTop="4dp"
        android:background="#6699ff" />

</LinearLayout>
```

要切换的Activity和对应的布局，照常写。

### 4.悬浮按钮的使用

#### ①添加依赖

在build.gradle里加入依赖：compile 'com.android.support:design:26.0.0-alpha1'，可在library dependency库里搜索对应版本。

#### ②FloatingActionButton

在main.xml里添加android.support.design.widget.FloatingActionButton控件，并在最外层布局里设置xmlns:app="http://schemas.android.com/apk/res-auto"

```java
<?xml version="1.0" encoding="utf-8"?>
<TabHost xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:id="@android:id/tabhost">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <TabWidget
            android:id="@android:id/tabs"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:paddingTop="15dp"
            android:paddingBottom="15dp"
            android:paddingLeft="50dp"
            android:paddingRight="50dp"/>

        <FrameLayout
            android:id="@android:id/tabcontent"
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <android.support.design.widget.FloatingActionButton
                android:id="@+id/floatMenu"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="bottom|right"
                android:layout_margin="16dp"
                android:src="@mipmap/float_add"
                app:backgroundTint="#ffffff"
                app:elevation="5dp"
                app:pressedTranslationZ="12dp"
                app:fabSize="normal"
                app:borderWidth="0dp"
                app:rippleColor="#cccccc" />

        </FrameLayout>

    </LinearLayout>

</TabHost>
```

在MainActivity里设置FloatingActionButton控件对应的点击事件，方法和普通Button一样。

### 5.短信查询

开启手机短信权限。

#### ①AndroidManifest.xml

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.SEND_SMS" />
<uses-permission android:name="android.permission.RECEIVE_SMS" />
<uses-permission android:name="android.permission.READ_SMS" />
```

#### ②观察者SmsObserver

监听短信数据库，收到匹配的短信后通过Handler发送消息给主线程。

```java
public class SmsObserver extends ContentObserver {
    private Context mContext;
    private Handler mHandler;
    private static int id=0; //这里必须用静态的，防止程序多次意外初始化情况

    public SmsObserver(Context context,Handler handler){
        super(handler);
        this.mContext = context;
        this.mHandler = handler;
    }

    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        String code = "";

        //过滤可能界面调用初始化两次的情况
        if (uri.toString().contains("content://sms/raw")) {
            return;
        }

        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor cursor = mContext.getContentResolver().query(inboxUri, null, null, null, "date desc");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String _id = cursor.getString(cursor.getColumnIndex("_id"));
                //比较id 解决重复问题
                if (id < Integer.parseInt(_id)) {
                    id = Integer.parseInt(_id);//将获取到的当前id记录，防止重复
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    Log.i("Info", body);
                    //正则表达式d{6}的意思是连续6位是数字的就提取出来
                    Pattern pattern = Pattern.compile("(\\d{8})");
                    //对短信的内容进行匹配
                    Matcher matcher = pattern.matcher(body);
                    if (body.contains("查询") && matcher.find()) {
                        code = matcher.group(0);
                        Log.i("Info", code);

                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("address",address);
                            obj.put("code",code);

                            // 发送到主线程
                            Message msMessage = new Message();
                            msMessage.what = MainActivity.Finding;
                            msMessage.obj = obj;
                            mHandler.sendMessage(msMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            cursor.close();
        }
    }
}
```

#### ③MainActivity

主线程注册观察者

```
//创建内容观察者的对象
smsObserver = new SmsObserver(MainActivity.this, mHandler);
//短信的uri为content://sms
Uri uri = Uri.parse("content://sms");
//注册内容观察者
this.getContentResolver().registerContentObserver(uri, true, smsObserver);
```

在Handler里处理数据，并发送短信。

```
SmsManager smsManager = SmsManager.getDefault();
smsManager.sendTextMessage(phone, null, message, null, null);
```

### 6.闹钟提醒

打开手机悬浮窗、后台权限。

#### ①AndroidManifest.xml

```
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
```

#### ②MainActivity

打开ClockService

```
Intent intent = new Intent(MainActivity.this, ClockService.class);
startService(intent);
```

#### ③ClockService

注册对象

```
public static AlarmManager alarmManager;
public static  PendingIntent pi;
public static Intent intent;
```

```
alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); // 获得系统提供的AlarmManager服务的对象
intent = new Intent(ClockService.this, TaskService.class);
pi = PendingIntent.getService(ClockService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); // PendingIntent是对Intent的描述，主要用来处理即将发生的事情，这个Intent会由其他程序进行调用，这里是由闹钟调用
```

设置触发时间

```
alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pi); // 用set时间不准
```

### 7.popupwindow悬浮框

[2.6.1 PopupWindow(悬浮框)的基本使用 | 菜鸟教程 (runoob.com)](https://www.runoob.com/w3cnote/android-tutorial-popupwindow.html)

### 8.ViewPager翻页

[(5条消息) ViewPager 全面总结_淡然一笑、的博客-CSDN博客_viewpager](https://blog.csdn.net/weixin_39251617/article/details/79399592)

[(5条消息) ViewPager 全面总结_淡然一笑、的博客-CSDN博客_viewpager](https://blog.csdn.net/weixin_39251617/article/details/79399592)

### 9.自定义监听器

[(5条消息) Android自定义监听器_猿小帅01的博客-CSDN博客_android自定义监听器](https://blog.csdn.net/qq_44203816/article/details/119415837)

### 10.高德定位

 [入门指南-Android 定位SDK|高德地图API (amap.com)](https://lbs.amap.com/api/android-location-sdk/gettingstarted) 

### 11.GridLayout.LayoutParams

1：位置：几行几列的意思 2：合并占几块 3：weight权重（float类型）

```java
GridLayout.LayoutParams iconParams = new GridLayout.LayoutParams( GridLayout.spec(rowIndex, 1, GridLayout.CENTER)，                 GridLayout.spec(columnStart, 1));
```

