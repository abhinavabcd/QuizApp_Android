package com.amcolabs.quizapp.databaseutils;

import java.util.List;

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
	String challengeId;	
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
	
}
