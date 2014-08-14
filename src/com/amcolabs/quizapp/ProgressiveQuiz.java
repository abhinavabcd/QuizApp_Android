package com.amcolabs.quizapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.amcolabs.quizapp.User.ShortUserInfo;

public class ProgressiveQuiz extends LinearLayout{

	
	public LinearLayout clashScreen;
	private LinearLayout questionView;
	private LinearLayout headerView;
	private Map<String , ProgressBar> userProgressBars = new HashMap<String, ProgressBar>();
	private TimerView timerView = null;
	
	Question currentQuestion=null;
	private boolean allUsersResponded;
	
	ProgressiveQuiz(Context context){
		super(context);
		timerView = new TimerView(-1){
			public void onTimerEnd() {
				ProgressiveQuiz.this.onTimerEnd(); 
			};
		};
	}

	public void start(){
		showWaitingScreen();
	}

	
	public void showWaitingScreen(){
		showClashScreen(User.getShortUserInfo() , null);
	}
	
	private void showClashScreen(ShortUserInfo ... users) {
		
	}
	
	private void finalizeClashScreen(){
		
	}
	
	private void newUserJoined(ShortUserInfo userInfo){
		
	}
	
	public void onQuestionsStarted(ArrayList<Question> questions){
		finalizeClashScreen();
		showNextQuestion();
	}
	
	public void showNextQuestion(){
		currentQuestion.fadeView();
		currentQuestion = getNextQuestion();
		questionView.removeAllViews();
		questionView.addView(currentQuestion.drawView());
	}
	
	private Question getNextQuestion() {
		// TODO Auto-generated method stub
		return null;
	}
	

	public void onAnswer(Question question){
		validateAnswerView();
		if(allUsersResponded){
			showNextQuestion();
		}
	}
	
	public void recieveServerCommand(Object A, Object B){
		switch(1){
			case 1://userAnswered 
				
		}
	}
	
	public void validateAnswerView(){
		
	}
	
	
	protected void onTimerEnd() {
		// TODO Auto-generated method stub
		
	}
	
}
