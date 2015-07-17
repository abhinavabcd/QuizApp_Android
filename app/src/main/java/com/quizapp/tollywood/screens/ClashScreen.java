package com.quizapp.tollywood.screens;

import java.util.ArrayList;

import android.view.View;
import android.widget.LinearLayout;

import com.quizapp.tollywood.AppController;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.Screen;
import com.quizapp.tollywood.User;
import com.quizapp.tollywood.appcontrollers.ProgressiveQuizController;
import com.quizapp.tollywood.databaseutils.Quiz;
import com.quizapp.tollywood.widgets.ChallengeView;
import com.quizapp.tollywood.widgets.GothamTextView;
import com.quizapp.tollywood.widgets.UserInfoCard;
import com.quizapp.tollywood.widgets.WaitingForUserView;

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
			userInfoCard.addLevelIndicator(getApp(), user.getPoints(quiz.quizId));
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

	public boolean removeOnBackPressed(){
		return getController().isRemoveScreenOnBackPressed();
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

	public ScreenType getScreenType(){
		return ScreenType.CLASH_SCREEN;
	}
}

