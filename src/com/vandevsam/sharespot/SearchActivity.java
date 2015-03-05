package com.vandevsam.sharespot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vandevsam.sharespot.data.GroupDataManager;
import com.vandevsam.sharespot.data.MarkerDataManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SearchActivity extends Activity {
	
	private Context context = this;	
	private List<String> list;
	MarkerDataManager data;
	private String coordinates;
	
	SessionManager session;
	
	//new
	HashMap<String, List<String>> group_address;
	
	AddressAdapter aa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
        session = new SessionManager(getApplicationContext());		
        					
		data = new MarkerDataManager(context);
        try {
			data.open();
		} catch (Exception e){
			Log.i("hello", "hello");
		} 	
        
        //I don't like this implementation, seems inefficient, TODO review
        group_address = new HashMap<String, List<String>>();
     
        list = listAddresses();
        
	    searchIntent(getIntent());	 
	    
	    aa = new AddressAdapter(list, group_address, this);
	    
        ListView listView = (ListView) findViewById(R.id.listview_search);
        listView.setAdapter(aa); 
    
       //now when I click on a result!
         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {        	   
        	 String item_str = (String) aa.getItem(position);
        	 if(data.queryAddress(item_str)){        	   
	            coordinates = data.getPosition(item_str);
	            if(data.queryPosition(coordinates))
	               returnResults(coordinates); //return to map activity
        	 }
        	 //else do nothing
           }			
         });	
	}	
	
	private void returnResults (String slatlng){
		
		//Intent resultIntent = new Intent();
		Intent resultIntent = new Intent(this, MainActivity.class);
		resultIntent.putExtra("note", slatlng);		
		//setResult(RESULT_OK, resultIntent);		
		//finish();	
		startActivity(resultIntent);
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    searchIntent(intent);
	}
	
	private void searchIntent (Intent intent){
		//wait for user
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			
	         String query = intent.getStringExtra(SearchManager.QUERY);	         
	         list = doMySearch(query);
	    }
	}
   
	private ArrayList<String> doMySearch (String search){
		    ArrayList<String> searchList = new ArrayList<String>();		      
		    searchList = data.getAddresses(search,0); 
	        return searchList;		
	}
	
	private ArrayList<String> listAddresses (){			
					
		GroupDataManager group = new GroupDataManager(context);
        try {
			group.open();
		} catch (Exception e){
			Log.i("hello", "hello");
		} 
        //TODO, test
        List<String> groups = group.getJoinedGroups();
             
        ArrayList<String> searchList = new ArrayList<String>();
        for (int i=0;i<groups.size();i++){
        	searchList.add(groups.get(i));        	
        	searchList.addAll(data.getAddresses(groups.get(i),1));                 	
        	group_address.put(groups.get(i), data.getAddresses(groups.get(i),0));
        }   
        
        return searchList;		
}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the options menu from XML
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.search, menu);
	    // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    return true;
	}
		
}
