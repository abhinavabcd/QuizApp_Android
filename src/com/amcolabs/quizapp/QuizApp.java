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

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.amcolabs.quizapp.notificationutils.NotificationReciever;
import com.amcolabs.quizapp.notificationutils.NotificationReciever.NotificationPayload;
import com.amcolabs.quizapp.notificationutils.NotificationReciever.NotificationType;
import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes;
import com.amcolabs.quizapp.screens.BadgeScreenController;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.QuizAppMenuItem;

public class QuizApp extends Fragment implements AnimationListener , IMenuClickListener ,ServiceConnection {



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
	public MainActivity getMainActivity() {
		return ref;
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
		// start music 
		startMusicService();
	}
	
	@Override
	public void onPause() {
		removeAllNotificationListeners();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		addNotificationListeners();
		super.onResume();
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


	private void removeAllNotificationListeners() {
		NotificationReciever.destroyAllListeners();
	}
	
	private void addNotificationListeners() {
		NotificationReciever.setListener(NotificationType.NOTIFICATION_GCM_CHALLENGE_NOTIFICATION, new DataInputListener<NotificationPayload>(){
			@Override
			public String onData(final NotificationPayload payload) {
				getDataBaseHelper().getAllUsersByUid(Arrays.asList(payload.fromUser), new DataInputListener<Boolean>(){
					@Override
					public String onData(Boolean s) {
						getStaticPopupDialogBoxes().challengeRequestedPopup(cachedUsers.get(payload.fromUser) , getDataBaseHelper().getQuizById(payload.quizId));
						return super.onData(s); 
					}
				});
				return null;
			}
		});
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
		//if(screen.showOnBackPressed())
		screenStack.push(screen);
	}

	private void disposeViews() {
		while(!disposeScreens.isEmpty()){
			Screen screen = disposeScreens.remove(0);
			screen.controller.setActive(false);
			mainFrame.removeView(screen);
			//if(!screen.showOnBackPressed()){
			screen.onRemovedFromScreen();
			//screen.beforeRemove(); // you can call before remove directly here because , it wont be shown again 
			//}
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
		return currentUser;
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
	private boolean shouldToggleNextScreen = false;

	private int isScreenAnimationActive = 0;
	public void onBackPressed() {
			if(isScreenAnimationActive!=0){
				return;
			}
			currentActiveMenu = -1;
			if(Config.getCurrentTimeStamp() - wantsToExitLastTimestamp<2){
				getActivity().finish();//all controllers finished
				wantsToExitLastTimestamp = Config.getCurrentTimeStamp();
				return;
			}

			try{
				// TODO: overridePendingTransition(R.anim.in,R.anim.out); fragment activity to animate screen out and in
				Screen screen = peekCurrentScreen();
				if(screenStack.size()<2 || screen==null){
					this.getActivity().moveTaskToBack(true);
					return;
				}
				Log.d(">>>screens<<<", screenStack.size()+" \n"+screenStack);
				if(screen!=null && !screen.controller.onBackPressed()){
					screen = popCurrentScreen();
					screen.beforeRemove();
					screen.controller.decRefCount();
					animateScreenRemove(screen , TO_RIGHT,null);
					
					Screen oldScreen = popCurrentScreen();
					while(oldScreen!=null && !oldScreen.showOnBackPressed()){
						oldScreen = popCurrentScreen();
						oldScreen.beforeRemove(); // we are already calling it in dispose view , that would be more appropriate
					}
					
					if(oldScreen==null){
						reinit(false);//should show first screen fetching updates and shit again
						((UserMainPageController)loadAppController(UserMainPageController.class))
						.checkAndShowCategories();
						return;
					}
					animateScreenIn(oldScreen, FROM_LEFT);
					oldScreen.refresh();
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
		++isScreenAnimationActive;
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
		--isScreenAnimationActive;
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
    
	double lastClick = 0;
	public boolean isRapidReClick(){
		if(Config.getCurrentNanos()-lastClick>1000000000){//1 sec
			lastClick = Config.getCurrentNanos();
			return false;
		}
		lastClick = Config.getCurrentNanos();
		return true;
	}

    int currentActiveMenu = -1;
	public void onMenuClick(int id) {
		if(isRapidReClick()) return;
		if(currentActiveMenu==id){
			screenStack.peek().refresh();
			return;
		}while(screenStack.size()>2){ //quietly remove old screen till the first screen
			Screen s = screenStack.pop();
			s.controller.decRefCount();
			s.beforeRemove();
		}
//		if(screenStack.size()==2){
//			Screen s = screenStack.pop();
//			s.controller.decRefCount();
//			s.beforeRemove();
//			animateScreenRemove(s, TO_RIGHT, null);
//		}
		
		
		
		switch(id){
			case MENU_HOME:
				if(screenStack.size()==2){ //remove all screens including the home screen
					animateScreenRemove(screenStack.peek() , TO_RIGHT,null);//currenly appearing screen
					animateScreenIn(screenStack.get(0),TO_RIGHT);
				}
				screenStack.get(0).refresh();
				currentActiveMenu = MENU_HOME;
				break;
			case MENU_MESSAGES:
				break;
			case MENU_ALL_QUIZZES:
				break;
			case MENU_BADGES:
				BadgeScreenController badgeController = (BadgeScreenController) loadAppController(BadgeScreenController.class);
				badgeController.showBadgeScreen();
				currentActiveMenu = MENU_BADGES;
				break;
			case MENU_FRIENDS:
				ProfileAndChatController profileController =( ProfileAndChatController)loadAppController(ProfileAndChatController.class);
				profileController.showFriendsList();
				currentActiveMenu = MENU_FRIENDS;
				break;
			case MENU_CHATS:
				ProfileAndChatController pcontroller = (ProfileAndChatController) loadAppController(ProfileAndChatController.class);
				pcontroller.showChatScreen();
				currentActiveMenu = MENU_CHATS;

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
				new QuizAppMenuItem(this, QuizApp.MENU_CHATS, R.drawable.home , UiText.CHATS.getValue()),
				new QuizAppMenuItem(this, QuizApp.MENU_FRIENDS, R.drawable.home , UiText.FRIENDS.getValue())
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
		this.menu.setVisibility(View.GONE);
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

	
	
	public HashMap<String , User> cachedUsers = new HashMap<String, User>();
//	{
//		
//		public User get(Object key) {
//			if(!super.containsKey(key)){
//				//wtf
//				getServerCalls().getUserByUidSync(key, new DataInputListener<Boolean>(){
//					
//				});
//			}
//			return super.get(key);
//		};
//	};

	private MusicService mServ;

	private boolean mIsBound;


	public void cacheUsersList(ArrayList<User> users) {
		for(User user : users){
			cachedUsers.put(user.uid, user);
		}
	}
	
	@Override
	public void onDestroy() {
		destroyAllScreens();
		super.onDestroy();
		doUnbindMusicService();
	}

	
	public void doBindMusicService(){
			Intent intent = new Intent(this.getActivity(), MusicService.class);
			intent.putExtra(Config.MUSIC_ID, R.raw.app_music);
			getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
			mIsBound = true;
	}
	public void doUnbindMusicService(){
		if(mIsBound){
			getActivity().unbindService(this);
			mIsBound = false;
		}
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder binder){
		mServ = ((MusicService.ServiceBinder) binder).getService();
		mServ.start();
	}
	
	@Override
	public void onServiceDisconnected(ComponentName name){
		mServ = null;
	}
	
	private void startMusicService() {
		Intent music = new Intent(getActivity(), MusicService.class);
		getActivity().startService(music);
		doBindMusicService();
		
	}
	
	public void changeMusic(int musicId){
		if(musicId<0){
			musicId = R.raw.app_music;
		}
		mServ.playAnother(musicId);
	}
}
