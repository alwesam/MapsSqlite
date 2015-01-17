package com.vandevsam.shareloc.beta;

import java.util.HashMap;
import java.util.List;

import com.vandevsam.shareloc.beta.data.GroupDataManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class ProfileActivity extends Activity {
	
	Context context = this;
	SessionManager session;
	private TextView textName;
	private TextView textDate;
	private TextView textGroup;
	
	HashMap<String, String> user;
	String name;
	String date;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        session = new SessionManager(getApplicationContext());        
        
       // get user data from session
        user = session.getUserDetails();
        name = user.get(SessionManager.KEY_NAME);
        date = user.get(SessionManager.KEY_DATE);
        
        //add groups to local db
        //TODO fix this mess
        //ServerUtilFunctions list = new ServerUtilFunctions(this);
        //list.listGroup(name);
        //ListGroups list = new ListGroups(this); 
        //list.listGroup(name); //return a list of groups        
                
        //get name and username
        textName = (TextView) findViewById(R.id.textName);
        String htmlName = "<h3>Name: "+name+"</h3>";
        textName.setText(Html.fromHtml(htmlName));
        
        textDate = (TextView) findViewById(R.id.textDate);
        String htmlDate = "<h3>Date joined: "+date+"</h3>";
        textDate.setText(Html.fromHtml(htmlDate));        
        
        //get groups signed in
        //special case TODO review later
        textGroup = (TextView) findViewById(R.id.textGroup);
        String htmlGroup = "<h3>Group(s) joined: "+parseGroups()+"</h3>";
        textGroup.setText(Html.fromHtml(htmlGroup));
        
	}
	
	public String parseGroups() {
		GroupDataManager data = new GroupDataManager(context);
        data.open();		
        List<String> groups = data.getAllGroups();		
		String result="";
		/*int i=0;
		while(groups.iterator()!=null){
            result += groups.get(i++)+",";                 
		}*/
		for (int i=0; i<groups.size(); i++){
		   if(i<groups.size()-1)
			 result += groups.get(i)+", ";
		   else
			 result += groups.get(i);
		}
		return result;
	}

}
