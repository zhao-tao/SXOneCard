package com.sxonecard.background;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HeQiang on 2017/10/27.
 */

public class OrderDb {
    private static String table = "create table t_order(id integer primary key autoincrement," +
            "name varchar)";
    private static DbHelper dbHelper;

    public static void init(Context context) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context, "cf_order", null, 1);
        }
    }

    public static long insert(String order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long res = -1;
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", order);
        try {
            res = db.insert("t_order", null, contentValues);
        } catch (Exception e) {

        }
        return res;
    }

    public static long delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long res = -1;
        try {
            res = db.delete("t_order", "id=" + id, null);
        } catch (Exception e) {

        }
        return res;
    }

    public static Map<Long, String> find() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("t_order", null, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        Map<Long, String> orders = new HashMap<>(2);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            orders.put(id, name);
        }
        return orders;
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(table);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
