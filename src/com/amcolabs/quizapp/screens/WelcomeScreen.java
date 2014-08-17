package com.amcolabs.quizapp.screens;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;

public class WelcomeScreen extends Screen {

	Button googlePlusButton;
	Button facebookButton;
	
	public WelcomeScreen(AppController controller) {
		super(controller);
		View v = getApp().getLayoutInflater().inflate(R.layout.welcome_login_fb_gplus, null);
		addView(v);
		googlePlusButton = (Button)v.findViewById(R.id.google_plus_button);
		facebookButton = (Button)v.findViewById(R.id.facebook_button);
	}

	public Button getPlusButton() {
		return googlePlusButton;
	}
	
	public Button getFacebookButton() {
		return facebookButton;
	}
}