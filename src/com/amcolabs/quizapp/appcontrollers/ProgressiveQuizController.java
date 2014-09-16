

package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import twitter4j.examples.oauth.GetAccessToken;
import android.os.Handler;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.WinOrLoseController;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Question;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.screens.ClashScreen;
import com.amcolabs.quizapp.screens.QuestionScreen;
import com.amcolabs.quizapp.screens.WinOrLoseScreen;
import com.amcolabs.quizapp.serverutils.ServerResponse;
import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;
import com.amcolabs.quizapp.serverutils.ServerWebSocketConnection;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

public class ProgressiveQuizController extends AppController{
	User user;
	User user2;
	
	private ServerWebSocketConnection serverSocket;
	
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
	
	public int getMaxScore(){
		if(currentQuestions==null || currentQuestions.size()==0)
			return 0;
		int mscore = 0;
		for(int i=0;i<currentQuestions.size();i++){
			mscore += currentQuestions.get(i).xp*quizApp.getConfig().multiplyFactor(i+1);
		}
		return mscore;
	}
	
	public void showQuestionScreen(ArrayList<User> users){
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
		questionScreen = new QuestionScreen(this);
		questionScreen.showUserInfo(users,getMaxScore());
		//animate TODO:
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				clearScreen();
				clashingScreen = null; // dispose of it 
				insertScreen(questionScreen);
			}
		}, Config.CLASH_SCREEN_DELAY);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
		        userAnswers = new HashMap<String , UserAnswer>();
		        userAnswersStack.clear();
		        currentScore = 0;
				Question currentQuestion = currentQuestions.remove(0);
				questionScreen.animateQuestionChange( UiText.GET_READY.getValue(), UiText.FOR_YOUR_FIRST_QUESTION.getValue() ,currentQuestion);
				if(isBotMode())
					scheduleBotAnswer(currentQuestion);

			}
		}, Config.CLASH_SCREEN_DELAY+Config.PREQUESTION_FADE_OUT_ANIMATION_TIME);
	}
	
	@Override
	public void onDestroy() {
		gracefullyCloseSocket();
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
			gracefullyCloseSocket();
			return false;
		}
	}
