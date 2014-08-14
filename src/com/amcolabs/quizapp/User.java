package com.amcolabs.quizapp;

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

public class User {

	public static class ShortUserInfo{
		String name;
		String country;
		String status;
		String picture;
	}
	
	private static SharedPreferences preferences;
	private static Activity currentActivity;
	private static String deviceId=null;
	public static boolean hasJustInstalled  = false;
	public static boolean newGcmNotifications  = true;
	
	private static boolean initialized =false;
	
	private static boolean initializedPreferences = false;
	private static boolean dbInitialized;
		
	// initialized on the main screen
	public static void initializePreferences(Activity activity){
		if(!initializedPreferences){
			initializedPreferences = true;
			preferences = activity.getSharedPreferences("vbetabeta", Context.MODE_PRIVATE);
		}
	}
	/**
	 * initialize user
	 * intitalize config
	 * initialize servercalls
	 * initialize immtableItemAdapter
	 */
	
	private static void initialize(final Activity startingActivity){
		if(!initialized){
			initialized = true;
			//setCurrentActivity(startingActivity);
			initializePreferences(startingActivity);
			initializeDb();
			User.hasJustInstalled = User.isFirstTimeUser();// false only after first call to getFeed from server
			User.getDeviceId(startingActivity.getContentResolver());
			
			Config.initialize();
//			ServerCalls.initialize();
		}
	}
	
	public static void clearAllStaticVariables(){
		initialized = false;
		initializedPreferences = false;
		dbInitialized = false;
		User.currentActivity = null;	
		Config.clearAllStaticVariables();
//		StaticPopupDialogBoxes.clearAllStaticVaribles();
//		ServerCalls.clearAllStaticVariables();
	}

	public static void initializeDb(){
		if(!dbInitialized){
//			Config.setDbhelper(OpenHelperManager.getHelper(User.getCurrentActivity(), DatabaseHelper.class));
//			dbInitialized=true;
		}
	}
	
	public  static Activity getCurrentActivity() {
		return currentActivity;
	}
	
	public  static Context getCurrentContext(Context defaultContext) {
		return currentActivity!=null?currentActivity:defaultContext;
	}
	

	public  static void setCurrentActivity(Activity currentActivity) {
		User.currentActivity = currentActivity;
		initialize(currentActivity);
	}

    public static String getDeviceId(ContentResolver resolver){
    	//only first time called will initialize the device messageId 
    	//change to uuid thing later
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
    public static void setPreference(String key, String value) {
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

    public static String getPreference(String key , String defaultValue) {
//    	if(preferenceCache.containsKey(key)){
//    		String val = preferenceCache.get(key);
//    		return val!=null?val:defaultValue;
//    	}
        return preferences.getString(key, defaultValue);
    }
    
	public static void clearUserPreferences(){
		preferences.edit().clear().commit();
	}

	static View getLoadingView(Context context) {
		LinearLayout mainLayout = new LinearLayout(context);
		mainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		mainLayout.setGravity(Gravity.CENTER);
		ImageView headerImageView = new ImageView(context);
		headerImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.logo));
		LayoutParams temp3 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		headerImageView.setLayoutParams(temp3);
	
		mainLayout.addView(headerImageView);
		return mainLayout;
	}

	public static void initializefirstTimeLaunch() {
//		User.setPreference(Config., "true");
	}
	public static void setLongPreference(String key, long l) {
		setPreference(key, Long.toString(l));		
	}
	public Long getLongPreference(String key, long l) {
		String temp = getPreference(key,null);
		if(temp!=null)	
			return Long.parseLong(temp);
		return l;
	}
	
	public static boolean isFirstTimeUser(){
		return User.getPreference(Config.PREF_IS_FIRST_TIME_LOAD, null)==null;
	}
	
	public static String getSharingText(){
//		String sharingText = User.getPreference(Config.PREF_SHARE_APP_TEXT, null);
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
	public static void setAppPaused(AppRunningState state) {
		 currentState = state;
	}
	public static boolean isRunning() {
		// TODO Auto-generated method stub
		return currentState == AppRunningState.IS_RUNNING;
	}
	public static synchronized void setDoublePreference(String key, double val) {
		setPreference(key, Double.toString(val));
	}
	public static synchronized double getDoublePreference(String key, double d) {
		String temp = getPreference(key,null);
		if(temp!=null)	
			return Double.parseDouble(temp);
		return d;

	}
	public static ShortUserInfo getShortUserInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
