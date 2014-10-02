package com.amcolabs.quizapp.appcontrollers;
 
import java.util.ArrayList;
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
import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes;
import com.amcolabs.quizapp.screens.HomeScreen;
import com.amcolabs.quizapp.screens.LeaderBoardScreen;
import com.amcolabs.quizapp.screens.QuizzesScreen;
import com.amcolabs.quizapp.screens.SelectFriendsScreen;
import com.amcolabs.quizapp.screens.WelcomeScreen;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.SocialNetworkManager.OnInitializationCompleteListener;
import com.androidsocialnetworks.lib.impl.FacebookSocialNetwork;
import com.androidsocialnetworks.lib.impl.GooglePlusSocialNetwork;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestAccessTokenCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.androidsocialnetworks.lib.persons.FacebookPerson;
import com.androidsocialnetworks.lib.persons.GooglePlusPerson;
import com.androidsocialnetworks.lib.persons.SocialPerson;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.plus.model.people.Person.Gender;


public class UserMainPageController  extends AppController implements OnInitializationCompleteListener, OnLoginCompleteListener, OnRequestDetailedSocialPersonCompleteListener<SocialPerson>{
	 
	public static final String SOCIAL_NETWORK_TAG = "com.amcolabs.quizapp.loginscreen";
    protected boolean mSocialNetworkManagerInitialized = false;
    User user= null;
	private SocialNetworkManager mSocialNetworkManager;
	private double currentQuizMaxTimeStamp;
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
					else{
						StaticPopupDialogBoxes.alertPrompt(quizApp.getFragmentManager(), UiText.COULD_NOT_CONNECT.getValue(), null);
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
		QuizzesScreen categoryQuizzesScreen = new QuizzesScreen(this);
		List<Quiz> quizzes = category.getQuizzes(quizApp);
		categoryQuizzesScreen.addQuizzesToList(category.description , quizzes, new DataInputListener<Quiz>(){
			@Override
			public String onData(final Quiz quiz) {
				quizApp.getStaticPopupDialogBoxes().showQuizSelectMenu(new DataInputListener<Integer>(){
					@Override
					public String onData(Integer s) {
						switch(s){
							case 1: 
								onQuizPlaySelected(quiz);
								break;
							case 2:
								break;
							case 3://challenge
								onStartChallengeQuiz(quiz);
								break;
							case 4://scoreboard
								showLeaderBoards(quiz.quizId);
								break;
						}
						return super.onData(s);
					}
				});
				return null;
			}
		});
		insertScreen(categoryQuizzesScreen);
	}
	
	public void onQuizPlaySelected(Quiz quiz){
		clearScreen();
		ProgressiveQuizController progressiveQuiz = (ProgressiveQuizController) quizApp.loadAppController(ProgressiveQuizController.class);
		progressiveQuiz.initlializeQuiz(quiz);
	}
	
	private void showUserHomeScreen(List<Feed> feeds) {
		GCMRegistrar.checkDevice(quizApp.getActivity());
        //TODO: uncomment this after testing
        //GCMRegistrar.checkManifest(quizApp.getActivity());
        final String regId = GCMRegistrar.getRegistrationId(quizApp.getActivity().getApplicationContext());
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(quizApp.getActivity(), Config.GCM_APP_ID);//
        }

		
		clearScreen();
		final HomeScreen homeScreen= new HomeScreen(this);
//		ArrayList<Category> categories = new ArrayList<Category>();
//		for(int i=0;i<10;i++){
//			categories.add(Category.createDummy());
//		}
		List<Category> categories = quizApp.getDataBaseHelper().getCategories(5);
		homeScreen.addCategoriesView(categories, categories.size()>4);
		 
		
		List<Quiz> quizzes = quizApp.getDataBaseHelper().getAllQuizzesOrderedByXP(5);
		homeScreen.addUserQuizzesView(quizzes ,quizzes.size()>4 , UiText.USER_FAVOURITES.getValue());
		
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
			for(Feed feed: feeds){
				switch(feed.getUserFeedType()){
					case FEED_CHALLENGE:
						OfflineChallenge offlineChallenge = quizApp.getDataBaseHelper().getOfflineChallengeByChallegeId(feed.message);
						if(!offlineChallenge.isCompleted()){
							offlineChallenge.setCompleted(true);
							//show popup that user has completed and win/lost TODO
								offlineChallenge.setChallengeData2(feed.message2);
								quizApp.getDataBaseHelper().updateOfflineChallenge(offlineChallenge);
						}
						break;
						
				}
			}
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

	
    public void onRemoveWelcomeScreen() {//destroy msocialNetwork
    	if(mSocialNetworkManager.getSocialNetwork(GooglePlusSocialNetwork.ID).isConnected()){
    		mSocialNetworkManager.getGooglePlusSocialNetwork().cancelAll();
    	}
    	if(mSocialNetworkManager.getSocialNetwork(FacebookSocialNetwork.ID).isConnected()){
    		mSocialNetworkManager.getFacebookSocialNetwork().cancelAll();
    	}
    	
    }
	
