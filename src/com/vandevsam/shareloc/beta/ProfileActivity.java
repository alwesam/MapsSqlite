package com.vandevsam.shareloc.beta;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class ProfileActivity extends Activity {
	
	//private CheckBox chkIos, chkAndroid, chkWindows;
	SessionManager session;
	private TextView textName;
	private TextView textDate;
	private TextView textGroup;
	
	HashMap<String, String> user;
	String name;
	String date;
	//String group;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        session = new SessionManager(getApplicationContext());
        
     // get user data from session
        user = session.getUserDetails();
        name = user.get(SessionManager.KEY_NAME);
        date = user.get(SessionManager.KEY_DATE);
        
        //get name and username
        textName = (TextView) findViewById(R.id.textName);
        String htmlName = "<h3>Name: "+name+"</h3>";
        textName.setText(Html.fromHtml(htmlName));
        
        textDate = (TextView) findViewById(R.id.textDate);
        String htmlDate = "<h3>Date joined: "+date+"</h3>";
        textDate.setText(Html.fromHtml(htmlDate));        
        
        //get groups signed in
        textGroup = (TextView) findViewById(R.id.textGroup);
        String htmlGroup = "<h3>Group(s): Vancouver</h3>";
        textGroup.setText(Html.fromHtml(htmlGroup));
        
	}
	
	/*
	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which checkbox was clicked
	    switch(view.getId()) {
	        case R.id.group_a:
	            if (checked)
	                // Put some meat on the sandwich	           
	            break;
	        case R.id.group_b:
	            if (checked)
	                // Cheese me	          
	            break;
	        // TODO: Veggie sandwich
	        case R.id.group_c:
	            if (checked)
	                // Cheese me	          
	            break;
	        case R.id.group_d:
	            if (checked)
	                // Cheese me	          
	            break;
	    } //end switch statement
	} //end method
	*/

}
