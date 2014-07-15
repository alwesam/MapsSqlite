package com.example.mapsqlite;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import android.content.Context;
//import android.content.Intent;
//import android.text.Editable;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements LocationListener,NoticeDialogFragment.NoticeDialogListener {

	Context context = this;
	private GoogleMap map;	
	
	private String address;
	private String snippet;
	private LatLng loc;
	
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

	double mLatitude=0;
    double mLongitude=0;
	
	MarkerDataSource data;
	
	HashMap<String, String> mMarkerPlaceLink = new HashMap<String, String>();	

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
	    map.setOnMapClickListener(new OnMapClickListener() {	    	    
	            @Override
	            public void onMapClick(LatLng latlng) { 
	            	setLoc(new LatLng(latlng.latitude,latlng.longitude));
	            	GetAddressTask getAddress = new GetAddressTask(context);	        	    
	        	    getAddress.execute(latlng);
	            	//String slatlng = String.valueOf(latlng.latitude)+" "+String.valueOf(latlng.longitude);
	                //data.addMarker(new MyMarkerObj("mAddress", "Press to add details", slatlng) );
	                //listMarker(); //to show on map
	            }
	    });	 
	    
	    //TODO fix that later, incorprate a function to delete marker along with entering details as show below
	    map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker marker) {
				//marker.remove(); //to remove from map immediately
				//data.deleteMarker( new MyMarkerObj(marker.getTitle(),marker.getSnippet(), 
				//		marker.getPosition().latitude+" "+marker.getPosition().longitude));
				//listMarker();	
				
				//TODO explore passing the object instead of using getters and setters 
				///MyMarkerObj m = new MyMarkerObj(marker.getTitle(),marker.getSnippet(), 
					//			marker.getPosition().latitude+" "+marker.getPosition().longitude);
				
				setAddress(marker.getTitle());
				setSnippet(marker.getSnippet());
				setLoc(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude));				
				showNoticeDialog();
			}	    	
	    });	   	    
	    
	    //new stuff! TODO get an activity where I can enter details about location marker
	 /*   map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {	    	 
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getBaseContext(), PlaceDetailsActivity.class);
                String location = marker.getPosition().latitude+","+marker.getPosition().longitude;
                intent.putExtra("location", location);
                // Starting the Place Details Activity
                startActivity(intent);
            }
        });   */	    
		    
	}

	
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
    	//delete old record
    	data.deleteMarker( new MyMarkerObj(oldTitle, oldSnippet, slatlng));  
    	//replace with new record
    	data.addMarker(new MyMarkerObj(oldTitle, value, slatlng) );
    	dialog.dismiss();        
    	listMarker();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {  
    	String oldTitle = this.getAddress();
    	String oldSnippet = this.getSnippet();
    	String slatlng = String.valueOf(getLoc().latitude)+" "+String.valueOf(getLoc().longitude);    	
    	//delete old record
    	data.deleteMarker( new MyMarkerObj(oldTitle, oldSnippet, slatlng));
    	dialog.dismiss();
    	listMarker();
    }	
	
	//list markers
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
			
			// Getting a place from the places list
            //String hmPlace = n.get(i).getSnippet();			
			//mMarkerPlaceLink.put(m.getId(), hmPlace);			
		}	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 /**
	    * A subclass of AsyncTask that calls getFromLocation() in the
	    * background. The class definition has these generic types:
	    * Location - A Location object containing
	    * the current location.
	    * Void     - indicates that progress units are not used
	    * String   - An address passed to onPostExecute()
	    */
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
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);	 
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(14));
    }	
	
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
