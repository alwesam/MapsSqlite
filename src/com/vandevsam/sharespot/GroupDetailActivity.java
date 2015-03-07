package com.vandevsam.sharespot;

import java.util.HashMap;

import com.vandevsam.sharespot.data.GroupDataManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GroupDetailActivity extends Activity {

	GroupDataManager data;
	private TextView groupTextView;
	private String groupName;
	SessionManager session;
	HashMap<String, String> user;
	String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupdetail);

		// get user details
		session = new SessionManager(getApplicationContext());
		user = session.getUserDetails();
		username = user.get(SessionManager.KEY_USERNAME);

		groupTextView = (TextView) findViewById(R.id.GroupName);
		// TODO fix
		Intent intent = getIntent();
		groupName = intent.getStringExtra("key");
		groupTextView.setText("Group: " + groupName);
		// TODO join this activity with mygroupdetails as well as the layout!

	}

	public void joinGroup(View view) {

		// TODO fix later
		GroupDataManager type = new GroupDataManager(this);
		type.open();

		if (!type.isPrivate(groupName)) { // returns true if private
			ServerUtilFunctions joinG = new ServerUtilFunctions(this);
			joinG.joinGroup(username, groupName);
			type.close();
		} else {
			Toast.makeText(this,
					"This group is private... contact group admin to add you",
					Toast.LENGTH_LONG).show();
			type.close();
			return;
		}

		// download markers associated with this marker
		// ServerUtilFunctions down = new ServerUtilFunctions(this,
		// "Downloading Markers...");
		// down.syncMySQLDBSQLite(groupName);

		// go back home
		this.callHomeActivity(view);
	}

	/**
	 * Navigate to Home Screen
	 * 
	 * @param view
	 */
	public void callHomeActivity(View view) {
		Intent objIntent = new Intent(getApplicationContext(),
				MainActivity.class);
		startActivity(objIntent);
	}

}
