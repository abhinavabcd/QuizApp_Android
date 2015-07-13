package com.amcolabs.quizapp.screens;

import android.content.Context;
import android.util.AttributeSet;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.Screen;

public class SignUpScreen extends Screen {

	public SignUpScreen(AppController appManager) {
		super(appManager);
		// TODO Auto-generated constructor stub
	}
	public ScreenType getScreenType(){
		return ScreenType.SIGNUP_SCREEN;
	}
}
