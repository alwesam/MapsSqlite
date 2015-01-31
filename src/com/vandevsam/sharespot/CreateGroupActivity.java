package com.vandevsam.sharespot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vandevsam.sharespot.data.GroupDataManager;
import com.vandevsam.sharespot.data.MyGroupObj;

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
	private EditText groupName;
	private EditText groupDescription;
	ProgressDialog prgDialog;	
	
	SessionManager session;
	HashMap<String, String> user;
	String creator;
	String group;
	String description;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup); 
        
        session = new SessionManager(getApplicationContext());	
        // get user data from session
        user = session.getUserDetails();
        //this guy, the creator will be added to the group
        creator = user.get(SessionManager.KEY_USERNAME);              
        
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
                  
	}	
	
	public void addNewGroup(View view){		
		
		Spinner spinner = (Spinner) findViewById(R.id.group_type);		
		String group_type = String.valueOf(spinner.getSelectedItem());
		
		group = groupName.getText().toString();
		description = groupDescription.getText().toString();
		
		if (group.equals("null") || group.equals("") ){
			Toast.makeText(getApplicationContext(), 
					 "Enter a group name", 
		              Toast.LENGTH_LONG).show();
			return;
		}
		
		GroupDataManager data = new GroupDataManager(context);        
        try {
           data.open();
        } catch (Exception e){
			Log.i("hello", "hello");
		} 
		//create the group in local sqlite db
		if (!data.queryGroup(group)){
		   data.createGroup(new MyGroupObj(group,
				                        description,
				                        group_type,
				                        "yes"
				                         ));
		} else {
			Toast.makeText(getApplicationContext(), 
					 "Group of this name already exists", 
		              Toast.LENGTH_LONG).show();
			return;
		}			
		data.close();
		
		//make sure it's checked!
	    //TODO move into a method
	    SaveGroupPreference pref = new SaveGroupPreference(this);  
	    List<String> name = new ArrayList<String>();    	
	    List<Boolean> check = new ArrayList<Boolean>();
	    name.add(group);
	    check.add(true);  	   	    	
	    pref.checkPref(name,check); 
		
		ServerUtilFunctions gr = new ServerUtilFunctions(this, "Creating the group....");
		gr.createGroup(creator, group, description, group_type);			
	
		this.callHomeActivity(view);
	}	
	 
    public void cancelAddGroup(View view) {
        this.callHomeActivity(view);
    }
    
    public void callHomeActivity(View view) {
        Intent objIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(objIntent);
    } 
  	
}
