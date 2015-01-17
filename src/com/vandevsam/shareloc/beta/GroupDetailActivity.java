package com.vandevsam.shareloc.beta;


import com.vandevsam.shareloc.beta.data.GroupDataManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GroupDetailActivity extends Activity {
	
	GroupDataManager data;
	private TextView groupTextView;
	private String groupName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupdetail);
		groupTextView = (TextView) findViewById(R.id.GroupName);
		//TODO fix
		Intent intent = getIntent();   
		groupName = intent.getStringExtra("key");
		groupTextView.setText("Group: "+groupName);	
	}	
	
	public void joinGroup(View view){	
		
		//JoinGroup joined = new JoinGroup(this);
		
		ServerUtilFunctions joined = new ServerUtilFunctions(this);
		//TODO fix, this is a placeholder
		joined.joinGroup("wesam","vancouver");		
		//go back home
		this.callHomeActivity(view);
	}	
	 
    /**
     * Called when Cancel button is clicked
     * @param view
     */
    public void cancelJoinGroup(View view) {
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
