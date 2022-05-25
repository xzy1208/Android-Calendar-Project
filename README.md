# Android-Final-Project

## 一、大纲

### 1.主要功能：

*表示待定

#### ①前端交互

| 功能                         | 结果或进度 |
| ---------------------------- | ---------- |
| 日历显示模式切换：年、月、日 |            |
| 年月日不同模式下日程显示     |            |
| 日程搜索                     |            |
| 日程总体显示                 |            |
| 单个日程详细显示             |            |
| 单个日程编辑界面             |            |
| 倒数日总体显示               |            |
| *任务清单总体显示            |            |
| *跳转到某一日期              |            |



#### ②后台功能

| 功能                 | 结果或进度 |
| -------------------- | ---------- |
| 数据库建立           | 基本实现   |
| 添加日程             |            |
| 删除日程             |            |
| 编辑日程             |            |
| 日程提醒             |            |
| 添加重要日           |            |
| 删除重要日           |            |
| 编辑重要日           |            |
| 重要日提醒           |            |
| *添加任务            |            |
| *删除任务            |            |
| *编辑任务            |            |
| *GPS定位（用于地点） |            |



### 2.、具体结构

**UI设计等待一起讨论完善**

#### 日历

##### 月

![month](.\UI\month.jpg)

##### 周

![week](.\UI\week.jpg)

![weekMuchSchedule](.\UI\weekMuchSchedule.jpg)

![weekMoreSchedule](.\UI\weekMoreSchedule.jpg)

![weekScheduledetail](.\UI\weekScheduledetail.jpg)

##### 日

![day](.\UI\day.jpg)

日的详细日程课参考周

#### 日程

![addSchedule](.\UI\addSchedule.jpg)

![scheduleDetail](.\UI\scheduleDetail.jpg)

#### 重要日

![addMatterDay](.\UI\addMatterDay.jpg)

![matterDayDetail](.\UI\matterDayDetail.jpg)

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
