package com.example.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DbManager {
	private SQLiteDatabase database;
	private DbOpenHelper helper;
	public static final String PACKAGE_NAME = "com.example.net";
	public static final String DB_NAME = "article.db";
	public static final String DB_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME;
	
	public DbManager(Context context) {
		helper=new DbOpenHelper(context, DB_NAME, null, 1);
		database=helper.getWritableDatabase();
	}
	
	public void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }
	
	public void add(List<Article> articles) {  
        database.beginTransaction();
        try {  
            for (Article article : articles) {  
                database.execSQL("INSERT INTO article VALUES(NULL,?,?,?,?)", 
                		new Object[]{article.title,article.intro,article.picurl,article.href});  
            }
            database.setTransactionSuccessful();
        } finally {  
            database.endTransaction();
        }  
    }  
	
	 public void updateArticle(Article article,int id) {  
	        ContentValues cv = new ContentValues();
	        cv.put("Title", article.title);
	        cv.put("Intro", article.intro);
	        cv.put("Picurl", article.picurl);
	        cv.put("Href", article.href);
	        database.update("article", cv, "_id=?", new String[]{String.valueOf(id)});
	    }
	 
	 /*public void deleteOldScore(score score) {  
	        database.delete("score", "id >= ?", new String[]{String.valueOf(score.id)});  
	    }*/
	 
	private SQLiteDatabase openDatabase(String dbfile) {
		if (!(new File(dbfile).exists())) {
			return null;
		}
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,
		        null);
		return db;
	}
	
	public ArrayList<Article> query() {
		ArrayList<Article> data=new ArrayList<Article>();
		Cursor cursor=queryTheCursor();
		while (cursor.moveToNext()) {
			Article mArticle=new Article();
			mArticle.title=cursor.getString(cursor.getColumnIndex("Title"));
			mArticle.intro=cursor.getString(cursor.getColumnIndex("Intro"));
			mArticle.picurl=cursor.getString(cursor.getColumnIndex("Picurl"));
			mArticle.href=cursor.getString(cursor.getColumnIndex("Href"));
			data.add(mArticle);
		}
		return data;
	}
	
	/*public ArrayList<score> query(String Zterm,String xh) {
		ArrayList<score> data=new ArrayList<score>();
		String cmd=String.format("SELECT * FROM SCORE where Term = '%s' and xh = '%s'", Zterm,xh);
		Log.i("cmd", cmd);
		Cursor cursor=database.rawQuery(cmd, null);
		while (cursor.moveToNext()) {
			score mScore=new score();
			mScore.xh=cursor.getString(cursor.getColumnIndex("xh"));
			mScore.lessonId=cursor.getString(cursor.getColumnIndex("LessonID"));
			mScore.lesson=cursor.getString(cursor.getColumnIndex("Lesson"));
			mScore.teacher=cursor.getString(cursor.getColumnIndex("Teacher"));
			mScore.myScore=cursor.getString(cursor.getColumnIndex("myScore"));
			mScore.sumScore=cursor.getString(cursor.getColumnIndex("sumScore"));
			mScore.realScore=cursor.getString(cursor.getColumnIndex("realScore"));
			mScore.eveScore=cursor.getString(cursor.getColumnIndex("eveScore"));
			mScore.reScore=cursor.getString(cursor.getColumnIndex("reScore"));
			data.add(mScore);
		}
		return data;
	}*/
	
	public ArrayList<String> queryLessonID() {
		ArrayList<String> LessonsID=new ArrayList<String>();
		Cursor cursor=database.rawQuery("SELECT LessonID FROM SCORE", null);
		while (cursor.moveToNext()) {
			String LessonID=cursor.getString(cursor.getColumnIndex("LessonID"));
			LessonsID.add(LessonID);
		}
		return LessonsID;
	}
	
	public ArrayList<String> queryXH() {
		ArrayList<String> XHs=new ArrayList<String>();
		Cursor cursor=database.rawQuery("SELECT xh FROM SCORE", null);
		while (cursor.moveToNext()) {
			String xh=cursor.getString(cursor.getColumnIndex("xh"));
			XHs.add(xh);
		}
		return XHs;
	}
	
	public boolean queryTerm(String Zterm){
		Cursor cursor=queryTheCursor();
		String term = null;
		while (cursor.moveToNext()) {
			term=term+cursor.getString(cursor.getColumnIndex("Term"));
		}
		if (term!=null) {
			if (!term.contains(Zterm)) {
				Log.i("getwebInfo",Zterm);
				return true;
			}else {
				Log.i("",Zterm);
				return false;
			}
		}
		return true;
	}
	
	public Cursor queryTheCursor() {  
        Cursor c = database.rawQuery("SELECT * FROM article", null);  
        return c;
    }
	
	public void closeDatabase() {
        this.database.close();
    }
}
