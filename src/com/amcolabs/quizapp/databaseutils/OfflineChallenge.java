package com.amcolabs.quizapp.databaseutils;

import java.util.List;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController.UserAnswer;
import com.j256.ormlite.field.DatabaseField;

public class OfflineChallenge {
	
	public static class ChallengeData{
		public ChallengeData(String quizId, List<UserAnswer> userAnswers2) {
			this.quizId = quizId;
			this.userAnswers = userAnswers2;
		}
		public String quizId;
		public List<UserAnswer> userAnswers;
	}

	public OfflineChallenge() {
	}
	
	@DatabaseField(id=true , index=true, unique=true)
	private
	String offlineChallengeId;	
	@DatabaseField
	String fromUid_userChallengeIndex;
	@DatabaseField
	String toUid_userChallengeIndex;
	@DatabaseField
	int challengeTye;
	@DatabaseField
	String  challengeData;
	@DatabaseField
	String challengeData2;
	@DatabaseField
	String wonUid;
	@DatabaseField
	public boolean isCompleted = false;
	@DatabaseField
	public boolean hasWon = false;
	
	
	ChallengeData cachedChallengeData = null;
	public ChallengeData getChallengeData(QuizApp quizApp){
		return cachedChallengeData==null?(cachedChallengeData = quizApp.getConfig().getGson().fromJson(challengeData, ChallengeData.class)) : cachedChallengeData;
	}
	
	public String getFromUserUid(){
		return fromUid_userChallengeIndex.split("_")[0];
	}

	private User cachedUser = null;
	public User getFromUser(QuizApp quizApp) { 
		return cachedUser==null?(cachedUser =quizApp.cachedUsers.get(getFromUserUid())):cachedUser;
	}

	public String getOfflineChallengeId() {
		return offlineChallengeId;
	}

	public void setOfflineChallengeId(String offlineChallengeId) {
		this.offlineChallengeId = offlineChallengeId;
	}
}
