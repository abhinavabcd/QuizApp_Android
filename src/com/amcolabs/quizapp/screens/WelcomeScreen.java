package com.amcolabs.quizapp.screens;

import android.view.View;
import android.widget.Button;

import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.appcontrollers.UserMainPageController;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

public class WelcomeScreen extends Screen {

	SignInButton googlePlusButton;
	LoginButton facebookButton;
	UserMainPageController userMainPageController=null;
	public WelcomeScreen(UserMainPageController controller) {
		super(controller);
		this.userMainPageController = controller;
		View v = getApp().getActivity().getLayoutInflater().inflate(R.layout.welcome_login_fb_gplus, this, false);
		addView(v);
		googlePlusButton = (SignInButton)v.findViewById(R.id.google_plus_button);
		facebookButton = (LoginButton)v.findViewById(R.id.facebook_button);
		Button emailButton = (Button)v.findViewById(R.id.signup_with_email);
		emailButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getApp().getStaticPopupDialogBoxes().yesOrNo(UiText.FEATURE_COMMING_SOON.getValue(), null, UiText.CANCEL.getValue(), null);
			}
		});
		googlePlusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userMainPageController.doGplusLogin();
			}
		});
		facebookButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userMainPageController.doFbLogin();
			}
		});
	}

	
	@Override
	public void beforeRemove() {
		super.beforeRemove();
	}
	@Override
	public boolean showOnBackPressed() {
		return false;
	}
	
	@Override
	public boolean shouldAddtoScreenStack() {
		// TODO Auto-generated method stub
		return false;
	}
}