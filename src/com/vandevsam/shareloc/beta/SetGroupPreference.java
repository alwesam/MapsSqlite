package com.vandevsam.shareloc.beta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vandevsam.shareloc.beta.data.GroupDataManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
//import android.preference.CheckBoxPreference;
//import android.preference.PreferenceActivity;
//import android.preference.PreferenceCategory;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridView;
 
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetGroupPreference extends Activity {
		
	private GridView gridView;
	private int grCount;
	
	SharedPreferences pref;
    Editor editor;
    
    private static final String PREFER_NAME = "groupslist";
    private static final String KEY_CHECKED = "IsChecked";
    public static final String KEY_NAME = "group";
    
   /** public SetGroupPreference(Context c){        
        pref = c.getSharedPreferences(PREFER_NAME, 0);
        editor = pref.edit();
    }**/
	
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
        searchList = gr_data.getAllGroups(); 
        
        ArrayAdapter<String> grAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_multiple_choice, searchList);
        
        gridView = (GridView) findViewById(R.id.group_list); 
        gridView.setAdapter(grAdapter);
        grCount = grAdapter.getCount();  
                   
       //all checked true by default
        for (int i = 0; i < grCount; i++) {
            gridView.setItemChecked(i, true);                       
        }  
        
    }
    
    private void savePref(){
    
    	List<String> group = new ArrayList<String>();    	
    	List<Boolean> check = new ArrayList<Boolean>();
    	
    	for (int i = 0; i < grCount; i++) {
            group.add((String) gridView.getItemAtPosition(i));
            
           /* if (gridView.isItemChecked(i))
               check.add("True");
            else
               check.add("False");*/
            
            check.add(gridView.isItemChecked(i));
            
        }  
    	
    	SaveGroupPreference pref = new SaveGroupPreference(this);    	
    	pref.checkPref(group,check);
    	
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
