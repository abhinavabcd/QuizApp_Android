package com.amcolabs.quizapp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EmptyStackException;
import java.util.Stack;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.amcolabs.quizapp.appcontrollers.UserHome;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.DatabaseHelper;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;


public class QuizApp extends FrameLayout{
	static final boolean FROM_LEFT = false;
	static final boolean FROM_RIGHT = true;
	static final boolean TO_LEFT = true;
	static final boolean TO_RIGHT = false;
	
	private User currentUser;
	private Stack<AppController> currentAppController;
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
		currentAppController = new Stack<AppController>();
		currentAppController.setSize(2);
		((UserHome)loadAppController(UserHome.class))
			.checkAndShowLoginOrSignupScreen();
	}
	
	public AppController loadAppController(Class<? extends AppController> clazz){
		AppController appController=null; 
		try {
			Constructor<?> constructor = clazz.getConstructor(QuizApp.class);
			appController =(AppController) constructor.newInstance(this);
		} catch (NoSuchMethodException e) {
			appController = new UserHome(this);
			e.printStackTrace();
		} catch (InstantiationException e) {
			appController = new UserHome(this);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			appController = new UserHome(this);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			appController = new UserHome(this);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			appController = new UserHome(this);
			e.printStackTrace();
		}
		currentAppController.push(appController);
		return appController;
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
		if(!currentAppController.peek().onBackPressed()){
			try{
				AppController c = currentAppController.pop();
				animateScreenIn(c.getCurrentScreen(), FROM_LEFT);
			}
			catch(EmptyStackException e) {
				((UserHome)loadAppController(UserHome.class)).checkAndShowLoginOrSignupScreen();
			}
		}
	}

	public void animateScreenIn(Screen newScreen) {
		animateScreenIn(newScreen,FROM_RIGHT);
	}

	public void animateScreenIn(Screen newScreen, boolean fromRight){
		addView(newScreen);
		TranslateAnimation animate = new TranslateAnimation((fromRight?1:-1) *newScreen.getWidth(),0,0,0);
		animate.setDuration(500);
		animate.setFillAfter(true);
		newScreen.startAnimation(animate);
	}
	
	public void animateScreenRemove(Screen currentScreen) {
		animateScreenRemove(currentScreen, TO_LEFT);
	}
	
	public void animateScreenRemove(Screen currentScreen , boolean toLeft) {
		TranslateAnimation animate = new TranslateAnimation(0,(toLeft?-1:1)*currentScreen.getWidth(),0,0);
		animate.setDuration(500);
		animate.setFillAfter(true);
		currentScreen.startAnimation(animate);
		currentScreen.setVisibility(View.GONE);
		removeView(currentScreen);
	}	
}
