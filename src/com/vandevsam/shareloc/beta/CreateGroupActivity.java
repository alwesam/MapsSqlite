package com.vandevsam.shareloc.beta;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateGroupActivity extends Activity {

	Context context = this;
	GroupDataManager data;
	private EditText groupName;
	private EditText groupDescription;
	//SessionManager session;
	ProgressDialog prgDialog;	
	private static final String webServer = "108.59.82.39"; //my google CE ip address
	
	private boolean created;
	
	SessionManager session;
	HashMap<String, String> user;
	String creator;
	String group;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup); 
        
        session = new SessionManager(getApplicationContext());	
        // get user data from session
        user = session.getUserDetails();
        //this guy, the creator will be added to the group
        creator = user.get(SessionManager.KEY_NAME);
        
        data = new GroupDataManager(context);
        
        try {
           data.open();
        } catch (Exception e){
			Log.i("hello", "hello");
		} 
        
        groupName = (EditText) findViewById(R.id.GroupName); 
        groupDescription = (EditText) findViewById(R.id.GroupDesc);        
        
        //create a spinner item
        Spinner spinner = (Spinner) findViewById(R.id.group_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
             R.array.group_array_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);   
        
      //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(context);
        prgDialog.setMessage("Synching with Remote MySQL DB. Please wait...");
        prgDialog.setCancelable(false);
                
	}	
	
	public void addNewGroup(View view){		
		group = groupName.getText().toString();
		data.createGroup(group);
		data.close();
		submitInfo();		
		/*if (created) {
			JoinGroup newMember = new JoinGroup();
			//group's creator will be its first memeber
			newMember.joinGroup(creator,group);
			if(newMember.getJoined())
			   Toast.makeText(getApplicationContext(), 
				 "Successfully joined "+creator+" as first member of "+group, 
               	 Toast.LENGTH_LONG).show();				
		}*/
		//go back home
		this.callHomeActivity(view);
	}	
	 
    public void cancelAddGroup(View view) {
        this.callHomeActivity(view);
    }
    
    public void callHomeActivity(View view) {
        Intent objIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(objIntent);
    } 
    
    //also add user
    
    
  //add a method to connect with login db
  	public void submitInfo(){					 
  		   
  		   String name =  groupName.getText().toString();  	  
  		   String description =  groupDescription.getText().toString();  
  		   //boolean created;
  	    // Create AsycHttpClient object
  	       AsyncHttpClient client = new AsyncHttpClient();	       
  	       // Http Request Params Object
  	       RequestParams params = new RequestParams();
  	       // Show ProgressBar
  	       prgDialog.show();
  	       params.put("name", name);
  	       params.put("description", description);
  	       //TODO fix up later
  	       params.put("type", "open");
  	       client.post("http://"+webServer+"/sqlitemysqlsyncMarkers/phplogin/create_group.php", 
  	    		       params, 
  	    		       new AsyncHttpResponseHandler() {
  	               @Override
  	               public void onSuccess(String response) {
  	                   // Hide ProgressBar
  	            	   prgDialog.hide();  	            	   
  	            	   try {
  						JSONObject jObject = new JSONObject(response);						
  						 if (jObject.getBoolean("status")) {
  							 Toast.makeText(getApplicationContext(), "Group successfully created!", 
  		                    		   Toast.LENGTH_LONG).show();
  							 //joinGroup();
  							 created = true;
  		                   } 
  					    } catch (JSONException e) {					
  						   e.printStackTrace();
  					    }            
  	               }
  	               // When error occured
  	               @Override
  	               public void onFailure(int statusCode, Throwable error, String content) {	                   
  	                   // Hide ProgressBar
  	                   prgDialog.hide();
  	                   if (statusCode == 404) {
  	                       Toast.makeText(getApplicationContext(), "Group name invalid", 
  	                    		   Toast.LENGTH_LONG).show();
  	                   } else if (statusCode == 500) {
  	                       Toast.makeText(getApplicationContext(), "Something went terrible at server end", 
  	                    		   Toast.LENGTH_LONG).show();
  	                   } else {
  	                       Toast.makeText(getApplicationContext(), "Device might not be connected to network",
  	                               Toast.LENGTH_LONG).show();
  	                   }
  	               }
  	       });	
  	     
  	}
	
}
