package com.amcolabs.quizapp.screens;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.databaseutils.Badge;

public class BadgeScreenController extends AppController{

	BadgeScreen badgeScreen;
	
	public BadgeScreenController(QuizApp quizApp) {
		super(quizApp);
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
	
	public void onBadgeClick(Badge badge){
		quizApp.getStaticPopupDialogBoxes().showUnlockedBadge(badge, true, null);
	}

	public void showBadgeScreen() {
		clearScreen();
		badgeScreen = new BadgeScreen(this);
		insertScreen(badgeScreen);
	}
}