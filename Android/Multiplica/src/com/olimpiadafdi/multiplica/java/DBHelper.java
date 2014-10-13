package com.olimpiadafdi.multiplica.java;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	private static final String DB_NAME = "items.db";
	private static final int DB_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE items ( "
					+ "id	INTEGER PRIMARY KEY, " 
					+ "url	TEXT NOT NULL, "
					+ "text	TEXT NOT NULL)");
		} catch (Exception e) {
			Log.d("tag", "Error creating DB");
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS items");
		onCreate(db);
	}
}
