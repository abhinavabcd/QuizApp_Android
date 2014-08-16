package com.amcolabs.quizapp;

import java.util.Stack;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public abstract class AppController {
	
	protected QuizApp quizApp;
	protected Stack<Screen> screenStack;
	public AppController(QuizApp quizApp) {
		this.quizApp = quizApp;
		screenStack = new Stack<Screen>();
		screenStack.setSize(2);
	}
	
	public LinearLayout getView(){
		return null;
	}
	
	public void addNewScreen(Screen newScreen){
		screenStack.push(newScreen);
    	quizApp.animateScreenIn(newScreen);//do into animation
	}
	
	public Screen getCurrentScreen(){
		return screenStack.peek();
	}

	public Context getContext() {
		return quizApp.getApplicationContext();
	}

	public void removeScreen() {
		if(!screenStack.isEmpty())
			quizApp.animateScreenRemove(screenStack.pop());//remove to side animation
	}

	public boolean onBackPressed(){
		if(!screenStack.isEmpty()){
			quizApp.animateScreenRemove(screenStack.pop(), QuizApp.TO_RIGHT);//remove to side animation
		}
		if(!screenStack.isEmpty()){
			quizApp.animateScreenRemove(screenStack.pop(), QuizApp.TO_LEFT);//remove to side animation
			return true;
		}
		return false;
	}
}
