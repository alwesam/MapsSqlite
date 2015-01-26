package com.vandevsam.sharespot.data;


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
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class MarkerDataManager {

	MySQLHelper dbhelper;
	SQLiteDatabase db;	
	String[] cols = { MySQLHelper.TITLE, MySQLHelper.SNIPPET, MySQLHelper.POSITION, MySQLHelper.GROUPS, MySQLHelper.STATUS};	
		
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
		v.put(MySQLHelper.GROUPS, n.getGroup());
		v.put(MySQLHelper.STATUS, n.getStatus());		
		db.insert(MySQLHelper.MARKER_TABLE, null, v);
	}
		
		
	public void deleteMarker(MyMarkerObj n) {	    	    
	    db.delete(MySQLHelper.MARKER_TABLE, MySQLHelper.POSITION
	        + " = '" + n.getPosition() + "'", null);
	 }	
	
	public void clear(){
		db.delete(MySQLHelper.MARKER_TABLE, null, null);
	}
	
	//TODO quick fix, visit later
	public void deleteMarkerGroup(String group) {	    	    
	    db.delete(MySQLHelper.MARKER_TABLE, MySQLHelper.GROUPS
	        + " = '" + group + "'", null);
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
	
    private String buildQuery(List<String> col_string){
    	
    	String[] sel = new String[col_string.size()];		
		for (int i=0; i<col_string.size(); i++)
			sel[i] = "SELECT * FROM locations where groups = '"+col_string.get(i)+"'";			
		SQLiteQueryBuilder stringBuild = new SQLiteQueryBuilder();
		    	
    	return stringBuild.buildUnionQuery(sel,null,null);
    }
	
	public List<MyMarkerObj> getSelMarkers(List<String> selGroups){	
		
		String selectQuery = buildQuery(selGroups); 
        
		List<MyMarkerObj> markers = new ArrayList<MyMarkerObj>();
		Cursor cursor = db.rawQuery(selectQuery, null);		
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
     
     private MyMarkerObj cursorToMarker(Cursor cursor){
    	MyMarkerObj m = new MyMarkerObj();
 		m.setTitle(cursor.getString(cursor.getColumnIndex(MySQLHelper.TITLE)));
 		m.setSnippet(cursor.getString(cursor.getColumnIndex(MySQLHelper.SNIPPET)));
 		m.setPosition(cursor.getString(cursor.getColumnIndex(MySQLHelper.POSITION)));
 		m.setGroup(cursor.getString(cursor.getColumnIndex(MySQLHelper.GROUPS))); //add a new mark
 		m.setStatus(cursor.getString(cursor.getColumnIndex(MySQLHelper.STATUS)));
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
                //TODO deal with this exception
                map.put(MySQLHelper.COLUMN_ID, 
                		cursor.getString(cursor.getColumnIndex(MySQLHelper.COLUMN_ID)));
                map.put(MySQLHelper.TITLE, 
                		cursor.getString(cursor.getColumnIndex(MySQLHelper.TITLE)));
                map.put(MySQLHelper.SNIPPET, 
                		cursor.getString(cursor.getColumnIndex(MySQLHelper.SNIPPET)));
                map.put(MySQLHelper.POSITION, 
                		cursor.getString(cursor.getColumnIndex(MySQLHelper.POSITION)));
                map.put(MySQLHelper.GROUPS, 
                		cursor.getString(cursor.getColumnIndex(MySQLHelper.GROUPS)));
                //map.put(MySQLHelper.STATUS, cursor.getString(5));
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
    
    public List<String> getMarkerDetails(String pos){
    	List<String> details = new ArrayList<String>();    	
    	String selectQuery = "SELECT  * FROM locations where position = '"+pos+"'";
    	Cursor cursor = db.rawQuery(selectQuery, null);   
    	if (cursor.moveToFirst()) {
    		details.add(cursor.getString(cursor.getColumnIndex(MySQLHelper.TITLE)));
    		details.add(cursor.getString(cursor.getColumnIndex(MySQLHelper.SNIPPET)));
    		details.add(cursor.getString(cursor.getColumnIndex(MySQLHelper.POSITION)));
    		details.add(cursor.getString(cursor.getColumnIndex(MySQLHelper.GROUPS)));
    	}
    	
    	return details;
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
        return cursor.getString(cursor.getColumnIndex(MySQLHelper.POSITION));
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
            	addresses.add(cursor.getString(cursor.getColumnIndex(MySQLHelper.SNIPPET)));
            } while (cursor.moveToNext());
        }
		
		return addresses;
     }      
    
    public ArrayList<String> getSelAddresses (List<String> selGroups) {     	
    	
		String selectQuery = buildQuery(selGroups);   	    	
    	
    	ArrayList<String> addresses = new ArrayList<String>();
    	//TODO temp solution
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	
		if (cursor.moveToFirst()) {
            do {            	            	
            	addresses.add(cursor.getString(cursor.getColumnIndex(MySQLHelper.SNIPPET)));
            } while (cursor.moveToNext());
        }
		
		return addresses;
     }   

}
