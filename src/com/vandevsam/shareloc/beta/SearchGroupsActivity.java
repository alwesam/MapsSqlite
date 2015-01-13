package com.vandevsam.shareloc.beta;

import java.util.ArrayList;
import java.util.List;

import com.vandevsam.shareloc.beta.data.GroupDataManager;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

public class SearchGroupsActivity extends Activity {
	
	private Context context = this;	
	private List<String> list;
	private ArrayAdapter<String> searchListAdapter;
	GroupDataManager data;

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
        
        list = doMySearch("ALL");
        
	    searchIntent(getIntent());
	    
	    searchListAdapter = new ArrayAdapter<String>(
                //the current context (this fragement's parent activity)
                this,
                //ID of list item layout
                R.layout.list_search_item,
                //ID of textView to populate
                R.id.list_search_item_textview,
                //forecast data
                list);

         ListView listView = (ListView) findViewById(R.id.listview_search);
         listView.setAdapter(searchListAdapter); 
    
       //now when I click on a result!
         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {	   
        	  String group = searchListAdapter.getItem(position);
	         //call another activity
        	  Intent detailActivity = new Intent(getBaseContext(), GroupDetailActivity.class)
                                        .putExtra(Intent.EXTRA_TEXT, group);
        	  startActivity(detailActivity);
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
           //move this to OnCreate on MainActivity!
           //ServerUtilFunctions list = new ServerUtilFunctions(this);
           //list.listAllGroup();		
		    ArrayList<String> searchList = new ArrayList<String>();
	        searchList = (ArrayList<String>) data.getAllGroups();
	        return searchList;		
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
		

}