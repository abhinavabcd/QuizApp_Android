package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

import android.view.View;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController;
import com.amcolabs.quizapp.widgets.UserInfoCard;
import com.amcolabs.quizapp.widgets.WaitingForUserView;

public class UserClashScreen extends Screen {
	
	public UserClashScreen(AppController controller) {
		super(controller);
	}
	ProgressiveQuizController getController(){
		return (ProgressiveQuizController) controller;
	}
	int clashCount = 0;
	public void setClashCount(int i) {
		clashCount = i;
	}
	
	ArrayList<View> userInfoViews = new ArrayList<View>();
	public void updateClashScreen(User user , int index){
		for(int i=userInfoViews.size();i<index;i++){
			WaitingForUserView waitingView = new WaitingForUserView(getApp());
			userInfoViews.add(waitingView);
			this.addView(waitingView,i);
		}
		if(index==userInfoViews.size()){
			addView(new UserInfoCard(getApp(), "images/bg_1.jpg", user), index);
		}
		else{
			userInfoViews.get(index).setVisibility(View.INVISIBLE);
			addView(new UserInfoCard(getApp(), "images/bg_1.jpg",user), index);
		}
	}
}
