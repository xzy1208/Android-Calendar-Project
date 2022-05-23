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

| 功能       | 结果或进度 |
| ---------- | ---------- |
| 添加日程   |            |
| 删除日程   |            |
| 编辑日程   |            |
| 日程提醒   |            |
| 添加重要日 |            |
| 删除重要日 |            |
| 编辑重要日 |            |
| 重要日提醒 |            |
| *添加任务  |            |
| *删除任务  |            |
| *编辑任务  |            |



### 二、具体结构

**UI设计等待一起讨论完善**

#### 日历月周日

![month](.\UI\month.jpg)

![week](.\UI\week.jpg)

![day](.\UI\day.jpg)

#### 日程

![addSchedule](.\UI\addSchedule.jpg)

![scheduleDetail](.\UI\scheduleDetail.jpg)

#### 重要日

![addMatterDay](.\UI\addMatterDay.jpg)

![matterDayDetail](.\UI\matterDayDetail.jpg)

### 三、数据库结构

**schedule**：日程表

| 列名         | 字段类型 | 含义                |
| ------------ | -------- | ------------------- |
| title        | text     | 标题                |
| *place       | text     | *地点               |
| *isAllDay    | boolean  | *是否全天           |
| startTime    | DATETIME | 开始时间            |
| endTime      | DATETIME | 结束时间            |
| *repeatTime1 | int      | *重复时间 数字      |
| *repeatTime2 | int      | *重复频率 文字      |
| remindTime   | DATETIME | 提醒时间/多久前提醒 |
| *isImportant | boolean  | *是否重要提醒       |
| *supplement  | text     | *补充说明           |

**daysMatter**：重要日表（暂定倒数）

| 列名         | 类型     | 含义                 |
| ------------ | -------- | -------------------- |
| title        | text     | 标题                 |
| date         | DATETIME | 日期                 |
| *repeatTime1 | int      | *重复时间 数字       |
| *repeatTime2 | int      | *重复时间 文字       |
| *remindTime  | DATETIME | *提醒时间/多久前提醒 |
| *type        | int      | 正数/倒数            |
| *supplement  | text     | *补充说明            |

**task**：任务清单（暂定）
