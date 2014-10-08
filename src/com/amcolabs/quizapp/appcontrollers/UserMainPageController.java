package com.amcolabs.quizapp.appcontrollers;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.UserDeviceManager;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.databaseutils.Feed.FeedType;
import com.amcolabs.quizapp.databaseutils.OfflineChallenge;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.databaseutils.Feed;
import com.amcolabs.quizapp.databaseutils.UserInboxMessage;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.datalisteners.DataInputListener2;
import com.amcolabs.quizapp.gameutils.GameUtils;
import com.amcolabs.quizapp.loginutils.FacebookLoginHelper;
import com.amcolabs.quizapp.loginutils.GoogleLoginHelper;
import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes;
import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes.YesNoDialog;
import com.amcolabs.quizapp.screens.HomeScreen;
import com.amcolabs.quizapp.screens.LeaderBoardScreen;
import com.amcolabs.quizapp.screens.QuizzesScreen;
import com.amcolabs.quizapp.screens.SelectFriendsScreen;
import com.amcolabs.quizapp.screens.WelcomeScreen;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.plus.model.people.Person.Gender;
/**
 * 
 * @author abhinav2
 *Manages login screen , homescreeen with categories , leaderboard screen
 */

public class UserMainPageController  extends AppController{
	 
	public static final String SOCIAL_NETWORK_TAG = "com.amcolabs.quizapp.loginscreen";
    protected boolean mSocialNetworkManagerInitialized = false;
    User user= null;
	private double currentQuizMaxTimeStamp;
	private int feedPreprocessedCount;
	protected YesNoDialog updatesErrorDialog=null;
	public UserMainPageController(QuizApp quizApp) {
		super(quizApp);
	}

//	public WelcomeScreen welcomeScreen;
//	public SignUpScreen signUpScreen;
//	public HomeScreen categoriesScreen;
//	public TopicsScreen topicsScreen;
	 
	
	
	public void checkAndShowCategories(){
		String encodedKey = quizApp.getUserDeviceManager().getEncodedKey();
		if(encodedKey!=null){
			//on fetch update new categories and draw the categories
			currentQuizMaxTimeStamp = quizApp.getDataBaseHelper().getMaxTimeStampQuiz();

			quizApp.getServerCalls().getAllUpdates(new DataInputListener2<List<Feed> ,List<UserInboxMessage> ,List<OfflineChallenge>, Boolean>(){
				@Override
				public void onData(List<Feed> feeds,List<UserInboxMessage> inboxMessages,List<OfflineChallenge> offlineChallenges, Boolean s) {
					if(offlineChallenges!=null)
					for(OfflineChallenge offlineChallenge : offlineChallenges){
						if(offlineChallenge.isCompleted()){
							offlineChallenge.setCompleted(true);
						}
						quizApp.getDataBaseHelper().updateOfflineChallenge(offlineChallenge);
					}
					if(s){
						showUserHomeScreen(feeds);
					}
					else{ // called twice , so need a flag here
						if(updatesErrorDialog==null)
							updatesErrorDialog = quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.COULD_NOT_CONNECT.getValue(), null , UiText.OK.getValue(),new DataInputListener<Boolean>(){
								public String onData(Boolean s) {
									updatesErrorDialog = null;
									return null;
								};
							});
					}
					return;
				}
			});
		}
		else if(quizApp.getUserDeviceManager().getPreference(Config.PREF_NOT_ACTIVATED, null)!=null){
			checkAndShowVerificationScreen();
		}
		else{
			showWelcomeScreen();
		}
	}
	
	private void checkAndShowVerificationScreen() {
		quizApp.getServerCalls().checkVerificationStatus(new DataInputListener<String>(){
			@Override
			public String onData(String encodedKey) {
				if(encodedKey!=null){
					checkAndShowCategories(); // if verified
				}
				return null;
			}
		});// on ACTIVATED, save user quizApp setUser
	}
	
	public void onCategorySelected(Category category){
		clearScreen();
		HomeScreen categoryQuizzesScreen = new HomeScreen(this);
		List<Quiz> quizzes = category.getQuizzes(quizApp);
		categoryQuizzesScreen.addQuizzesToListFullView(category.description , quizzes);
		insertScreen(categoryQuizzesScreen);
	}
	
	public void onQuizPlaySelected(Quiz quiz){
		clearScreen();
		ProgressiveQuizController progressiveQuiz = (ProgressiveQuizController) quizApp.loadAppController(ProgressiveQuizController.class);
		progressiveQuiz.initlializeQuiz(quiz);
	}
	
	HomeScreen homeScreen = null;
	private DataInputListener<User> loginListener;
	private void showUserHomeScreen(final List<Feed> feeds) {
		GCMRegistrar.checkDevice(quizApp.getActivity());
        //TODO: uncomment this after testing
        //GCMRegistrar.checkManifest(quizApp.getActivity());
        final String regId = GCMRegistrar.getRegistrationId(quizApp.getActivity().getApplicationContext());
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(quizApp.getActivity(), Config.GCM_APP_ID);//
        }

		
		clearScreen();
		homeScreen= new HomeScreen(this);
