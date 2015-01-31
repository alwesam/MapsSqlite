package com.vandevsam.sharespot;

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

public class AuthenticateActivity extends Activity {
	
	SessionManager session;
	ProgressDialog prgDialog;
	private EditText usernameField, passwordField;
	private static final String webServer = "108.59.82.39"; //my google CE ip address
	//private static final String webServer = "192.168.0.11"; //localhost
	private TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate);	
		
		session = new SessionManager(getApplicationContext());
		
		//if already logged in, skip to map main activity
		if(session.checkLogin())
			loadMainActivity();
		
		usernameField = (EditText)findViewById(R.id.username);
	    passwordField = (EditText)findViewById(R.id.password);
		
		prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Authenticating, please wait...");
        prgDialog.setCancelable(false);
        
        textView = (TextView) findViewById(R.id.textView);
        String htmlText = "<p></p><br><br><p align=\"center\">" +
        		"<a href=\"http://108.59.82.39/blog\">VanDevSam</a> &copy; 2014.</p>";
        textView.setText(Html.fromHtml(htmlText));
	}	
	
	/**
	 * Enter user name & password 
	 */
    public void passLogin(View view) {
	       // Create AsycHttpClient object
	       AsyncHttpClient client = new AsyncHttpClient();	       
	       // Http Request Params Object
	       RequestParams params = new RequestParams();
	       final String username = usernameField.getText().toString();
	       String password = passwordField.getText().toString();
	       // Show ProgressBar
	       prgDialog.show();
	       params.put("user", username);
	       params.put("pass", password);
	       //TODO connect via https
	       client.post("http://"+webServer+"/b/phpfiles/phplogin/login.php", params, new AsyncHttpResponseHandler() {
	               @Override
	               public void onSuccess(String response) {
	                   // Hide ProgressBar
	            	   prgDialog.hide();		            	   
	            	   try {
						JSONObject jObject = new JSONObject(response);						
						 if (jObject.getBoolean("status")) {
							 session.loginSession(jObject.getString("name"), username, jObject.getString("date"));
							 Toast.makeText(getApplicationContext(), "Welcome "+jObject.getString("name"), 
               		                     Toast.LENGTH_LONG).show();							 
		                     loadMainActivity();
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
    
    public void guestLogin(View view){
    	loadMainActivity();
    }
    
    public void createAccount(View view){
    	Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
    	startActivity(register);
    	finish();
    }

    public void loadMainActivity() {
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);                                     
        objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        objIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(objIntent);
        finish();
    }

}
