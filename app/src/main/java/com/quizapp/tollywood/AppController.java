package com.quizapp.tollywood;

import android.content.Context;

public abstract class AppController {
	
	public QuizApp quizApp;
	public AppController(QuizApp quizApp) {
		this.quizApp = quizApp;
	}
	public Context getContext() {
		return quizApp.getContext();
	}

	public void insertScreen(Screen newScreen){
    	quizApp.animateScreenIn(newScreen);//do into animation
	}
	
	public abstract void onDestroy();

	@Deprecated
	public void showScreen(Screen newScreen){
		clearScreen();
		insertScreen(newScreen);
	}
	
	public void clearScreen() {
		quizApp.animateScreenRemove();
	}	
	
	private boolean isActive;
	private int refCount;
	
	public boolean isActive(){
		return isActive;
	}
	public void setActive(boolean b) {
		isActive = b;
	}
	public int incRefCount() {
		return ++refCount;
	}

	public void decRefCount() {
		--this.refCount;
		if(refCount<=0){
			onDestroy();
		}
	}

}
