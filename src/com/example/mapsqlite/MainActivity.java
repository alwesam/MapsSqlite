package com.example.mapsqlite;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener,NoticeDialogFragment.NoticeDialogListener {

	Context context = this;
	private GoogleMap map;	
	private static final int zoom = 13;
		
	//move to utilities class?
	private LatLng loc;	
		
	public LatLng getLoc() {
		return loc;
	}
	public void setLoc(LatLng loc) {
		this.loc = loc;
	}
	
	MarkerDataSource data;	
	ProgressDialog prgDialog;
	//private static final String webServer = "146.148.74.145";
	private static final String webServer = "192.168.0.11";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();		
		 // Enabling MyLocation in Google Map
        map.setMyLocationEnabled(true);
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location From GPS
        Location location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
        // Find location from gps
        if(location!=null){
            onLocationChanged(location);
        }			
		//create new database to store markers
		data = new MarkerDataSource(context);		    
		try {
			data.open();
		} catch (Exception e){
			Log.i("hello", "hello");
		}	
		
		//show available markers
		listMarker();

		//add markers	
		//TODO go to a new activity to enter details and address! (fetch address)
	    map.setOnMapClickListener(new OnMapClickListener() {	    	    
	            @Override
	            public void onMapClick(LatLng latlng) { 	            	
	            	//close database first before launching a new activity (which also will acces the same db)!
	            	data.close();	            	
	            	String coordinates = String.valueOf(latlng.latitude)+" "+String.valueOf(latlng.longitude);					
					Intent newLocation = new Intent(getBaseContext(), NewLocation.class)
	            	                               .putExtra(Intent.EXTRA_TEXT, coordinates);
                    startActivity(newLocation);
	            }
	    });	 
	    
	    //delete markers
	    map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker marker) {	
				//save coordinates of location marker to be deleted
				setLoc(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude));				
				showNoticeDialog();
			}	    	
	    });	   	  
	    
	  //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
        prgDialog.setCancelable(false);
 		    
	}
	//end onCreate method
	
	//dialog methods
	public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoticeDialogFragment(context);
        dialog.show(getFragmentManager(), "NoticeDialogFragment");
    }
    
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
    	//retreive location marker coordinates
    	String slatlng = String.valueOf(getLoc().latitude)+" "+String.valueOf(getLoc().longitude);    	
    	//delete record
    	data.deleteMarker( new MyMarkerObj(slatlng));      	
    	dialog.dismiss();  
    	Toast.makeText(getApplicationContext(), "Marker deleted", Toast.LENGTH_LONG).show();
    	listMarker();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {    
    	dialog.dismiss();    	
    }	
	
	//list markers on the map
	private void listMarker(){		
		map.clear();
		List <MyMarkerObj> n = data.getMyMarkers();			
		for (int i=0; i < n.size(); i++){
			String[] slatlng = n.get(i).getPosition().split(" ");
			LatLng latlng = new LatLng (Double.valueOf(slatlng[0]), Double.valueOf(slatlng[1]));
			map.addMarker(new MarkerOptions()			   
			   .title(n.get(i).getTitle())
			   .snippet(n.get(i).getSnippet())
			   .position(latlng)
			   .draggable(true));			
		}	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //When Sync action button is clicked
        if (id == R.id.refresh) {
            //Sync SQLite DB data to remote MySQL DB
            syncSQLiteMySQLDB();            
            return true;
        } 
        if (id == R.id.refresh2) {            
            syncMySQLDBSQLite();
            return true;
        }        
        
        return super.onOptionsItemSelected(item);
    }
	
   @Override
    public void onLocationChanged(Location location) {
        double mLatitude = location.getLatitude();
        double mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);	 
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }	
   
   //syncing sqlite to mysql
   public void syncSQLiteMySQLDB(){
       //Create AsycHttpClient object
               AsyncHttpClient client = new AsyncHttpClient();
               RequestParams params = new RequestParams();     
               prgDialog.show();
               params.put("locationsJSON", data.composeJSONfromSQLite());
               client.post("http://"+webServer+"/sqlitemysqlsyncMarkers/insertmarker.php",params ,new AsyncHttpResponseHandler() {
                   @Override
                   public void onSuccess(String response) {        
                       System.out.println(response);
                       prgDialog.hide();
                       try {
                           JSONArray arr = new JSONArray(response);                            
                           System.out.println(arr.length());
                           for(int i=0; i<arr.length();i++){
                               JSONObject obj = (JSONObject)arr.get(i);                               
                               System.out.println(obj.get("id"));                               
                               System.out.println(obj.get("status"));
                               data.updateSyncStatus(obj.get("id").toString(),obj.get("status").toString());
                           }
                           Toast.makeText(getApplicationContext(), "DB Sync completed!", Toast.LENGTH_LONG).show();
                       } catch (JSONException e) {
                           // TODO Auto-generated catch block
                           Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                           e.printStackTrace();
                       }
                   }
                   @Override
                   public void onFailure(int statusCode, Throwable error,
                       String content) {
                       // TODO Auto-generated method stub
                       prgDialog.hide();
                       if(statusCode == 404){
                           Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                       }else if(statusCode == 500){
                           Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                       }else{
                           Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                       }
                   }
               });  
   }
   //end sync sqlite to mysql method
 
   // Method to Sync MySQL to SQLite DB
   public void syncMySQLDBSQLite() {
       // Create AsycHttpClient object
       AsyncHttpClient client = new AsyncHttpClient();
       // Http Request Params Object
       RequestParams params = new RequestParams();
       // Show ProgressBar
       prgDialog.show();
       // Make Http call to getusers.php
       client.post("http://"+webServer+"/sqlitemysqlsyncMarkers/getlocations.php", params, new AsyncHttpResponseHandler() {
               @Override
               public void onSuccess(String response) {
                   // Hide ProgressBar
                   prgDialog.hide();
                   // Update SQLite DB with response sent by getusers.php
                   //updateSQLite(response);
                   try {
                       // Extract JSON array from the response
                       JSONArray arr = new JSONArray(response);
                       System.out.println(arr.length());
                       // If no of array elements is not zero
                       if(arr.length() != 0){
                           // Loop through each array element, get JSON object which id,title,snippet,position
                           for (int i = 0; i < arr.length(); i++) {
                               // Get JSON object
                               JSONObject obj = (JSONObject) arr.get(i);
                               System.out.println(obj.get("title"));
                               System.out.println(obj.get("snippet"));
                               System.out.println(obj.get("position"));
                               // Insert User into SQLite DB
                               //double check if data already exists in database
                               if (!data.queryPosition(obj.get("position").toString()) 
                            	        && !data.queryAddress(obj.get("title").toString()))                            	   
                                    data.addMarker(new MyMarkerObj(obj.get("title").toString(),
                            		                               obj.get("snippet").toString(),
                            		                               obj.get("position").toString()));
                               else
                            	   System.out.println("already there");                 
                           }             
                           // Reload the Main Activity
                           //reloadActivity();
                       }
                   } catch (JSONException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                   }
               }
               // When error occured
               @Override
               public void onFailure(int statusCode, Throwable error, String content) {
                   // TODO Auto-generated method stub
                   // Hide ProgressBar
                   prgDialog.hide();
                   if (statusCode == 404) {
                       Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                   } else if (statusCode == 500) {
                       Toast.makeText(getApplicationContext(), "Something went terrible at server end", Toast.LENGTH_LONG).show();
                   } else {
                       Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                               Toast.LENGTH_LONG).show();
                   }
               }
       });
   }

   // Reload MainActivity
   public void reloadActivity() {
       Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
       startActivity(objIntent);
   }
     
   @Override
	protected void onStart() {
	    super.onStart();
	    data.open();
	    listMarker();	    
	}

	/**
	   * unimplemented methods of the listener class
	   * @return -  void
	   * @param - not used
	*/
	@Override
	public void onProviderDisabled(String arg0) {
			
	}

	@Override
	public void onProviderEnabled(String arg0) {
				
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				
	}
	
}
