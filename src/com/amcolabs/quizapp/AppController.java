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
	}
	public Context getContext() {
		return quizApp.getContext();
	}

	public void clearScreen(boolean remove) {
		Screen p = null;
		while(!screenStack.isEmpty() && (p=remove ? screenStack.pop():screenStack.peek())==null ){
		}
		if(p!=null)
			quizApp.animateScreenRemove(p);//remove to side animation
	}
	
	public void showScreen(Screen newScreen){
		screenStack.push(newScreen);
    	quizApp.animateScreenIn(newScreen);//do into animation
	}
	
	public Screen getCurrentScreen(){
		if(!screenStack.isEmpty())
			return screenStack.peek();
		return null;
	}
	
	public Screen popScreen(){
		Screen p = null;
		while(!screenStack.isEmpty() && (p=screenStack.pop())==null ){
		}
		return p;
	}
	

	public boolean onBackPressed(){
		if(!screenStack.isEmpty()){
			quizApp.animateScreenRemove(screenStack.pop(), QuizApp.TO_RIGHT);//move screen to right
		}
		if(!screenStack.isEmpty()){
			quizApp.animateScreenIn(screenStack.pop(), QuizApp.TO_LEFT);//add the old screen
			return true;
		}
		return false;
	}
	public abstract void onDestroy();
}
