package com.crash.monitor.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作工具类 Created by jale on 14-7-2.
 */
public class DBManager {

	private SQLiteDatabase db;

	public DBManager(Context context) {
		DBHelper helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	/**
	 * 存储数据
	 *
	 * @param key
	 *            键
	 * @param val
	 *            值
	 */
	public void add(final String key, final String val) {
		if (query(key) != null) {
			updateVal(key, val);
		} else {
			db.execSQL("INSERT INTO kv VALUES(null, ?, ?)", new Object[] { key, val });
		}
	}

	/**
	 * 更新数据
	 *
	 * @param key
	 *            键
	 * @param val
	 *            值
	 */
	public void updateVal(final String key, final String val) {
		try{
			ContentValues cv = new ContentValues();
			cv.put("val", val);
			db.update("kv", cv, "key = ?", new String[] { key });
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 删除数据
	 *
	 * @param key
	 *            键
	 */
	public void deleteKey(final String key) {
		db.delete("kv", "key = ?", new String[] { key });
	}

	/**
	 * 查询
	 *
	 * @param key
	 *            键
	 * @return val
	 */
	public String query(final String key) {
		String val = "";
		Cursor c = null;
		try{
			c = db.rawQuery("SELECT * FROM kv where key = ?", new String[] { key });
			while (c.moveToNext() && val == null) {
				val = c.getString(c.getColumnIndex("val"));
			}
			c.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c != null && !c.isClosed()){
				c.close();
			}
		}
		
		return val;
	}

	/**
	 * close database
	 */
	public void closeDB() {
		db.close();
	}

}
