package com.amcolabs.quizapp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.appcontrollers.ProfileAndChatController;
import com.amcolabs.quizapp.appcontrollers.UserMainPageController;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.DatabaseHelper;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.gameutils.BadgeEvaluator;
import com.amcolabs.quizapp.gameutils.GameUtils;
import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes;
import com.amcolabs.quizapp.screens.BadgeScreenController;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.QuizAppMenuItem;

public class QuizApp extends Fragment implements AnimationListener , IMenuClickListener {



	private FrameLayout mainFrame;

	static final boolean FROM_LEFT = false;
	static final boolean FROM_RIGHT = true;
	static final boolean TO_LEFT = true;
	static final boolean TO_RIGHT = false;

	public static final int MENU_HOME = 1;
	public static final int MENU_BADGES = 2;
	public static final int MENU_ALL_QUIZZES = 3;
	public static final int MENU_FRIENDS = 4;
	public static final int MENU_MESSAGES=5;
	private static final int MENU_CHATS = 6;

	
	private User currentUser;
	private AppController currentAppController;
	private UserDeviceManager userDeviceManager;
	private UiUtils uiUtils;
	private ServerCalls serverCalls;
	private DatabaseHelper dbHelper;
	private StaticPopupDialogBoxes staticPopupDialogBoxes;
	private boolean initializedDb;
	private Config config;
	private ArrayList<Screen> disposeScreens;

	private View loadingView;

	private boolean initialized = false;
	private MainActivity ref = null;

	private GameUtils gameUtils;

	private BadgeEvaluator badgeEvaluator;

	 
	public void setMainActivity(MainActivity mainActivity) {
		ref = mainActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainFrame = (FrameLayout)getActivity().getLayoutInflater().inflate(R.layout.quizapp_layout,null);
		mainFrame.addView(loadingView);
		((UserMainPageController)loadAppController(UserMainPageController.class))
		.checkAndShowCategories();
		return mainFrame;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		reinit(false);
	}
	
	public void reinit(boolean force) {
		if(!initialized || force){
			initialized = true;
			userDeviceManager = new UserDeviceManager(this);//initialized preferences , device badgeId , pertaining to device
			config = new Config(this);
			uiUtils = new UiUtils(this);
			gameUtils = new GameUtils(this);
			serverCalls = new ServerCalls(this);
			badgeEvaluator = new BadgeEvaluator(this);
			setStaticPopupDialogBoxes(new StaticPopupDialogBoxes(this));
			loadingView = userDeviceManager.getLoadingView(this.getActivity());
			disposeScreens = new ArrayList<Screen>();
			addMenuItems();
		}
	}


	public GameUtils getGameUtils() {
		return gameUtils;
	}


	private void addView(Screen screen) {
		ViewParent tmp = screen.getParent();
		if(tmp!=null){
			((ViewGroup) tmp).removeView(screen);
		}
		mainFrame.addView(screen);
		if(screen.showMenu()){
			this.menu.setVisibility(View.VISIBLE);
		}
		else{
			this.menu.setVisibility(View.GONE);
		}
		screen.controller.setActive(true);
		screenStack.push(screen);
	}

	private void disposeViews() {
		while(!disposeScreens.isEmpty()){
			Screen screen = disposeScreens.remove(0);
			screen.controller.setActive(false);
			mainFrame.removeView(screen);
		}
	}
	
