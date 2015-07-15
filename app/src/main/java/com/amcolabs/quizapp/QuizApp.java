package com.amcolabs.quizapp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import com.amcolabs.quizapp.NotificationReciever.NotificationPayload;
import com.amcolabs.quizapp.NotificationReciever.NotificationType;
import com.amcolabs.quizapp.Screen.ScreenType;
import com.amcolabs.quizapp.appcontrollers.BadgeScreenController;
import com.amcolabs.quizapp.appcontrollers.ProfileAndChatController;
import com.amcolabs.quizapp.appcontrollers.UserMainPageController;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.ChatList;
import com.amcolabs.quizapp.databaseutils.DatabaseHelper;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.gameutils.BadgeEvaluator;
import com.amcolabs.quizapp.gameutils.GameUtils;
import com.amcolabs.quizapp.notificationutils.NotifificationProcessingState;
import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.google.android.gms.analytics.HitBuilders;

/**
 * 
 * @author abhinav2
 * Main base , and delegate to other objects in controllers and screen 
 * takes care of layouts/screen animations
 */
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
	private static final int MENU_PROFILE = 7;
	public static final int MENU_SHARE_WITH_FRIENDS = 8;
	private static final int SEND_FEEDBACK = 9;

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
	public DataInputListener<NotificationPayload> defaultChatMessagesListener;
	private NotificationReciever notificationReciever;

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
		//cleanup music service start music
		MusicService.release();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		removeAllNotificationListeners();
		MusicService.pause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		addNotificationListeners();
		if(userDeviceManager.isLoggedInUser())
			MusicService.start(getContext(), MusicService.MUSIC_GAME);
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
			disposeScreens = new ArrayList<>();
			notificationReciever = new NotificationReciever();
