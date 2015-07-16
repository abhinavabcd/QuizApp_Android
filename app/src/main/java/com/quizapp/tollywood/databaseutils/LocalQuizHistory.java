package com.quizapp.tollywood.databaseutils;

import java.util.ArrayList;
import java.util.List;

import com.quizapp.tollywood.QuizApp;
import com.quizapp.tollywood.User;
import com.quizapp.tollywood.appcontrollers.ProgressiveQuizController.UserAnswer;
import com.quizapp.tollywood.configuration.Config;
import com.quizapp.tollywood.gameutils.GameUtils;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;

public class LocalQuizHistory {

	@DatabaseField(index=true , unique = true ,  generatedId=true)
	private
	long quizHistoryid; // game with uid
	
	@DatabaseField(index=true)
	private
	String	withUid; // game with uid //TODO: this should be a list and searchable ? 
	
	@DatabaseField(index=true)
	private
	String	currentUid; // game with uid //TODO: this should be a list and searchable ? 
	
	@DatabaseField
	private
	int quizResult; // win loose , tie , server error
	@DatabaseField
	public
	String quizId;
	@DatabaseField
	public
	int xpGain;
	@DatabaseField
	String questionIds;//json array list of string
	
	@DatabaseField
	public
	String questionsJson;
	
	@DatabaseField
	public
	int maxScore;
	
	@DatabaseField
	public
	String usersJson;
	
	
	@DatabaseField
	private
	String userAnswers1;//json of user answers

	@DatabaseField
	private
	String userAnswers2;//json of user answers
	

	@DatabaseField
	public
	double timestamp;//json of user answers
	
	public List<UserAnswer> getUserAnswers1(QuizApp quizApp) {
		return cachedUserAnswer1==null ? (cachedUserAnswer1 = quizApp.getConfig().getGson().fromJson(userAnswers1, new TypeToken<List<UserAnswer>>(){}.getType())):cachedUserAnswer1;
	}



	public void setUserAnswers1(String userAnswers1) {
		this.userAnswers1 = userAnswers1;
	}
	
	public List<UserAnswer> getUserAnswers2(QuizApp quizApp) {
		return cachedUserAnswer2==null ? (cachedUserAnswer2 = quizApp.getConfig().getGson().fromJson(userAnswers2, new TypeToken<List<UserAnswer>>(){}.getType())):cachedUserAnswer2;
	}



	public void setUserAnswers2(String userAnswers2) {
		this.userAnswers2 = userAnswers2;
	}
	
	
	
	List<UserAnswer> cachedUserAnswer1 = null;
	List<UserAnswer> cachedUserAnswer2 = null;
	
	public int[] getLevelUpAndWinBonus(QuizApp quizApp){ // vague
		int total = GameUtils.getLastElement(getUserAnswers1(quizApp)).whatUserGot;
		int total2 = GameUtils.getLastElement(getUserAnswers2(quizApp)).whatUserGot;
		int winBonus = (int) (total>total2 ?Config.QUIZ_WIN_BONUS :0);
		int levelUpBonus = xpGain - total-winBonus;
		return new int[]{levelUpBonus, winBonus};
	}
	
	
	public LocalQuizHistory(){
		
	}
	public LocalQuizHistory(String quizId, int quizResult, double xpGain ,String currentUid , String withUid, int maxScrore , String currentQuestionsJson , String usersJson, String userAnswers1Json, String userAnswers2Json) {
		setWithUid(withUid);
		this.currentUid = currentUid;
		this.quizId = quizId;
		this.maxScore = maxScore;
		this.setQuizResult(quizResult);
		this.xpGain = (int) xpGain;
		this.questionsJson = currentQuestionsJson;
		this.usersJson  = usersJson;
		this.userAnswers1 = userAnswers1Json;
		this.userAnswers1 = userAnswers2Json;
	}
	

	public int getQuizResult() {
		return quizResult;
	}

	public void setQuizResult(int quizResult) {
		this.quizResult = quizResult;
	}



	public String getWithUid() {
		return withUid;
	}



	public void setWithUid(String withUid) {
		this.withUid = withUid;
	}



	public List<Question> getQuestions() {
		// TODO Auto-generated method stub
		return null;
	}



	public ArrayList<User> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}
		
}
