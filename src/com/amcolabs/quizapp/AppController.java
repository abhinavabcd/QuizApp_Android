package com.amcolabs.quizapp;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class AppController {
	
	protected QuizApp quizApp;
	protected Screen currentScreen;
	public AppController(QuizApp quizApp) {
		this.quizApp = quizApp;
	}
	
	
	public LinearLayout getView(){
		return null;
	}
	
	public void addNewScreen(Screen newScreen){
		currentScreen = newScreen;
    	quizApp.animateScreenIn(newScreen);//do into animation
	}
	
	public Screen getCurrentScreen(){
		return currentScreen;
	}

	public Context getContext() {
		return quizApp.getContext();
	}

	public void removeScreen() {
		if(currentScreen!=null)
			quizApp.animateScreenRemove(currentScreen);//remove to side animation
		currentScreen=null;
	}

	public boolean onBackPressed(){
		return false;
	}
}
