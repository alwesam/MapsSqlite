package com.vandevsam.shareloc.beta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.ProgressDialog;

public class RemoteConn {
	
	RemoteConn(){
		//Initialize Progress Dialog properties
        //prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
        prgDialog.setCancelable(false);
      	
	}
	
	ProgressDialog prgDialog;
	private static final String webServer = "192.168.0.11"; //localhost
	MarkerDataSource data;
	
	 // Method to Sync MySQL to SQLite DB
    public void syncMySQLDBSQLite() {
	       // Create AsycHttpClient object
	       AsyncHttpClient client = new AsyncHttpClient();
	       // Http Request Params Object
	       RequestParams params = new RequestParams();
	       // Show ProgressBar
	       prgDialog.show();
	       // Make Http call to getusers.php
	       client.post("http://"+webServer+"/sqlitemysqlsyncMarkers/getlocations.php", params, new AsyncHttpResponseHandler() {
	               @Override
	               public void onSuccess(String response) {
	                   // Hide ProgressBar
	                   prgDialog.hide();
	                   // Update SQLite DB with response sent by getusers.php
	                   //updateSQLite(response);
	                   try {
	                       // Extract JSON array from the response
	                       JSONArray arr = new JSONArray(response);
	                       System.out.println(arr.length());
	                       // If no of array elements is not zero
	                       if(arr.length() != 0){
	                           // Loop through each array element, get JSON object which id,title,snippet,position
	                           for (int i = 0; i < arr.length(); i++) {
	                               // Get JSON object
	                               JSONObject obj = (JSONObject) arr.get(i);
	                               System.out.println(obj.get("title"));
	                               System.out.println(obj.get("snippet"));
	                               System.out.println(obj.get("position"));
	                               // Insert User into SQLite DB
	                               //double check if data already exists in database
	                               if (!data.queryPosition(obj.get("position").toString()) 
	                            	        && !data.queryAddress(obj.get("title").toString()))                            	   
	                                    data.addMarker(new MyMarkerObj(obj.get("title").toString(),
	                            		                               obj.get("snippet").toString(),
	                            		                               obj.get("position").toString()));
	                               else
	                            	   System.out.println("already there");   
	                               
	                               //return obj.get("title").toString();
	                           }             
	                           // Reload the Main Activity
	                           //reloadActivity();
	                       }
	                   } catch (JSONException e) {
	                       // TODO Auto-generated catch block
	                       e.printStackTrace();
	                   }
	               }
	               // When error occured
	               @Override
	               public void onFailure(int statusCode, Throwable error, String content) {
	                   // TODO Auto-generated method stub
	                   // Hide ProgressBar
	                   prgDialog.hide();
	                   if (statusCode == 404) {
	                      // Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
	                   } else if (statusCode == 500) {
	                     //  Toast.makeText(getApplicationContext(), "Something went terrible at server end", Toast.LENGTH_LONG).show();
	                   } else {
	                     //  Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
	                     //          Toast.LENGTH_LONG).show();
	                   }
	               }
	       });
	       
   }

	  

}
