package com.amcolabs.quizapp.screens;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.google.android.gms.common.SignInButton;

public class WelcomeScreen extends Screen {

	SignInButton googlePlusButton;
	Button facebookButton;
	
	public WelcomeScreen(AppController controller) {
		super(controller);
		View v = getApp().getActivity().getLayoutInflater().inflate(R.layout.welcome_login_fb_gplus, null);
		addView(v);
		googlePlusButton = (SignInButton)v.findViewById(R.id.google_plus_button);
		facebookButton = (Button)v.findViewById(R.id.facebook_button);
	}

	public SignInButton getPlusButton() {
		return googlePlusButton;
	}
	
	public Button getFacebookButton() {
		return facebookButton;
	}
}