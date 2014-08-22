package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.UserDeviceManager;
import com.amcolabs.quizapp.databaseutils.Question;
import com.amcolabs.quizapp.widgets.TimerView;

public class ProgressiveQuizController extends AppController{
	
	public ProgressiveQuizController(QuizApp quizApp) {
		super(quizApp);
		// TODO Auto-generated constructor stub
	}


	int nPeople;
	int nQuestions;
    String quizType = "progressive";
    String name = null;
    
	public Screen clashScreen;
	private Screen questionView;
	private Screen headerView;
	private Map<String , ProgressBar> userProgressBars = new HashMap<String, ProgressBar>();
	private TimerView timerView = null;
	
	Question currentQuestion=null;
	private boolean allUsersResponded;
		
	public void showWaitingScreen(){
		showClashScreen(quizApp.getUser() , null);
	}
 	
	private void showClashScreen(User ... users) {
		
	}
	
	private void finalizeClashScreen(){
		//animate for a second and start questions
	}
	
	public void onQuestionsStarted(ArrayList<Question> questions){
		finalizeClashScreen();
		showNextQuestion();
	}
	
	public void showNextQuestion(){
		//currentQuestion.fadeView();
		currentQuestion = getNextQuestion();
		questionView.removeAllViews();
		//questionView.addView(currentQuestion.drawView());
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
	
	@Override
	public Screen getCurrentScreen() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

}
