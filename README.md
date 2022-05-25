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
| 数据库建立           |            |
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



### 二、具体结构

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

### 三、数据库结构

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
