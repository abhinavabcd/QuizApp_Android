package com.quizapp.tollywood.appcontrollers;

import com.quizapp.tollywood.AppController;
import com.quizapp.tollywood.QuizApp;
import com.quizapp.tollywood.databaseutils.Badge;
import com.quizapp.tollywood.screens.BadgeScreen;

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