package com.example.mapsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_NAME = "locations";	
	
	public static final String COLUMN_ID = "_id";
	public static final String TITLE = "title";
	public static final String SNIPPET = "snippet";
	public static final String POSITION = "position";

	private static final String DATABASE_NAME = "markerlocations.db";
	private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	private static final String DATABASE_CREATE = 
		  "create table " + TABLE_NAME + "("
	      + COLUMN_ID     + " integer primary key autoincrement, " 
	      + TITLE + " text, "
	      + SNIPPET + " text, "
	      + POSITION + " text);";
	

	  public MySQLHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	@Override
	public void onCreate(SQLiteDatabase db) {		
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
		 Log.w(MySQLHelper.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
		onCreate(db);
	}

}