	public AppController loadAppController(Class<? extends AppController> clazz){
		AppController appController=null; 
		if(currentAppController!=null && currentAppController.getClass().equals(clazz)){
			return currentAppController;
		}
		//reuse the existing controller
		for(Screen screen: screenStack){
			if(screen.controller.getClass().equals(clazz)){
				return screen.controller;
			}
		}
		try {
			Constructor<?> constructor = clazz.getConstructor(QuizApp.class);
			appController =(AppController) constructor.newInstance(this);
		} catch (NoSuchMethodException e) {
			appController = new UserMainPageController(this);
			e.printStackTrace();
		} catch (InstantiationException e) {
			appController = new UserMainPageController(this);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			appController = new UserMainPageController(this);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			appController = new UserMainPageController(this);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			appController = new UserMainPageController(this);
			e.printStackTrace();
		} catch (java.lang.InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		appController.setActive(true);
		return appController;
	}
	
	public UserDeviceManager getUserDeviceManager(){
		return userDeviceManager;
	}
	
	public User getUser(){
		return currentUser==null?User.getDummyUser(this):currentUser;
	}

	public void setUser(User user) {
		currentUser = user;
		cachedUsers.put(user.uid , user);
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
	
	public BadgeEvaluator getBadgeEvaluator(){
		return badgeEvaluator;
	}
	
	private double wantsToExitLastTimestamp = 0;
	public void onBackPressed() {
			try{
				// TODO: overridePendingTransition(R.anim.in,R.anim.out); fragment activity to animate screen out and in
				Screen screen = peekCurrentScreen();
				if(screen==null){
					this.getActivity().moveTaskToBack(true);
				}
				if(screen!=null && !screen.controller.onBackPressed()){
					screen = popCurrentScreen();
					screen.beforeRemove();
					screen.controller.decRefCount();
					animateScreenRemove(screen , TO_RIGHT,null);
					
					Screen oldScreen = popCurrentScreen();
					while(oldScreen!=null && !oldScreen.showOnBackPressed()){
						oldScreen = popCurrentScreen();
					}
					
					if(oldScreen==null){
						if(Config.getCurrentTimeStamp() - wantsToExitLastTimestamp<2){
							//pressed twice in 3 seconds
							getActivity().finish();//all controllers finished
						}
						else{
							reinit(false);//should show first screen fetching updates and shit again
							((UserMainPageController)loadAppController(UserMainPageController.class))
							.checkAndShowCategories();

						}
						wantsToExitLastTimestamp = Config.getCurrentTimeStamp();
						return;
					}
					oldScreen.refresh();
					animateScreenIn(oldScreen);
				}
			}
			catch(EmptyStackException e) {
				getActivity().finish();//all controllers finished
			}
	}

	public void animateScreenIn(Screen newScreen) {
		animateScreenIn(newScreen,FROM_RIGHT);
	}

	public void animateScreenRemove() {
		Screen currentScreen = peekCurrentScreen();
		animateScreenRemove(currentScreen , TO_LEFT,null);
	}
	
	
	private Object uiSync = new Object();
	Stack<Screen> screenStack = new Stack<Screen>();

	private HorizontalScrollView menu;

	private List<QuizAppMenuItem> menuItems;



	public Screen popCurrentScreen(){
		if(screenStack.isEmpty())
			return null;
		return screenStack.pop();
	}
	
	public Screen peekCurrentScreen() {
		if(screenStack.isEmpty())
			return null;
		return screenStack.peek();

	}

	public void animateScreenIn(Screen newScreen, boolean fromRight){
		synchronized (uiSync) {
			addView(newScreen);
			loadingView.setVisibility(View.INVISIBLE);
			if(fromRight)
				newScreen.startAnimation(getUiUtils().getAnimationSlideInRight());
			else
				newScreen.startAnimation(getUiUtils().getAnimationSlideInLeft());		
		}
	}
	
	public void setAnimationListener(Animation a , AnimationListener l){
		if(l!=null)
			a.setAnimationListener(l);
		else
			a.setAnimationListener(this);
	}
	public void animateScreenRemove(Screen currentScreen , boolean toLeft, AnimationListener endListener) {
		synchronized (uiSync) {
			if(currentScreen==null) return;
			loadingView.setVisibility(View.VISIBLE);//while removing
			disposeScreens.add(currentScreen);
			if(toLeft){
					setAnimationListener(getUiUtils().getAnimationSlideOutLeft(), endListener);
					currentScreen.startAnimation(	getUiUtils().getAnimationSlideOutLeft());
			}
			else{
				setAnimationListener(getUiUtils().getAnimationSlideOutRight(), endListener);
				currentScreen.startAnimation(getUiUtils().getAnimationSlideOutRight());
			}
		}
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		try{
			new Handler().post(new Runnable() {
		        public void run() {
		        	disposeViews();
		        }
			});
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

        Fragment fragment = getFragmentManager().findFragmentByTag(UserMainPageController.SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    int currentActiveMenu = -1;
	public void onMenuClick(int id) {
		while(screenStack.size()>1){
			Screen s = screenStack.remove(0);
			s.controller.decRefCount();
			s.beforeRemove();
		}
		if(currentActiveMenu==id){
			screenStack.peek().refresh();
			return;
		}
		currentActiveMenu = id;
		switch(id){
			case MENU_HOME:
				break;
			case MENU_MESSAGES:
				break;
			case MENU_ALL_QUIZZES:
				break;
			case MENU_BADGES:
				BadgeScreenController badgeController = (BadgeScreenController) loadAppController(BadgeScreenController.class);
				badgeController.showBadgeScreen();
				break;
			case MENU_FRIENDS:
				break;
			case MENU_CHATS:
				ProfileAndChatController pcontroller = (ProfileAndChatController) loadAppController(ProfileAndChatController.class);
				pcontroller.showChatScreen();
				break;
		}
	}

	public void hideTitleBar(){
		ref.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ref.getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

	}
	
	public void showTitleBar(){
		ref.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		ref.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}


	public void destroyAllScreens() {
		Screen s = null;
		while( (s = popCurrentScreen())!=null){
			s.controller.onDestroy();
		}
	}
	
	public void addMenuItems(){
		LinearLayout buttonsContainer = (LinearLayout) menu.findViewById(R.id.nav_items_container);
		buttonsContainer.removeAllViews();
		menuItems = Arrays.asList(
				new QuizAppMenuItem(this, QuizApp.MENU_HOME, R.drawable.home , UiText.HOME.getValue()),
				new QuizAppMenuItem(this, QuizApp.MENU_BADGES, R.drawable.badges,UiText.BADGES.getValue()),
				new QuizAppMenuItem(this, QuizApp.MENU_ALL_QUIZZES, R.drawable.all_quizzes, UiText.SHOW_QUIZZES.getValue()),
//				new QuizAppMenuItem(this, QuizApp.MENU_MESSAGES, R.drawable.messages , UiText.SHOW_MESSAGES.getValue()),
				new QuizAppMenuItem(this, QuizApp.MENU_CHATS, R.drawable.home , UiText.CHATS.getValue())
				);
		
		for(QuizAppMenuItem item : menuItems){
			buttonsContainer.addView(item);
		}
	}
	
	public void setMenuItemDirty(int id , String text){
		for(QuizAppMenuItem item : menuItems){
			if(item.getId()==id){
				item.setDirtyText(text);
			}
		}
	}
	
	public void setHorizontalMenu( HorizontalScrollView hmenu) {
		this.menu = hmenu; 
	} 
	public HorizontalScrollView getMenu(){
		return menu;
	}
	
	public StaticPopupDialogBoxes getStaticPopupDialogBoxes() {
		return staticPopupDialogBoxes;
	}

	public void setStaticPopupDialogBoxes(StaticPopupDialogBoxes staticPopupDialogBoxes) {
		this.staticPopupDialogBoxes = staticPopupDialogBoxes;
	}

	
	
	public HashMap<String , User> cachedUsers = new HashMap<String, User>(){
		
		public User get(Object key) {
			if(!super.containsKey(key)){
				//wtf
				getServerCalls().getUserByUidSync(key, new DataInputListener<Boolean>(){
					
				});
			}
			return super.get(key);
		};
	};


	public void cacheUsersList(ArrayList<User> users) {
		for(User user : users){
			cachedUsers.put(user.uid, user);
		}
	}
	
	@Override
	public void onDestroy() {
		destroyAllScreens();
		super.onDestroy();
	}
	
}
