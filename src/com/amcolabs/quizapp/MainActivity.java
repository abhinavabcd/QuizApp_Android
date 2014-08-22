package com.amcolabs.quizapp;

import com.amcolabs.quizapp.UserDeviceManager.AppRunningState;
import com.amcolabs.quizapp.appcontrollers.UserMainController;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;



public class MainActivity extends ActionBarActivity {

	QuizApp quizApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(quizApp==null) quizApp = new QuizApp();
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, quizApp)
                    .commit();
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(UserMainController.SOCIAL_NETWORK_TAG);
        if (fragment != null) { //google plus unnecessary thing
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
    
	@Override
	protected void onPause() {
		UserDeviceManager.setAppRunningState(AppRunningState.IS_IN_BACKGROUND);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		UserDeviceManager.setAppRunningState(AppRunningState.IS_RUNNING);
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		UserDeviceManager.setAppRunningState(AppRunningState.IS_DESTROYED);
		super.onDestroy();
	}


 }
