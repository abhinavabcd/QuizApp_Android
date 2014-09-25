

package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.os.Handler;
import android.text.style.LineHeightSpan.WithDensity;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.OfflineChallenge;
import com.amcolabs.quizapp.databaseutils.OfflineChallenge.ChallengeData;
import com.amcolabs.quizapp.databaseutils.Question;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.databaseutils.QuizHistory;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.datalisteners.DataInputListener2;
import com.amcolabs.quizapp.gameutils.BadgeEvaluator;
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
		playType = RANDOM_USER_TYPE;
		noResponseFromServer = true;
		quizMode = -1;
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
			mscore += currentQuestions.get(i).xp*quizApp.getConfig().multiplyFactor(i+1);
		}
		return mscore;
	}
	
	public void showQuestionScreen(ArrayList<User> users){
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
	
	
	private static final int NORMAL_MODE = -1;
	protected static final int CHALLENGE_MODE = 2;
	private static final int BOT_MODE = 3;
	private static final int CHALLENGED_MODE = 4;

	
	// type of quiz
	private static final int CHALLENGE_QUIZ_TYPE = 2; // for layout adjustments //TODO: remove this carefully  , don't remmeber usage
	private static final int RANDOM_USER_TYPE = 1; 
	private static final int CHALLENGED_QUIZ_TYPE = 3;

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
	private int quizMode = NORMAL_MODE;
	private boolean waitingForRematch;
	private int playType = RANDOM_USER_TYPE;
	private DataInputListener2<ServerWebSocketConnection, Quiz, Void, Void> socketConnectedListener = null;
	
	private void setQuizMode(int mode){
		quizMode = mode;
	}
	
	private boolean isBotMode(){
		return quizMode == BOT_MODE;
	}
	
	public boolean isChallengeMode(){
		return quizMode == CHALLENGE_MODE;
	}
	
	private boolean isNormalMode() {
		return quizMode==NORMAL_MODE;
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

		if(!isChallegeTypeQuiz()){
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(noResponseFromServer){
						serverSocket.sendTextMessage(constructSocketMessage(MessageType.ACTIVATE_BOT, null, null));
					}
				}
			}, Config.BOT_INTIALIZE_AFTER_NO_USER_TIME);
		}
		if(socketConnectedListener!=null){
			socketConnectedListener.onData(mConnection, quiz,null);
		}
	}
	
	private void setQuizType(int type){
		playType = type;
	}
	private boolean isChallegeTypeQuiz() {
		return playType == CHALLENGE_QUIZ_TYPE;
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
						questionScreen.animateQuestionChange(UiText.QUESTION.getValue(quiz.nQuestions - currentQuestions.size()), UiText.GET_READY.getValue(), currentQuestion);
						if(isBotMode())
							scheduleBotAnswer(currentQuestion);
						
				        userAnswers.clear();
					}
					else if(!isNormalMode()){
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
						
						int elapsedTime = rand.nextInt((int) (5*Math.max(0, (100-quizApp.getUser().getLevel(quizApp,quiz.quizId))/100))); 
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
		 
		loadResultScreen(quiz,currentUsers,userAnswersStack);

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
	    		startQuestions(response);
	    		break;
	    	case ANNOUNCING_WINNER:
	    		validateAndShowWinningScreen();
	    		break; 
	    	case USER_DISCONNECTED:
	    		if(currentQuestions.size()>0){ // still there are questions ? 
	    			gracefullyCloseSocket();
	    			quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.USER_HAS_DISCONNECTED.getValue(getOtherUser().name), UiText.CHALLENGE.getValue() , UiText.NO.getValue() , new DataInputListener<Boolean>(){
	    				@Override
	    				public String onData(Boolean s) {
	    					if(s){
	    						//setQuizMode(CHALLENGE_MODE);
	    					}
	    					else{
	    						validateAndShowWinningScreen();
	    					}
	    					return super.onData(s);
	    				}
	    			});
	    		}
	    		else if(waitingForRematch){
	    			waitingForRematch = false;
	    		}
	    		break; 
	    	case NEXT_QUESTION:
	    		break; 
	    	case STATUS_WHAT_USER_GOT:
	    		break; 
	    	case OK_ACTIVATING_BOT: 
	    		quizApp.getServerCalls().informActivatingBot(quiz, serverSocket.serverId); 
	    		setQuizMode(BOT_MODE);
	    		serverSocket.disconnect();
	    		startQuestions(response);
	    		break;
	    	case START_QUESTIONS:
	    		startQuestions(response);
	    		break; 
	    	case REMATCH_REQUEST: 
	    		User user = quizApp.cachedUsers.get(response.payload);
	    		quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.USER_WANTS_REMATCH.getValue(user.name), UiText.CHALLENGE.getValue() , UiText.EXIT.getValue() , new DataInputListener<Boolean>(){
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
	    		quizApp.getStaticPopupDialogBoxes().removeRematchRequestScreen();
	    		currentQuestions = quizApp.getConfig().getGson().fromJson(response.payload1, new TypeToken<List<Question>>(){}.getType());
	    		showWaitingScreen(quiz);
	    		showQuestionScreen(currentUsers);
	    		break;
	    	case LOAD_CHALLENGE_FROM_OFFLINE:
	    		gracefullyCloseSocket();
	    		loadOfflineChallenge();
	    		break;
//	    	case START_CHALLENGE_NOW:
	    	case OK_CHALLENGE_WITHOUT_OPPONENT:
				setQuizMode(CHALLENGE_MODE);
				startQuestions(response);
				break;

			default:
				break;
		}
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
	}


	private void startQuestions(ServerResponse response) {
		currentQuestions = quizApp.getConfig().getGson().fromJson(response.payload2, new TypeToken<List<Question>>(){}.getType());
		try{
			currentUsers = quizApp.getConfig().getGson().fromJson(response.payload1, new TypeToken<List<User>>(){}.getType());
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
		showQuestionScreen(currentUsers);
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
	public void requestRematch(){
		if(serverSocket!=null && serverSocket.isConnected()){
			serverSocket.sendTextMessage(constructSocketMessage(MessageType.REMATCH_REQUEST, null,null));
			waitingForRematch = true;
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
			currentScore += ( Math.ceil(currentQuestion.getTime()-timeElapsed)*quizApp.getConfig().multiplyFactor(currentQuestions.size()));
		}
		payload = new UserAnswer(currentQuestion.questionId, quizApp.getUser().uid, answer, (int)timeElapsed, currentScore);
		if(!isBotMode() || !isChallengeMode())
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
	final int WON = 1;
	final int LOOSE = -1;
	final int TIE = 0;
	final int SERVER_ERR = -2;
	private ChallengeData challengeData;
	private List<UserAnswer> challengeUserAnswers;
	
	public void loadResultScreen(Quiz quiz, ArrayList<User> currentUsers, HashMap<String, List<UserAnswer>> userAnswersStack) {
		// TODO Auto-generated method stub
		ArrayList<String> winnersList = whoWon(userAnswersStack);
		int quizResult = TIE;

		if (quizResultScreen==null){
			quizResultScreen = new WinOrLoseScreen(this,currentUsers);
		}
		User curUser;
		double oldPoints;
		double newPoints;
		List<UserAnswer> uAns;
		
		// All user updates
		for(int i=0,qResult = TIE;i<currentUsers.size();i++){
			curUser = currentUsers.get(i);
			if(winnersList.contains(curUser.uid)){
				if(winnersList.size()==1){
					qResult = WON;
				}
				else{
					qResult = TIE;
				}
			}
			else{
				qResult = LOOSE;
			}
			oldPoints = curUser.getPoints(quiz.quizId);
			uAns = userAnswersStack.get(curUser.uid);
			newPoints = oldPoints+uAns.get(uAns.size()-1).whatUserGot+(qResult>0?Config.QUIZ_WIN_BONUS:0);
			
			if(qResult!=-2){
				curUser.setPoints(quiz.quizId, (int) newPoints);
				
				Integer[] winsLossesQuiz = curUser.getWinsLosses(quiz.quizId);
				winsLossesQuiz[0]+= (qResult==WON?1:0);
				winsLossesQuiz[1]+= (qResult==LOOSE?1:0);
				winsLossesQuiz[2]+= (qResult==TIE?1:0); 
			}
		}
		
		//  Host User updates
		if(winnersList.contains(quizApp.getUser().uid)){
			if(winnersList.size()==1){
				quizResult = WON;
			}
			else{
				quizResult = TIE;
			}
		}
		else{
			quizResult = LOOSE;
		}
//		cPoints = quizApp.getUser().getPoints(quiz);
//		uAns = userAnswersStack.get(quizApp.getUser().uid);
//		newPoints = cPoints+uAns.get(uAns.size()-1).whatUserGot+(quizResult>0?Config.QUIZ_WIN_BONUS:0);
		QuizHistory qHistory;
		
		oldPoints = quizApp.getUser().getPoints(quiz.quizId);
		uAns = userAnswersStack.get(quizApp.getUser().uid);
		newPoints = oldPoints+uAns.get(uAns.size()-1).whatUserGot+(quizResult>0?Config.QUIZ_WIN_BONUS:0);
		
		if(isChallengeMode()){
			quizApp.getServerCalls().addOfflineChallange(quiz , getOtherUser(), userAnswersStack.get(quizApp.getUser().uid),  new DataInputListener<Boolean>(){
				@Override
				public String onData(Boolean s) {
					if(s){
						
					}
					else{
						quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.SERVER_ERROR_MESSAGE.getValue(), null, UiText.CLOSE.getValue(), null);
					}
					return super.onData(s);
				}
			});//server call  
		}

		else if(quizResult>=LOOSE){			
			quizApp.getServerCalls().updateQuizWinStatus(quiz.quizId , quizResult , newPoints, getOtherUser());//server call 
			qHistory = quizApp.getDataBaseHelper().getQuizHistoryById(quiz.quizId);
			if(qHistory==null){
				qHistory = new QuizHistory(quiz.quizId,quizResult,Config.getCurrentTimeStamp());
			}
			else{
				qHistory.setTotalCount(qHistory.getTotalCount()+1);
				qHistory.setModifiedTimeStamp(Config.getCurrentTimeStamp());
				if(quizResult<0){
					qHistory.setLose(qHistory.getLose()+1);;
					qHistory.setStreak(0);
				}
				else if(quizResult==0){
					qHistory.setTie(qHistory.getTie()+1);;
					qHistory.setStreak(0);
				}
				else if(quizResult>0){
					qHistory.setWin(qHistory.getWin()+1);
					qHistory.setStreak(qHistory.getStreak()+1);
				} 
			}
			quizApp.getDataBaseHelper().createOrUpdateQuizHistory(qHistory);
			
			BadgeEvaluator badgeEvaluator = quizApp.getBadgeEvaluator();
			badgeEvaluator.evaluateBadges();
		}
		
		quizResultScreen.showResult(userAnswersStack,quizResult,didUserLevelUp(oldPoints,newPoints) , isChallengeMode());
		showScreen(quizResultScreen);
		
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
	
	public boolean didUserLevelUp(double oldPoints,double newPoints){
		if (Math.floor(quizApp.getGameUtils().getLevelFromXp(oldPoints))!=
				Math.floor(quizApp.getGameUtils().getLevelFromXp(newPoints))){
			return true;
		}
		return false;
	}

	public void loadProfile(User user) {
		ProfileAndChatController profileAndChat = (ProfileAndChatController) quizApp.loadAppController(ProfileAndChatController.class);
//		profileAndChat.loadChatScreen(user, -1, true);
		profileAndChat.showProfileScreen(user);
	}

	public void startNewChallenge(User otherUser){
		startNewChallenge(otherUser , quiz);
	}
	public void startNewChallenge(User otherUser , final Quiz quiz){
		this.quiz = quiz;
		final User withUser;
		if(otherUser==null) {
			withUser = getOtherUser();
		}
		else{
			withUser = otherUser;
		}
		setQuizType(CHALLENGE_QUIZ_TYPE);
		//TODO: clear socket , 
		// master server get sid
		// open new socket with &isChallenge=uid2 , get challengeId , etc from server , 
		// else let the user click on start now to Send message START_CHALLENGE_NOW to server , get questions , 
		// and then wait for the opponent to connect ,
		// users , setmode as challenge , complete it to send the offlinechallenge to server 
		gracefullyCloseSocket();//previous socket 
		HashMap<String , String> temp = new HashMap<String, String>();
		temp.put("isChallenge", otherUser.uid);
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
			showChallengeScreen(otherUser, quiz);
		}
		quizApp.getServerCalls().startProgressiveQuiz(this, quiz, CHALLENGE_QUIZ_TYPE ,temp);
	}
	
	
	
	public void startChallengedGame(User otherUser , final ChallengeData challengeData ){
		final User withUser;
		if(otherUser==null) {
			withUser = getOtherUser();
		}
		else{
			withUser = otherUser;
		}
		this.challengeData = challengeData;
		
		setQuizMode(CHALLENGED_MODE);
		setQuizType(CHALLENGED_QUIZ_TYPE);
		//get questions from server , 
		
		gracefullyCloseSocket();//previous socket 
		clearScreen();
		clashingScreen = new ClashScreen(this);
		clashingScreen.setClashCount(2); 
		clashingScreen.updateClashScreen(quizApp.getUser()/*quizApp.getUser()*/,quiz,  0 );
		// load questions from the users answers array , 
		// rearrange questions in the same order 
		// startQuestions 
		// isChallengedMode , run bot answers 
		// on end , onChallengeComplete , update on server , and notify other user
		quiz = quizApp.getDataBaseHelper().getQuizById(challengeData.quizId);
		challengeUserAnswers = challengeData.userAnswers;
		ArrayList<String > questionIds = new ArrayList<String>();
		for(UserAnswer userAnswer : challengeUserAnswers){
			questionIds.add(userAnswer.questionId);
		}
		currentUsers.clear();
		currentUsers.add(quizApp.getUser());
		currentUsers.add(otherUser);
		quizApp.getServerCalls().loadQuestionsInOrder(questionIds,new DataInputListener<List<Question>>(){
			@Override
			public String onData(List<Question> s) {
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
								quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.CANNOT_CHALLENGE_PRIVATE_USERS.getValue(), null, UiText.CLOSE.getValue(), null);
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
				}
				return super.onData(s);
			}
		});
	}
}