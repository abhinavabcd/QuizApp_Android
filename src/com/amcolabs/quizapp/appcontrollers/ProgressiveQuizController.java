

package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.GameEvents;
import com.amcolabs.quizapp.databaseutils.GameEvents.EventType;
import com.amcolabs.quizapp.databaseutils.LocalQuizHistory;
import com.amcolabs.quizapp.databaseutils.OfflineChallenge;
import com.amcolabs.quizapp.databaseutils.OfflineChallenge.ChallengeData;
import com.amcolabs.quizapp.databaseutils.Question;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.databaseutils.QuizPlaySummary;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.datalisteners.DataInputListener2;
import com.amcolabs.quizapp.gameutils.BadgeEvaluator;
import com.amcolabs.quizapp.gameutils.GameUtils;
import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes.YesNoDialog;
import com.amcolabs.quizapp.screens.ClashScreen;
import com.amcolabs.quizapp.screens.QuestionScreen;
import com.amcolabs.quizapp.screens.WinOrLoseScreen;
import com.amcolabs.quizapp.serverutils.ServerResponse;
import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;
import com.amcolabs.quizapp.serverutils.ServerWebSocketConnection;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.ChallengeView;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

public class ProgressiveQuizController extends AppController{
	public static enum QuizMode{
		UNKNOWN,
		NORMAL_MODE,
		BOT_MODE,
		CHALLENGE_MODE,
		CHALLENGED_MODE,
		CHALLENGE_LIVE_MODE;
		
		private QuizMode() {
			// TODO Auto-generated constructor stub
		}
		QuizMode(String id){
			this.id=null;
		}
		
		String id=null;
		private Object tag;
		public String getId(){
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public void setTag(Object tag) {
			this.tag =tag;
			
		}
		public Object getTag() {
			return tag;
		}
		
	}
	
	User user;
	User user2;
	
	private ServerWebSocketConnection serverSocket;
	
	private Quiz quiz;
	

	
	public Quiz getQuiz() {
		return quiz;
	}


	public ProgressiveQuizController(QuizApp quizApp) {
		super(quizApp);
	}

	
	public void initlializeQuiz(Quiz quiz) {
		this.quiz = quiz;
		showWaitingScreen(quiz);
		playType = RANDOM_USER_TYPE;
		noResponseFromServer = true;
		quizMode = QuizMode.NORMAL_MODE;
	}

	ClashScreen clashingScreen = null;
	QuestionScreen questionScreen = null;
	public void showWaitingScreen(Quiz quiz){
		clearScreen();
		clashingScreen = new ClashScreen(this);
		clashingScreen.setClashCount(2); 
		clashingScreen.updateClashScreen(quizApp.getUser()/*quizApp.getUser()*/,quiz, 0);//TODO: change to quizApp.getUser()
		insertScreen(clashingScreen);
		quizApp.getServerCalls().startProgressiveQuiz(this, quiz, RANDOM_USER_TYPE, null);
	}	
	
	
	public int getMaxScore(){
		if(currentQuestions==null || currentQuestions.size()==0)
			return 0;
		int mscore = 0;
		for(int i=0;i<currentQuestions.size();i++){
			mscore += currentQuestions.get(i).xp*quizApp.getGameUtils().multiplyFactor(i+1);
		}
		return mscore;
	}
	
