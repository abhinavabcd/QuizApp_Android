package com.amcolabs.quizapp.screens;

import android.content.Context;
import android.os.UserHandle;
import android.view.View;
import android.widget.Button;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.appcontrollers.UserMainPageController;
import com.google.android.gms.common.SignInButton;

public class WelcomeScreen extends Screen {

	SignInButton googlePlusButton;
	Button facebookButton;
	UserMainPageController userMainPageController=null;
	public WelcomeScreen(UserMainPageController controller) {
		super(controller);
		this.userMainPageController = controller;
		View v = getApp().getActivity().getLayoutInflater().inflate(R.layout.welcome_login_fb_gplus, this, false);
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
	
	@Override
	public void beforeRemove() {
		userMainPageController.onRemoveWelcomeScreen();

		super.beforeRemove();
	}
	@Override
	public boolean showOnBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}
}