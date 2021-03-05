package pl.michalikjan.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "forecast";
    private static final int DB_VER = 1;
    private static final String DB_TABLE = "Task";
    public static final String DB_COLUMN = "TaskName";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format(
                "CREATE TABLE %s " +
                        "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT NOT NULL, " +
                        "DAY TEXT NOT NULL, " +
                        "CITY TEXT NOT NULL, " +
                        "WEATHER TEXT NOT NULL, " +
                        "TEMP TEXT NOT NULL, " +
                        "PRE TEXT NOT NULL, " +
                        "WIND TEXT NOT NULL);",
                DB_TABLE, DB_COLUMN);
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE);
        db.execSQL(query);
        onCreate(db);
    }

    public void insertNewTask(String task, String day, String city, String weather, String temp, String pre, String wind) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN, task);
        values.put("DAY", day);
        values.put("CITY", city);
        values.put("WEATHER", weather);
        values.put("TEMP", temp);
        values.put("PRE", pre);
        values.put("WIND", wind);
        db.insertWithOnConflict(DB_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public String getOldWeather(String day, String city) {
        String result = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Task WHERE DAY=" + day + " AND CITY=" + city, null);
        if (cursor.moveToLast()) {
            result += cursor.getString(cursor.getColumnIndex("WEATHER"));
            result += cursor.getString(cursor.getColumnIndex("TEMP"));
            result += cursor.getString(cursor.getColumnIndex("PRE"));
            result += cursor.getString(cursor.getColumnIndex("WIND"));
        }
        cursor.close();
        db.close();
        return result;
    }
}