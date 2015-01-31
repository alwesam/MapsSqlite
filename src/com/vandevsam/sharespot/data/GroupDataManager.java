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
import android.util.Log;

public class GroupDataManager {
	
	MySQLHelper dbhelper;
	SQLiteDatabase db;		
	String[] cols = { MySQLHelper.GROUP_NAME, MySQLHelper.GROUP_DESC, MySQLHelper.GROUP_TYPE, MySQLHelper.GROUP_STATUS};
	
	public GroupDataManager(Context c){		
		dbhelper = new MySQLHelper(c);		
	}
	
	public void open() throws SQLException {
		db = dbhelper.getWritableDatabase();
	}
	
	public void close(){
		db.close();
	}
	
	public void createGroup(MyGroupObj n) {
		ContentValues v = new ContentValues();		
		v.put(MySQLHelper.GROUP_NAME, n.getName());
		v.put(MySQLHelper.GROUP_DESC, n.getDescription());
		v.put(MySQLHelper.GROUP_TYPE, n.getType());
		v.put(MySQLHelper.GROUP_STATUS, n.getStatus()); //join status
		db.insert(MySQLHelper.GROUP_TABLE, null, v);			
	}
	
	public void clear(){
		db.delete(MySQLHelper.GROUP_TABLE, null, null);
	}
		

	public void deleteGroup(String groupName) {	    	    
	    db.delete(MySQLHelper.GROUP_TABLE, MySQLHelper.GROUP_NAME
	        + " = '" + groupName + "'", null);	
	  }	
	
	public String composegroupJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM groups where group_status = '"+"yes"+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();               
                map.put(MySQLHelper.GROUP_NAME, 
                		cursor.getString(cursor.getColumnIndex(MySQLHelper.GROUP_NAME)));                
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }
	
	public List<String> getAllGroups(){		
		List<String> groups = new ArrayList<String>();		
		Cursor cursor = db.query(MySQLHelper.GROUP_TABLE, cols, null, null, null, null, null);		
		cursor.moveToFirst();		
		while (!cursor.isAfterLast()){
			String g = cursor.getString(0);
			groups.add(g);
			cursor.moveToNext();
		}		
		return groups;
	}	
	
	public List<String> getJoinedGroups(){
		List<String> groups = new ArrayList<String>();	
		String selectQuery = "SELECT  * FROM groups where group_status = '"+"yes"+"' ";
		Cursor cursor = db.rawQuery(selectQuery, null);		
		cursor.moveToFirst();		
		while (!cursor.isAfterLast()){
			String g = cursor.getString(cursor.getColumnIndex(MySQLHelper.GROUP_NAME));
			groups.add(g);
			cursor.moveToNext();
		}		
		return groups;
	}
	
	public List<String> getDetails(String group){		
		List<String> details = new ArrayList<String>();		
		String selectQuery = "SELECT  * FROM groups where group_name = '"+group+"'";
    	Cursor cursor = db.rawQuery(selectQuery, null);  		
				
		if (cursor.moveToFirst()){
			details.add(cursor.getString(1)); //name	
			details.add(cursor.getString(2)); //description
			details.add(cursor.getString(3)); //type
		}		
		return details;
	}
		
	public boolean queryGroup(String group){
		//TODO change
		String selectQuery = "SELECT  * FROM groups where group_name = '"+group+"'";
    	Cursor cursor = db.rawQuery(selectQuery, null);    	
    	if (cursor.getCount()<=0)
    	   return false; //doesn't exist
    	else
    	   return true; //exists
	}
	
	public void updateStatus(String group, String status){    
        String updateQuery = "Update groups set group_status = '"
                                + status +"' where group_name="+"'"+ group +"'";
        Log.d("query",updateQuery);        
        db.execSQL(updateQuery);        
    }
	
	public boolean queryStatus(String group){
		//TODO change
		String selectQuery = "SELECT  * FROM groups where group_name = '"+group+"'";
    	Cursor cursor = db.rawQuery(selectQuery, null);  
    	//TODO make it dependent on searching the string instead of the the position
    	cursor.moveToFirst();	
    	if (cursor.getString(cursor.getColumnIndex(MySQLHelper.GROUP_STATUS)).equalsIgnoreCase("yes"))
    	   return true; //you're a member
    	else
    	   return false; //you're not a member
	}
	
	public boolean isPrivate(String group){
		//TODO change
		String selectQuery = "SELECT  * FROM groups where group_name = '"+group+"'";
	   	Cursor cursor = db.rawQuery(selectQuery, null);  
	   	//TODO make it dependent on searching the string instead of the the position
	   	cursor.moveToFirst();	
	   	if (cursor.getString(cursor.getColumnIndex(MySQLHelper.GROUP_TYPE)).equalsIgnoreCase("private"))
	   	   return true; //this group is private
	   	else
	   	   return false; //this group is not priavate
		
	}

}
