package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.UserDeviceManager;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes;
import com.amcolabs.quizapp.screens.QuestionScreen;
import com.amcolabs.quizapp.screens.QuizzesScreen;
import com.amcolabs.quizapp.screens.HomeScreen;
import com.amcolabs.quizapp.screens.WelcomeScreen;
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
import com.google.android.gms.plus.model.people.Person.Gender;


public class UserMainController  extends AppController implements OnInitializationCompleteListener, OnLoginCompleteListener, OnRequestDetailedSocialPersonCompleteListener<SocialPerson>{
	 
	public static final String SOCIAL_NETWORK_TAG = "com.amcolabs.quizapp.loginscreen";
    protected boolean mSocialNetworkManagerInitialized = false;
    User user= null;
	private SocialNetworkManager mSocialNetworkManager;
	public UserMainController(QuizApp quizApp) {
		super(quizApp);
	}

//	public WelcomeScreen welcomeScreen;
//	public SignUpScreen signUpScreen;
//	public HomeScreen categoriesScreen;
//	public TopicsScreen topicsScreen;
	 
	
	public void checkAndShowCategories(){
		String encodedKey = quizApp.getUserDeviceManager().getPreference(Config.PREF_ENCODED_KEY, null);
		if(encodedKey!=null){  
			//on fetch update new categories and draw the categories
			showUserHomeScreen();
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
					showUserHomeScreen();
				}
				return null;
			}
		});// on ACTIVATED, save user quizApp setUser
	}
	public void onCategorySelected(Category category){
		clearScreen();
		QuizzesScreen categoryQuizzesScreen = new QuizzesScreen(this);
		
		ArrayList<Quiz> quizzes = new ArrayList<Quiz>();
		for(int i=0;i<10;i++){ 
			quizzes.add(Quiz.createDummy());
		}
		categoryQuizzesScreen.addQuizzesToList(quizzes);
		showScreen(categoryQuizzesScreen);
	}
	
	public void onQuizSelected(Quiz quiz){
		clearScreen();
		QuestionScreen questionScreen = new QuestionScreen(this);
		
		ArrayList<Quiz> quizzes = new ArrayList<Quiz>();
		for(int i=0;i<10;i++){ 
			quizzes.add(Quiz.createDummy());
		}
		showScreen(questionScreen);

	}
	
	
	
	private void showUserHomeScreen() {
		clearScreen();
		HomeScreen cs= new HomeScreen(this);
		ArrayList<Category> categories = new ArrayList<Category>();
		for(int i=0;i<10;i++){
			categories.add(Category.createDummy());
		}
		cs.fillCategories(categories);
		/*
		List<Category> categories = quizApp.getDataBaseHelper().getCategories();
		showScreen(new HomeScreen(this));
		quizApp.getServerCalls().getNewCategories(quizApp.getUserDeviceManager().getDoublePreference(Config.PREF_LAST_CATEGORIES_FETCH_TIME, 0), new DataInputListener<List<Category>>(){
			public String onData(List<Category> s) {
				updateCategoriesScreen(s);
				return null;
			}
		});
		*/
		showScreen(cs);
	}
	

	@Override
	public void clearScreen() {
		if(getCurrentScreen() instanceof WelcomeScreen){
			onRemoveWelcomeScreen();
		}
		super.clearScreen();
	}
	
    public void onRemoveWelcomeScreen() {//destroy msocialNetwork
    	if(mSocialNetworkManager.getSocialNetwork(GooglePlusSocialNetwork.ID).isConnected()){
    		
    	}
    	if(mSocialNetworkManager.getSocialNetwork(FacebookSocialNetwork.ID).isConnected()){
    		
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
					plusNetwork.requestLogin(UserMainController.this);
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
					fbNetwork.requestLogin(UserMainController.this);
				}
				else{
					afterFbConnected();
				}
			}
		});
		
        showScreen(welcomeScreen);
        return welcomeScreen;
	}

	protected void onFacebookButtonPressed() {
 
    }

  
	public void afterUserLoggedIn(User user){
		quizApp.setUser(user);
		checkAndShowCategories();
	}
 
	@Override
	public boolean onBackPressed() {
		return super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		Screen screen = popScreen();
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
				plusNetwork.requestDetailedCurrentPerson(UserMainController.this);
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
			plusNetwork.requestLogin(UserMainController.this);
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
				UserMainController.this.afterUserLoggedIn(s);
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
}
