package com.example.mapsqlite;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
//import android.content.Intent;
//import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener,NoticeDialogFragment.NoticeDialogListener {

	Context context = this;
	private GoogleMap map;	
	private static final int zoom = 13;
	
	//move to utilities class?
	private String address;
	private String snippet;
	private LatLng loc;
	
	//TODO find a better way to handle address and snippet and locations
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	
	public LatLng getLoc() {
		return loc;
	}

	public void setLoc(LatLng loc) {
		this.loc = loc;
	}
	
	MarkerDataSource data;
	
	ProgressDialog prgDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		        .getMap();		
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
        // Find location from gps
        if(location!=null){
            onLocationChanged(location);
        }                
        
        locationManager.requestLocationUpdates(provider, 20000, 0, this);			
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
	            	//setLoc(new LatLng(latlng.latitude,latlng.longitude));
	            	//GetAddressTask getAddress = new GetAddressTask(context);	        	    
	        	    //getAddress.execute(latlng);
	            	String slatlng = String.valueOf(latlng.latitude)+" "+String.valueOf(latlng.longitude);
	                data.addMarker(new MyMarkerObj("Title TBD", "Position:"+slatlng, slatlng) );
	                listMarker(); //to show on map
	            }
	    });	 
	    
	    //delete markers
	    map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker marker) {	
				//saved concerned datapoints, TODO save Id instead?
				setAddress(marker.getTitle());
				setSnippet(marker.getSnippet());
				setLoc(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude));				
				showNoticeDialog();
			}	    	
	    });	   	  
	    
	  //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
        prgDialog.setCancelable(false);
 		    
	}
	//end oncreate method
	
	//dialog methods
	public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoticeDialogFragment(context);
        dialog.show(getFragmentManager(), "NoticeDialogFragment");
    }
    
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String value) {
        // User touched the dialog's positive button
    	String oldTitle = this.getAddress();
    	String oldSnippet = this.getSnippet();
    	String slatlng = String.valueOf(getLoc().latitude)+" "+String.valueOf(getLoc().longitude);    	
    	//delete record
    	data.deleteMarker( new MyMarkerObj(oldTitle, oldSnippet, slatlng));      	
    	dialog.dismiss();  
    	Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //When Sync action button is clicked
        if (id == R.id.refresh) {
            //Sync SQLite DB data to remote MySQL DB
            syncSQLiteMySQLDB();
            return true;
        } 
        return super.onOptionsItemSelected(item);
    }
	
	/**
	  * A subclass of AsyncTask that calls getFromLocation() in the
	    * background. The class definition has these generic types:
	    * Location - A Location object containing
	    * the current location.
	    * Void     - indicates that progress units are not used
	    * String   - An address passed to onPostExecute()
	 **/
	private class GetAddressTask extends AsyncTask<LatLng, Void, String> {
	        Context mContext;
	        
	        public GetAddressTask(Context context) {
	            super();
	            mContext = context;
	        }
	        
	        /**
	         * Get a Geocoder instance, get the latitude and longitude
	         * look up the address, and return it
	         *
	         * @params params One or more Location objects
	         * @return A string containing the address of the current
	         * location, or an empty string if no address can be found,
	         * or an error message
	         */
	        @Override
	        protected String doInBackground(LatLng... params) {
	            Geocoder geocoder =
	                    new Geocoder(mContext, Locale.getDefault());
	            // Get the current location from the input parameter list
	            LatLng loc = params[0];
	            // Create a list to contain the result address
	            List<Address> addresses = null;
	            try {
	                /*
	                 * Return 1 address.
	                 */
	                addresses = geocoder.getFromLocation(loc.latitude,
	                        loc.longitude, 1);
	            } catch (IOException e1) {
	            Log.e("LocationSampleActivity",
	                    "IO Exception in getFromLocation()");
	            e1.printStackTrace();
	            return ("IO Exception trying to get address");
	            } catch (IllegalArgumentException e2) {
	            // Error message to post in the log
	            String errorString = "Illegal arguments " +
	                    Double.toString(loc.latitude) +
	                    " , " +
	                    Double.toString(loc.longitude) +
	                    " passed to address service";
	            Log.e("LocationSampleActivity", errorString);
	            e2.printStackTrace();
	            return errorString;
	            }
	            // If the reverse geocode returned an address
	            if (addresses != null && addresses.size() > 0) {
	                // Get the first address
	                Address address = addresses.get(0);
	                /*
	                 * Format the first line of address (if available),
	                 * city, and country name.
	                 */
	                String addressText = String.format(
	                        "%s, %s, %s",
	                        // If there's a street address, add it
	                        address.getMaxAddressLineIndex() > 0 ?
	                                address.getAddressLine(0) : "",
	                        // Locality is usually a city
	                        address.getLocality(),
	                        // The country of the address
	                        address.getCountryName());
	                // Return the text
	                return addressText;
	            } else {
	                return "No address found";
	            }
	        }
	    
	        protected void onPostExecute(String address) {
	            // Set activity indicator visibility to "gone"
	            //mActivityIndicator.setVisibility(View.GONE);
	            // Display the results of the lookup.
	        	String slatlng = String.valueOf(getLoc().latitude)+" "+String.valueOf(getLoc().longitude);
	        	data.addMarker(new MyMarkerObj(address, "Press to add details", slatlng) );
	        	listMarker();
	        }	    
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
      // ArrayList<HashMap<String, String>> locList =  data.getAllLocations();
     //  if(locList.size()!=0){
     //      if(data.dbSyncCount() != 0){
               prgDialog.show();
               params.put("locationsJSON", data.composeJSONfromSQLite());
               client.post("http://192.168.0.11/sqlitemysqlsyncMarkers/insertmarker.php",params ,new AsyncHttpResponseHandler() {
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
       //    }else{
       //        Toast.makeText(getApplicationContext(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
       //    }
     //  }else{
       //        Toast.makeText(getApplicationContext(), "No data in SQLite DB, please do enter a location first", Toast.LENGTH_LONG).show();
     //  }
               
   }
   //end method
   
   
	
	  @Override
	protected void onResume() {
	    data.open();
	    super.onResume();
	  }

	  @Override
	protected void onPause() {
	    data.close();
	    super.onPause();
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
