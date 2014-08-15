package com.amcolabs.quizapp;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class AppManager {
	
	protected QuizApp quizApp;
	public AppManager(QuizApp quizApp) {
		this.quizApp = quizApp;
	}
	
	public void start(Context context){
		
	}
	
	public LinearLayout getView(){
		return null;
	}
	
	public abstract void onScreenAdded(Screen screen);
	public abstract void onScreenRemoved(Screen screen);
	public abstract void animateNewScreen(Screen newScreen, Screen oldScreen);
	public abstract Screen getCurrentScreen();
}
