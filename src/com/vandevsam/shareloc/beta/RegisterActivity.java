package com.vandevsam.shareloc.beta;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class RegisterActivity extends Activity {
	
	//SessionManager session;
	ProgressDialog prgDialog;
	private EditText nameField, usernameField, passwordField, passwordField2;
	private static final String webServer = "108.59.82.39"; //my google CE ip address
	//private static final String webServer = "192.168.0.11"; //localhost	
	private TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);				
		
		nameField = (EditText)findViewById(R.id.enterName);
		usernameField = (EditText)findViewById(R.id.enterUser);
	    passwordField = (EditText)findViewById(R.id.enterPass);
	    passwordField2 = (EditText)findViewById(R.id.reenterPass);
	    
	    //save entered data
	    nameField.setText(nameField.getText().toString());
	    usernameField.setText(usernameField.getText().toString());
		
		prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Creating account, please wait...");
        prgDialog.setCancelable(false);        
        
        textView = (TextView) findViewById(R.id.textView);
        String htmlText = "<p></p><br><br><p align=\"center\">" +
        		"<a href=\"http://108.59.82.39/blog\">VanDevSam</a> &copy; 2014.</p>";
        textView.setText(Html.fromHtml(htmlText));        
		
	}	
	
	//add a method to connect with login db
	public void submitInfo(View view){					 
		   
		   String name =  nameField.getText().toString();
	       String username = usernameField.getText().toString();
	       String password = passwordField.getText().toString();
	       String password2 = passwordField2.getText().toString();
	       
	       //NOTE: remember to compare string values, use .equals(), the == compares references (objects) not values
	       
	    // Create AsycHttpClient object
	       AsyncHttpClient client = new AsyncHttpClient();	       
	       // Http Request Params Object
	       RequestParams params = new RequestParams();
	       // Show ProgressBar
	       prgDialog.show();
	       params.put("name", name);
	       params.put("user", username);
	       params.put("pass", password);
	       params.put("repass", password2);
	       //TODO connect via https
	       //TODO write a register php file
	       client.post("http://"+webServer+"/sqlitemysqlsyncMarkers/phplogin/register.php", params, new AsyncHttpResponseHandler() {
	               @Override
	               public void onSuccess(String response) {
	                   // Hide ProgressBar
	            	   prgDialog.hide();
	            	   
	            	   try {
						JSONObject jObject = new JSONObject(response);						
						 if (jObject.getBoolean("status")) {
							 Toast.makeText(getApplicationContext(), "Account successfully created!", 
		                    		   Toast.LENGTH_LONG).show();						 						 
		                     loadLoginActivity();
		                   } 
					    } catch (JSONException e) {					
						   e.printStackTrace();
					    }                	                   
	                                
	               }
	               // When error occured
	               @Override
	               public void onFailure(int statusCode, Throwable error, String content) {	                   
	                   // Hide ProgressBar
	                   prgDialog.hide();
	                   if (statusCode == 404) {
	                       Toast.makeText(getApplicationContext(), "Usernmae and/or password incorrect", 
	                    		   Toast.LENGTH_LONG).show();
	                   } else if (statusCode == 500) {
	                       Toast.makeText(getApplicationContext(), "Something went terrible at server end", 
	                    		   Toast.LENGTH_LONG).show();
	                   } else {
	                       Toast.makeText(getApplicationContext(), "Device might not be connected to network",
	                               Toast.LENGTH_LONG).show();
	                   }
	               }
	       });	
	     
	}
	
	public void loadLoginActivity() {
        Intent objIntent = new Intent(getApplicationContext(), AuthenticateActivity.class);                                     
        objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        objIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(objIntent);
        finish();
    }


}
