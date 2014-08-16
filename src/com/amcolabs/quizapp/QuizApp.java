package com.amcolabs.quizapp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import com.amcolabs.quizapp.appcontrollers.UserHome;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.DatabaseHelper;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;


public class QuizApp extends FrameLayout{
	private User currentUser;
	private AppController currentAppController;
	private UserDeviceManager userDeviceManager;
	private UiUtils uiUtils;
	private ServerCalls serverCalls;
	private DatabaseHelper dbHelper;
	private boolean initializedDb;
	private Config config;
	
	public QuizApp(Context context) {
		super(context);
		userDeviceManager = new UserDeviceManager(this);//initialized preferences , device id , pertaining to device
		config = new Config(this);
		uiUtils = new UiUtils(this);
		serverCalls = new ServerCalls(this);
		((UserHome)loadAppController(UserHome.class))
			.checkAndShowLoginOrSignupScreen();
	}
	
	public AppController loadAppController(Class<? extends AppController> clazz){
		try {
			Constructor<?> constructor = clazz.getConstructor(QuizApp.class);
			currentAppController = (AppController) constructor.newInstance(this);
		} catch (NoSuchMethodException e) {
			currentAppController = new UserHome(this);
			e.printStackTrace();
		} catch (InstantiationException e) {
			currentAppController = new UserHome(this);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			currentAppController = new UserHome(this);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			currentAppController = new UserHome(this);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			currentAppController = new UserHome(this);
			e.printStackTrace();
		}
		return currentAppController;
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

	public Config getConfig() {
		return config;
	}

	public DatabaseHelper getDataBaseHelper() {
		if(!initializedDb){
			initializedDb = true;
			dbHelper = DatabaseHelper.getHelper(this);
		}
		return dbHelper;
	}

	public void onBackPressed() {
		currentAppController.onBackPressed();
	}
}
