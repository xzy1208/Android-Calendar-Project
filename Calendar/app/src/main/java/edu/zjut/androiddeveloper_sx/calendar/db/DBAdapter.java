package edu.zjut.androiddeveloper_sx.calendar.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import edu.zjut.androiddeveloper_sx.calendar.bean.BigDay;
import edu.zjut.androiddeveloper_sx.calendar.bean.Schedule;


/**
 * Created by Gkuma on 2022/5/24.
 */

public class DBAdapter {
    private int DB_VERSION = 1;
    private static final String DB_NAME = "calendar.db";



    private static DBAdapter DBAdapter =null;
    private SQLiteDatabase db;
    private final Context context;
    private DBAdapter.DBOpenHelper dbOpenHelper;

    private  class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        /*private static final String UPDATE_TIME_TRIGGER = "CREATE TRIGGER update_time_trigger" +
                        " AFTER UPDATE ON " + DB_TABLE + " FOR EACH ROW" +
                        " BEGIN " +
                        "UPDATE " + DB_TABLE +
                        " SET " + KEY_TIME + " = (datetime('now','localtime'))" +
                        " WHERE " + KEY_ID + " = old." + KEY_ID + ";" +
                        " END";*/
        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(ScheduleTable.CREATE_TABLE_SCHEDULE );
            _db.execSQL(BigDayTable.CREATE_TABLE_BIGDAY );


        }
        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + ScheduleTable.CREATE_TABLE_SCHEDULE+ BigDayTable.CREATE_TABLE_BIGDAY);

            onCreate(_db);
        }
    }


    public DBAdapter(Context _context) {
        context = _context;
    }
    //单例模式
    public static synchronized DBAdapter setDBAdapter(Context context){
        if(DBAdapter==null){
            DBAdapter=new DBAdapter(context);
        }
        return DBAdapter;
    }
    public void open() throws SQLiteException {
        dbOpenHelper = new DBAdapter.DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        }catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }
    public void close() {
        if (db != null){
            db.close();
            db = null;
        }
    }

    /*
        Schedule表方法,待补充
    *
    * */
    public long insertSchedule(Schedule s) {
        return db.insert(ScheduleTable.TABLE, null, ScheduleTable.CovertToContentValues(s));
    }

    public long deleteAllDataFromSchedule() {
        return db.delete(ScheduleTable.TABLE, null, null);
    }

    public long deleteOneDataFromSchedule(int id) {
        return db.delete(ScheduleTable.TABLE,  ScheduleTable.KEY_ID + "=" + id, null);
    }

    public long updateOneDataFromSchedule(int id , Schedule s){
        ContentValues updateValues = ScheduleTable.CovertToContentValues(s);
        return db.update(ScheduleTable.TABLE, updateValues, ScheduleTable.KEY_ID + "=" + id, null);
    }

    public List<Schedule> getOneDataFromSchedule(int id) {
        Cursor results =  db.query(ScheduleTable.TABLE, ScheduleTable.getAllColumnIndex(), ScheduleTable.KEY_ID + "=" + id, null, null, null, null);
        return ScheduleTable.ConvertToSchedule(results);
    }
    public List<Schedule> getAllDataFromSchedule() {
        Cursor results = db.query(ScheduleTable.TABLE, ScheduleTable.getAllColumnIndex(), null, null, null, null, null);
        return ScheduleTable.ConvertToSchedule(results);
    }

    /*
        BigDay表方法,待补充
    *
    * */
    public long insertBigDay(BigDay b) {
        return db.insert(BigDayTable.TABLE, null, BigDayTable.CovertToContentValues(b));
    }

    public long deleteAllDataFromBigDay() {
        return db.delete(BigDayTable.TABLE, null, null);
    }

    public long deleteOneDataFromBigDay(int id) {
        return db.delete(BigDayTable.TABLE,  BigDayTable.KEY_ID + "=" + id, null);
    }

    public long updateOneDataFromBigDay(int id , BigDay b){
        ContentValues updateValues = BigDayTable.CovertToContentValues(b);
        return db.update(BigDayTable.TABLE, updateValues, BigDayTable.KEY_ID + "=" + id, null);
    }

    public List<BigDay> getOneDataFromBigDay(int id) {
        Cursor results =  db.query(BigDayTable.TABLE, BigDayTable.getAllColumnIndex(), BigDayTable.KEY_ID + "=" + id, null, null, null, null);
        return BigDayTable.ConvertToBigDay(results);
    }
    public List<BigDay> getAllDataFromBigDay() {
        Cursor results = db.query(BigDayTable.TABLE, BigDayTable.getAllColumnIndex(), null, null, null, null, null);
        return BigDayTable.ConvertToBigDay(results);
    }

}