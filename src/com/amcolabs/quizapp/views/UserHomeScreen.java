package com.amcolabs.quizapp.views;

import android.widget.LinearLayout;

import com.amcolabs.quizapp.AppManagerInterface;

public class UserHomeScreen  implements AppManagerInterface{
	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public LinearLayout getView() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public void showSpecialTopics(){
	}
	
	
	public void showTopics(){
		
	}
	
	public void showUserFavourites(){
		
	}
	
	public void showQuizAppActivity(){
		
	}
	
	public void login(){
		showTopics();
		showUserFavourites();
		showQuizAppActivity();
	}
	
	public void onLogin(String userName , String pictureUrl, String statusMessage, String country){
		
	}
	
	public void beforeGooglePlusSignUp(){
		
	}
	
	public void afterGooglePlusSignUp(){
		
	}
	
	public void beforeFacebookSignUp(){
		
	}
	
	public void afterFacebookSignUp(){
		
	}
	
	
}

