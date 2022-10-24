package com.crash.monitor.lib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite访问工具类
 * Created by jale on 14-7-2.
 */
public class DBHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "mrd_security.db";
    public final static int DB_VERSION = 1;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table kv(_id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT NOT NULL,val TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
