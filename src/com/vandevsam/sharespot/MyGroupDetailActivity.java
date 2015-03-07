package com.vandevsam.sharespot;

import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vandevsam.sharespot.data.GroupDataManager;
import com.vandevsam.sharespot.data.MarkerDataManager;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyGroupDetailActivity extends Activity implements
		NoticeDialogFragment.NoticeDialogListener {

	Context context = this;
	private String groupName;
	private TextView textName;
	private TextView textDesc;
	private TextView textType;

	GroupDataManager group;

	SessionManager session;
	HashMap<String, String> user;
	String username;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mygroupdetail);

		// get user details
		session = new SessionManager(getApplicationContext());
		user = session.getUserDetails();
		username = user.get(SessionManager.KEY_USERNAME);

		Intent intent = getIntent();
		groupName = intent.getStringExtra("key");

		group = new GroupDataManager(context);

		group.open();
		// fetch details
		List<String> details = group.getDetails(groupName);
		group.close();

		// extractions
		String name = details.get(0);
		String desc = details.get(1);
		String type = details.get(2);

		// get name and username
		textName = (TextView) findViewById(R.id.textName);
		String htmlName = "<h3>Group Name: " + name + "</h3>";
		textName.setText(Html.fromHtml(htmlName));

		textDesc = (TextView) findViewById(R.id.textDesc);
		String htmlDate = "<h3>Group Description: " + desc + "</h3>";
		textDesc.setText(Html.fromHtml(htmlDate));

		// get groups signed in
		// special case TODO review later
		textType = (TextView) findViewById(R.id.textType);
		String htmlGroup = "<h3>Group Type: " + type + "</h3>";
		textType.setText(Html.fromHtml(htmlGroup));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mygroupdetail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.add_user) {
			addFriend();
			return true;
		}
		if (id == R.id.leave_group) {
			deleteGroup();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void deleteGroup() {

		group.open();
		group.deleteGroup(groupName);
		group.close();

		// also delete markers in the group
		MarkerDataManager markers = new MarkerDataManager(this);
		markers.open();
		markers.deleteMarkerGroup(groupName);
		markers.close();

		// unjoin group
		ServerUtilFunctions unjoinG = new ServerUtilFunctions(this);
		unjoinG.leaveGroup(username, groupName);

		Toast.makeText(getApplicationContext(), "group deleted",
				Toast.LENGTH_LONG).show();
		this.callHomeActivity();
	}

	public void addFriend() {

		DialogFragment dialog = new NoticeDialogFragment(this);
		dialog.show(getFragmentManager(), "NoticeDialogFragment");

	}

	private void addUser(String friend) {

		ServerUtilFunctions joinG = new ServerUtilFunctions(this);
		joinG.joinGroup(friend, groupName);

	}

	/**
	 * Navigate to Home Screen
	 * 
	 * @param view
	 */
	public void callHomeActivity() {

		Intent resultIntent = new Intent();
		setResult(RESULT_OK, resultIntent);
		finish();

	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String value) {
		// TODO Auto-generated method stub
		addUser(value);
		dialog.dismiss();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		dialog.dismiss();
	}

}
