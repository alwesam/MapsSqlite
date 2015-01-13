package com.vandevsam.shareloc.beta.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class GroupDataManager {
	
	MySQLHelper dbhelper;
	SQLiteDatabase db;		
	String[] cols = { MySQLHelper.GROUP_NAME, MySQLHelper.GROUP_TYPE};
	
	public GroupDataManager(Context c){		
		dbhelper = new MySQLHelper(c);		
	}
	
	public void open() throws SQLException {
		db = dbhelper.getWritableDatabase();
	}
	
	public void close(){
		db.close();
	}
	
	public void createGroup(String groupName) {
		ContentValues v = new ContentValues();		
		v.put(MySQLHelper.GROUP_NAME, groupName);
		//TODO for now, it's only open
		v.put(MySQLHelper.GROUP_TYPE, "open");
		db.insert(MySQLHelper.GROUP_TABLE, null, v);		
	}
	
	public void deleteGroup(String groupName) {	    	    
	    db.delete(MySQLHelper.GROUP_TABLE, MySQLHelper.GROUP_NAME
	        + " = '" + groupName + "'", null);
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
	
	public boolean queryGroup(String group){
		//TODO change
		String selectQuery = "SELECT  * FROM groups where group_name = '"+group+"'";
    	Cursor cursor = db.rawQuery(selectQuery, null);    	
    	if (cursor.getCount()<=0)
    	   return false; //doesn't exist
    	else
    	   return true; //exists
	}

}
