package com.vandevsam.shareloc.beta;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class ProfileActivity extends Activity {
	
	//private CheckBox chkIos, chkAndroid, chkWindows;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
	}
	
	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which checkbox was clicked
	    switch(view.getId()) {
	        case R.id.group_a:
	            if (checked)
	                // Put some meat on the sandwich
	           
	            break;
	        case R.id.group_b:
	            if (checked)
	                // Cheese me	          
	            break;
	        // TODO: Veggie sandwich
	        case R.id.group_c:
	            if (checked)
	                // Cheese me	          
	            break;
	        case R.id.group_d:
	            if (checked)
	                // Cheese me	          
	            break;
	    } //end switch statement
	} //end method

}
