package com.quizapp.tollywood.screens;

import com.quizapp.tollywood.AppController;
import com.quizapp.tollywood.Screen;

public class SignUpScreen extends Screen {

	public SignUpScreen(AppController appManager) {
		super(appManager);
		// TODO Auto-generated constructor stub
	}
	public ScreenType getScreenType(){
		return ScreenType.SIGNUP_SCREEN;
	}
}
