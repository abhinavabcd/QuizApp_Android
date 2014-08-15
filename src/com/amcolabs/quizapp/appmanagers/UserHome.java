package com.amcolabs.quizapp.appmanagers;

import android.content.Context;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.AppManager;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.screens.CategoryScreen;
import com.amcolabs.quizapp.screens.SignUpScreen;
import com.amcolabs.quizapp.screens.TopicsScreen;
import com.amcolabs.quizapp.screens.WelcomeScreen;

public class UserHome  extends AppManager{
	 
	public UserHome(QuizApp quizApp) {
		super(quizApp);
	}

	public WelcomeScreen welcomeScreen;
	public SignUpScreen signUpScreen;
	public CategoryScreen categoriesScreen;
	public TopicsScreen topicsScreen;
	
	@Override
	public void start(Context context) {
	}
	
	@Override
	public LinearLayout getView() {
		// TODO Auto-generated method stub
		return null;
	}
}

