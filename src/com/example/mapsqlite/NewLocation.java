package com.example.mapsqlite;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

public class NewLocation extends Activity {

	private Context context;
	private LatLng coordinates;
	
	private String address; //one address at a time? Not convinced, TODO review later
	
	MarkerDataSource data;
	
	public NewLocation(LatLng latlng, Context c){
		this.coordinates = latlng;
		//this.context = c;
	}	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_location);
        //userName = (EditText) findViewById(R.id.userName);
        
        //TODO add fields to enter the stuff and buttons including 
        //button to retrieve address!
        
        
        data = new MarkerDataSource(context);
        try {
			data.open();
		} catch (Exception e){
			Log.i("hello", "hello");
		}
    }
	
	
	/**
	 * called when new marker is saved
	 */
	public void addNewLocation(View view){
		
		GetAddressTask getAddress = new GetAddressTask(context);	        	    
	    getAddress.execute(coordinates);
		//TODO, get decoded address    
	    this.address = "";
		String slatlng = String.valueOf(coordinates.latitude)+" "+String.valueOf(coordinates.longitude);
		data.addMarker(new MyMarkerObj(this.address, "Position:"+slatlng, slatlng) );
		data.close();
		//go back home
		this.callHomeActivity(view);
	}
	
	/**
     * Navigate to Home Screen 
     * @param view
     */
    public void callHomeActivity(View view) {
        Intent objIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(objIntent);
    }
 
    /**
     * Called when Cancel button is clicked
     * @param view
     */
    public void cancelAddUser(View view) {
        this.callHomeActivity(view);
    }
	
    //alternatively, find a jar file that does this already!!
    //or even better create a jarfile that does this for me!
	
	/**
	  * A subclass of AsyncTask that calls getFromLocation() in the
	    * background. The class definition has these generic types:
	    * Location - A Location object containing
	    * the current location.
	    * Void     - indicates that progress units are not used
	    * String   - An address passed to onPostExecute()
	 **/
	private class GetAddressTask extends AsyncTask<LatLng, Void, String> {
	        private Context mContext;	       
	        private String decodedAddress;
	        
	        public void setdecodedAddress(String result){
	        	this.decodedAddress = result;
	        }
	        
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
	            setdecodedAddress(address);	        		        	
	        }	    
	    }
	
}
