package com.amcolabs.quizapp;

import java.io.IOException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.amcolabs.quizapp.UserDeviceManager.AppRunningState;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.datalisteners.DataInputListener2;
import com.amcolabs.quizapp.loginutils.GoogleLoginHelper;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.Plus;



public class MainActivity extends ActionBarActivity {

	QuizApp quizApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } 
        

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
        
        if(resultCode==GoogleLoginHelper.RC_GOOLE_SIGN_IN){
        	
        }
        if(activityResultListener!=null){
        	activityResultListener.onData(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        
    }
    
	@Override
	protected void onPause() {
		UserDeviceManager.setAppRunningState(AppRunningState.IS_IN_BACKGROUND);
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
		UserDeviceManager.setAppRunningState(AppRunningState.IS_DESTROYED);
		super.onDestroy();
	}
	
	public void getTokenAndUser(final User user , final DataInputListener<User> listener){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				quizApp.addUiBlock(UiText.CONNECTING.getValue());
		        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
		            @Override
		            protected String doInBackground(Void... params) {
		                String token;
		                try {
		                	
		                	token = GoogleAuthUtil
									.getToken(
											quizApp.getContext().getApplicationContext(),
											user.emailId,
											"oauth2:"//+"server"
													//+":client_id:" + Config.GOOGLE_PLUS_SERVER_CLIENT_ID 
						                            //+":api_scope:"
													+ Scopes.PLUS_LOGIN
													+ " "+Scopes.PROFILE+" "+Scopes.PLUS_ME);
		                } 
		                catch (UserRecoverableAuthException e) {
		              	     // Recover
		                	
		                	startActivityForResult(e.getIntent(), GoogleLoginHelper.RC_GOOLE_SIGN_IN);
		                    e.printStackTrace();
		                    token= null;
		             	} catch (GoogleAuthException authEx) {
		             		authEx.printStackTrace();
		              	     authEx.getMessage();
		              	     token = null;
		              	} catch (IOException e) {
							e.printStackTrace();
							token=null;
						} 
		                finally{
		                }
		                return token;
		            }

		            @Override
		            protected void onPostExecute(String token) {
		                quizApp.removeUiBlock();
		                if(token != null) {
		                	user.googlePlus = token;
		                	listener.onData(user);
		                } 
		                else {
		                	listener.onData(null);
		                }
		            }
		        };
		        task.execute();				
			}
		}, 0);
	}
 }
