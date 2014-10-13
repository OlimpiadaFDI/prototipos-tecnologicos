package com.olimpiadafdi.multiplica.java;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

public class DBHandler {
	private DBHelper db;

	public DBHandler(Context context) {
		db = new DBHelper(context);
	}

	public void clearItems() {
		db.getWritableDatabase().execSQL("DELETE FROM items");
	}

	public void writeItems(ArrayList<Item> items) {
		String url;
		String text;
		StringBuilder query;
		for (int i = 0; i < items.size(); i++) {
			url = DatabaseUtils.sqlEscapeString(items.get(i).getUrl());
			text = DatabaseUtils.sqlEscapeString(items.get(i).getText());

			query = new StringBuilder();
			query.append("INSERT INTO items (url, text) VALUES (")
					.append(url).append(", ").append(text).append(")");
			Log.d("test", query.toString());
			try {
				db.getWritableDatabase().execSQL(query.toString());
			} finally {
				db.close();
			}
		}
	}

	public ArrayList<Item> readItems() {
		Cursor result = db.getWritableDatabase().rawQuery(
				"SELECT * FROM items", null);

		ArrayList<Item> items = new ArrayList<Item>();

		while (result.moveToNext()) {
			Item item = new Item();

			item.setUrl(result.getString(result.getColumnIndex("url")));
			item.setText(result.getString(result.getColumnIndex("text")));
			Log.d("test", "[" + item.getUrl() + "] " + item.getText()
					+ "]");

			items.add(item);
		}

		db.close();

		return items;
		
	}
}
