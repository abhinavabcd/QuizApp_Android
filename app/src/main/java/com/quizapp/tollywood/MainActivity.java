package com.quizapp.tollywood;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.quizapp.tollywood.configuration.Config;
import com.quizapp.tollywood.datalisteners.DataInputListener2;


public class MainActivity extends FragmentActivity {

	QuizApp quizApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        //Initialize notificaton reciever


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
		super.onPause();
        NotificationReciever.setOffline();
        UserDeviceManager.lastActiveTime = Config.getCurrentServerTimeStamp();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        NotificationReciever.setOnline();
        quizApp.setMainActivity(this);
        quizApp.setMenu(getMenu());
    }
	
	@Override
	protected void onDestroy() {
        quizApp.onDestroy();
        UserDeviceManager.lastActiveTime = Config.getCurrentServerTimeStamp();
		super.onDestroy();
	}
 }
