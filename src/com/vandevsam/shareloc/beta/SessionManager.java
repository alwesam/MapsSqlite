package com.vandevsam.shareloc.beta;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

    SharedPreferences pref;
    Editor editor;
    Context mContext;
    private static final String PREFER_NAME = "AuthenticateShareLoc";
    private static final String SESSION_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "username";
    public static final String KEY_DATE = "date";
     
    // Constructor
    public SessionManager(Context context){
        mContext = context;
        pref = context.getSharedPreferences(PREFER_NAME, 0);
        editor = pref.edit();
    }
     
    //Create login session
    public void loginSession(String name, String username, String date){
        // Storing login value as TRUE
        editor.putBoolean(SESSION_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, username);
        editor.putString(KEY_DATE, date);
        editor.commit();
    }
    
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
         
        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();
         
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
         
        // user email id
        user.put(KEY_DATE, pref.getString(KEY_DATE, null));
         
        // return user
        return user;
    }
    
    public boolean checkLogin(){
        // Check login status
    	return pref.getBoolean(SESSION_LOGIN, false);
    }
    
    public void logoutSession(){         
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();      
    }  
	
}
