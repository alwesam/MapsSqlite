package com.vandevsam.shareloc.beta;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class JoinGroup {
	
	//TODO make this a global variable stored somewhere
	private static final String webServer = "108.59.82.39"; //my google CE ip address
    public boolean joined;
    
    public void setJoined(boolean joined){
    	this.joined = joined;
    }
    
    public boolean getJoined(){
    	return this.joined;
    }
	
	public JoinGroup(){
		
	}
	
	public void joinGroup(String member, String group){
  		
		   AsyncHttpClient client = new AsyncHttpClient();	       
	       // Http Request Params Object
	       RequestParams params = new RequestParams();
	       // Show ProgressBar	       
	       params.put("user", member);	  
	       params.put("group", group);
	       client.post("http://"+webServer+"/sqlitemysqlsyncMarkers/phplogin/join_group.php", 
	    		       params, 
	    		       new AsyncHttpResponseHandler() {
	               @Override
	               public void onSuccess(String response) {	                    	            	   
	            	   try {
						JSONObject jObject = new JSONObject(response);						
						 if (jObject.getBoolean("status")) {
							  //joined = true;
							  setJoined(true);
		                   } 
					    } catch (JSONException e) {					
						   e.printStackTrace();
					    }            
	               }
	               // When error occured
	               @Override
	               public void onFailure(int statusCode, Throwable error, String content) {	                   
	                   	                  
	                   if (statusCode == 404) {
	                       //use a utility function to display failutre of whatever
	                   } else if (statusCode == 500) {
	                       //ibid
	                   } else {
	                       //ibid
	                   }
	               }
	       });	
		
	}

}
