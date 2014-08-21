package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.UserDeviceManager;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.screens.CategoryScreen;
import com.amcolabs.quizapp.screens.WelcomeScreen;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.SocialNetworkManager.OnInitializationCompleteListener;
import com.androidsocialnetworks.lib.impl.FacebookSocialNetwork;
import com.androidsocialnetworks.lib.impl.GooglePlusSocialNetwork;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestAccessTokenCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.androidsocialnetworks.lib.persons.FacebookPerson;
import com.androidsocialnetworks.lib.persons.GooglePlusPerson;
import com.androidsocialnetworks.lib.persons.SocialPerson;
import com.google.android.gms.plus.model.people.Person.Gender;

public class UserHomeController  extends AppController implements OnInitializationCompleteListener, OnLoginCompleteListener, OnRequestDetailedSocialPersonCompleteListener, OnRequestAccessTokenCompleteListener{
	 
	public static final String SOCIAL_NETWORK_TAG = "com.amcolabs.quizapp.loginscreen";
    protected SocialNetworkManager mSocialNetworkManager;
    protected boolean mSocialNetworkManagerInitialized = false;
    User user= null;
	public UserHomeController(QuizApp quizApp) {
		super(quizApp);
	}

//	public WelcomeScreen welcomeScreen;
//	public SignUpScreen signUpScreen;
//	public CategoryScreen categoriesScreen;
//	public TopicsScreen topicsScreen;
	 
	
	public void checkAndShowCategories(){
		String encodedKey = quizApp.getUserDeviceManager().getPreference(Config.PREF_ENCODED_KEY, null);
		if(encodedKey!=null){  
			//on fetch update new categories and draw the categories
			showCategoriesScreen();
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
					showCategoriesScreen();
				}
				return null;
			}
		});// on ACTIVATED, save user quizApp setUser
	}

	private void updateCategoriesScreen(List<Category> newCategories) {
		if(getCurrentScreen() instanceof CategoryScreen){
			//update all that 
		}
	}
	
	private void showCategoriesScreen() {
		removeScreen();
		List<Category> categories = quizApp.getDataBaseHelper().getCategories();
		showScreen(new CategoryScreen(this));
		quizApp.getServerCalls().getNewCategories(quizApp.getUserDeviceManager().getDoublePreference(Config.PREF_LAST_CATEGORIES_FETCH_TIME, 0), new DataInputListener<List<Category>>(){
			public String onData(List<Category> s) {
				updateCategoriesScreen(s);
				return null;
			}
		});
	}

	@Override
	public void removeScreen() {
		if(getCurrentScreen() instanceof WelcomeScreen){
			onRemoveWelcomeScreen();
		}
		super.removeScreen();
	}
	
    public void onRemoveWelcomeScreen() {//destroy msocialNetwork
		 for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
	            socialNetwork.cancelAll();
        }
    }
	
	public WelcomeScreen showWelcomeScreen(){
		WelcomeScreen welcomeScreen = new WelcomeScreen(this);
		welcomeScreen.getPlusButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onGooglePlusAction();
			}
		});
		welcomeScreen.getFacebookButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onFacebookAction();
			}
		});
		
		mSocialNetworkManager = (SocialNetworkManager) quizApp.getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);

		ArrayList<String> facebookPermissions = new ArrayList<String>();
		facebookPermissions.addAll(Arrays.asList("email","user_birthday","user_location"));
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = SocialNetworkManager.Builder.from(quizApp)
                    .facebook(facebookPermissions)
                    .googlePlus()
                    .build();
            quizApp.getSupportFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            // we need to setup buttons correctly, mSocialNetworkManager isn't null, so
            // we are sure that it was initialized
            mSocialNetworkManagerInitialized = true;
        }
        showScreen(welcomeScreen);
        return welcomeScreen;
	}

    protected void onFacebookAction() {
    	FacebookSocialNetwork fbNetwork = mSocialNetworkManager.getFacebookSocialNetwork();
    	if(fbNetwork.isConnected())
    		fbNetwork.requestLogin(this);
    }

    protected void onGooglePlusAction() {
    	GooglePlusSocialNetwork plusNetwork = mSocialNetworkManager.getGooglePlusSocialNetwork();
    	if(!plusNetwork.isConnected())
    		plusNetwork.requestLogin(this);
    }
    
	public void onUserLoggedIn(User user){
		quizApp.setUser(user);
	}

    
     protected boolean checkIsLoginned(int socialNetworkID) {
        if (mSocialNetworkManager.getSocialNetwork(socialNetworkID).isConnected()) {
            return true;
        }
        return false;
    }


	@Override
	public void onSocialNetworkManagerInitialized() {
        if (mSocialNetworkManager.getFacebookSocialNetwork().isConnected()) {
        	if(getCurrentScreen() instanceof WelcomeScreen){
        		((WelcomeScreen)getCurrentScreen()).getFacebookButton().setText(UiText.FETCHING_USER.getValue());
        	}
    		mSocialNetworkManager.getSocialNetwork(FacebookSocialNetwork.ID).requestDetailedCurrentPerson(this);
        }
        else if (mSocialNetworkManager.getGooglePlusSocialNetwork().isConnected()) {
        	if(getCurrentScreen() instanceof WelcomeScreen){
        		((WelcomeScreen)getCurrentScreen()).getPlusButton().setText(UiText.FETCHING_USER.getValue());
        	}
    		mSocialNetworkManager.getSocialNetwork(GooglePlusSocialNetwork.ID).requestDetailedCurrentPerson(this);
        }
        else{
        	
        }
	}

	@Override
	public void onError(int socialNetworkID, String requestID,
			String errorMessage, Object data) {
	}

	@Override
	public void onLoginSuccess(int socialNetworkID) {
		mSocialNetworkManager.getSocialNetwork(socialNetworkID).requestDetailedCurrentPerson(this);
	}

	@Override
	public boolean onBackPressed() {
		return super.onBackPressed();
	}

	@Override
	public void onRequestDetailedSocialPersonSuccess(int socialNetworkID,SocialPerson socialPerson) {		
		String details = null;
		User user = new User();
		GooglePlusPerson gPerson;
		switch(socialNetworkID){
			case GooglePlusSocialNetwork.ID:
				gPerson =((GooglePlusPerson)socialPerson);
				details = gPerson.toString();
				user.uid = gPerson.id;
				user.deviceId = UserDeviceManager.getDeviceId(quizApp.getContentResolver());
				user.name = gPerson.name;
				user.emailId = gPerson.email;
				user.pictureUrl = gPerson.avatarURL;
				user.coverPictureUrl = gPerson.coverURL;
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
				user.coverPictureUrl = fPerson.coverUrl;
				user.place = fPerson.city;
				user.gender = fPerson.gender;
				user.birthday = 0;//fPerson.birthday;
				break;
		}
		mSocialNetworkManager.getSocialNetwork(socialNetworkID).requestAccessToken(this);
		this.user = user;
	}

	@Override
	public void onRequestAccessTokenComplete(int socialNetworkID,AccessToken accessToken) {
		DataInputListener<User> loginListener = new DataInputListener<User>(){
			@Override
			public String onData(User s) {
				UserHomeController.this.onUserLoggedIn(s);
				return super.onData(s);
			}
		};
		
		switch(socialNetworkID){
			case GooglePlusSocialNetwork.ID:
				user.googlePlus = accessToken.token;
				quizApp.getServerCalls().setGooglePlusLogin(user,loginListener);
				break;
			case FacebookSocialNetwork.ID:
				user.facebook = accessToken.token;
				quizApp.getServerCalls().setFacebookLogin(user,loginListener);

				break;
		}
	}	
}

