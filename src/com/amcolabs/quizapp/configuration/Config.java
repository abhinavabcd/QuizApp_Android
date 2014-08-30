package com.amcolabs.quizapp.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.ImageButton;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.UserDeviceManager;
import com.amcolabs.quizapp.databaseutils.UserPreferences;
import com.google.gson.Gson;

public class Config{

	private final Gson gson = new Gson();
	public static final String PREF_SERVER_TIME_DIFF = "serverTimeDiff";
	public static final int RETRY_URL_COUNT = 1;
	public static final String AD_UNIT_ID = "ca-app-pub-3957510202036052/9058751323";
	public static final String GCM_APP_ID = "246677287773";
	public static final int DEFAULT_TEXT_COLOR = Color.BLACK;
	
	
	//contact photo prefs
	public static final Locale LOCALE = Locale.getDefault();
	public static final int NOTIFICATION_ID = 12323;
	
	public static final boolean IS_TEST_BUILD = true;
	public static final boolean ENABLE_LOG = true;
	
	public static final String PREF_IS_FIRST_TIME_LOAD = "isFirstTimeUser";
	public static final String VERSION_TEXT = "v0.0.1b";
	public static final String PREF_SERVER_TIME = "serverTime";
	
	public static final String PREF_USER_NAME = "userName";
	public static final String PREF_APP_RATING = "appRating";
	public static final String playStoreUrl = "https://play.google.com/store/apps/details?id=com.amcolabs.quizApp";
	public static final String sharingAppText = "Try this app \n "+playStoreUrl;
	public static final int NOTIFICATION_ID_SERVER_PUSH = 10;
	public static final String FORCE_APP_VERSION = "forceAppVersion";
	public static final String PREF_ENCODED_KEY = "encodedKey";
	public static final String PREF_NOT_ACTIVATED = "isNotActivated";
	public static final String PREF_LAST_CATEGORIES_FETCH_TIME = "categoriesFetchTimeStamp";
	public static final String NOTIFICATION_KEY_MESSAGE_TYPE = "notificationType";
	public static final String NOTIFICATION_KEY_TEXT_MESSAGE = "message";
	public static final String GOOGLE_PLUS_SERVER_CLIENT_ID = "591807556804-qltit1nk5rga581b5a2j6tuoogum0s79.apps.googleusercontent.com";
	public static final String CDN_IMAGES_PATH = "http://localhost:8081/images";
	public static int[] themeColors = new int []{Color.rgb(139, 171,66),
									    		Color.rgb(232, 93,12),
									    		Color.rgb(37, 142,161),
									    		Color.rgb(216, 159,57),
									    		Color.rgb(58, 129,186),
										};
	
	
	private QuizApp quizApp;
	

	public Config(QuizApp quizApp) {
		this.quizApp = quizApp;
		serverTimeZoneDiff = Double.parseDouble(quizApp.getUserDeviceManager().getPreference(Config.PREF_SERVER_TIME_DIFF , "0"));
	}
	
	
	private int tempThemeCount=0;
	public int getAThemeColor(){
		return themeColors[++tempThemeCount%themeColors.length];
	}
	
	private int tempBgIndex = 0;
	public String getRandomImageBg(){
		return "images/bg_2.jpg";
	}
	
	private static double serverTime = 0;
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
	
	public static  double getCurrentTimeStamp(){
		return System.currentTimeMillis()*1.0d / 1000; //(new Date()).getTime()/1000;
	}

	public static long getCurrentNanos() {
		return System.nanoTime();	 //(new Date()).getTime()/1000;
	}
	public static double getElapsedTimeInSec(double elapsedTime){
		return  (double) ((elapsedTime) / 1000000000.0);
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
	
	public void setServerTime(double serverTime , double webRequestTimeInNanos) {
		Config.serverTime = serverTime;
		Config.serverTimeZoneDiff = getCurrentTimeStamp() - (serverTime+ getElapsedTimeInSec(webRequestTimeInNanos)/2 );	
		quizApp.getUserDeviceManager().setPreference(Config.PREF_SERVER_TIME_DIFF , Double.toString(Config.serverTimeZoneDiff));
		try {
			quizApp.getDataBaseHelper().getUserPreferencesDao().createOrUpdate(new UserPreferences(Config.PREF_SERVER_TIME_DIFF, Double.toString(Config.serverTimeZoneDiff)));
		} catch (SQLException e) {
			e.printStackTrace();
		} 
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

	public Gson getGson() {
		return gson;
	}
}
