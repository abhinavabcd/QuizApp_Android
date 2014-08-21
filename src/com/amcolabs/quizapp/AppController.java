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
	public Context getContext() {
		return quizApp.getContext();
	}

	public void clearScreen() {
		Screen p = null;
		while(!screenStack.isEmpty() && (p=screenStack.pop())==null ){
		}
		if(p!=null)
			quizApp.animateScreenRemove(p);//remove to side animation
	}
	
	public void showScreen(Screen newScreen){
		screenStack.push(newScreen);
    	quizApp.animateScreenIn(newScreen);//do into animation
	}
	
	public Screen getCurrentScreen(){
		return screenStack.peek();
	}
	
	public Screen popScreen(){
		if(!screenStack.isEmpty())
			return screenStack.pop();
		return null;
	}
	

	public boolean onBackPressed(){
		if(!screenStack.isEmpty()){
			quizApp.animateScreenRemove(screenStack.pop(), QuizApp.TO_RIGHT);//remove to side animation
		}
		if(!screenStack.isEmpty()){
			quizApp.animateScreenIn(screenStack.pop(), QuizApp.TO_LEFT);//remove to side animation
			return true;
		}
		return false;
	}
	public abstract void onDestroy();
}
