package com.amcolabs.quizapp.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.ImageButton;

import com.amcolabs.quizapp.User;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

public class Config{

	public static final Gson gson = new Gson();
	public static final String PREF_SERVER_TIME_DIFF = "serverTimeDiff";
	public static final int RETRY_URL_COUNT = 1;
	public static final String AD_UNIT_ID = "ca-app-pub-3957510202036052/9058751323";
	public static final String GCM_APP_ID = "246677287773";
	public static final int DEFAULT_TEXT_COLOR = Color.BLACK;
	
	
	//contact photo prefs
	public static final Locale LOCALE = Locale.getDefault();
	public static final int NOTIFICATION_ID = 12323;
	
	public static final boolean IS_TEST_BUILD = false;
	public static final boolean ENABLE_LOG = false;
	
	public static final String PREF_IS_FIRST_TIME_LOAD = "isFirstTimeUser";
	public static final String VERSION_TEXT = "v0.0.1b";
	public static final String PREF_SERVER_TIME = "serverTime";
	
	public static final String PREF_USER_NAME = "userName";
	public static final String PREF_APP_RATING = "appRating";
	public static final String playStoreUrl = "https://play.google.com/store/apps/details?id=com.amcolabs.quizApp";
	public static final String sharingAppText = "Try this app \n "+playStoreUrl;
	public static final int NOTIFICATION_ID_SERVER_PUSH = 10;
	public static final String FORCE_APP_VERSION = "forceAppVersion";

	public static File sdDir = new File(Environment.getExternalStorageDirectory().getPath());
	
	
	private static double serverTime = 0;

	private static DatabaseHelper dbhelper = null;//OpenHelperManager.getHelper(User.getCurrentActivity(), DatabaseHelper.class);

	public static DatabaseHelper getDbhelper() {
		return dbhelper;
	}

	public static void setDbhelper(DatabaseHelper dbhelper) {
		Config.dbhelper = dbhelper;
	}

	public static List<String> metaTitles= new ArrayList<String>();
	public static double serverTimeZoneDiff = 0;
	
	public static double getCurrentServerTimeStamp(){
		return getCurrentTimeStamp() - serverTimeZoneDiff;
	}
	
	public static double convertToUserTimeStamp(double serverTimeStamp){
		return serverTimeStamp + serverTimeZoneDiff;
	}
	
	public static double convertToServerTimeStamp(double userTimeStamp){
		return userTimeStamp - serverTimeZoneDiff;
	}
	
	public static double getCurrentTimeStamp(){
		return System.currentTimeMillis()*1.0d / 1000; //(new Date()).getTime()/1000;
	}

	public static long getCurrentNanos() {
		return System.nanoTime();	 //(new Date()).getTime()/1000;
	}
	public static double getElapsedTimeInSec(double elapsedTime){
		return  (double) ((elapsedTime) / 1000000000.0);
	}
	
	public static void initialize(){
		metaTitles.add("Before");
		metaTitles.add("After");
		metaTitles.add("During");
		metaTitles.add("Subject");
		sdDir.mkdir();
		Config.serverTimeZoneDiff = Double.parseDouble(User.getPreference(Config.PREF_SERVER_TIME_DIFF , "0"));
	}

	public static void loadCategries(){	
        BufferedReader reader = null;
        StringBuilder out = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(User.getCurrentActivity().getAssets().open("CountryCodes.json")));
	        String line;
	        try {
				while ((line = reader.readLine()) != null) {
				    out.append(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        reader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        System.out.println(out.toString());   //Prints the string content read from input stream
		try { 
			JSONObject jsonobj = new JSONObject(out.toString());
			Iterator<Object> tmp = jsonobj.keys();
			while(tmp.hasNext()){
				String cname = (String) tmp.next();
				String t = jsonobj.getString(cname);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat ("E dd.MMM.yyyy 'at' hh:mm:ss a");
		formatter.setTimeZone(TimeZone.getDefault());
		return formatter.format(date);
	}
//TODO: movie to UiUtils
	public static void setImageViewBg(ImageButton closeButton, Context c, String path) {
		// TODO Auto-generated method stub
    	try {
			closeButton.setImageDrawable(Drawable.createFromStream(c.getAssets().open(path), null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void setServerTime(double serverTime , double webRequestTimeInNanos) {
		Config.serverTime = serverTime;
		Config.serverTimeZoneDiff = getCurrentTimeStamp() - (serverTime+ getElapsedTimeInSec(webRequestTimeInNanos)/2 );	
		User.setPreference(Config.PREF_SERVER_TIME_DIFF , Double.toString(Config.serverTimeZoneDiff));
//		dbhelper.getUserPreferencesDataDao().createOrUpdate(new UserPreferences(Config.PREF_SERVER_TIME_DIFF, Double.toString(Config.serverTimeZoneDiff))); 
	}

	public static double getMaxTime(){
		return System.currentTimeMillis() + Double.valueOf("315360000000") ; // Ten years from now
	}
	
	public static double getMinTime(){
		return System.currentTimeMillis() - 2000;
	}
	
	
	public static void clearAllStaticVariables(){
	
	}
	
	public static boolean almostEqual(double a , double b){
		if(Math.abs(a-b)<0.00002)
			return true;
		return false;
	}
}
