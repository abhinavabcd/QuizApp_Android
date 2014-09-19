package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

import android.view.View;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.widgets.ChallengeView;
import com.amcolabs.quizapp.widgets.UserInfoCard;
import com.amcolabs.quizapp.widgets.WaitingForUserView;

public class ClashScreen extends Screen {
	
	public ClashScreen(AppController controller) {
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
	
	public void updateClashScreen(User user ,Quiz quiz,  int index){
		updateClashScreen(user, quiz , index, new WaitingForUserView(getApp()));	
	}
	
	public void updateClashScreen(User user , Quiz quiz , int index,  View waitingView){
		for(int i=userInfoViews.size();i<index;i++){
			userInfoViews.add(waitingView);
			this.addView(waitingView,i);
		}
		if(index==userInfoViews.size()){
			UserInfoCard userInfoCard = new UserInfoCard(getApp(), null, user);
			userInfoCard.addLevelIndicator(getApp(), user.getStats().containsKey(quiz.quizId) ?user.getStats().get(quiz.quizId):100);
			userInfoViews.add(userInfoCard);
			addView(userInfoCard, index);
		}
		else{
			userInfoViews.get(index).setVisibility(View.GONE);
			UserInfoCard userInfoCard = new UserInfoCard(getApp(), null,user);
			userInfoViews.set(index,userInfoCard); 
			addView(userInfoCard, index);
			userInfoCard.setAnimation(getApp().getUiUtils().getAnimationSlideInLeft());
		}
		
		for(int i=userInfoViews.size();i<clashCount;i++){
			userInfoViews.add(waitingView);
			this.addView(waitingView,i);
		}
	}
	
	public void beforeRemove(){
		for(View userInfoView : userInfoViews){
			if(userInfoView instanceof WaitingForUserView){
				((WaitingForUserView) userInfoView).cleanUp();
			}
			else if(userInfoView instanceof ChallengeView){
				((ChallengeView) userInfoView).cleanUp();
			}
		}
	}
	@Override
	public boolean showOnBackPressed() {
		return false;
	}
}
