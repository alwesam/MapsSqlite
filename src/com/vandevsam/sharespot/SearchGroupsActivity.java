package com.vandevsam.sharespot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vandevsam.sharespot.data.GroupDataManager;

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
import android.widget.Toast;

public class SearchGroupsActivity extends Activity {
	
	private Context context = this;	
	private List<String> list;
	//private ArrayAdapter<String> searchListAdapter;
	GroupDataManager data;
	
	AddressAdapter aa;	
	//new
	HashMap<String, List<String>> group_type;
	
	private static String joined = "Joined";
	private static String unjoined = "Non-Joined";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
					
		data = new GroupDataManager(context);
        try {
			data.open();
		} catch (Exception e){
			Log.i("hello", "hello");
		} 	
  
        group_type = new HashMap<String, List<String>>();
        
        list = listGroups();
        
        if (list.isEmpty()) {
        	Toast.makeText(this, 
        			"Empty list, did you checkout groups?", 
         	     	  Toast.LENGTH_LONG).show();
        	finish();
        }
                
	    searchIntent(getIntent());
	    	    
	    /**searchListAdapter = new ArrayAdapter<String>(
                //the current context (this fragement's parent activity)
                this,
                //ID of list item layout
                R.layout.list_search_item,
                //ID of textView to populate
                R.id.list_search_item_textview,
                //forecast data
                list);
         **/
	    

	     aa = new AddressAdapter(list, group_type, this);

         ListView listView = (ListView) findViewById(R.id.listview_search);
         //listView.setAdapter(searchListAdapter); 
         listView.setAdapter(aa);
    
       //now when I click on a result!
         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {	   
        	 // String group = searchListAdapter.getItem(position);
        	  
        	  String group = aa.getItem(position);
        	          	          	         	  
        	  Intent detailActivity;
        	   //TODO combine these activities into one   
        	  if(data.queryGroup(group)){
        	     if (data.queryStatus(group))
	               detailActivity = new Intent(getBaseContext(), MyGroupDetailActivity.class)
                                        .putExtra("key", group);
        	     else
        		   detailActivity = new Intent(getBaseContext(), GroupDetailActivity.class)
                                          .putExtra("key", group);   
        	  
        	     startActivityForResult(detailActivity,0);
        	  }
           }			
         });	
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
	    searchList = data.getGroups(search); 
        return searchList;		
}
   
	//TODO fix
	private ArrayList<String> listGroups (){

		    try {
				ArrayList<String> searchList = new ArrayList<String>();
								
			  //idiotic implementation, but I'm having a headache, review later
		        for (int i=0;i<2;i++){
		        	if (i==0) {
		        	  searchList.add(joined);
		        	  group_type.put(joined, data.getGroups(0));
		        	}
		        	else{
		              searchList.add(unjoined);
		        	  group_type.put(unjoined, data.getGroups(1));
		        	}		        	
		        	searchList.addAll(data.getGroups(i));
		        }  		
				
				return searchList;
			} catch (Exception e) {
				 Toast.makeText(this, "Try again later", 
           	     	  Toast.LENGTH_LONG).show();	
				e.printStackTrace();
				return null;				
			}		
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent response) {
	  //super.onActivityResult(requestCode, resultCode, response);
       if (response != null){
	     switch(requestCode) {
	       case 0 : {	    	
	         if (resultCode == RESULT_OK) {
	    	  //list = doMySearch("ALL");
	    	  Intent objIntent = new Intent(getApplicationContext(), SearchGroupsActivity.class);
	          startActivity(objIntent);
	         }
	         break;
	       }  	    
	    }	    
      } 
	}	
		

}