//		ArrayList<Category> categories = new ArrayList<Category>();
//		for(int i=0;i<10;i++){
//			categories.add(Category.createDummy());
//		}
		List<Category> categories = quizApp.getDataBaseHelper().getCategories(Config.MAX_CATEGORIES_ON_HOME_SCREEN);
		if(categories.size()==Config.MAX_CATEGORIES_ON_HOME_SCREEN)
			categories.remove(categories.size()-1);
		homeScreen.addCategoriesView(categories, categories.size()>Config.MAX_CATEGORIES_ON_HOME_SCREEN-1);
		 
		
		List<Quiz> quizzes = quizApp.getDataBaseHelper().getAllQuizzesOrderedByXP(Config.MAX_QUIZZES_ON_HOME_SCREEN);
		if(quizzes.size()==Config.MAX_QUIZZES_ON_HOME_SCREEN)
			quizzes.remove(quizzes.size()-1);
		homeScreen.addUserQuizzesView(quizzes ,quizzes.size()>Config.MAX_QUIZZES_ON_HOME_SCREEN , UiText.USER_FAVOURITES.getValue());
		
		List<Quiz> recentQuizzes = quizApp.getDataBaseHelper().getAllQuizzes(10, currentQuizMaxTimeStamp);
		if(recentQuizzes!=null && recentQuizzes.size()>0)
			homeScreen.addUserQuizzesView(quizzes ,false , UiText.RECENT_QUIZZES.getValue());
		
		final List<OfflineChallenge> offlineChallenges = quizApp.getDataBaseHelper().getPendingRecentOfflineChallenges(7);
		if(offlineChallenges!=null && offlineChallenges.size()>0){
			List<String> uidsList = new ArrayList<String>();
			for(OfflineChallenge offlineChallenge : offlineChallenges){
				uidsList.add(offlineChallenge.getFromUserUid());
			}
			quizApp.getDataBaseHelper().getAllUsersByUid(uidsList, new DataInputListener<Boolean>(){ // should run on ui thread
				@Override
				public String onData(Boolean s) {
					homeScreen.addOfflineChallengesView(offlineChallenges, offlineChallenges.size()>6, UiText.OFFLINE_CHALLENGES.getValue() , true);
					return null;
				}
			});
		}
		if(feeds!=null){
			feedPreprocessedCount = feeds.size();
			List<String> uidsList = new ArrayList<String>();
			for(Feed feed: feeds){ // pre processing all the feed
				uidsList.add(feed.fromUid);
			}
			if(feeds.size()==0){
				homeScreen.addFeedView(feeds, UiText.USER_FEED.getValue());
			}
			quizApp.getDataBaseHelper().getAllUsersByUid(uidsList, new DataInputListener<Boolean>(){ // first cache all users
				@Override
				public String onData(Boolean s) {
					for(final Feed feed: feeds){ // pre processing all the feed
						switch(feed.getUserFeedType()){
							case FEED_CHALLENGE://show popups
								quizApp.getServerCalls().getOfflineChallenge(feed.message, new DataInputListener<OfflineChallenge>(){
									@Override 
									public String onData(OfflineChallenge offlineChallenge) {
										--feedPreprocessedCount;
										if(offlineChallenge==null){
											if(feedPreprocessedCount<1) homeScreen.addFeedView(feeds, UiText.USER_FEED.getValue());
											return null;
										}
										if(!offlineChallenge.isCompleted()){
											offlineChallenge.setCompleted(true);
												//show popup that user has completed and win/lost TODO
												offlineChallenge.setChallengeData2(feed.message2);
												quizApp.getDataBaseHelper().updateOfflineChallenge(offlineChallenge);
										}
										quizApp.getStaticPopupDialogBoxes().showChallengeWinDialog(offlineChallenge);
										if(feedPreprocessedCount<1) 		homeScreen.addFeedView(feeds, UiText.USER_FEED.getValue());
										return null;
									}
								}, true); 
								break;
							case FEED_USED_JOINED:
								--feedPreprocessedCount;
								if(feedPreprocessedCount<1) homeScreen.addFeedView(feeds, UiText.USER_FEED.getValue());
								break;
						}
					} 
					return null;
				}
			});
			
		}
