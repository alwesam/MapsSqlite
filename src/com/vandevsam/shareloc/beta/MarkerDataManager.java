package com.vandevsam.shareloc.beta;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MarkerDataManager {

	MySQLHelper dbhelper;
	SQLiteDatabase db;	
	String[] cols = { MySQLHelper.TITLE, MySQLHelper.SNIPPET, MySQLHelper.POSITION, MySQLHelper.STATUS};	
	
	public MarkerDataManager(Context c){		
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
		v.put(MySQLHelper.STATUS, n.getStatus());
		db.insert(MySQLHelper.MARKER_TABLE, null, v);		
	}
	
	public void deleteMarker(MyMarkerObj n) {	    	    
	    db.delete(MySQLHelper.MARKER_TABLE, MySQLHelper.POSITION
	        + " = '" + n.getPosition() + "'", null);
	  }	
	
	public List<MyMarkerObj> getAllMarkers(){		
		List<MyMarkerObj> markers = new ArrayList<MyMarkerObj>();		
		Cursor cursor = db.query(MySQLHelper.MARKER_TABLE, cols, null, null, null, null, null);		
		cursor.moveToFirst();		
		while (!cursor.isAfterLast()){
			MyMarkerObj m = cursorToMarker(cursor);
			markers.add(m);
			cursor.moveToNext();
		}		
		return markers;
	}	

     public MyMarkerObj getSelectMarker(String pos){
     	Cursor cursor = db.query(MySQLHelper.MARKER_TABLE, 
     			                  cols, 
     			                 "position = '"+pos+"'", 
     			                  null, null, null, null);
     	cursor.moveToFirst();
		return cursorToMarker(cursor);
	 }

	private MyMarkerObj cursorToMarker(Cursor cursor) {
		MyMarkerObj m = new MyMarkerObj();
		m.setTitle(cursor.getString(0));
		m.setSnippet(cursor.getString(1));
		m.setPosition(cursor.getString(2));
		m.setStatus(cursor.getString(3));
		return m;
	}
	
	/**
     * Compose JSON out of SQLite records that are not
     * synced with remote db
     * @return JSON String
     */
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        //only upload markers which haven't been synced to remote db yet
        String selectQuery = "SELECT  * FROM locations where updateStatus = '"+"no"+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(MySQLHelper.COLUMN_ID, cursor.getString(0));
                map.put(MySQLHelper.TITLE, cursor.getString(1));
                map.put(MySQLHelper.SNIPPET, cursor.getString(2));
                map.put(MySQLHelper.POSITION, cursor.getString(3));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }
    
    /**
     * Update Sync status against each User ID
     * @param id
     * @param status
     */
    public void updateSyncStatus(String id, String status){    
        String updateQuery = "Update locations set updateStatus = '"
                                + status +"' where id="+"'"+ id +"'";
        Log.d("query",updateQuery);        
        db.execSQL(updateQuery);        
    }
    
    /**
     * a function to query if coordinates are in the database
     * @param pos
     * @return boolean
     */
    public boolean queryPosition (String pos) {    	
    	String selectQuery = "SELECT  * FROM locations where position = '"+pos+"'";
    	Cursor cursor = db.rawQuery(selectQuery, null);    	
    	if (cursor.getCount()<=0)
    	   return false; //doesn't exist
    	else
    	   return true; //exists
    }
    
    public boolean queryAddress (String address) {    	
    	String selectQuery = "SELECT  * FROM locations where snippet = '"+address+"'";
    	Cursor cursor = db.rawQuery(selectQuery, null);    	
    	if (cursor.getCount()<=0)
    	   return false; //doesn't exist
    	else
    	   return true; //exists
    }
    
    public String getPosition (String address) {    	
    	String selectQuery = "SELECT  * FROM locations where snippet = '"+address+"'";
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	cursor.moveToFirst();
        return cursor.getString(3);     	
    }
    
    public ArrayList<String> getAddresses (String query) {     	
    	ArrayList<String> addresses = new ArrayList<String>();
    	//TODO temp solution
    	Cursor cursor;
    	if (query == "ALL")    		
    		cursor = db.rawQuery("SELECT * FROM locations", null);     
    	else 
	        cursor = db.rawQuery("SELECT * FROM locations WHERE snippet LIKE '%"+query+"%'", null); 
		if (cursor.moveToFirst()) {
            do {            	            	
            	addresses.add(cursor.getString(2));
            } while (cursor.moveToNext());
        } else
        {
        	addresses.add("No Address Found");
    		return addresses;
        }		
		return addresses;
     }   

}
