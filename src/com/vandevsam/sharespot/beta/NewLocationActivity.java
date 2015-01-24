package com.vandevsam.sharespot.beta;

import java.io.IOException;
import java.util.ArrayList;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.vandevsam.shareloc.beta.R;
import com.vandevsam.sharespot.beta.data.GroupDataManager;
import com.vandevsam.sharespot.beta.data.MarkerDataManager;
import com.vandevsam.sharespot.beta.data.MyMarkerObj;

public class NewLocationActivity extends Activity {

	private Context context = this;	
	private String coordinates;
	//MarkerDataManager data;
	//GroupDataManager gr_data;
	private TextView addressTextView;
	private EditText userComment;
	private EditText editAddress;
	//private List<String> searchList;
	//private ArrayAdapter<String> adapter;
	private Spinner spinner;
	
	//private String group_sel;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlocation);   
        
        fixSpinner();
                
        userComment = (EditText) findViewById(R.id.Comment); 
        editAddress = (EditText) findViewById(R.id.Location);
        addressTextView = (TextView) findViewById(R.id.addressText); 
        Intent intent = this.getIntent();
        coordinates = intent.getStringExtra(Intent.EXTRA_TEXT);
        
        //decoding address and calling async function to get result
        GetAddressTask getAddress = new GetAddressTask(context);	        	    
	    getAddress.execute(coordinates);

    }		
	
	private void fixSpinner (){
		GroupDataManager gr_data = new GroupDataManager(context);    
        try {
			gr_data.open();
		} catch (Exception e){
			Log.i("Cannot open db", "Error");
		} 	               
        List<String> searchList = new ArrayList<String>();
        searchList = gr_data.getJoinedGroups(); 
        spinner = (Spinner) findViewById(R.id.Spinner01);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                              android.R.layout.simple_spinner_item, 
                              searchList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); 
        //TODO implement class
        //spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		
	}
	
	private void updateAddress (String address){
		addressTextView.setText("Selected address: "+address);
		editAddress.setText(address);
	}
		
	public void addNewLocation(View view){	
		
		spinner = (Spinner) findViewById(R.id.Spinner01);		
		String group_sel = String.valueOf(spinner.getSelectedItem());
		String comment = userComment.getText().toString();
				
		if (comment.equals("null") || comment.equals("") ){
			comment = "  ";
		}		
			
		//comment: this is something peculiar in java where "null" will be assigned 
		//to a String that hasn't been initialized yet
		//For any non-null reference value x, x.equals(null) should return false.
		//therefore mystring.equals(null) will always return false regardless
		//myString.isEmpty() works only with API 9 and above
		if (group_sel.equals("null") || group_sel.equals("")){
			Toast.makeText(getApplicationContext(), 
					"Create or join a group to choose from", 
					Toast.LENGTH_LONG).show();
			return;
		}
		
		MarkerDataManager data = new MarkerDataManager(context);
        try {
			data.open();
		} catch (Exception e){
			Log.i("Cannot open db", "Error");
		}  		
		//TODO add conditionals to ensure legal data is entered into database
		data.addMarker(new MyMarkerObj(comment, //enter comments
				                       editAddress.getText().toString(), //edit or enter address
				                       coordinates, //position
				                       group_sel, //group name
				                       "no") //it's not yet synced to remote db
		               );
		data.close();
		Toast.makeText(getApplicationContext(), 
				"Marker added", 
				Toast.LENGTH_LONG).show();
		//go back home
		this.callHomeActivity(view, true);
	}	
	 
    /**
     * Called when Cancel button is clicked
     * @param view
     */
    public void cancelAddLocation(View view) {
        this.callHomeActivity(view, false);
    }
	
	/**
     * Navigate to Home Screen 
     * @param view
     */
    private void callHomeActivity(View view, Boolean added) {
        //Intent objIntent = new Intent(getApplicationContext(),MainActivity.class);
        //startActivity(objIntent);
    	Intent resultIntent = new Intent();
		resultIntent.putExtra("coord", coordinates);
		resultIntent.putExtra("added", added);
		setResult(RESULT_OK, resultIntent);
		finish();
    }    
    
	
    //alternatively, find a jar file that does this already!!
    //or even better create a jarfile that does this for me!
    //TODO there is an occasional problem with Network locator (in which case the phone
    //is rebooted to make it work again
	/**
	  * A subclass of AsyncTask that calls getFromLocation() in the
	    * background. The class definition has these generic types:
	    * Location - A Location object containing
	    * the current location.
	    * Void     - indicates that progress units are not used
	    * String   - An address passed to onPostExecute()
	 **/
	private class GetAddressTask extends AsyncTask<String, Void, String> {
	        private Context mContext;	       
	        
	        public GetAddressTask(Context context) {
	            super();
	            mContext = context;	            
	        }
	        
	        /**
	         * Get a Geocoder instance, get the latitude and longitude
	         * look up the address, and return it
	         * @params params One or more Location objects
	         * @return A string containing the address of the current
	         * location, or an empty string if no address can be found,
	         * or an error message
	         */
	        @Override
	        protected String doInBackground(String... params) {
	            Geocoder geocoder =
	                    new Geocoder(mContext, Locale.getDefault());
	            // Get the current location from the input parameter list	            
	            String[] splited = params[0].split("\\s+");
	            LatLng loc = new LatLng(Double.parseDouble(splited[0]),Double.parseDouble(splited[1]));
	            // Create a list to contain the result address
	            List<Address> addresses = null;
	            try {	              
	                addresses = geocoder.getFromLocation(loc.latitude,
	                        loc.longitude, 1);
	            } catch (IOException e1) {
	               Log.e("LocationSampleActivity",
	                    "IO Exception in getFromLocation()");
	               e1.printStackTrace();	               
	            //return ("IO Exception trying to get address");
	            return null;
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
	    
	        protected void onPostExecute(String result) {
	        	if (result == null)
	        	  Toast.makeText(getApplicationContext(), 
	            		   "IO Exception, check network connection or restart device", 
	            		   Toast.LENGTH_LONG).show();
	        	else
	        	  updateAddress(result);
	        }	    
	    }
	
}
