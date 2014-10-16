package com.amcolabs.quizapp.appcontrollers;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.screens.BadgeScreen;

public class BadgeScreenController extends AppController{
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
		BadgeScreen badgeScreen = new BadgeScreen(this);
		insertScreen(badgeScreen);
	}
}