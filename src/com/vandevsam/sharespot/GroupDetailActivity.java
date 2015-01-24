package com.vandevsam.sharespot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vandevsam.sharespot.data.GroupDataManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GroupDetailActivity extends Activity {
	
	GroupDataManager data;
	private TextView groupTextView;
	private String groupName;
	SessionManager session;
	HashMap<String, String> user;
	String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupdetail);
		
		//get user details
		session = new SessionManager(getApplicationContext());
	    user = session.getUserDetails();
	    name = user.get(SessionManager.KEY_NAME);
		
		groupTextView = (TextView) findViewById(R.id.GroupName);
		//TODO fix
		Intent intent = getIntent();   
		groupName = intent.getStringExtra("key");
		groupTextView.setText("Group: "+groupName);	
		
		//TODO join this activity with mygroupdetails as well as the layout!		
		
	}	
	
	public void joinGroup(View view){	
		
		//JoinGroup joined = new JoinGroup(this);		
		ServerUtilFunctions joined = new ServerUtilFunctions(this);
		joined.joinGroup(name,groupName);	
		//update queryStatus
        GroupDataManager group = new GroupDataManager(this);
        group.open();
		group.updateStatus(groupName,"yes");
		group.close();
        
		//make sure it's checked!
	    SaveGroupPreference pref = new SaveGroupPreference(this);  
	    List<String> name = new ArrayList<String>();    	
	    List<Boolean> check = new ArrayList<Boolean>();
	    name.add(groupName);
	    check.add(true);  	   	    	
	    pref.checkPref(name,check); 
	    
	    //download markers associated with this marker
	    ServerUtilFunctions down = new ServerUtilFunctions(this, "Downloading Markers...");
        down.syncMySQLDBSQLite(groupName);
	
		//go back home
		this.callHomeActivity(view);
	}	
 	
	/**
     * Navigate to Home Screen 
     * @param view
     */
    public void callHomeActivity(View view) {
        Intent objIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(objIntent);    	
    }    
	
}
