package com.vandevsam.shareloc.beta;

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
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity 
                          implements LocationListener,
                          NoticeDialogFragment.NoticeDialogListener
                          {

	Context context = this;
	private GoogleMap map;	
	private static final int zoom = 13;
		
	SessionManager session;
	//move to utilities class?
	private LatLng loc;			
	public LatLng getLoc() {
		return loc;
	}
	public void setLoc(LatLng loc) {
		this.loc = loc;
	}	
	
	private String[] mListTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;	
    private CharSequence mTitle;      
    //private ArrayAdapter<String> listAdapter;
	
	MarkerDataSource data;	
	ProgressDialog prgDialog;
	private static final String webServer = "146.148.91.48"; //my google CE ip address
	//private static final String webServer = "192.168.0.11"; //localhost
	private ArrayAdapter<String> mDrawerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		/**
		 * Here I'm initialzing drawer pane list variables and setting the
		 * onItemclicklistnere method
		 */		
		session = new SessionManager(getApplicationContext());		
		
        if (session.checkLogin())
		   mListTitles = getResources().getStringArray(R.array.sidepane_array);
        else
           mListTitles = getResources().getStringArray(R.array.sidepane_array_guest);			
		
		mDrawerAdapter = new ArrayAdapter<String>(this,
				                                  R.layout.drawer_list_item, 
				                                  mListTitles);
		
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        mDrawerList.setAdapter(mDrawerAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	 @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {  
        		 //String item = mDrawerAdapter.getItem(position);
        		 selectItem(mDrawerAdapter.getItem(position),position);
             }
		});    
        /**
		 * Map initialization
		 */	             
        //related to main framgment: map
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
        }	else {
        	//TODO: clean up code
    	    LatLng latLng = new LatLng(49.28964841702669, -122.7909402921796);	 
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
		//create new database to store markers
        /**
         * SQLITE database open
         */
		data = new MarkerDataSource(context);		    
		try {
			data.open();
		} catch (Exception e){
			Log.i("Error!", "Cannot open SQLite DB");
		}			
		//show available markers
		listMarker();
		//add markers			
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
	
	/** Swaps fragments in the map view */
	private void selectItem(String item, int position) {
		 // update the main content by replacing fragments and/or activities
		DrawerListEnum enumval = DrawerListEnum.valueOf(item.toUpperCase());
        
        switch (enumval) {
        case SEARCH: //search
        	//data.close();
        	startActivityForResult(new Intent(this, SearchActivity.class), 90);
            break;
        case PROFILE:  //profile
        	startActivity(new Intent(this, ProfileActivity.class));
            break; 
        case SETTINGS://settings activity
        	startActivity(new Intent(this, SettingsActivity.class));
            break;
        case LOGOUT: //Log out        
        	session.logoutSession();
        	//to reload activity
        	reloadActivity();
            break; 
        case CONTACT: //send feedback
        	sendEmail();
            break; 
        case LOGIN: //login
        	map.clear();
        	startActivity(new Intent(this, AuthenticateActivity.class));
        	finish();
            break; 
        case REGISTER: //register 
        	//TODO find out diff between this and getapplicationcontext()
        	startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        	finish();
            break;
        default:
            break;
        }
        
	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(position, true);
	    setTitle(mListTitles[position]);
	    mDrawerLayout.closeDrawer(mDrawerList);
	    
	}
	
	@Override
	public void setTitle(CharSequence title) {
	    mTitle = title;
	    getActionBar().setTitle(mTitle);
	}
	
	//TODO review
	protected void sendEmail() {
	      Log.i("Send email", "");

	      String[] TO = {"alwesam@gmail.com"};
	      Intent emailIntent = new Intent(Intent.ACTION_SEND);
	      emailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	      emailIntent.setData(Uri.parse("mailto:"));
	      emailIntent.setType("text/plain");
	      emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);	      
	      emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
	      emailIntent.putExtra(Intent.EXTRA_TEXT, "Feedback");

	      try {
	         startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	         finish();
	         Log.i("Finished sending email...", "");
	      } catch (android.content.ActivityNotFoundException ex) {
	         Toast.makeText(MainActivity.this, 
	         "There is no messaging client installed.", Toast.LENGTH_SHORT).show();
	      }
	   }
	
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
		List <MyMarkerObj> n = data.getAllMarkers();			
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
	
	
	private Marker listAMarker(String pos){
		map.clear();
		MyMarkerObj n = data.getSelectMarker(pos);		
	    String[] slatlng = n.getPosition().split(" ");
		LatLng latlng = new LatLng (Double.valueOf(slatlng[0]), Double.valueOf(slatlng[1]));
		Marker marker = map.addMarker(new MarkerOptions()			   
		               .title(n.getTitle())
		        	   .snippet(n.getSnippet())
			           .position(latlng)
			           .draggable(true));		
		return marker;
	}	
	
	private void findMarker(String slatlng){
		
		Marker marker = listAMarker(slatlng);
		LatLng latLng = marker.getPosition();	 
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom+2));
        Toast.makeText(getApplicationContext(), 
  			  "Found marker!", 
  			  Toast.LENGTH_LONG).show();
		marker.showInfoWindow();
		
	}
	
	/**
	 * call back from SearchActivity
	 */		
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  //super.onActivityResult(requestCode, resultCode, data);
     if (data != null){
	    switch(requestCode) {
	      case 90 : {	    	
	        if (resultCode == RESULT_OK) {
	    	  String coordinates = data.getStringExtra("note");
	    	  findMarker(coordinates);
	    	  //listMarker();
	        }
	       break;
	      } 
	    }
      }  else {
    	  Toast.makeText(getApplicationContext(), 
    			  "Data Null", 
    			  Toast.LENGTH_LONG).show();
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
        if (id == R.id.action_search){
           //data.close();
           //startActivity(new Intent(this, SearchActivity.class));
           startActivityForResult(new Intent(this, SearchActivity.class), 90);
           return true;
        }
        if (id == R.id.refresh){
        	reloadActivity();
        	return true;
        }
        if (id == R.id.action_settings) {
        	data.close();
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.sync_to_DB) {
            //Sync SQLite DB data to remote MySQL DB
        	if (session.checkLogin())
               syncSQLiteMySQLDB(); 
        	else
        		Toast.makeText(getApplicationContext(), "You have to login to upload markers", 
        				Toast.LENGTH_LONG).show();
            return true;
        } 
        if (id == R.id.sync_from_DB) {            
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
                           Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                           e.printStackTrace();
                       }
                   }
                   @Override
                   public void onFailure(int statusCode, Throwable error,
                       String content) {
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
                           reloadActivity();
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
               // When error occured
               @Override
               public void onFailure(int statusCode, Throwable error, String content) {                   
                   // Hide ProgressBar
                   prgDialog.hide();
                   if (statusCode == 404) {
                       Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                   } else if (statusCode == 500) {
                       Toast.makeText(getApplicationContext(), "Something went terrible at server end", Toast.LENGTH_LONG).show();
                   } else {
                       Toast.makeText(getApplicationContext(), "Device might not be connected to network",
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
   
   //TODO check if this is needed
   @Override
	protected void onStart() {
	    super.onStart();
	    data.open();
	}
   
	
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