//			addMenuItems();
		}
		setNotificationProcessingState(NotifificationProcessingState.CONTINUE);
	}

	public static HashMap<NotificationType , ArrayList<NotificationPayload>> pendingNotifications = new HashMap<NotificationReciever.NotificationType, ArrayList<NotificationPayload>>();
	
	private void removeAllNotificationListeners() {
		getNotificationReciever().destroyAllListeners();
	}
	
	private void addNotificationListeners() {
		getNotificationReciever().setListener(NotificationType.NOTIFICATION_GCM_CHALLENGE_NOTIFICATION, new DataInputListener<NotificationPayload>() {
			@Override
			public String onData(final NotificationPayload payload) {
				getDataBaseHelper().getAllUsersByUid(Arrays.asList(payload.fromUser), new DataInputListener<Boolean>() {
					@Override
					public String onData(Boolean s) {
						getStaticPopupDialogBoxes().challengeRequestedPopup(cachedUsers.get(payload.fromUser), getDataBaseHelper().getQuizById(payload.quizId), payload, new DataInputListener<Boolean>() {
							@Override
							public String onData(Boolean s) {
								if (!s)// all notifications will be pending until user returns back
									setNotificationProcessingState(NotifificationProcessingState.CONTINUE);
								return super.onData(s);
							}
						});
						return null;
					}
				});
				return null;
			}
		});
		//notify user that he has a new offline challenge
		getNotificationReciever().setListener(NotificationType.NOTIFICATION_GCM_OFFLINE_CHALLENGE_NOTIFICATION, new DataInputListener<NotificationPayload>(){
			@Override
			public String onData(final NotificationPayload payload) { 
				Quiz quiz = getDataBaseHelper().getQuizById(payload.quizId);
				getStaticPopupDialogBoxes().yesOrNo(UiText.NEW_OFFLINE_CHALLENGE_IN.getValue(payload.fromUserName , UiText.IN.getValue(quiz.name)), null, UiText.OK.getValue(), null);
				return null;
			}
		});
		
		//new chat message message from server

		defaultChatMessagesListener = new DataInputListener<NotificationPayload>(){
			public String onData(final NotificationPayload payload) {
				getStaticPopupDialogBoxes().yesOrNo(UiText.USER_SAYS.getValue(payload.fromUserName, payload.textMessage), UiText.START_CONVERSATION.getValue(""), UiText.OK.getValue(), new DataInputListener<Boolean>(){//opens popup
					@Override
					public String onData(Boolean s) {
						ChatList chatListItem = getDataBaseHelper().getChatListByUid(payload.fromUser);
						if(chatListItem==null){
							chatListItem = new ChatList(payload.fromUser, payload.textMessage, getConfig().getCurrentTimeStamp(), 0);
						}

						if(s){//load chat screen
							chatListItem.unseenMessagesFlag=0;
							getDataBaseHelper().getAllUsersByUid(Arrays.asList(payload.fromUser), new DataInputListener<Boolean>(){ // get user obj
								@Override
								public String onData(Boolean s) {
									((ProfileAndChatController)loadAppController(ProfileAndChatController.class)).loadChatScreen(cachedUsers.get(payload.fromUser), -1); //load chat screen
									return super.onData(s);
								}
							});
						}
						else{// add to pending messages
							chatListItem.unseenMessagesFlag++;
						}
						getDataBaseHelper().updateChatList(chatListItem);// update in db
						setNotificationProcessingState(NotifificationProcessingState.CONTINUE);
						return super.onData(s);
					}
				});
				return null;
			}
		};


		getNotificationReciever().setListener(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE, defaultChatMessagesListener);
		setNotificationProcessingState(NotifificationProcessingState.CONTINUE);
	}

	
	
	private void doPendingNotifications() {//only for messages in queue
		if(peekCurrentScreen()!=null && !peekCurrentScreen().doNotDistrub()){
			for(NotificationType type : NotificationType.values()){
				if(pendingNotifications.containsKey(type) && pendingNotifications.get(type)!=null && pendingNotifications.get(type).size()>0){
					NotificationPayload p=null;
					while(pendingNotifications.get(type).size()>0)
						getNotificationReciever().checkAndCallListener(type, (p = pendingNotifications.get(type).remove(0)));
					setNotificationProcessingState(NotifificationProcessingState.DEFER);
					return; // only one notification at a time , if notification returns something , then we do stuff
				} 
			}
		}
	}

	public GameUtils getGameUtils() {
		return gameUtils;
	}
	public NotificationReciever getNotificationReciever(){
		return notificationReciever;
	}

	private void addView(Screen screen) {
		QuizAppInit.tracker().send(new HitBuilders.EventBuilder()
				.setCategory(Tracking.SCREEN_ACTIVITY.toString())
				.setAction(screen.getScreenType().toString())
				.setLabel(screen.getScreenType().getData())
				.build());
		loadingView.setVisibility(View.GONE);

		ViewParent tmp = screen.getParent();
		if(tmp!=null){
			((ViewGroup) tmp).removeView(screen);
		}
		mainFrame.addView(screen);
		if(screen.showMenu() && menu!=null){
			this.menu.setVisibility(View.VISIBLE);
		}
		else{
			if(this.menu ==null) {
				setMenu(((MainActivity) getActivity()).getMenu());
			}
			this.menu.setVisibility(View.GONE);
		}
		screen.controller.setActive(true);
	//	if(screen.shouldAddtoScreenStack())
			screenStack.push(screen);
	}

	private void disposeViews() {
		while(!disposeScreens.isEmpty()){
			Screen screen = disposeScreens.remove(0);

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
		if(user==null){
			getStaticPopupDialogBoxes().yesOrNo(UiText.SERVER_ERROR.getValue(), UiText.OK.getValue(), UiText.CANCEL.getValue() , new DataInputListener<Boolean>(){
					@Override
					public String onData(Boolean s) {
						getActivity().finish();
						return super.onData(s);
					}
			});
			return;
		}
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
			synchronized (uiSync) {
				if (isScreenAnimationActive != 0) {
					return;
				}
				currentActiveMenu = -1;
				if (Config.getCurrentTimeStamp() - wantsToExitLastTimestamp < 2) {
					getActivity().finish();//all controllers finished
					wantsToExitLastTimestamp = Config.getCurrentTimeStamp();
					return;
				}

				try {
					// TODO: overridePendingTransition(R.anim.in,R.anim.out); fragment activity to animate screen out and in
					Screen screen = peekCurrentScreen();
					if (screenStack.size() < 2 || screen == null) {
						this.getActivity().finish();
						return;
					}
					Log.d(">>>screens<<<", screenStack.size() + " \n" + screenStack);
					if(screen.removeOnBackPressed()) {
						screen = popCurrentScreen();
						screen.beforeRemove();
						animateScreenRemove(screen, TO_RIGHT, null);

						Screen oldScreen = popCurrentScreen();
						while (oldScreen != null && !oldScreen.showOnBackPressed()) {
							oldScreen.beforeRemove(); // we are already calling it in dispose view , that would be more appropriate
							disposeScreens.add(oldScreen);
							oldScreen = popCurrentScreen();
						}

						if (oldScreen == null) {
							reinit(false);//should show first screen fetching updates and shit again
							((UserMainPageController) loadAppController(UserMainPageController.class))
									.checkAndShowCategories();
							return;
						}
						animateScreenIn(oldScreen, FROM_LEFT);
						oldScreen.refresh();
						if (!oldScreen.doNotDistrub())
							setNotificationProcessingState(NotifificationProcessingState.CONTINUE);
					}
				} catch (EmptyStackException e) {
					getActivity().finish();//all controllers finished
				}
			}
	}

	public void animateScreenIn(Screen newScreen) {
		animateScreenIn(newScreen,FROM_RIGHT);
	}

	public void animateScreenRemove() {
		Screen currentScreen = peekCurrentScreen();
		animateScreenRemove(currentScreen , TO_LEFT,null);
	}
	
	
	Stack<Screen> screenStack = new Stack<Screen>();

	private View menu;

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
//		synchronized (uiSync) {
			addView(newScreen);
			if(fromRight){
					setAnimationListener(getUiUtils().getAnimationSlideInRight() , null);
					newScreen.startAnimation(getUiUtils().getAnimationSlideInRight());
			}
			else{
				setAnimationListener(getUiUtils().getAnimationSlideInLeft() , null);
				newScreen.startAnimation(getUiUtils().getAnimationSlideInLeft());		
			}
	//	}
	}
	
	public void setAnimationListener(Animation a , AnimationListener l){
		if(l!=null)
			a.setAnimationListener(l);
		else
			a.setAnimationListener(this);
	}
	public void animateScreenRemove(Screen currentScreen , boolean toLeft, AnimationListener endListener) {
		//synchronized (uiSync) {
			if(currentScreen==null) return;
			disposeScreens.add(currentScreen);
			if(toLeft){
				setAnimationListener(getUiUtils().getAnimationSlideOutRight() , null);
				currentScreen.startAnimation(	getUiUtils().getAnimationSlideOutLeft());
			}
			else{
				setAnimationListener(getUiUtils().getAnimationSlideOutRight() , null);
				currentScreen.startAnimation(getUiUtils().getAnimationSlideOutRight());
			}
//		}
	}
	Object uiSync = new Object();
	public void setScreenAnimationActive(boolean activate){
		synchronized (uiSync) {
			if (activate)
				++isScreenAnimationActive;
			else
				--isScreenAnimationActive;
		}
	}
	
	@Override
	public void onAnimationStart(Animation animation) {
		uiUtils.addUiBlock("Loading...");
		setScreenAnimationActive(true);
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
		setScreenAnimationActive(false);
		uiUtils.removeUiBlock();
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
	String lastClickId = null;
	public boolean isRapidReClick(String clickId){
		if(!clickId.equalsIgnoreCase(clickId) || Config.getCurrentNanos()-lastClick>2000000000){//1 sec
			lastClick = Config.getCurrentNanos();
			lastClickId = clickId;
			return false;
		}
		lastClick = Config.getCurrentNanos();
		return true;
	}

	private int reClickId = 0;
	public boolean isRapidReClick(int id){
		if(id==reClickId){
			if((Config.getCurrentNanos()-lastClick)> 10000000000d){//1 sec
				lastClick = Config.getCurrentNanos();
				return false;
			}
			lastClick = Config.getCurrentNanos();
			return true;
		}
		reClickId = id;
		return false;
	}
	
	public boolean isRapidReClick(){
		if(Config.getCurrentNanos()-lastClick>2000000000){//1 sec
			lastClick = Config.getCurrentNanos();
			return false;
		}
		lastClick = Config.getCurrentNanos();
		return true;
	}

    int currentActiveMenu = -1;

	private HashMap<Integer, UiText> menuItems = null;
	public void onMenuClick(int id) {
		if(isRapidReClick()) return;
		synchronized (uiSync) {
			if (isScreenAnimationActive != 0) {
				return;
			}
			if (currentActiveMenu == id) {
				screenStack.peek().refresh();
				return;
			}
			Screen s;
			Screen lastScreen = s = screenStack.pop();
			while (screenStack.size() > 1) { //quietly remove old screen till the first screen
				s = screenStack.pop();
				s.controller.decRefCount();
				s.beforeRemove();
			}
			screenStack.push(lastScreen);
			//		if(screenStack.size()==2){
			//			Screen s = screenStack.pop();
			//			s.controller.decRefCount();
			//			s.beforeRemove();
			//			animateScreenRemove(s, TO_RIGHT, null);
			//		}


			switch (id) {
				case MENU_HOME:
					if (screenStack.size() == 2) { //remove all screens including the home screen
						if (screenStack.get(0).getScreenType() != ScreenType.WELCOME_SCREEN) {// not on first login welcomeScreen dirty fix
							animateScreenRemove(screenStack.peek(), TO_RIGHT, null);//currenly appearing screen
							animateScreenIn(screenStack.get(0), TO_RIGHT);
							screenStack.get(0).refresh();
						} else {
							screenStack.get(1).refresh();
						}
					}
					currentActiveMenu = MENU_HOME;
					break;
				case MENU_MESSAGES:
					break;
				case MENU_ALL_QUIZZES:
					UserMainPageController uController = (UserMainPageController) loadAppController(UserMainPageController.class);
					uController.showAllUserQuizzes();
					currentActiveMenu = MENU_ALL_QUIZZES;
					break;
				case MENU_BADGES:
					BadgeScreenController badgeController = (BadgeScreenController) loadAppController(BadgeScreenController.class);
					badgeController.showBadgeScreen();
					currentActiveMenu = MENU_BADGES;
					break;
				case MENU_FRIENDS:
					ProfileAndChatController profileController = (ProfileAndChatController) loadAppController(ProfileAndChatController.class);
					profileController.showFriendsList();
					currentActiveMenu = MENU_FRIENDS;
					break;
				case MENU_CHATS:
					ProfileAndChatController pcontroller = (ProfileAndChatController) loadAppController(ProfileAndChatController.class);
					pcontroller.showChatScreen();
					currentActiveMenu = MENU_CHATS;
					break;
				case MENU_PROFILE:
					ProfileAndChatController c = (ProfileAndChatController) loadAppController(ProfileAndChatController.class);
					c.showProfileScreen(getUser());
					currentActiveMenu = MENU_PROFILE;
					break;
				case MENU_SHARE_WITH_FRIENDS:
					Intent sharingIntent = new Intent(Intent.ACTION_SEND);
					sharingIntent.setType("text/plain");
					sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Play realtime quizzes with your friends on tollywood.");
					String link = "https://play.google.com/store/apps/details?id=com.amcolabs.quizapp";
					sharingIntent.putExtra(Intent.EXTRA_TEXT, link);
					getContext().startActivity(sharingIntent);
					break;

				case SEND_FEEDBACK:

					getStaticPopupDialogBoxes().promptInput(UiText.TELL_SOMETHING_ABOUT_APP.getValue(), 100 , "" , new DataInputListener<String>(){
						@Override
						public String onData(String s) {
							getServerCalls().sendFeedBack(s);
							return super.onData(s);
						}
					});

			}
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
	
//	public void addMenuItems(){
//		LinearLayout buttonsContainer = (LinearLayout) menu.findViewById(R.id.nav_items_container);
//		buttonsContainer.removeAllViews();
//		menuItems = Arrays.asList(
//				new QuizAppMenuItem(this, QuizApp.MENU_HOME, R.drawable.home , UiText.HOME.getValue()),
//				new QuizAppMenuItem(this, QuizApp.MENU_BADGES, R.drawable.badges,UiText.BADGES.getValue()),
//				new QuizAppMenuItem(this, QuizApp.MENU_ALL_QUIZZES, R.drawable.all_quizzes, UiText.SHOW_QUIZZES.getValue()),
////				new QuizAppMenuItem(this, QuizApp.MENU_MESSAGES, R.drawable.messages , UiText.SHOW_MESSAGES.getValue()),
//				new QuizAppMenuItem(this, QuizApp.MENU_CHATS, R.drawable.home , UiText.CHATS.getValue()),
//				new QuizAppMenuItem(this, QuizApp.MENU_FRIENDS, R.drawable.home , UiText.FRIENDS.getValue())
//			);
//		
//		for(QuizAppMenuItem item : menuItems){
//			buttonsContainer.addView(item);
//		}
//	}
	
//	public void setMenuItemDirty(int id , String text){
//		for(QuizAppMenuItem item : menuItems){
//			if(item.getId()==id){
//				item.setDirtyText(text);
//			}
//		}
//	}
	
	public void setMenu( View view) {
		this.menu = view; 
		menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getStaticPopupDialogBoxes().showMenu(getMenuItems());	
			}
		});
		this.menu.setVisibility((peekCurrentScreen()==null|| !peekCurrentScreen().showMenu())?View.GONE:View.VISIBLE);
	}

	protected HashMap<Integer, UiText> getMenuItems() {
		if(menuItems==null){
			menuItems = new LinkedHashMap<Integer, UiText>();
			menuItems.put(QuizApp.MENU_HOME, UiText.HOME);
			menuItems.put(QuizApp.MENU_PROFILE, UiText.PROFILE);
			menuItems.put(QuizApp.MENU_BADGES,UiText.BADGES);
			menuItems.put(QuizApp.MENU_ALL_QUIZZES, UiText.SHOW_QUIZZES);
			menuItems.put(QuizApp.MENU_CHATS,UiText.CHATS);
			menuItems.put(QuizApp.MENU_FRIENDS, UiText.FRIENDS);
			menuItems.put(QuizApp.MENU_SHARE_WITH_FRIENDS, UiText.TELL_SOMONE);
			menuItems.put(QuizApp.SEND_FEEDBACK, UiText.SEND_FEEDBACK);

		}
		return menuItems;
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
				User u =getDataBaseHelper().getUserByUid(key.toString());
				if(u!=null){
					put(u.uid, u);
					return u;
				}
			}
			return super.get(key);
		};
	};



	public void cacheUsersList(List<User> users) {
		for(User user : users){
			cachedUsers.put(user.uid, user);
		}
	}
	
	@Override
	public void onDestroy() {
		destroyAllScreens();
		NotificationReciever.setOffline();
		MusicService.release();
		super.onDestroy();
	}
	public static NotifificationProcessingState nState = NotifificationProcessingState.CONTINUE;
	
	public void setNotificationProcessingState(
		NotifificationProcessingState notifificationProcessingState) {
		if(notifificationProcessingState == NotifificationProcessingState.CONTINUE){
			doPendingNotifications();
		}
		nState = notifificationProcessingState;
	}
	public void fetchAndLoadUserProfile(String uid) {
			ProfileAndChatController profileAndChat = (ProfileAndChatController)loadAppController(ProfileAndChatController.class);
			profileAndChat.fetchAndLoadProfile(uid);
 	}

	public void showHomeScreen() {

	}



	public synchronized void toggleMusic() {
		int musicState = getUserDeviceManager().getPreference(Config.PREF_MUSIC_STATE, 1);
		musicState^=1;
		getUserDeviceManager().setPreference(Config.PREF_MUSIC_STATE, musicState);
		MusicService.setApplicationMusicVolume(musicState * 99);//0-99 is the volume
	}
}