/*
 * Current running quiz
 */
	
	public final  static String QUESTIONS = "1";
	public final  static String CURRENT_QUESTION = "2";
	public final  static String MESSAGE_TYPE = "3";
	public final static String QUESTION_ID = "4";
	public final  static String WHAT_USER_HAS_GOT = "5";
	public final  static String N_CURRENT_QUESTION_ANSWERED = "6";
	public final  static String USER_ANSWER = "7";
	public final static  String USERS="8";
	public final  static String CREATED_AT="9";
	public final  static String ELAPSED_TIME="10"; 
	
	double waitinStartTime = 0;
	boolean noResponseFromServer = true;
	String serverId = null;
	private boolean botMode = false;
	
	private ArrayList<Question> currentQuestions = new ArrayList<Question>();
	private ArrayList<User> currentUsers = new ArrayList<User>();
	private HashMap<String ,UserAnswer> userAnswers;
	private HashMap<String ,List<UserAnswer>> userAnswersStack = new HashMap<String, List<UserAnswer>>();
	
	private int currentScore = 0;
	private int botScore =0;
	protected Random rand = new Random();
	
	private  void setBotMode(boolean mode){
		botMode = mode;
		botScore = 0;
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
		waitinStartTime = Config.getCurrentTimeStamp();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(noResponseFromServer){
					serverSocket.sendTextMessage(constructSocketMessage(MessageType.ACTIVATE_BOT, null, null));
				}
			}
		}, Config.BOT_INTIALIZE_AFTER_NO_USER_TIME);
	}
	
	public static class UserAnswer{
		@SerializedName(MESSAGE_TYPE)
		public int messageType = MessageType.USER_ANSWERED_QUESTION.getValue();
	    @SerializedName(QUESTION_ID)
	    public String questionId;
	    @SerializedName("uid")
	    public String uid;
	    @SerializedName(USER_ANSWER)
	    public String userAnswer;
	    @SerializedName(ELAPSED_TIME)
	    public int elapsedTime;
	    @SerializedName(WHAT_USER_HAS_GOT)
	    public int whatUserGot;
		
		public UserAnswer(String questionId, String uid, String userAnswer, int elapsedTime, int whatUserGot) {
			this.questionId = questionId;
			this.uid = uid;
			this.userAnswer = userAnswer;
			this.elapsedTime = elapsedTime;
			this.whatUserGot = whatUserGot;
		}
	}
	
	private void checkAndProceedToNextQuestion(UserAnswer userAnswer){
		userAnswers.put(userAnswer.uid,  userAnswer);
		questionScreen.animateProgressView(userAnswer.uid, userAnswer.whatUserGot);
		if(userAnswersStack.containsKey(userAnswer.uid)){
			userAnswersStack.get(userAnswer.uid).add(userAnswer);
		}
		else{
			List<UserAnswer> temp = new ArrayList<UserAnswer>();
			temp.add(userAnswer);
			userAnswersStack.put(userAnswer.uid , temp);
		}
		if(currentUsers.size() == userAnswers.keySet().size()){//every one answered 
			
    		questionScreen.getTimerView().resetTimer();
			for(String u: userAnswers.keySet()){
				questionScreen.animateXpPoints(u, userAnswers.get(u).whatUserGot);
			}
			for(String uid: userAnswers.keySet()){
				questionScreen.highlightOtherUsersOption(uid, userAnswers.get(uid).userAnswer);
			}
			
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(currentQuestions.size()>0){ // more questions
						Question currentQuestion = currentQuestions.remove(0);
						questionScreen.animateQuestionChange(UiText.QUESTION.getValue(quiz.nQuestions - currentQuestions.size()), UiText.GET_READY.getValue(), currentQuestion);
						if(isBotMode())
							scheduleBotAnswer(currentQuestion);
						
				        userAnswers.clear();
					}
					else{
						validateAndShowWinningScreen();
					}
				}
			}, Config.QUESTION_END_DELAY_TIME);
		}
	}
	
	private void scheduleBotAnswer(final Question currentQuestion) {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
					for(User user:currentUsers){ // check for any bots and schedule
						if(!user.isBotUser()) continue;
						
						int elapsedTime = rand.nextInt(5*Math.max(0, (100-quizApp.getUser().getLevel(quiz))/100)); 
						boolean isRightAnswer = rand.nextInt(2)==1? false:true;
						if(isRightAnswer){
							botScore+=Math.ceil((currentQuestion.getTime() - elapsedTime)*currentQuestion.xp/currentQuestion.getTime())*quizApp.getConfig().multiplyFactor(currentQuestions.size());
						}
						final UserAnswer botAnswer = new UserAnswer(currentQuestion.questionId, user.uid, isRightAnswer?currentQuestion.getCorrectAnswer():currentQuestion.getWrongRandomAnswer(rand),
								 	elapsedTime, botScore);
			    		
						new Handler().postDelayed( new Runnable() {
							
							@Override
							public void run() {
								questionScreen.getTimerView().stopPressed(2, botAnswer.elapsedTime);
								checkAndProceedToNextQuestion(botAnswer);
							}
						}, elapsedTime*1000);
					}
			}
		}, Config.PREQUESTION_FADE_OUT_ANIMATION_TIME);

	}

	public List<User> getOtherUsers(){
		ArrayList<User> otherUsers = new ArrayList<User>();
		for(User user : currentUsers){
			if(user.uid != quizApp.getUser().uid)
			otherUsers.add(user);
		}
		return otherUsers;
	}
	
	public void validateAndShowWinningScreen(){
		List<UserAnswer> l = userAnswersStack.get(quizApp.getUser().uid);
		clearScreen();
		
//		ProfileAndChatController profileAndChat = (ProfileAndChatController) quizApp.loadAppController(ProfileAndChatController.class);
//
//		profileAndChat.loadChatScreen(getOtherUsers().get(0), -1, true);
		
		WinOrLoseController resultScreenController = (WinOrLoseController) quizApp.loadAppController(WinOrLoseController.class);
		resultScreenController.loadResultScreen(quiz,currentUsers,userAnswersStack);

//		WinOrLoseScreen resultScreen = new WinOrLoseScreen(this,currentUsers);
//		resultScreen.showResult(userAnswersStack,true);
//		showScreen(resultScreen);

	}
	
	public void onMessageRecieved(MessageType messageType, ServerResponse response, String data) {
		switch(messageType){
	    	case USER_ANSWERED_QUESTION:
	    		UserAnswer userAnswer = quizApp.getConfig().getGson().fromJson(response.payload, UserAnswer.class);
	    		//questionId , self.uid, userAnswer,elapsedTime , whatUserGot
	    		questionScreen.getTimerView().stopPressed(2, userAnswer.elapsedTime);
	    		checkAndProceedToNextQuestion(userAnswer);
	    		break;
	    	case GET_NEXT_QUESTION://client trigger
	    		break; 
	    	case STARTING_QUESTIONS:// start questions // user finalised
	    		noResponseFromServer = false;
	    		currentUsers = quizApp.getConfig().getGson().fromJson(response.payload1,new TypeToken<ArrayList<User>>(){}.getType());
	    		currentQuestions  = quizApp.getConfig().getGson().fromJson(response.payload2,new TypeToken<ArrayList<Question>>(){}.getType());
	    		showQuestionScreen(currentUsers);
	    		break;
	    	case ANNOUNCING_WINNER:
	    		validateAndShowWinningScreen();
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
	    		try{
	    			currentUsers = quizApp.getConfig().getGson().fromJson(response.payload, new TypeToken<List<User>>(){}.getType());
	    		}
	    		catch(JsonSyntaxException ex){
	    			currentUsers.add(quizApp.getUser());
	    			currentUsers.add((User) quizApp.getConfig().getGson().fromJson(response.payload, new TypeToken<User>(){}.getType()));
	    		}
	    		setBotMode(true);
	    		serverSocket.disconnect();
	    		showQuestionScreen(currentUsers);
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
	
	public void onOptionSelected(Boolean isAnwer, String answer , Question currentQuestion) {
		UserAnswer payload =null; 
		double timeElapsed = questionScreen.getTimerView().stopPressed(1);
		if(isAnwer){
			currentScore += ( Math.ceil(currentQuestion.getTime()-timeElapsed)*quizApp.getConfig().multiplyFactor(currentQuestions.size()));
		}
		payload = new UserAnswer(currentQuestion.questionId, quizApp.getUser().uid, answer, (int)timeElapsed, currentScore);
		if(!isBotMode())
			serverSocket.sendTextMessage(quizApp.getConfig().getGson().toJson(payload));
		checkAndProceedToNextQuestion(payload);
	}


	public String onNoAnswer(Question currentQuestion) {
		UserAnswer payload =null; 
		currentScore += 0;
		payload = new UserAnswer(currentQuestion.questionId, quizApp.getUser().uid, "", currentQuestion.getTime(), currentScore);//all time elapsed
		if(!isBotMode())
			serverSocket.sendTextMessage(quizApp.getConfig().getGson().toJson(payload));
		questionScreen.highlightCorrectAnswer();
		checkAndProceedToNextQuestion(payload);
		return null;
	}
}