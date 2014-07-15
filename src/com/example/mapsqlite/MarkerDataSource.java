package com.example.mapsqlite;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MarkerDataSource {

	MySQLHelper dbhelper;
	SQLiteDatabase db;
	
	String[] cols = { MySQLHelper.TITLE, MySQLHelper.SNIPPET, MySQLHelper.POSITION };	
	
	public MarkerDataSource(Context c){
		
		dbhelper = new MySQLHelper(c);		
	}
	
	public void open() throws SQLException {
		db = dbhelper.getWritableDatabase();
	}
	
	public void close(){
		db.close();
	}
	
	public void addMarker(MyMarkerObj n) {
		ContentValues v = new ContentValues();
		
		v.put(MySQLHelper.TITLE, n.getTitle());
		v.put(MySQLHelper.SNIPPET, n.getSnippet());
		v.put(MySQLHelper.POSITION, n.getPosition());
		
		db.insert(MySQLHelper.TABLE_NAME, null, v);
		
	}
	
	public void deleteMarker(MyMarkerObj n) {
	    	    
	    db.delete(MySQLHelper.TABLE_NAME, MySQLHelper.POSITION
	        + " = '" + n.getPosition() + "'", null);
	  }
	
	//modify database based on one value
	//TODO finish it up
	public void modifyMarker(String var, String anchor1, String anchor2, String anchor3){
		
		deleteMarker( new MyMarkerObj(anchor1,anchor2, anchor3));  
    	//replace with new record
    	addMarker(new MyMarkerObj(anchor1, var, anchor3) );		
		
	}
	
	
	public List<MyMarkerObj> getMyMarkers(){
		
		List<MyMarkerObj> markers = new ArrayList<MyMarkerObj>();
		
		Cursor cursor = db.query(MySQLHelper.TABLE_NAME, cols, null, null, null, null, null);
		
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()){
			MyMarkerObj m = cursorToMarker(cursor);
			markers.add(m);
			cursor.moveToNext();
		}
		
		return markers;
	}

	private MyMarkerObj cursorToMarker(Cursor cursor) {
		MyMarkerObj m = new MyMarkerObj();
		m.setTitle(cursor.getString(0));
		m.setSnippet(cursor.getString(1));
		m.setPosition(cursor.getString(2));
		return m;
	}
	
}
