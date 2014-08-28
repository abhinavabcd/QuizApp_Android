package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.widget.ProgressBar;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.databaseutils.Question;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.screens.QuestionScreen;
import com.amcolabs.quizapp.screens.ClashScreen;
import com.amcolabs.quizapp.serverutils.ServerResponse;
import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;
import com.amcolabs.quizapp.widgets.TimerView;
import com.amcolabs.quizapp.widgets.UserInfoCard;
import com.google.gson.reflect.TypeToken;

import de.tavendo.autobahn.WebSocketConnection;

public class ProgressiveQuizController extends AppController{
	User user;
	User user2;
	
	private WebSocketConnection serverSocket;
	
   	private Map<String , ProgressBar> userProgressBars = new HashMap<String, ProgressBar>();
	private TimerView timerView = null;
	
	Question currentQuestion=null;
	private boolean allUsersResponded;
	private Quiz quiz;
		

	
	public ProgressiveQuizController(QuizApp quizApp) {
		super(quizApp);
	}

	
	public void initlializeQuiz(Quiz quiz) {
		this.quiz = quiz;
//		QuestionScreen questionScreen = new QuestionScreen(this);
//		insertScreen(questionScreen);
		showWaitingScreen();
	}

	
	ArrayList<User> clashingUsers = new ArrayList<User>();
	
	public void showWaitingScreen(){
		clearScreen();
		ClashScreen clashingScreen = new ClashScreen(this);
		clashingScreen.setClashCount(2);
		clashingScreen.updateClashScreen(User.getDummyUser(quizApp)/*quizApp.getUser()*/, 0);//TODO: change to quizApp.getUser()
		insertScreen(clashingScreen);
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
	public void onDestroy() {
	}
	
	int backPressedCount = 0;
	
	@Override
	public boolean onBackPressed() {
		if(quizApp.peekCurrentScreen() instanceof QuestionScreen){
			backPressedCount++;
			if(backPressedCount>1){
				backPressedCount = 0;
				return false;
			}
			return true;
		}
		
		else{
			return false;
		}
	}


	public void startSocketConnection(WebSocketConnection mConnection) {
		serverSocket = mConnection;
	}


	public void onMessageRecieved(MessageType messageType, ServerResponse response, String data) {
		switch(messageType){
	    	case USER_ANSWERED_QUESTION:
	    		break; 
	    	case GET_NEXT_QUESTION://client trigger
	    		break; 
	    	case STARTING_QUESTIONS:// start questions
	    		User user2 = quizApp.getConfig().getGson().fromJson(response.payload,User.class);
	    		//pre download assets if ever its possible 
	    		ArrayList<Question> questions = quizApp.getConfig().getGson().fromJson(response.payload1,new TypeToken<ArrayList<Question>>(){}.getType());
	    		
	    		break; 
	    	case ANNOUNCING_WINNER:
	    		break; 
	    	case USER_DISCONNECTED:
	    		break; 
	    	case NEXT_QUESTION:
	    		break; 
	    	case START_QUESTIONS:
	    		break; 
	    	case STATUS_WHAT_USER_GOT:
	    		break; 
			default:
				break;
		}

	}


}
