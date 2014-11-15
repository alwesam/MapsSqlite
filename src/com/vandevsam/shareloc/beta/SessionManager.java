package com.vandevsam.shareloc.beta;

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
     
    // Constructor
    public SessionManager(Context context){
        mContext = context;
        pref = context.getSharedPreferences(PREFER_NAME, 0);
        editor = pref.edit();
    }
     
    //Create login session
    public void loginSession(String name, String email){
        // Storing login value as TRUE
        editor.putBoolean(SESSION_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.commit();
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
