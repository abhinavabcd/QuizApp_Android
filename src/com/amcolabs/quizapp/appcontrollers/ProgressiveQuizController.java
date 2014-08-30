package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.widget.ProgressBar;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.databaseutils.Question;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.screens.ClashScreen;
import com.amcolabs.quizapp.screens.QuestionScreen;
import com.amcolabs.quizapp.serverutils.ServerResponse;
import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;
import com.amcolabs.quizapp.serverutils.ServerWebSocketConnection;
import com.amcolabs.quizapp.widgets.TimerView;
import com.google.gson.reflect.TypeToken;

public class ProgressiveQuizController extends AppController{
	User user;
	User user2;
	
	private ServerWebSocketConnection serverSocket;
	
   	private Map<String , ProgressBar> userProgressBars = new HashMap<String, ProgressBar>();
	private TimerView timerView = null;
	
	private Quiz quiz;
		

	
	public ProgressiveQuizController(QuizApp quizApp) {
		super(quizApp);
	}

	
	public void initlializeQuiz(Quiz quiz) {
		this.quiz = quiz;
//		QuestionScreen questionScreen = new QuestionScreen(this);
//		insertScreen(questionScreen);
		showWaitingScreen(quiz);
	}

	ClashScreen clashingScreen = null;
	QuestionScreen questionScreen = null;
	public void showWaitingScreen(Quiz quiz){
		clearScreen();
		clashingScreen = new ClashScreen(this);
		clashingScreen.setClashCount(2);
		clashingScreen.updateClashScreen(quizApp.getUser()/*quizApp.getUser()*/, 0);//TODO: change to quizApp.getUser()
		insertScreen(clashingScreen);
		quizApp.getServerCalls().startProgressiveQuiz(this, quiz);
	}	
	
	
	public void showQuestionScreen(ArrayList<User> users, Question firstQuestion){
		clearScreen();
		clashingScreen = null; // dispose of it 
		questionScreen = new QuestionScreen(this);
		questionScreen.loadUserInfo(users);
		questionScreen.loadQuestion(firstQuestion);
		insertScreen(questionScreen);
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
				gracefullyCloseSocket();
				return false;
			}
			return true;
		}
		
		else{
			return false;
		}
	}
/*
 * Current running quiz
 */
	
	public final String QUESTIONS = "1";
	public final String CURRENT_QUESTION = "2";
	public final String MESSAGE_TYPE = "3";
	public final String QUESTION_ID = "4";
	public final String WHAT_USER_HAS_GOT = "5";
	public final String N_CURRENT_QUESTION_ANSWERED = "6";
	public final String USER_ANSWER = "7";
	public final String USERS="8";
	
	double waitinStartTime = 0;
	boolean noResponseFromServer = true;
	String serverId = null;
	private boolean botMode = false;

	private  void setBotMode(boolean mode){
		botMode = mode;
	}
	private boolean isBotMode(){
		return botMode;
	}
	
	private String constructSocketMessage(MessageType messageType , HashMap<String, String> data , HashMap<Integer, String> data1){
		String jsonStr = "{\""+MESSAGE_TYPE+"\":"+Integer.toString(messageType.getValue())+",";
		if(data!=null){
			for(String key:data.keySet()){
				jsonStr+="\""+key+"\":\""+data.get(key)+"\",";
			}
		}
		if(data1!=null){
			for(int key:data1.keySet()){
				jsonStr+="\""+Integer.toString(key)+"\":\""+data.get(key)+"\",";
			}
		}
		jsonStr = jsonStr.substring(0, jsonStr.length()-1); //remove a ,
		return jsonStr+"}";
	}

	public void startSocketConnection(ServerWebSocketConnection mConnection, final Quiz quiz) {
		serverSocket = mConnection;
		waitinStartTime = quizApp.getConfig().getCurrentTimeStamp();
        new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(noResponseFromServer){
					serverSocket.sendTextMessage(constructSocketMessage(MessageType.ACTIVATE_BOT, null, null));
				}
			}
		}, 5000);
        
        
	}
	
	public ArrayList<Question> currentQuestions = new ArrayList<Question>();
	public ArrayList<User> currentUsers = new ArrayList<User>();
	private ArrayList<Question> questions = null;
	
	public void onMessageRecieved(MessageType messageType, ServerResponse response, String data) {
		switch(messageType){
	    	case USER_ANSWERED_QUESTION:
	    		break; 
	    	case GET_NEXT_QUESTION://client trigger
	    		break; 
	    	case STARTING_QUESTIONS:// start questions // user finalised
	    		noResponseFromServer = false;
	    		currentUsers = quizApp.getConfig().getGson().fromJson(response.payload1,new TypeToken<ArrayList<User>>(){}.getType());
	    		for(User user: currentUsers){
	    			int index = 0;
	    			if(user.uid!=quizApp.getUser().uid){
	    				try{
	    					clashingScreen.updateClashScreen(user, ++index);
	    				}
	    				catch(NullPointerException e){
	    					e.printStackTrace();
	    				}
	    			}
	    		}
	    		//pre download assets if ever its possible 
	    		 questions  = quizApp.getConfig().getGson().fromJson(response.payload2,new TypeToken<ArrayList<Question>>(){}.getType());
	    		new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
			    		showQuestionScreen(currentUsers , questions.remove(0));
					}
				}, 2000);
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
	    	case OK_ACTIVATING_BOT: 
	    		quizApp.getServerCalls().informActivatingBot(quiz, serverSocket.serverId); 
	    		currentQuestions = quizApp.getConfig().getGson().fromJson(response.payload1, new TypeToken<List<Question>>(){}.getType());
	    		currentUsers = quizApp.getConfig().getGson().fromJson(response.payload, new TypeToken<List<User>>(){}.getType());
	    		setBotMode(true);
	    		serverSocket.disconnect();
	    		break;
			default:
				break;
		}

	}


	public void onSocketClosed() {
		//TODO: poup
	}
	
	public void gracefullyCloseSocket(){
		if(serverSocket!=null){
			serverSocket.disconnect();
		}
	}


	public void ohNoDammit() {
	}
	
}

