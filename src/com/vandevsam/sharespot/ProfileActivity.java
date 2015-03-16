package com.vandevsam.sharespot;

import java.util.HashMap;
import java.util.List;

import org.xml.sax.XMLReader;

import com.vandevsam.sharespot.data.GroupDataManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

//TODO add a tag handler
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

		// get name and username
		textName = (TextView) findViewById(R.id.textName);
		String htmlName = "<h4>Name: " + name + "</h4>";
		textName.setText(Html.fromHtml(htmlName));
		
		textDate = (TextView) findViewById(R.id.textDate);
		String htmlDate = "<h4>Date joined: " + date + "</h4>";
		textDate.setText(Html.fromHtml(htmlDate));

		// get groups signed in
		// special case TODO review later
		textGroup = (TextView) findViewById(R.id.textGroup);
		String htmlGroup = "<h4>Group(s) joined: </h4>" + parseGroups();
		textGroup.setText(Html.fromHtml(htmlGroup));

	}

	public String parseGroups() {
		GroupDataManager data = new GroupDataManager(context);
		data.open();
		// List<String> groups = data.getAllGroups();
		List<String> groups = data.getJoinedGroups();
		String result = "";

		for (int i = 0; i < groups.size(); i++) {
			result += "<p>- " + groups.get(i) + "</p>";	
		}
		return result;
	}

}
