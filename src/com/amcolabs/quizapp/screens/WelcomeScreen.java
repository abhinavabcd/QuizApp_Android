package com.amcolabs.quizapp.screens;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.amcolabs.quizapp.AppManager;
import com.amcolabs.quizapp.Screen;

public class WelcomeScreen extends Screen {

	Button googlePlusButton;
	Button facebookPlusButton;
	
	public WelcomeScreen(AppManager appManager) {
		super(appManager);
	}

	public Button getPlusButton() {
		return googlePlusButton;
	}
	
	public Button getFacebookButton() {
		return facebookPlusButton;
	}
}