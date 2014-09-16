package com.amcolabs.quizapp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.notificationutils.NotificationReciever;

public class UserDeviceManager {

	private SharedPreferences preferences;
	private static String deviceId=null;
	public boolean hasJustInstalled  = false;
	public static boolean newGcmNotifications  = true;
	
	
	private boolean initializedPreferences = false;
	private QuizApp quizApp;
	
	public UserDeviceManager(QuizApp quizApp) {
		this.quizApp = quizApp; 
		initializePreferences(quizApp.getContext());
		hasJustInstalled = isFirstTimeUser();// false only after first call to getFeed from server
		UserDeviceManager.getDeviceId(quizApp.getContentResolver());
		currentState = AppRunningState.IS_RUNNING;
	}
		
	// initialized on the main screen
	public void initializePreferences(Context context){
		if(!initializedPreferences){
			initializedPreferences = true;
			preferences = context.getSharedPreferences("quizUserPrefs", Context.MODE_PRIVATE);
		}
	}
	
	public static void clearAllStaticVariables(){
	}

    public static String getDeviceId(ContentResolver resolver){
        if(deviceId==null){
	    	deviceId = Secure.getString(resolver,
	                Secure.ANDROID_ID);
	    		
        }
    	return deviceId;
    }
    
    public static String getDeviceId(){
    	return deviceId;
    }
    
//    static HashMap<String,String> preferenceCache = new HashMap<String,String>();
    public void setPreference(String key, String value) {
        SharedPreferences prefs = preferences;
        SharedPreferences.Editor editor = prefs.edit();
        if(key!=null)
        	editor.putString(key, value);
        else{
        	editor.remove(key);
        }
 //       preferenceCache.put(key,value);
        editor.commit();
    }

    public String getPreference(String key , String defaultValue) {
//    	if(preferenceCache.containsKey(key)){
//    		String val = preferenceCache.get(key);
//    		return val!=null?val:defaultValue;
//    	}
        return preferences.getString(key, defaultValue);
    }
    
	public void clearUserPreferences(){
		preferences.edit().clear().commit();
	}

	public View getLoadingView(Context context) {
		LinearLayout mainLayout = new LinearLayout(context);
		mainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		mainLayout.setGravity(Gravity.CENTER);
		ImageView headerImageView = new ImageView(context);
		headerImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.small_logo));
		LayoutParams temp3 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		headerImageView.setLayoutParams(temp3);
	
		mainLayout.addView(headerImageView);
		return mainLayout;
	}

	public void initializefirstTimeLaunch() {
//		UserDeviceManager.setPreference(Config., "true");
	}
	public void setLongPreference(String key, long l) {
		setPreference(key, Long.toString(l));		
	}
	public Long getLongPreference(String key, long l) {
		String temp = getPreference(key,null);
		if(temp!=null)	
			return Long.parseLong(temp);
		return l;
	}
	
	public boolean isFirstTimeUser(){
		boolean ret = getPreference(Config.PREF_IS_FIRST_TIME_LOAD, null)==null;
		return ret;
	}
	
	public static String getSharingText(){
//		String sharingText = UserDeviceManager.getPreference(Config.PREF_SHARE_APP_TEXT, null);
//		if(sharingText==null) return UiText.SHARING_TEXT.getValue(Config.sharingAppText);
//		return sharingText;		
		return null;
	}
	
	
	static enum AppRunningState{
		IS_IN_BACKGROUND,
		IS_RUNNING,
		IS_DESTROYED;
	};

	static AppRunningState currentState = AppRunningState.IS_DESTROYED;
	public static boolean isAppPaused() {
		return AppRunningState.IS_IN_BACKGROUND == currentState;
	}
	public static void setAppRunningState(AppRunningState state) {
		 currentState = state;
		 NotificationReciever.destroyAllListeners();
	}
	public static boolean isRunning() {
		// TODO Auto-generated method stub
		return currentState == AppRunningState.IS_RUNNING;
	}
	public synchronized void setDoublePreference(String key, double val) {
		setPreference(key, Double.toString(val));
	}
	public synchronized double getDoublePreference(String key, double d) {
		String temp = getPreference(key,null);
		if(temp!=null)	
			return Double.parseDouble(temp);
		return d;

	}
	String encodedKey;
	public String getEncodedKey() {
		if(encodedKey==null)
			encodedKey = getPreference(Config.PREF_ENCODED_KEY, null);
		try {
			if(encodedKey==null) return null;
			return URLEncoder.encode(encodedKey,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	
}
