package com.vandevsam.shareloc.beta;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

public class SearchActivity extends Activity {
	
	private Context context = this;	
	private List<String> list;
	private ArrayAdapter<String> searchListAdapter;
	MarkerDataSource data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		data = new MarkerDataSource(context);
        try {
			data.open();
		} catch (Exception e){
			Log.i("hello", "hello");
		} 
	
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
                //String address = searchListAdapter.getItem(position);
            	//TODO a test
            	//data.close();
            	String address = "49.28964841702669 -122.7909402921796";
                returnResults(address);
            }
			
        });        
	}	
	
	private void returnResults (String slatlng){
				
		Intent resultIntent = new Intent();
		resultIntent.putExtra("note", slatlng);
		setResult(RESULT_OK, resultIntent);
		finish();		
	}
	
	private void searchIntent (Intent intent){
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	         String query = intent.getStringExtra(SearchManager.QUERY);	         
	         list = doMySearch(query);
	    } 
	    else {
	         list = fakeList("fine");
	    }
	}
	
   private ArrayList<String> fakeList(String search){		
		ArrayList<String> searchList = new ArrayList<String>();		
		searchList.add("one");
		searchList.add("two");
		searchList.add("three");
		searchList.add("four");		
		return searchList;		
	}
   
	private ArrayList<String> doMySearch (String search){		
		    ArrayList<String> searchList = new ArrayList<String>();
	        searchList = data.getAddresses(search);
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
	
	@Override
	protected void onStop(){
		super.onStop();
		data.close();
	}
	
}