	public void showQuestionScreen(ArrayList<User> users){
		//pre download assets if ever its possible
		questionScreen = new QuestionScreen(this);
		questionScreen.showUserInfo(users,getMaxScore()); //load user info
		//animate TODO:
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				clearScreen();//removes the clash screen
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
				questionScreen.animateQuestionChange( UiText.GET_READY.getValue(), UiText.FOR_YOUR_FIRST_QUESTION.getValue() ,currentQuestion, 0);
				if(isBotMode())
					scheduleBotAnswer(currentQuestion);
				if(isChallengedMode()){
					for(UserAnswer userAnswer : userChallengeAnswers ){
						if(userAnswer.questionId.equalsIgnoreCase(currentQuestion.questionId)){
							//userChallengeAnswers.remove(userAnswer);
							scheduleChallengedAnswer(userAnswer);		
							break;
						}
					}
				}

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
	
	

	
	// type of quiz
	private static final int CHALLENGE_QUIZ_TYPE = 2; // for layout adjustments //TODO: remove this carefully  , don't remmeber usage
	private static final int RANDOM_USER_TYPE = 1; 
	private static final int CHALLENGED_LIVE_QUIZ_TYPE = 3;

	double waitinStartTime = 0;
	boolean noResponseFromServer = true;
	String serverId = null;
	
	private List<Question> currentQuestions = new ArrayList<Question>();
	private ArrayList<User> currentUsers = new ArrayList<User>();
	private HashMap<String ,UserAnswer> userAnswers;
	private HashMap<String ,List<UserAnswer>> userAnswersStack = new HashMap<String, List<UserAnswer>>();
	
	private int currentScore = 0;
	private int botScore =0;
	protected Random rand = new Random();
	private WinOrLoseScreen quizResultScreen;
	private QuizMode quizMode = QuizMode.NORMAL_MODE;
	private boolean waitingForRematch;
	private int playType = RANDOM_USER_TYPE;
	private DataInputListener2<ServerWebSocketConnection, Quiz, Void, Void> socketConnectedListener = null;
	private YesNoDialog rematchDialog = null;
	protected double userLevel;
	private String currentQuestionsJson;
	
	private void setQuizMode(QuizMode mode){
		quizMode = mode;
	}
	
	private boolean isBotMode(){
		return quizMode == QuizMode.BOT_MODE;
	}
	
	public boolean isChallengeMode(){
		return quizMode == QuizMode.CHALLENGE_MODE;
	}
	
	private boolean isNormalMode() {
		return quizMode==QuizMode.NORMAL_MODE;
	}

	private boolean isChallengedMode() {
		return quizMode == QuizMode.CHALLENGED_MODE;
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

		if(isNormalMode()){
			// TODO : remove handler on screen remove , this triggers of in like 15 seconds
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(noResponseFromServer && !preventBot){
						if(serverSocket!=null)
							serverSocket.sendTextMessage(constructSocketMessage(MessageType.ACTIVATE_BOT, null, null));
					}
					preventBot = false;
				}
			}, Config.BOT_INTIALIZE_AFTER_NO_USER_TIME);
		}
		if(socketConnectedListener!=null){
			socketConnectedListener.onData(mConnection, quiz,null);
		}
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
		if(userAnswer.uid.equalsIgnoreCase(quizApp.getUser().uid))
			questionScreen.animateXpPoints(quizApp.getUser().uid, userAnswers.get(quizApp.getUser().uid).whatUserGot);
		if(userAnswersStack.containsKey(userAnswer.uid)){
			userAnswersStack.get(userAnswer.uid).add(userAnswer);
		}
		else{
			List<UserAnswer> temp = new ArrayList<UserAnswer>();
			temp.add(userAnswer);
			userAnswersStack.put(userAnswer.uid , temp);
		}
		if(currentUsers.size() == userAnswers.keySet().size() || isChallengeMode()){//every one answered 
			
    		questionScreen.getTimerView().resetTimer();
			for(String u: userAnswers.keySet()){
				if(!isChallengeMode() || u.equalsIgnoreCase(quizApp.getUser().uid))
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
						int bonusFactor = quizApp.getGameUtils().multiplyFactor(currentQuestions.size());
						int questionNum = quiz.nQuestions - currentQuestions.size();
						if(bonusFactor<2){
							questionScreen.animateQuestionChange(UiText.QUESTION.getValue(questionNum), UiText.GET_READY.getValue(), currentQuestion , questionNum-1);
						}	
						else{
							questionScreen.animateQuestionChange(UiText.QUESTION.getValue(questionNum), UiText.GET_READY.getValue(), UiText.QUESTIONS_BONUS.getValue(bonusFactor), currentQuestion , questionNum-1);
						} 
						if(isBotMode())
							scheduleBotAnswer(currentQuestion);
						if(isChallengedMode()){
							for(UserAnswer userAnswer : userChallengeAnswers ){
								if(userAnswer.questionId.equalsIgnoreCase(currentQuestion.questionId)){
									userChallengeAnswers.remove(userAnswer);
									scheduleChallengedAnswer(userAnswer);		
									break;
								}
							}
						}

				        userAnswers.clear();//for new answers
					}
					else if(!isNormalMode()){
						validateAndShowWinningScreen(false);
					}
				}
			}, Config.QUESTION_END_DELAY_TIME);
		}
	}
	
	private void scheduleChallengedAnswer(final UserAnswer userAnswer){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
						int elapsedTime =  userAnswer.elapsedTime;
						new Handler().postDelayed( new Runnable() {
							@Override
							public void run() {
								questionScreen.getTimerView().stopPressed(2, userAnswer.elapsedTime);
								checkAndProceedToNextQuestion(userAnswer);
							}
						}, elapsedTime*1000);
					}
		}, Config.PREQUESTION_FADE_OUT_ANIMATION_TIME);
	}
	
	private void scheduleBotAnswer(final Question currentQuestion) {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
					for(User user:currentUsers){ // check for any bots and schedule
						if(!user.isBotUser()) continue;
						userLevel = quizApp.getUser().getLevel(quizApp,quiz.quizId);
						int elapsedTime = rand.nextInt((int) (5*Math.max(0, (100-userLevel)/100))); 
						boolean isRightAnswer = rand.nextInt(100) < (50+Math.sqrt(25*userLevel))? true:false;
						if(isRightAnswer){
							botScore+=Math.ceil((currentQuestion.getTime() - elapsedTime)*currentQuestion.xp/currentQuestion.getTime())*quizApp.getGameUtils().multiplyFactor(currentQuestions.size());
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
	
	public void validateAndShowWinningScreen(boolean isServerError){
		List<UserAnswer> l = userAnswersStack.get(quizApp.getUser().uid);
		clearScreen();
		
//		ProfileAndChatController profileAndChat = (ProfileAndChatController) quizApp.loadAppController(ProfileAndChatController.class);
//
//		profileAndChat.loadChatScreen(getOtherUsers().get(0), -1, true);
		
		if(!isServerError){
			loadResultScreen(quiz,currentUsers,userAnswersStack);
		}
		else{
			quizApp.getDataBaseHelper().addGameEvents(new GameEvents(EventType.SERVER_ERROR_QUIZ, quiz.quizId , getOtherUser().uid , null));
			quizResultScreen = new WinOrLoseScreen(this,currentUsers, null);
			quizResultScreen.showResult(userAnswersStack,Quiz.SERVER_ERR, false , quizMode);
			insertScreen(quizResultScreen);
		}
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
				showQuestionScreen(currentUsers); 
				break;

	    	case LOAD_QUESTIONS:
	    		noResponseFromServer = false;
	    		loadQuestionsAndUsers(response , new DataInputListener<Boolean>(){
					@Override
					public String onData(Boolean fullLoaded , int progress) {
						if(fullLoaded){
							serverSocket.sendTextMessage(constructSocketMessage(MessageType.USER_READY, null,null));
							//TODO:UPDATE CLASHING SCREEN , SAYING WAITING FOR OTHER USER
							clashingScreen.setCurrentUserDebugText(UiText.USER_DOWNLOADING_ASSETS_WAITING.getValue());
						}
						else{
							clashingScreen.setCurrentUserDebugText(UiText.DOWNLOADING_QUESTIONS_AND_ASSETS.getValue());
						}
						return super.onData(fullLoaded);
					}
				});
	    		break;
	    	case ANNOUNCING_WINNER:
	    		validateAndShowWinningScreen(false);
	    		break; 
	    	case USER_DISCONNECTED:
	    		if(currentQuestions.size()>0){ // still there are questions ? 
	    			gracefullyCloseSocket();
	    			quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.USER_HAS_DISCONNECTED.getValue(getOtherUser().getName()), UiText.CHALLENGE.getValue() , UiText.CLOSE.getValue() , new DataInputListener<Boolean>(){
	    				@Override
	    				public String onData(Boolean s) {
	    					if(s){
	    						startNewChallenge(getOtherUser());
	    					}
	    					else{
	    						validateAndShowWinningScreen(true);
	    					}
	    					return super.onData(s);
	    				}
	    			});
	    		}
	    		else if(waitingForRematch){
	    			waitingForRematch = false;
	    			requestRematch();
	    		}
	    		break; 
	    	case NEXT_QUESTION:
	    		break; 
	    	case STATUS_WHAT_USER_GOT:
	    		break; 
	    	case OK_ACTIVATING_BOT: 
	    		quizApp.getServerCalls().informActivatingBot(quiz, serverSocket.serverId); 
	    		setQuizMode(QuizMode.BOT_MODE);
	    		serverSocket.disconnect();
	    		loadQuestionsAndUsers(response , new DataInputListener<Boolean>(){
					@Override
					public String onData(Boolean fullLoaded , int progress) {
						if(fullLoaded)
							showQuestionScreen(currentUsers);//load questions directly
						else{
							//TODO:UPDATE CLASHING SCREEN , SAYING percentage of question assets loaded
						}
						return super.onData(fullLoaded);
					}
				});
				clashingScreen.setCurrentUserDebugText(UiText.LOADING_QUESTIONS.getValue());

	    		break; 
//	    	case START_QUESTIONS:
//	    		startQuestions(response);
//	    		break; 
	    	case REMATCH_REQUEST: 
	    		User user = quizApp.cachedUsers.get(response.payload);
	    		rematchDialog  = quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.USER_WANTS_REMATCH.getValue(user.getName()), UiText.CHALLENGE.getValue() , UiText.EXIT.getValue() , new DataInputListener<Boolean>(){
	    			@Override
	    			public String onData(Boolean s) {
	    				if(s){
	    					serverSocket.sendTextMessage(constructSocketMessage(MessageType.REMATCH_REQUEST, null,null));
	    				}
	    				else{
	    					gracefullyCloseSocket();
	    				}
	    				return super.onData(s);
	    			}
	    		});
	    		break;
	    	case OK_START_REMATCH:
	    		if(rematchDialog!=null && rematchDialog.isShowing())
	    			rematchDialog.dismissQuietly();
//	    		quizApp.getStaticPopupDialogBoxes().removeRematchRequestScreen();
	    		setCurrentQuestions((List<Question>) quizApp.getConfig().getGson().fromJson(response.payload1, new TypeToken<List<Question>>(){}.getType()) , response.payload1);
	    		showWaitingScreen(quiz);
	    		showQuestionScreen(currentUsers);
	    		break;
	    	case LOAD_CHALLENGE_FROM_OFFLINE:
	    	case USER_HAS_LEFT_POOL:
	    		gracefullyCloseSocket();
	    		quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.USER_HAS_LEFT.getValue(), null, UiText.OK.getValue(),new DataInputListener<Boolean>(){
	    			@Override
	    			public String onData(Boolean s) {
	    				if(!s){
	    					onBackPressed();
	    				}
	    				return super.onData(s);
	    			}
	    		});
	    		
	    		loadOfflineChallenge();
	    		break;
	    		
//	    	case START_CHALLENGE_NOW:
	    	case OK_CHALLENGE_WITHOUT_OPPONENT:
				setQuizMode(QuizMode.CHALLENGE_MODE);
				quizMode.setId(response.payload3);
				loadQuestionsAndUsers(response, new DataInputListener<Boolean>(){
					@Override
					public String onData(Boolean fullLoaded , int progress) {
						if(fullLoaded)
							showQuestionScreen(currentUsers);
						else{
							//TODO:UPDATE CLASHING SCREEN , SAYING percentage of question assets loaded
						}
						return super.onData(fullLoaded);
					}
				});
				clashingScreen.setCurrentUserDebugText(UiText.LOADING_QUESTIONS.getValue());
				gracefullyCloseSocket();
				break;
	    	case USER_READY:
	    		User greenUser = quizApp.cachedUsers.get(response.payload);
	    		break;
			default:
				break;
		}
	}

	private void setCurrentQuestions(List<Question> currentQuestions, String currentQuestionsJson) {
		this.currentQuestions = currentQuestions;
		this.currentQuestionsJson = currentQuestionsJson;
	}


	private boolean checkAndRemoveDuplicateUsers(ArrayList<User> currentUsers) {
		HashMap<String,Boolean>  map = new HashMap<String, Boolean>();
		boolean duplicatesFound = false;
		for(User user : currentUsers){
			if(!map.containsKey(user.uid)){
				map.put(user.uid, true);
			}
			else{
				currentUsers.remove(user);
				duplicatesFound = true;
			}
		}
		return duplicatesFound;
	}


	private void loadOfflineChallenge() {
		// fetch data from server and start quiz
		//TODO: check if an offline challenge available in line
	}


	private void loadQuestionsAndUsers(ServerResponse response, DataInputListener<Boolean> questionsLoadedListener) {
		setCurrentQuestions((List<Question>) quizApp.getConfig().getGson().fromJson(response.payload2, new TypeToken<List<Question>>(){}.getType()), response.payload2);
		try{
			currentUsers = quizApp.getConfig().getGson().fromJson(response.payload1, new TypeToken<List<User>>(){}.getType());
			for(User user : currentUsers){//remove current user and add the current user obj locally  more better
				if(quizApp.getUser().uid.equalsIgnoreCase(user.uid)){
					currentUsers.remove(user);
					currentUsers.add(quizApp.getUser());
					break;
				}
			}
		}
		catch(JsonSyntaxException ex){//single user in payload
			currentUsers.clear();
			currentUsers.add(quizApp.getUser());
			currentUsers.add((User) quizApp.getConfig().getGson().fromJson(response.payload1, new TypeToken<User>(){}.getType()));
		}
		if(checkAndRemoveDuplicateUsers(currentUsers)){
			quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.UNEXPECTED_ERROR.getValue(), null, UiText.CLOSE.getValue(), null);
			gracefullyCloseSocket();
			return;
		}
		quizApp.cacheUsersList(currentUsers);
		
		for(User user: currentUsers){
			int index = 0;
			if(!user.uid.equalsIgnoreCase(quizApp.getUser().uid)){
				try{
					clashingScreen.updateClashScreen(user, quiz, ++index);
				}
				catch(NullPointerException e){
					e.printStackTrace();
				}
			}
		}


		preProcessQuestions(currentQuestions, questionsLoadedListener);
	}
	
	
	private void preProcessQuestions(final List<Question> currentQuestions, final DataInputListener<Boolean> questionsLoadedListener){
		// load all assets prior to game 
		for(int i=0;i<currentQuestions.size();i++){
			List<String> assets = currentQuestions.get(i).getAssetPaths();
			if(assets.size()>0){
				Picasso.with(quizApp.getContext()).load(assets.get(0)).fetch();//warm up
			}
		}
		questionsLoadedListener.onData(true, 100);// full loaded and precentage
	}


	private void startQuestions(List<Question> questions){
		currentQuestions = questions;
		if(checkAndRemoveDuplicateUsers(currentUsers)){
			quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.UNEXPECTED_ERROR.getValue(), null, UiText.CLOSE.getValue(), null);
			gracefullyCloseSocket();
			return;
		}
		quizApp.cacheUsersList(currentUsers);
		showQuestionScreen(currentUsers);
	}

	/*
	 * Return first opponent 
	 */
	public User getOtherUser(){
		User otherUser = null;
		for(User user:currentUsers){
			otherUser = user;
			if(!otherUser.uid.equalsIgnoreCase(quizApp.getUser().uid)){
				break;
			}
		}
		return otherUser;
	}
	public boolean requestRematch(){
		if(serverSocket!=null && serverSocket.isConnected()){
			if(!waitingForRematch)
				serverSocket.sendTextMessage(constructSocketMessage(MessageType.REMATCH_REQUEST, null,null));
			waitingForRematch = true;
			return true;
		}
		else{
			quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.USER_HAS_LEFT.getValue(), UiText.CHALLENGE.getValue(), UiText.OK.getValue(), new DataInputListener<Boolean>(){
				@Override
				public String onData(Boolean s) {
					if(s){
						startNewChallenge(getOtherUser());
					}
					else{
						
					}
					return super.onData(s);
				}
			});
			return false;
		}
	}

	public void onSocketClosed() {
		
	}
	
	public void gracefullyCloseSocket(){
		if(serverSocket!=null){
			serverSocket.disconnect();
			serverSocket = null;
		}
		waitingForRematch = false;
	}


	public void ohNoDammit() {
	}
	
	public void onOptionSelected(Boolean isAnwer, String answer , Question currentQuestion) {
		UserAnswer payload =null; 
		double timeElapsed = questionScreen.getTimerView().stopPressed(1);
		if(isAnwer){
			currentScore += ( Math.ceil(currentQuestion.getTime()-timeElapsed)*quizApp.getGameUtils().multiplyFactor(currentQuestions.size()));
		}
		payload = new UserAnswer(currentQuestion.questionId, quizApp.getUser().uid, answer, (int)timeElapsed, currentScore);
		if(isNormalMode())
			serverSocket.sendTextMessage(quizApp.getConfig().getGson().toJson(payload));
		checkAndProceedToNextQuestion(payload);
	}


	public String onNoAnswer(Question currentQuestion) {
		UserAnswer payload = null; 
		currentScore += 0;
		payload = new UserAnswer(currentQuestion.questionId, quizApp.getUser().uid, "", currentQuestion.getTime(), currentScore);//all time elapsed
		if(isNormalMode()){
			if(serverSocket==null || !serverSocket.isConnected()){
				return null;
			}
			serverSocket.sendTextMessage(quizApp.getConfig().getGson().toJson(payload));
		}
		questionScreen.highlightCorrectAnswer();
		checkAndProceedToNextQuestion(payload);
		return null;
	}
	


	public void setChallengeData(){
		
	}
	
	/**
	 * Main method to load result screen after quiz
	 * @param quiz Current quiz user has played
	 * @param currentUsers Current list of users who played quiz
	 * @param userAnswersStack Current user's answers in hashmap mapped with uid's
	 */
	private ChallengeData challengeData;
	private List<UserAnswer> userChallengeAnswers;
	private MediaPlayer possitiveButtonSounds;
	private MediaPlayer negetiveButtonSounds;
	private boolean preventBot = false;
	
	public void loadResultScreen(Quiz quiz, ArrayList<User> currentUsers, HashMap<String, List<UserAnswer>> userAnswersStack) {
		// TODO Auto-generated method stub
		ArrayList<String> winnersList = whoWon(userAnswersStack);
		int quizResult = Quiz.TIE;

		User curUser;
		double oldPoints;
		double newPoints;
		List<UserAnswer> uAns;
		
		// All user updates
		if(!isChallengeMode()){
			for(int i=0,qResult = Quiz.TIE;i<currentUsers.size();i++){
				curUser = currentUsers.get(i);
				if(winnersList.contains(curUser.uid)){
					if(winnersList.size()==1){
						qResult = Quiz.WON;
					}
					else{
						qResult = Quiz.TIE;
					}
				}
				else{
					qResult = Quiz.LOOSE;
				}
				oldPoints = curUser.getPoints(quiz.quizId);
				uAns = userAnswersStack.get(curUser.uid);
				newPoints = oldPoints+uAns.get(uAns.size()-1).whatUserGot+(qResult>0?Config.QUIZ_WIN_BONUS:0);
				
				if(qResult>=Quiz.LOOSE){
					curUser.setPoints(quiz.quizId, (int) newPoints);
					
					Integer[] winsLossesQuiz = curUser.getWinsLosses(quiz.quizId);
					winsLossesQuiz[0]+= (qResult==Quiz.WON?1:0);
					winsLossesQuiz[1]+= (qResult==Quiz.LOOSE?1:0);
					winsLossesQuiz[2]+= (qResult==Quiz.TIE?1:0); 
				}
			}
		}
		
		//  Host User updates
		if(winnersList.contains(quizApp.getUser().uid)){
			if(winnersList.size()==1){
				quizResult = Quiz.WON;
			}
			else{
				quizResult = Quiz.TIE;
			}
		}
		else{
			quizResult = Quiz.LOOSE;
		}
//		cPoints = quizApp.getUser().getPoints(quiz);
//		uAns = userAnswersStack.get(quizApp.getUser().uid);
//		newPoints = cPoints+uAns.get(uAns.size()-1).whatUserGot+(quizResult>0?Config.QUIZ_WIN_BONUS:0);

		QuizPlaySummary qPlaySummary;
		
		newPoints = quizApp.getUser().getPoints(quiz.quizId);
		uAns = userAnswersStack.get(quizApp.getUser().uid);
		oldPoints = newPoints - (uAns.get(uAns.size()-1).whatUserGot+(quizResult>0?Config.QUIZ_WIN_BONUS:0));
		
		if(isChallengeMode()){ 
			quizApp.getServerCalls().addOfflineChallange(quiz , getOtherUser(), userAnswersStack.get(quizApp.getUser().uid), quizMode.getId() ,new DataInputListener<OfflineChallenge>(){
				@Override
				public String onData(OfflineChallenge offlineChallenge) {
					if(offlineChallenge!=null){
						quizApp.getDataBaseHelper().updateOfflineChallenge(offlineChallenge);
					}
					else{
						quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.SERVER_ERROR_MESSAGE.getValue(), null, UiText.CLOSE.getValue(), null);
					}
					return null;
				}
			});//server call  
		}

		else if(quizResult>=Quiz.LOOSE){
			//Upates quiz history 
			
			String userAnswers1Json =quizApp.getConfig().getGson().toJson(userAnswersStack.get(quizApp.getUser().uid));
			String userAnswers2Json =quizApp.getConfig().getGson().toJson(userAnswersStack.get(getOtherUser().uid));
			String usersJson = quizApp.getConfig().getGson().toJson(currentUsers);
			LocalQuizHistory quizHistory = new LocalQuizHistory(quiz.quizId , quizResult , newPoints-oldPoints, quizApp.getUser().uid,  getOtherUser().uid , getMaxScore(), currentQuestionsJson , usersJson,  userAnswers1Json , userAnswers2Json);
			quizApp.getDataBaseHelper().insertToQuizHistory(quizHistory);
			quizApp.getDataBaseHelper().addGameEvents(new GameEvents(quizResult==Quiz.TIE? EventType.TIE_QUIZ : (quizResult==Quiz.LOOSE? EventType.LOST_QUIZ : EventType.WON_QUIZ),
													quiz.quizId , getOtherUser().uid , 
													(int)Math.abs(newPoints-oldPoints)+""));

			if(!isChallengedMode()){
				if(shouldUserSendAllUserAnswers()){ // this is a funny mechanism we choose  , where one user with higher preference sends the all the game details to the server , we keep this check on the server too
					quizApp.getServerCalls().updateQuizWinStatus(quiz.quizId , quizResult , newPoints-oldPoints, getOtherUser() , userAnswers1Json , userAnswers2Json);//server call 
				}
				else
					quizApp.getServerCalls().updateQuizWinStatus(quiz.quizId , quizResult , newPoints-oldPoints, getOtherUser() ,null , null);//server call 
			}
			
			//update stats in the quizSummary table
			qPlaySummary = quizApp.getDataBaseHelper().getQuizSummaryByQuizId(quiz.quizId);
			if(qPlaySummary==null){
				qPlaySummary = new QuizPlaySummary(quiz.quizId,quizResult,Config.getCurrentTimeStamp());
			}
			else{
				qPlaySummary.setTotalCount(qPlaySummary.getTotalCount()+1);
				qPlaySummary.setModifiedTimeStamp(Config.getCurrentTimeStamp());
				if(quizResult<0){
					qPlaySummary.setLose(qPlaySummary.getLose()+1);;
					qPlaySummary.setStreak(0);
				}
				else if(quizResult==0){
					qPlaySummary.setTie(qPlaySummary.getTie()+1);;
					qPlaySummary.setStreak(0);
				}
				else if(quizResult>0){
					qPlaySummary.setWin(qPlaySummary.getWin()+1);
					qPlaySummary.setStreak(qPlaySummary.getStreak()+1);
				} 
				
				// TODO: update in local summary -NOT NEEDED REMOVE < ADDED DURING SERVER CALLS
				// TODO: update game event 
			}
			
			final boolean hasWon = quizResult==Quiz.WON;
			
			if(isChallengedMode()){
				quizApp.getServerCalls().completeOfflineChallenge( quizMode.getId() , new ChallengeData(quiz.quizId, userAnswersStack.get(quizApp.getUser().uid)),  new DataInputListener<Boolean>(){
					@Override 
					public String onData(Boolean s) {
						if(s){ 
							OfflineChallenge offlineChallenge = ((OfflineChallenge)quizMode.getTag());
							offlineChallenge.setCompleted(true);
							offlineChallenge.hasWon = hasWon;
							offlineChallenge.setChallengeData2(quizApp.getConfig().getGson().toJson(new ChallengeData(ProgressiveQuizController.this.quiz.quizId, ProgressiveQuizController.this.userAnswersStack.get(quizApp.getUser().uid))));
							quizApp.getDataBaseHelper().updateOfflineChallenge(offlineChallenge);
						}
						else{
							quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.SERVER_ERROR_MESSAGE.getValue(), null, UiText.CLOSE.getValue(), null);
						}
						return super.onData(s);
					}
				});//server call
				
			}

			quizApp.getDataBaseHelper().createOrUpdateQuizSummary(qPlaySummary);
			
			BadgeEvaluator badgeEvaluator = quizApp.getBadgeEvaluator();
			badgeEvaluator.evaluateBadges(userAnswersStack,quizResult);
		}
		if (quizResultScreen==null){
			quizResultScreen = new WinOrLoseScreen(this,currentUsers , 
					       QuestionScreen.getBitmapOfQuestions(this,
								(List<Question>) quizApp.getConfig().getGson().fromJson(currentQuestionsJson,new TypeToken<List<Question>>(){}.getType()),
								quizApp.getUser().uid, 
								currentUsers, 
								getMaxScore(), 
								userAnswersStack.get(quizApp.getUser().uid), 
								userAnswersStack.get(getOtherUser().uid))
							);
		}
		
		boolean isLevelUp = GameUtils.didUserLevelUp(oldPoints,newPoints);
		if(isLevelUp){
			//update game event
			quizApp.getDataBaseHelper().addGameEvents(new GameEvents(EventType.LEVEL_UP , quiz.quizId ,null , null));
		}
		quizResultScreen.showResult(userAnswersStack,quizResult,isLevelUp , quizMode);
		insertScreen(quizResultScreen);
		
	}

	private boolean shouldUserSendAllUserAnswers() {
		User currentUser = quizApp.getUser();
		for(User user : getOtherUsers()){
			if(!user.isBotUser()  && currentUser.uid.compareTo(user.uid)<=0){
				return false;
			}
		}
		return true;
	}


	private ArrayList<String> whoWon(HashMap<String, List<UserAnswer>> userAnswersStack){
		List<UserAnswer> uAns;
		ArrayList<String> winnersList = new ArrayList<String>();
		Set<String> allUsers = userAnswersStack.keySet();
		Iterator<String> itr = allUsers.iterator();
		String uid;
		int maxScore=0;
		while(itr.hasNext()){
			uid = itr.next();
			uAns = userAnswersStack.get(uid);
			if(maxScore<uAns.get(uAns.size()-1).whatUserGot){
				maxScore = uAns.get(uAns.size()-1).whatUserGot;
				winnersList.clear();
				winnersList.add(uid);
			}
			else if(maxScore==uAns.get(uAns.size()-1).whatUserGot){
				winnersList.add(uid);
			}
		}
		return winnersList;
	}
	

	public void showProfileScreen(User user) {
		ProfileAndChatController profileAndChat = (ProfileAndChatController) quizApp.loadAppController(ProfileAndChatController.class);
//		profileAndChat.loadChatScreen(user, -1, true);
		profileAndChat.showProfileScreen(user);
	}

	public void startNewChallenge(User otherUser){
		if(otherUser==null){
			otherUser = getOtherUser();
		}
		startNewChallenge(otherUser , quiz);
	}
	public void startNewChallenge(User otherUser , final Quiz quiz){
		if(otherUser!=null && quizApp.isRapidReClick(otherUser.uid)){
			return;
		}
		this.quiz = quiz;
		final User withUser;
		if(otherUser==null) {
			withUser = getOtherUser();
		}
		else{
			withUser = otherUser;
		}
		setQuizMode(QuizMode.NORMAL_MODE);
		//TODO: clear socket , 
		// master server get sid
		// open new socket with &isChallenge=uid2 , get offlineChallengeId , etc from server , 
		// else let the user click on start now to Send message START_CHALLENGE_NOW to server , get questions , 
		// and then wait for the opponent to connect ,
		// users , setmode as challenge , complete it to send the offlinechallenge to server 
		gracefullyCloseSocket();//previous socket 
		HashMap<String , String> temp = new HashMap<String, String>();
		temp.put("isChallenge", withUser.uid);
		clearScreen();
		if(otherUser.isBotUser()){
			setOnSocketConnectionOpenListener(new DataInputListener2<ServerWebSocketConnection, Quiz, Void, Void>(){
				@Override
				public void onData(ServerWebSocketConnection a, Quiz b, Void c) {
					showChallengeScreen(withUser, quiz);
				}
			});
		}
		else{
			preventBot = true;
			showChallengeScreen(otherUser, quiz);
		}
		quizApp.getServerCalls().startProgressiveQuiz(this, quiz, CHALLENGE_QUIZ_TYPE ,temp);
	}
	
	public void startChallengedLiveGame(String serverId , String poolId, String quizId ){
		quiz = quizApp.getDataBaseHelper().getQuizById(quizId);
		// live game mode is equal to normal mode only
		clearScreen();
		clashingScreen = new ClashScreen(this);
		clashingScreen.setClashCount(2); 
		clashingScreen.updateClashScreen(quizApp.getUser()/*quizApp.getUser()*/,quiz, 0);//TODO: change to quizApp.getUser()
		clashingScreen.setCurrentUserDebugText(UiText.CHECKING_IF_USER_IS_STILL_WAITING.getValue());
		insertScreen(clashingScreen);
		preventBot = true; // to prevent bot
		HashMap<String , String> params = new HashMap<String, String>();
		params.put(Config.URL_PARAM_IS_CHALLENGED, poolId);
		quizApp.getServerCalls().startProgressiveQuiz(this, quiz, CHALLENGED_LIVE_QUIZ_TYPE, params , serverId);
	}
	
	public void startChallengedGame(final OfflineChallenge offlineChallenge ){
		if(offlineChallenge.isCompleted()){
			quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.COMPLETED_CHALLENGE.getValue(), null, UiText.CLOSE.getValue(), null);
			return;
		}
		final User fromUser = offlineChallenge.getFromUser(quizApp);
		
		this.challengeData = offlineChallenge.getChallengeData(quizApp);
		QuizMode mode = QuizMode.CHALLENGED_MODE;
		mode.setId(offlineChallenge.getOfflineChallengeId());
		mode.setTag(offlineChallenge);
		setQuizMode(mode);
		//get questions from server , 
		
		gracefullyCloseSocket();//previous socket 
		clearScreen();
		quiz = quizApp.getDataBaseHelper().getQuizById(challengeData.quizId);
		clashingScreen = new ClashScreen(this);
		clashingScreen.setClashCount(2); 
		clashingScreen.updateClashScreen(quizApp.getUser()/*quizApp.getUser()*/,quiz,  0 );
		clashingScreen.updateClashScreen(offlineChallenge.getFromUser(quizApp), quiz, 1);
		insertScreen(clashingScreen);
		// load questions from the users answers array , 
		// rearrange questions in the same order 
		// startQuestions 
		// isChallengedMode , run bot answers 
		// on end , onChallengeComplete , update on server , and notify other user
		userChallengeAnswers = challengeData.userAnswers;
		ArrayList<String > questionIds = new ArrayList<String>();
		for(UserAnswer userAnswer : userChallengeAnswers){
			questionIds.add(userAnswer.questionId);
		}
		currentUsers.clear();
		currentUsers.add(quizApp.getUser());
		currentUsers.add(fromUser);
		quizApp.getServerCalls().loadQuestionsInOrder(questionIds,new DataInputListener<List<Question>>(){
			@Override
			public String onData(List<Question> s) {
				List<Question> temp = new ArrayList<Question>();
				for(int i=0;i<userChallengeAnswers.size();i++){
					String questionId = userChallengeAnswers.get(i).questionId;
					int j=0;
					for(j=0;j<s.size();j++){
						if(questionId.equalsIgnoreCase(s.get(j).questionId)){
							break;
						}
					}
					temp.add(s.get(j));
				}
				s = temp;
				
				startQuestions(s);
				return super.onData(s);
			}
		});
	}

	
	
	private void setOnSocketConnectionOpenListener(DataInputListener2<ServerWebSocketConnection, Quiz, Void, Void> dataInputListener2) {
		socketConnectedListener = dataInputListener2;
	}


	public void showChallengeScreen(final User otherUser , Quiz quiz){
			clashingScreen = new ClashScreen(this);
			clashingScreen.setClashCount(2); 
			clashingScreen.updateClashScreen(quizApp.getUser()/*quizApp.getUser()*/,quiz,  0 , new ChallengeView(quizApp , otherUser, null , new DataInputListener<Integer>(){
				int pressed = 0;
				@Override
				public String onData(Integer s) {
					if(pressed == s)//repress , not a correct implementation though, for temporary
						return null;
					pressed = s;
					switch(s){
						case 1://challege start now
							if(serverSocket!=null && !otherUser.isBotUser()){
								serverSocket.sendTextMessage(constructSocketMessage(MessageType.START_CHALLENGE_NOW, null, null));
								break;
							}
							else if(otherUser.isBotUser()){
								quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.CANNOT_OFFLINE_CHALLENGE_PRIVATE_USERS.getValue(), null, UiText.CLOSE.getValue(), null);
								break;
							}
						case 2://exit
							backPressedCount++;
							quizApp.onBackPressed();
							break;
					}
					return super.onData(s);
				}
			}))	;//TODO: change to quizApp.getUser()
			insertScreen(clashingScreen);
	}
	
	public void addFriend(final User user) {
		quizApp.getServerCalls().subscribeTo(user, new DataInputListener<Boolean>(){
			@Override
			public String onData(Boolean s) {
				if(s){
					quizApp.getUser().getSubscribedTo().add(user.uid);
					quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.ADDED_USER.getValue(user.getName()), null, UiText.CLOSE.getValue(), null);
					user.isFriend = true;
				}
				return super.onData(s);
			}
		});
	}


	public void removeFriend(final User user) {
		quizApp.getServerCalls().unSubscribeTo(user, new DataInputListener<Boolean>(){
			@Override
			public String onData(Boolean s) {
				if(s){
					for(String uid : quizApp.getUser().getSubscribedTo()){
						if(user.uid.equalsIgnoreCase(uid)){
							quizApp.getUser().getSubscribedTo().remove(user);
							break;
						}
					}
					quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.REMOVED_USER.getValue(user.getName()), null, UiText.CLOSE.getValue(), null);
					user.isFriend = false;
				}
				return super.onData(s);
			}
		});
	}
}