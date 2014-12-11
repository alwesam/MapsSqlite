package com.vandevsam.shareloc.beta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLHelper extends SQLiteOpenHelper {
	
	public static final String MARKER_TABLE = "locations";		
	public static final String COLUMN_ID = "id";
	public static final String TITLE = "title";
	public static final String SNIPPET = "snippet";
	public static final String POSITION = "position";
	public static final String STATUS = "updateStatus";
	
	public static final String GROUP_TABLE = "groups";		
	public static final String GROUP_ID = "group_id";
	public static final String GROUP_NAME = "group_name";
	public static final String GROUP_TYPE = "group_type";

	private static final String DATABASE_NAME = "markerlocations.db";
	private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	private static final String MARKERS_TABLE_CREATE = 
		  "create table " + MARKER_TABLE + "("
	      + COLUMN_ID     + " integer primary key autoincrement, " 
	      + TITLE + " text, "
	      + SNIPPET + " text, "
	      + POSITION + " text, "
	      + STATUS + " text );";
	
	private static final String GROUPS_TABLE_CREATE = 
			  "create table " + GROUP_TABLE + "("
		      + GROUP_ID     + " integer primary key autoincrement, " 
		      + GROUP_NAME + " text, "
		      + GROUP_TYPE + " text );";

	  public MySQLHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	@Override
	public void onCreate(SQLiteDatabase db) {		
		db.execSQL(MARKERS_TABLE_CREATE);
		db.execSQL(GROUPS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
		 Log.w(MySQLHelper.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS" + MARKER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS" + GROUP_TABLE);
		onCreate(db);
	}

}
