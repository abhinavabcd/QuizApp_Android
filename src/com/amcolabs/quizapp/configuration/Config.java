package com.amcolabs.quizapp.configuration;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.databaseutils.UserPreferences;
import com.google.gson.Gson;

public class Config{

	private final Gson gson = new Gson();
	public static final String PREF_SERVER_TIME_DIFF = "serverTimeDiff";
	public static final int RETRY_URL_COUNT = 1;
	public static final String GCM_APP_ID = "591807556804";
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
	public static final String playStoreUrl = "https://play.google.com/store/apps/details?badgeId=com.amcolabs.quizApp";
	public static final String sharingAppText = "Try this app \n "+playStoreUrl;
	public static final int NOTIFICATION_ID_SERVER_PUSH = 10;
	public static final String RECIEVER_NEW_GCM_NOTIFICATION = "com.amcolabs.quizapp.gcmnotification";
	public static final String FORCE_APP_VERSION = "forceAppVersion";
	public static final String PREF_ENCODED_KEY = "encodedKey";
	public static final String PREF_NOT_ACTIVATED = "isNotActivated";
	public static final String PREF_LAST_CATEGORIES_FETCH_TIME = "categoriesFetchTimeStamp";
	public static final String NOTIFICATION_KEY_MESSAGE_TYPE = "messageType";
	public static final String NOTIFICATION_KEY_TEXT_MESSAGE = "message";
	public static final String GOOGLE_PLUS_SERVER_CLIENT_ID = "591807556804-qltit1nk5rga581b5a2j6tuoogum0s79.apps.googleusercontent.com";
	public static final long CLASH_SCREEN_DELAY = 3000;
	public static final long PREQUESTION_FADE_OUT_ANIMATION_TIME = 2000;
	public static final long BOT_INTIALIZE_AFTER_NO_USER_TIME = 10000;
	public static final long QUESTION_END_DELAY_TIME = 3000;
	public static final String KEY_GCM_FROM_USER = "fromUser";
	public static final String KEY_GCM_TEXT_MESSAGE = "textMessage";
    public static final String PREF_GCM_REG_ID = "registration_id";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public static int[] themeColors = new int []{
											Color.rgb(149, 181,76),
								    		Color.rgb(242, 103,22),
								    		Color.rgb(47, 152,171),
								    		Color.rgb(226, 169,67),
								    		Color.rgb(68, 139,196),
//											Color.rgb(255, 250, 120),
//											Color.rgb(102, 232, 148),
//											Color.rgb(156, 197, 255),
//											Color.rgb(209, 113, 232),
//											Color.rgb(252, 187, 157)
										};
	
	public static int  TIMER_SLIGHT_DELAY_START = 500;
	
	public static final double QUIZ_WIN_BONUS = 20;
	public static final double QUIZ_LEVEL_UP_BONUS = 20;
	public static final int PIE_CHART_MAX_FIELDS = 4;
	public static final int MAX_CATEGORIES_ON_HOME_SCREEN = 6;
	public static final int MAX_QUIZZES_ON_HOME_SCREEN = 6;
	public static final String MUSIC_ID = "music_id";
	public static final String APP_VERSION = "app_version";
	public static final String APP_LOADING_VIEW_IMAGE = null;
	public static final String URL_PARAM_IS_CHALLENGED = "isChallenged";
	
	private QuizApp quizApp;
	

	public Config(QuizApp quizApp) {
		this.quizApp = quizApp;
		serverTimeZoneDiff = Double.parseDouble(quizApp.getUserDeviceManager().getPreference(Config.PREF_SERVER_TIME_DIFF , "0"));
	}
	
	
	private int tempThemeCount=0;
	public int getAThemeColor(){
		return themeColors[++tempThemeCount%themeColors.length];
	}
	
	public int getUniqueThemeColor(String a){
		if(a==null) return 0;
		return themeColors[(a.charAt(0)+ a.charAt(a.length()-1))%themeColors.length];
	}
	private int tempBgIndex = 0;
	private List<String> userBgAssets = Arrays.asList( "bg/bg_2.jpg" , "bg/bg_1.jpg");
	public String getRandomImageBg(){
		return userBgAssets.get(tempBgIndex++%userBgAssets.size());
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
	
	
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
