package com.amcolabs.quizapp;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;


public class QuizApp extends FrameLayout{
	private User currentUser;
	private UserDeviceManager userDeviceManager;
	
	public QuizApp(Context context) {
		super(context);
		userDeviceManager = new UserDeviceManager();
		userDeviceManager.initializePreferences(context);//partial initialize
	}
	
	public FragmentManager getFragmentManager() {
		return getFragmentManager();
	}

	public Context getActivity() {
		return null;
	}
	
	public UserDeviceManager getUserDeviceManager(){
		return null;
	}
	
	public User getUser(){
		return currentUser;
	}

	public void setUser(User user) {
		currentUser = user;
	}
}
