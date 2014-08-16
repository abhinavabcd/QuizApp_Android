package com.amcolabs.quizapp;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class AppManager {
	
	protected QuizApp quizApp;
	protected Screen currentScreen;
	public AppManager(QuizApp quizApp) {
		this.quizApp = quizApp;
	}
	
	public void start(Context context){
		
	}
	
	public LinearLayout getView(){
		return null;
	}
	
	public void addNewScreen(Screen newScreen){
		currentScreen = newScreen;
    	quizApp.addView(newScreen);//do into animation
	}
	
	public Screen getCurrentScreen(){
		return currentScreen;
	}

	public Context getContext() {
		return quizApp.getContext();
	}

	public void removeScreen() {
		if(currentScreen!=null)
			quizApp.removeView(currentScreen);//remove to side animation
		currentScreen=null;
	}
	
}
