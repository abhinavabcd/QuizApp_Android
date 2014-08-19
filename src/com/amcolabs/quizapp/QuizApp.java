package com.amcolabs.quizapp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import com.amcolabs.quizapp.UserDeviceManager.AppRunningState;
import com.amcolabs.quizapp.appcontrollers.UserHomeController;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.DatabaseHelper;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;

public class QuizApp extends Fragment implements AnimationListener {



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

	private boolean initialized = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainFrame = (FrameLayout)getActivity().getLayoutInflater().inflate(R.layout.activity_main,null);
		addView(loadingView);
		((UserHomeController)loadAppController(UserHomeController.class))
		.checkAndShowCategories();

		return mainFrame;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!initialized){
			initialized = true;
			userDeviceManager = new UserDeviceManager(this);//initialized preferences , device id , pertaining to device
			config = new Config(this);
			uiUtils = new UiUtils(this);
			serverCalls = new ServerCalls(this);
			appControllerStack = new Stack<AppController>();
			appControllerStack.setSize(3);
			loadingView = userDeviceManager.getLoadingView(this.getActivity());
			disposeScreens = new LinkedList<Screen>();
		}
	}

	private void addView(View view) {
		mainFrame.addView(view);
	}

	private void removeView(Screen screen) {
		mainFrame.removeView(screen);
	}
	
	public AppController loadAppController(Class<? extends AppController> clazz){
		AppController appController=null; 
		try {
			Constructor<?> constructor = clazz.getConstructor(QuizApp.class);
			appController =(AppController) constructor.newInstance(this);
		} catch (NoSuchMethodException e) {
			appController = new UserHomeController(this);
			e.printStackTrace();
		} catch (InstantiationException e) {
			appController = new UserHomeController(this);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			appController = new UserHomeController(this);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			appController = new UserHomeController(this);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			appController = new UserHomeController(this);
			e.printStackTrace();
		} catch (java.lang.InstantiationException e) {
			// TODO Auto-generated catch block
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
		try{
				if(!appControllerStack.peek().onBackPressed()){
					AppController c = appControllerStack.pop();
					animateScreenIn(c.getCurrentScreen(), FROM_LEFT);
				}
			}
			catch(EmptyStackException e) {
				getActivity().finish();//all controllers finished
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

	public Context getContext() {
		// TODO Auto-generated method stub
		return getActivity();
	}

	public ContentResolver getContentResolver() {
		// TODO Auto-generated method stub
		return getActivity().getContentResolver();
	}
	
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getFragmentManager().findFragmentByTag(UserHomeController.SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
	

}
