package com.amcolabs.quizapp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import com.amcolabs.quizapp.appcontrollers.UserHome;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.DatabaseHelper;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;

public class QuizApp extends ActionBarActivity implements AnimationListener {



	private FrameLayout mainFrame;

	static final boolean FROM_LEFT = false;
	static final boolean FROM_RIGHT = true;
	static final boolean TO_LEFT = true;
	static final boolean TO_RIGHT = false;
	
	private User currentUser;
	private Stack<AppController> appControllerStack;
	private UserDeviceManager userDeviceManager;
	private UiUtils uiUtils;
	private ServerCalls serverCalls;
	private DatabaseHelper dbHelper;
	private boolean initializedDb;
	private Config config;
	private LinkedList<Screen> disposeScreens;

	private View loadingView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainFrame = (FrameLayout)getLayoutInflater().inflate(R.layout.activity_main,null);
		setContentView(mainFrame);
		userDeviceManager = new UserDeviceManager(this);//initialized preferences , device id , pertaining to device
		config = new Config(this);
		uiUtils = new UiUtils(this);
		serverCalls = new ServerCalls(this);
		appControllerStack = new Stack<AppController>();
		appControllerStack.setSize(2);
		loadingView = userDeviceManager.getLoadingView(this);
		disposeScreens = new LinkedList<Screen>();
		addView(loadingView);

		((UserHome)loadAppController(UserHome.class))
			.checkAndShowLoginOrSignupScreen();
	}

	private void addView(View view) {
		mainFrame.addView(view);
	}

	private void removeView(Screen screen) {
		mainFrame.removeView(screen);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
		appControllerStack.push(appController);
		return appController;
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
		if(!appControllerStack.peek().onBackPressed()){
			try{
				AppController c = appControllerStack.pop();
				animateScreenIn(c.getCurrentScreen(), FROM_LEFT);
			}
			catch(EmptyStackException e) {
				finish();
			}
		}
	}

	public void animateScreenIn(Screen newScreen) {
		animateScreenIn(newScreen,FROM_RIGHT);
	}

	public void animateScreenRemove(Screen currentScreen) {
		animateScreenRemove(currentScreen, TO_LEFT);
	}
	

	public void animateScreenIn(Screen newScreen, boolean fromRight){
		addView(newScreen);
		loadingView.setVisibility(View.INVISIBLE);
		if(fromRight)
			newScreen.startAnimation(getUiUtils().getAnimationSlideInRight());
		else
			newScreen.startAnimation(getUiUtils().getAnimationSlideInLeft());
				
	}
		public void animateScreenRemove(Screen currentScreen , boolean toLeft) {
		if(toLeft){
			currentScreen.startAnimation(getUiUtils().getAnimationSlideOutLeft());
		}
		else{
			currentScreen.startAnimation(getUiUtils().getAnimationSlideOutRight());
		}
		disposeScreens.add(currentScreen);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		loadingView.setVisibility(View.VISIBLE);//while removing
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		try{
			Screen screen = disposeScreens.remove();
			removeView(screen);
		}
		catch(NoSuchElementException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(!appControllerStack.isEmpty() && appControllerStack.peek() instanceof UserHome){
	        Fragment fragment = getSupportFragmentManager().findFragmentByTag(UserHome.SOCIAL_NETWORK_TAG);
	        if (fragment != null) { //google plus unnecessary thing
	            fragment.onActivityResult(requestCode, resultCode, data);
	        }
        }
    }


}
