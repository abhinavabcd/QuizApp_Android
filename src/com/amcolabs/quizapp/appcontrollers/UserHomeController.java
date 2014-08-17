package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.screens.CategoryScreen;
import com.amcolabs.quizapp.screens.WelcomeScreen;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.SocialNetworkManager.OnInitializationCompleteListener;
import com.androidsocialnetworks.lib.impl.GooglePlusSocialNetwork;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.androidsocialnetworks.lib.persons.SocialPerson;

public class UserHomeController  extends AppController implements OnInitializationCompleteListener, OnLoginCompleteListener, OnRequestSocialPersonCompleteListener{
	 
	public static final String SOCIAL_NETWORK_TAG = "com.amcolabs.quizapp.loginscreen";
    protected SocialNetworkManager mSocialNetworkManager;
    protected boolean mSocialNetworkManagerInitialized = false;

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

        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = SocialNetworkManager.Builder.from(quizApp)
                    .facebook(new ArrayList<String>())
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
    	mSocialNetworkManager.getFacebookSocialNetwork().requestLogin(this);
    }

    protected void onGooglePlusAction() {
    	GooglePlusSocialNetwork plusNetwork = mSocialNetworkManager.getGooglePlusSocialNetwork();
    	if(!plusNetwork.isConnected())
    		plusNetwork.requestLogin(this);
    	else{
    		onSocialNetworkManagerInitialized();
    	}
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
        	
        }
        else if (mSocialNetworkManager.getGooglePlusSocialNetwork().isConnected()) {
        	
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
		mSocialNetworkManager.getSocialNetwork(socialNetworkID).requestCurrentPerson(this);
	}

	@Override
	public void onRequestSocialPersonSuccess(int socialNetworkId,SocialPerson socialPerson) {		
		AccessToken accessToken = mSocialNetworkManager.getSocialNetwork(socialNetworkId).getAccessToken();
	}
	
	@Override
	public boolean onBackPressed() {
		return super.onBackPressed();
	}
}