//		cs.addFeedView();
//		cs.addQuizzes();
		
		insertScreen(homeScreen);
		List<Badge> pendingBadges = quizApp.getDataBaseHelper().getAllPendingBadges();
		if(pendingBadges!=null)
			quizApp.getBadgeEvaluator().newBadgeUnlocked(new ArrayList<Badge>(pendingBadges));
		
		
//		insertScreen(new UserProfileScreen(this));
//		insertScreen(new WinOrLoseScreen(this));
	}


    public void doGplusLogin(){
    	GoogleLoginHelper gPlusHelper = new GoogleLoginHelper(quizApp);
    	
    	gPlusHelper.doLogin(new DataInputListener<User>(){
    		@Override
    		public String onData(User user) {
    			quizApp.getServerCalls().doGooglePlusLogin(user, new DataInputListener<User>(){
    				@Override
    				public String onData(User user) {
    	    			afterUserLoggedIn(user);
    					return null;
    				}
    			});
    			
    			return null;
    		}
    	});
    }
    
    public void doFbLogin(){
    	FacebookLoginHelper fbLoginHelper = new FacebookLoginHelper(quizApp);
    	fbLoginHelper.doLogin(new DataInputListener<User>(){
    		@Override
    		public String onData(User user) {
    			quizApp.getServerCalls().doFacebookLogin(user, new DataInputListener<User>(){
    				@Override
    				public String onData(User user) {
    	    			afterUserLoggedIn(user);
    					return null;
    				}
    			});
    			return null;
    		}
    	});
    }
   


	public void setLoginListener(DataInputListener<User> loginListener) {
		this.loginListener = loginListener;
	}

	public WelcomeScreen showWelcomeScreen(){
		  WelcomeScreen welcomeScreen = new WelcomeScreen(this);
        insertScreen(welcomeScreen);
        return welcomeScreen;
	}
  
	public void afterUserLoggedIn(User user){
		quizApp.setUser(user);
		checkAndShowCategories();
	}

	@Override
	public void onDestroy() {
		Screen screen = quizApp.peekCurrentScreen();
		while(screen!=null){
			if(screen instanceof WelcomeScreen){
			}
		}
	}
	
	

		
		
	public void showAllCategories() {
		clearScreen();
		HomeScreen allCategoriesScreen = new HomeScreen(this);
		List<Category> categories = quizApp.getDataBaseHelper().getAllCategories();
		allCategoriesScreen.addCategoriesView(categories, false);
		insertScreen(allCategoriesScreen);
	}

	public void showAllUserQuizzes() {
		clearScreen();
		HomeScreen allQuizzesScreen = new HomeScreen(this);
		List<Quiz> quizzes = quizApp.getDataBaseHelper().getAllQuizzesOrderedByXP();
		allQuizzesScreen.addUserQuizzesView(quizzes ,false , UiText.USER_FAVOURITES.getValue());
		insertScreen(allQuizzesScreen);
	}
	
	public void showLeaderBoards(String  quizId){
		clearScreen(); 
		quizApp.getServerCalls().getScoreBoards(quizId , new DataInputListener2<HashMap<String , Integer[]>, HashMap<String , Integer[]>,Void , Void>(){
			@Override
			public void onData(final HashMap<String , Integer[]> a, final HashMap<String, Integer[]> b, Void c) {
				ArrayList<String> allUids = new ArrayList<String>(a.keySet());
				allUids.addAll(b.keySet());
				quizApp.getDataBaseHelper().getAllUsersByUid(allUids, new DataInputListener<Boolean>(){
					@Override
					public String onData(Boolean s) {
						LeaderBoardScreen lscreen = new LeaderBoardScreen(UserMainPageController.this);
						lscreen.addLeaderBoards(a, UiText.GLOBAL_RANKINGS.getValue());
						lscreen.addLeaderBoards(b, UiText.LOCAL_RANKINGS.getValue());
						insertScreen(lscreen);
						return super.onData(s);
					}
				});
			}
		});
	}

	public void onStartChallengeQuiz(final Quiz quiz) {
		final User user = quizApp.getUser();
		if(quizApp.getUser().getSubscribedTo().size()==0){
			quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.NO_FRIENDS_TRY_ADDING.getValue(), null, UiText.OK.getValue(), null);
		}
		clearScreen();
		quizApp.getDataBaseHelper().getAllUsersByUid(user.getSubscribedTo(), new DataInputListener<Boolean>(){
			@Override
			public String onData(Boolean s) {
				SelectFriendsScreen selectFriendsScreen = new SelectFriendsScreen(UserMainPageController.this);
				selectFriendsScreen.doNotShowOnBackPress = true;
				List<User> users = new ArrayList<User>();
				for(String uid : user.getSubscribedTo()){
					users.add(quizApp.cachedUsers.get(uid));
				}
				selectFriendsScreen.showFriendsList(UiText.SELECT_FRIENDS_TO_CHALLENGE.getValue(), users,new DataInputListener<User>(){
					@Override
					public String onData(User s) {
						ProgressiveQuizController progressiveQuiz = (ProgressiveQuizController) quizApp.loadAppController(ProgressiveQuizController.class);
						progressiveQuiz.startNewChallenge(s, quiz);
						return super.onData(s);
					}
				}, true , false);
				insertScreen(selectFriendsScreen);
				return super.onData(s);
		    }
		});
	}

	public void startNewOfflineChallenge(final OfflineChallenge offlineChallenge) {
		quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.DO_YOU_START_CHALLENGE.getValue(), UiText.START.getValue(), UiText.CANCEL.getValue(), new DataInputListener<Boolean>(){
			@Override
			public String onData(Boolean s) {
				if(s){
					((ProgressiveQuizController)quizApp.loadAppController(ProgressiveQuizController.class)).startChallengedGame(offlineChallenge);
				}
				return super.onData(s);
			}
		});
		
	}

	public void showAllOfflineChallenges() {
		clearScreen();
		HomeScreen homeScreen = new HomeScreen(this);
		homeScreen.addOfflineChallengesView(quizApp.getDataBaseHelper().getPendingRecentOfflineChallenges(-1), false, UiText.OFFLINE_CHALLENGES.getValue(), false);
		insertScreen(homeScreen);
	}
}
