package com.amcolabs.quizapp;

import com.amcolabs.quizapp.UserDeviceManager.AppRunningState;
import com.amcolabs.quizapp.appcontrollers.UserHomeController;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;



public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new QuizApp())
                    .commit();
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        super.onBackPressed();
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
        
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(UserHomeController.SOCIAL_NETWORK_TAG);
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
