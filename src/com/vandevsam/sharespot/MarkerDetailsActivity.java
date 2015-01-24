package com.vandevsam.sharespot;

import java.util.List;

import com.vandevsam.sharespot.data.MarkerDataManager;
import com.vandevsam.sharespot.data.MyMarkerObj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MarkerDetailsActivity extends Activity {
	
	Context context = this;
	private String coordinates;
	private TextView textName;	
	private TextView textDesc;
	private TextView textType;
	
	MarkerDataManager marker;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygroupdetail);
        
        Intent intent = this.getIntent();   
		coordinates = intent.getStringExtra(Intent.EXTRA_TEXT);	
        
        marker = new MarkerDataManager(context);
        
        marker.open();        
        //fetch details
        List<String> details = marker.getMarkerDetails(coordinates);             
        marker.close();
        
        //extractions
        String desc = details.get(0);
        String address = details.get(1);        
        String group = details.get(3);
        
      //get name and username
        textName = (TextView) findViewById(R.id.textName);
        String htmlName = "<h3>Address: "+address+"</h3>";
        textName.setText(Html.fromHtml(htmlName));
        
        textDesc = (TextView) findViewById(R.id.textDesc);
        String htmlDate = "<h3>Description: "+desc+"</h3>";
        textDesc.setText(Html.fromHtml(htmlDate));        
        
        //get groups signed in
        //special case TODO review later
        textType = (TextView) findViewById(R.id.textType);
        String htmlGroup = "<h3>Group: "+group+"</h3>";
        textType.setText(Html.fromHtml(htmlGroup));

	}
	
	public void deleteGroup(View view) {
		marker.open();
		marker.deleteMarker(new MyMarkerObj(coordinates));
		marker.close();
		Toast.makeText(getApplicationContext(), 
				 "marker deleted", 
	              Toast.LENGTH_LONG).show();	
        this.callHomeActivity(view);
    }
	
	/**
     * Navigate to Home Screen 
     * @param view
     */
    public void callHomeActivity(View view) {        
		finish();
    }   
	
	

}
