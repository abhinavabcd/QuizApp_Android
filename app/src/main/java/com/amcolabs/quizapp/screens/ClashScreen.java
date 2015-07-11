package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.widgets.ChallengeView;
import com.amcolabs.quizapp.widgets.GothamTextView;
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
	private GothamTextView debugInfoView;
	
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
//			userInfoCard.addLevelIndicator(getApp(), user.getPoints(quiz.quizId)!=0?user.getPoints(quiz.quizId):100);
			userInfoCard.addLevelIndicator(getApp(), user.getPoints(quiz.quizId));
			userInfoViews.add(userInfoCard);
			addView(userInfoCard, index);
			if(user.uid.equalsIgnoreCase(getApp().getUser().uid)){
				addDebugView();
			}
		}
		else{//updating the users cards
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

	private void addDebugView() {
		LinearLayout temp = (LinearLayout)getApp().getActivity().getLayoutInflater().inflate(R.layout.debug_text_view, null);
		debugInfoView = (GothamTextView) temp.findViewById(R.id.debug_text_1);
		addView(temp);
	}
	GothamTextView debugView = null;
	public void setCurrentUserDebugText(String text){
		if(debugInfoView!=null){
			debugInfoView.setText(text);
		}
	}
	
	public void beforeRemove(){
	}
	
	@Override
	public void onRemovedFromScreen() {
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
