package com.example.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper{

	public DbOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String cmd="CREATE TABLE IF NOT EXISTS article" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT,Title TEXT,Intro TEXT,Picurl TEXT,Href TEXT)";
		db.execSQL(cmd);  
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE score ADD COLUMN other STRING");
	}

}
