package com.quizapp.tollywood.screens;

import com.quizapp.tollywood.AppController;
import com.quizapp.tollywood.Screen;

public class LoginScreen extends Screen {

	public LoginScreen(AppController appManager) {
		super(appManager);
	}
	
	@Override
	public boolean showOnBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}
	public ScreenType getScreenType(){
		return ScreenType.LOGIN_SCREEN;
	}
}
