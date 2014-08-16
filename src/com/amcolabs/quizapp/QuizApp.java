package com.amcolabs.quizapp;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;


public class QuizApp extends FrameLayout{
	private User currentUser;
	private UserDeviceManager userDeviceManager;
	private UiUtils uiUtils;
	private ServerCalls serverCalls;
	private DatabaseHelper dbHelper;
	private boolean initializedDb;
	
	public QuizApp(Context context) {
		super(context);
		userDeviceManager = new UserDeviceManager(this);//initialized preferences , device id , pertaining to device
		uiUtils = new UiUtils(this);
		serverCalls = new ServerCalls(this);
	}
	
	public void initializeDb(){
		if(!initializedDb){
			initializedDb = true;
			dbHelper = new DataBaseHelper();
		}
	}
	public FragmentManager getFragmentManager() {
		return getFragmentManager();
	}

	public Context getActivity() {
		return null;
	}
	
	public UserDeviceManager getUserDeviceManager(){
		return userDeviceManager;
	}
	
	public User getUser(){
		return currentUser;
	}

	public void setUser(User user) {
		currentUser = user;
	}

	public void addUiBlock(){
		addUiBlock(UiText.TEXT_LOADING.getValue());	
	}
	public void addUiBlock(String string) {
		getUiUtils().addUiBlock(string);
	}
	public void removeUiBlock(){
		getUiUtils().removeUiBlock();
	}

	public UiUtils getUiUtils() {
		return uiUtils;
	}

	public ServerCalls getServerCalls() {
		// TODO Auto-generated method stub
		return serverCalls;
	}
}
