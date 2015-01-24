package com.vandevsam.sharespot;

import java.util.ArrayList;
import java.util.List;

import com.vandevsam.sharespot.data.GroupDataManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridView;
 
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetGroupPreference extends Activity {
		
	private GridView gridView;
	private int grCount;
  	
    @Override 
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouppref);              
        //addPreferencesFromResource(R.xml.checkboxgroup);
        
        //TODO add this to a helper function, see newlocation as well
        GroupDataManager gr_data = new GroupDataManager(this);    
        try {
			gr_data.open();
		} catch (Exception e){
			Log.i("Cannot open db", "hello");
		}	                
        List<String> searchList = new ArrayList<String>();
        searchList = gr_data.getJoinedGroups(); 
        
        ArrayAdapter<String> grAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_multiple_choice, searchList);
        
        gridView = (GridView) findViewById(R.id.group_list); 
        gridView.setAdapter(grAdapter);
        grCount = grAdapter.getCount();  
     
        //TODO fix later
     /**   
        try {
			SaveGroupPreference pref = new SaveGroupPreference(this); 
			List<Boolean> group_check = pref.getPrefCheck();
			
			Toast.makeText(getBaseContext(), 
					group_check.size(), 
				   Toast.LENGTH_LONG).show(); 
			           
      //all checked true by default
			for (int i = 0; i < grCount; i++) {
			    gridView.setItemChecked(i, group_check.get(i));                       
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getBaseContext(), 
					"Empty List", 
				   Toast.LENGTH_LONG).show(); 
		}   **/
        
    }
    
    private void savePref(){
    
    	List<String> group = new ArrayList<String>();    	
    	List<Boolean> check = new ArrayList<Boolean>();
    	
    	for (int i = 0; i < grCount; i++) {
            group.add((String) gridView.getItemAtPosition(i));                    
            check.add(gridView.isItemChecked(i));
        }  
    	
    	SaveGroupPreference pref = new SaveGroupPreference(this);    	
    	pref.checkPref(group,check);
    	
    }
    
    public void setPref(){
    	
    }
    
    @Override
    public void onBackPressed() {	
    	
    	//call a method to save
    	savePref();
    	//return to main activity
		Intent resultIntent = new Intent();
		setResult(RESULT_OK, resultIntent);		
		finish();		
	}
   
}
