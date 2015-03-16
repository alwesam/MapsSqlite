package com.vandevsam.sharespot;

import java.util.List;

import com.vandevsam.sharespot.data.MarkerDataManager;
import com.vandevsam.sharespot.data.MyMarkerObj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MarkerDetailsActivity extends Activity {

	Context context = this;
	private String coordinates;
	private TextView textName;
	private TextView textDesc;
	private TextView textType;

	MarkerDataManager marker;

	String group;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymarkerdetail);

		Intent intent = this.getIntent();
		coordinates = intent.getStringExtra(Intent.EXTRA_TEXT);

		marker = new MarkerDataManager(context);

		marker.open();
		// fetch details
		List<String> details = marker.getMarkerDetails(coordinates);
		marker.close();

		// extractions
		String desc = details.get(0);
		String address = details.get(1);
		group = details.get(3);

		// get name and username
		textName = (TextView) findViewById(R.id.textName);
		String htmlName = "<h4>Address: </h4>" + "<p>" + address + "</p>";
		textName.setText(Html.fromHtml(htmlName));

		textDesc = (TextView) findViewById(R.id.textDesc);
		String htmlDate = "<h4>Description: </h4>" + "<p>" + desc + "</p>";
		textDesc.setText(Html.fromHtml(htmlDate));

		// get groups signed in
		// special case TODO review later
		textType = (TextView) findViewById(R.id.textType);
		String htmlGroup = "<h4>Group: </h4>" + "<p>" + group + "</p>";
		textType.setText(Html.fromHtml(htmlGroup));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.markerdetail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.remove_marker) {
			deleteMarker();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void deleteMarker() {
		marker.open();
		marker.deleteMarker(new MyMarkerObj(coordinates));
		marker.close();

		ServerUtilFunctions removeM = new ServerUtilFunctions(this);
		removeM.removeMarker(coordinates, group);

		this.callHomeActivity();
	}

	/**
	 * Navigate to Home Screen
	 * 
	 * @param view
	 */
	public void callHomeActivity() {
		finish();
	}

}
