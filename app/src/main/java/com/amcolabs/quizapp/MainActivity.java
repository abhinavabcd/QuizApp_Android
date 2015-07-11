package com.amcolabs.quizapp;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;
import java.io.IOException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.amcolabs.quizapp.UserDeviceManager.AppRunningState;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.datalisteners.DataInputListener2;
import com.amcolabs.quizapp.loginutils.GoogleLoginHelper;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.crittercism.app.Crittercism;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;



public class MainActivity extends FragmentActivity {

	QuizApp quizApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        Crittercism.initialize(getApplicationContext(), "545e493f0729df126e000001");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        initQuizApp(savedInstanceState);
//        SatelliteMenu menu = (SatelliteMenu) findViewById(R.id.menu);
//        
////		  Set from XML, possible to programmatically set        
////        float distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, getResources().getDisplayMetrics());
////        menu.setSatelliteDistance((int) distance);
////        menu.setExpandDuration(500);
////        menu.setCloseItemsOnClick(false);
////        menu.setTotalSpacingDegree(60);
//        menu.setTotalSpacingDegree(80);
//        menu.setSatelliteDistance((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()));
//        
//        List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
//        items.add(new SatelliteMenuItem(quizApp.MENU_FRIENDS, R.drawable.friends));
//        items.add(new SatelliteMenuItem(quizApp.MENU_BADGES, R.drawable.badges));
//        items.add(new SatelliteMenuItem(quizApp.MENU_ALL_QUIZZES, R.drawable.all_quizzes));
//        items.add(new SatelliteMenuItem(quizApp.MENU_MESSAGES, R.drawable.messages));
//        items.add(new SatelliteMenuItem(quizApp.MENU_HOME, R.drawable.home));
////        items.add(new SatelliteMenuItem(5, R.drawable.sat_item));
//        menu.addItems(items);        
//        
//        menu.setOnItemClickedListener(new SateliteClickedListener() {
//			
//			public void eventOccured(int badgeId) {
//				quizApp.onMenuClick(badgeId);
//			}
//		});
//        
//        quizApp.setMenu(menu);
    }
    
    public void initQuizApp(Bundle savedInstanceState){
        if(quizApp==null) quizApp = new QuizApp();
        quizApp.setMainActivity(this);
   	
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, quizApp)
                    .commit();
        }
        quizApp.setMenu(getMenu());             

    }
 
    public View getMenu(){
    	return findViewById(R.id.menu_button);
    }
    @Override
    public void onBackPressed() {
        quizApp.onBackPressed();
//        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    DataInputListener2<Integer, Integer, Intent, Void> activityResultListener = null;
    

	public void setActivityResultListener(
			DataInputListener2<Integer, Integer, Intent, Void> activityResultListener) {
		this.activityResultListener = activityResultListener;
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(activityResultListener!=null){
        	activityResultListener.onData(requestCode, resultCode, data);
        }
    }
    
	@Override
	protected void onPause() {
		UserDeviceManager.setAppRunningState(AppRunningState.IS_IN_BACKGROUND);
		UserDeviceManager.lastActiveTime = Config.getCurrentServerTimeStamp();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		quizApp.setMainActivity(this);
		quizApp.setMenu(getMenu());
		UserDeviceManager.setAppRunningState(AppRunningState.IS_RUNNING);
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		quizApp.onDestroy();
		UserDeviceManager.lastActiveTime = Config.getCurrentServerTimeStamp();
		UserDeviceManager.setAppRunningState(AppRunningState.IS_DESTROYED);
		super.onDestroy();
	}
 }
