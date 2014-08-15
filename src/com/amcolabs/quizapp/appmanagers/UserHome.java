package com.amcolabs.quizapp.appmanagers;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.amcolabs.quizapp.AppManager;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.screens.CategoryScreen;
import com.amcolabs.quizapp.screens.LoginScreen;
import com.amcolabs.quizapp.screens.WelcomeScreen;
import com.androidsocialnetworks.lib.SocialNetwork;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.SocialNetworkManager.OnInitializationCompleteListener;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.androidsocialnetworks.lib.persons.SocialPerson;

public class UserHome  extends AppManager implements OnInitializationCompleteListener, OnLoginCompleteListener, OnRequestSocialPersonCompleteListener{
	 
	private static final String SOCIAL_NETWORK_TAG = "com.amcolabs.quizapp.loginscreen";
    protected SocialNetworkManager mSocialNetworkManager;
    protected boolean mSocialNetworkManagerInitialized = false;
	private Screen currentScreen;

	public UserHome(QuizApp quizApp) {
		super(quizApp);
	}

//	public WelcomeScreen welcomeScreen;
//	public SignUpScreen signUpScreen;
//	public CategoryScreen categoriesScreen;
//	public TopicsScreen topicsScreen;
	
	@Override
	public void start(Context context) {
		String encodedKey = quizApp.getUserDeviceManager().getPreference(Config.PREF_ENCODED_KEY, null);
		if(encodedKey!=null){
			ServerCalls.getNewCategories(quizApp.getUserDeviceManager().getDoublePreference(Config.PREF_LAST_CATEGORIES_FETCH_TIME, 0));
			//on fetch update new categories and draw the categories
			animateNewScreen(new CategoryScreen(context, this), null);
		}
		else if(quizApp.getUserDeviceManager().getPreference(Config.PREF_NOT_ACTIVATED, null)!=null){
			ServerCalls.checkVerificationStatus();// on ACTIVATED, save user quizApp setUser
		}
		else{
			WelcomeScreen welcomeScreen = new WelcomeScreen(context, this);
			animateNewScreen(welcomeScreen, null);
		}
	}
	
	@Override
	public Screen getCurrentScreen() {
		return currentScreen;
	}
	
	public void onUserLoggedIn(User user){
		quizApp.setUser(user);
	}

    protected void onFacebookAction() {
    	mSocialNetworkManager.getFacebookSocialNetwork().requestLogin(this);
    }

    protected void onGooglePlusAction() {
        mSocialNetworkManager.getGooglePlusSocialNetwork().requestLogin(this);
    }
    
    @Override
    public void animateNewScreen(Screen newScreen, Screen oldScreen){
    	if(oldScreen!=null){
	    	onScreenRemoved(oldScreen);
	    	quizApp.removeView(oldScreen);
    	}
    	onScreenAdded(newScreen);
    	quizApp.addView(newScreen);
    	currentScreen = newScreen;
    	//do some animation
    }

    @Override 
    public void onScreenAdded(Screen screen) {
    	if(screen instanceof LoginScreen){
    		WelcomeScreen welcomeScreen = (WelcomeScreen)screen;
    		welcomeScreen.getPlusButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onGooglePlusAction();
				}
			});
    		welcomeScreen.getPlusButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onFacebookAction();
				}
			});
    		
    		mSocialNetworkManager = (SocialNetworkManager) quizApp.getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
	
	        if (mSocialNetworkManager == null) {
	            mSocialNetworkManager = SocialNetworkManager.Builder.from(quizApp.getActivity())
	                    .facebook(new ArrayList<String>())
	                    .googlePlus()
	                    .build();
	            quizApp.getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
	
	            mSocialNetworkManager.setOnInitializationCompleteListener(this);
	        } else {
	            // we need to setup buttons correctly, mSocialNetworkManager isn't null, so
	            // we are sure that it was initialized
	            mSocialNetworkManagerInitialized = true;
	        }
    	}
    	
    }

    @Override 
    public void onScreenRemoved(Screen screen) {//destroy app managers
    	if(screen instanceof LoginScreen){
    		//cancel if any 
    		 for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
    	            socialNetwork.cancelAll();
	         }
    	}
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
	}
}