	public WelcomeScreen showWelcomeScreen(){
		  WelcomeScreen welcomeScreen = new WelcomeScreen(this);
	      mSocialNetworkManager = (SocialNetworkManager) quizApp.getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
		  if (mSocialNetworkManager == null) {
			    mSocialNetworkManager = SocialNetworkManager.Builder.from(quizApp.getActivity())
			            .facebook(new ArrayList<String>())
			            .googlePlus()
			            .build();
			    quizApp.getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
			    mSocialNetworkManager.setOnInitializationCompleteListener(this);
			} else {
			    mSocialNetworkManagerInitialized = true;
			}

		welcomeScreen.getPlusButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GooglePlusSocialNetwork plusNetwork = (GooglePlusSocialNetwork) mSocialNetworkManager.getSocialNetwork(GooglePlusSocialNetwork.ID);
				if(!plusNetwork.isConnected()){
					plusNetwork.requestLogin(UserMainPageController.this);
				}
				else{
					afterGooglePlusConnected();
				}
			}
		});
		welcomeScreen.getFacebookButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FacebookSocialNetwork fbNetwork = (FacebookSocialNetwork) mSocialNetworkManager.getSocialNetwork(FacebookSocialNetwork.ID);
				if(!fbNetwork.isConnected()){
					fbNetwork.requestLogin(UserMainPageController.this);
				}
				else{
					afterFbConnected();
				}
			}
		});
		
        insertScreen(welcomeScreen);
        return welcomeScreen;
	}

	protected void onFacebookButtonPressed() {
 
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
				onRemoveWelcomeScreen();
			}
		}
	}
	
	public void afterGooglePlusConnected(){
		final GooglePlusSocialNetwork plusNetwork = (GooglePlusSocialNetwork) mSocialNetworkManager.getSocialNetwork(GooglePlusSocialNetwork.ID);
		plusNetwork.requestAccessToken(quizApp.getActivity(), new OnRequestAccessTokenCompleteListener() {
			@Override
			public void onError(int socialNetworkID, String requestID,
					String errorMessage, Object data) {
				System.out.println("");
			}
			@Override
			public void onRequestAccessTokenComplete(int socialNetworkID,AccessToken accessToken) {
				user.googlePlus = accessToken.token;
				plusNetwork.requestDetailedCurrentPerson(UserMainPageController.this);
			}
		});
	}
	public void afterFbConnected(){
		
	}

	@Override
	public void onSocialNetworkManagerInitialized() {
		if(mSocialNetworkManagerInitialized) return;
	    mSocialNetworkManager.getSocialNetwork(GooglePlusSocialNetwork.ID).isConnecting();
		mSocialNetworkManagerInitialized = true;
		GooglePlusSocialNetwork plusNetwork = (GooglePlusSocialNetwork) mSocialNetworkManager.getSocialNetwork(GooglePlusSocialNetwork.ID);
		FacebookSocialNetwork fbNetwork = (FacebookSocialNetwork) mSocialNetworkManager.getSocialNetwork(FacebookSocialNetwork.ID);
		user = new User();
		if(plusNetwork.isConnected() && !plusNetwork.isConnecting()){
			afterGooglePlusConnected();
		}
		if(plusNetwork.isConnected() && plusNetwork.isConnecting()){
			plusNetwork.requestLogin(UserMainPageController.this);
		}
		
		//else wait for user to click
		if(fbNetwork.isConnected()){
			
		}
	}
	@Override
	public void onRequestDetailedSocialPersonSuccess(int socialNetworkID,SocialPerson socialPerson) {		
		String details = null;
		switch(socialNetworkID){
			case GooglePlusSocialNetwork.ID:
				GooglePlusPerson gPerson;
				gPerson =((GooglePlusPerson)socialPerson);
				details = gPerson.toString();
				user.uid = gPerson.id;
				user.deviceId = UserDeviceManager.getDeviceId(quizApp.getContentResolver());
				user.name = gPerson.name;
				user.emailId = gPerson.email;
				user.pictureUrl = gPerson.avatarURL;
				user.coverUrl = gPerson.coverURL;
				user.place = gPerson.currentLocation;
				user.gender = gPerson.gender==Gender.MALE ?"male":"female";
				user.birthday = 0;//gPerson.birthday;
				break;
			case FacebookSocialNetwork.ID:
				FacebookPerson fPerson = (FacebookPerson)socialPerson;
				user.uid = fPerson.id;
				user.deviceId = UserDeviceManager.getDeviceId(quizApp.getContentResolver());
				user.name = fPerson.name;
				user.emailId = fPerson.email;
				user.pictureUrl = fPerson.avatarURL;
				user.coverUrl = fPerson.coverUrl;
				user.place = fPerson.city;
				user.gender = fPerson.gender;
				user.birthday = 0;//fPerson.birthday;
				break;
	}
		DataInputListener<User> loginListener = new DataInputListener<User>(){
			@Override
			public String onData(User s) {
				UserMainPageController.this.afterUserLoggedIn(s);
				return super.onData(s);
			}
		};
		
		switch(socialNetworkID){
			case GooglePlusSocialNetwork.ID:
				quizApp.getServerCalls().doGooglePlusLogin(user,loginListener);
				break;
			case FacebookSocialNetwork.ID:
				quizApp.getServerCalls().doFacebookLogin(user,loginListener); 
				break;
		}
	}
	
	@Override
	public void onLoginSuccess(int socialNetworkID) {
		if(socialNetworkID == GooglePlusSocialNetwork.ID){
			afterGooglePlusConnected();
		}
		if(socialNetworkID == FacebookSocialNetwork.ID){
			afterFbConnected();
		}
	}

	@Override
	public void onError(int socialNetworkID, String requestID,String errorMessage, Object data) {
		StaticPopupDialogBoxes.alertPrompt(quizApp.getFragmentManager(), requestID+errorMessage, null);
	}
	
	
	public void showAllCategories() {
		
	}

	public void showAllUserQuizzes() {
		
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
				}, true);
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
