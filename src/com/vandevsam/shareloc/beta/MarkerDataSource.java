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
		v.put(MySQLHelper.STATUS, "no");
		//v.put(MySQLHelper.DEL_STATUS, "no");
		db.insert(MySQLHelper.TABLE_NAME, null, v);		
	}
	
	public void deleteMarker(MyMarkerObj n) {
	    	    
	    db.delete(MySQLHelper.TABLE_NAME, MySQLHelper.POSITION
	        + " = '" + n.getPosition() + "'", null);
	  }	
	
	public List<MyMarkerObj> getAllMarkers(){
		
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
	
	/**
	 * 
	 * @param pos
	 * @return
	 */
     public MyMarkerObj getSelectMarker(String pos){
		
    	 MyMarkerObj marker = new MyMarkerObj();		
    	 String selectQuery = "SELECT  * FROM locations where position = '"+pos+"'";
     	 Cursor cursor = db.rawQuery(selectQuery, null);
     	 cursor.moveToFirst();
    		    //markers = cursorToMarker(cursor);
     	 //TODO quick n dirty fix
     	marker.setTitle(cursor.getString(1));
		marker.setSnippet(cursor.getString(2));
		marker.setPosition(cursor.getString(3));
		 
		 return marker;
	 }

	private MyMarkerObj cursorToMarker(Cursor cursor) {
		MyMarkerObj m = new MyMarkerObj();
		m.setTitle(cursor.getString(0));
		m.setSnippet(cursor.getString(1));
		m.setPosition(cursor.getString(2));
		return m;
	}
	
	/**
     * Compose JSON out of SQLite records
     * @return
     */
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
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
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM locations where udpateStatus = '"+"no"+"'";        
        Cursor cursor = db.rawQuery(selectQuery, null);
        count = cursor.getCount();        
        return count;
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
    
    //TODO update comments
    public void updateComments(String id, String comment){
    	
    	String updateQuery = "Update locations set snippet = '"
                + comment +"' where id="+"'"+ id +"'";
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
    	String selectQuery = "SELECT  * FROM locations where title = '"+address+"'";
    	Cursor cursor = db.rawQuery(selectQuery, null);    	
    	if (cursor.getCount()<=0)
    	   return false; //doesn't exist
    	else
    	   return true; //exists
    }
    
    public ArrayList<String> getAddresses (String query) { 
    	
    	ArrayList<String> addresses = new ArrayList<String>();
    	String selectQuery = " SELECT  * FROM locations WHERE title LIKE '%"+query+"%' ";
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	
    	/*if (cursor.getCount()<=0){
    		addresses.add("No Address Found");
    		return addresses;
    	}*/
    	
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
