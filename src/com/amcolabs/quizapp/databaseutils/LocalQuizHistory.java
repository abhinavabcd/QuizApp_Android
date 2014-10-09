package com.amcolabs.quizapp.databaseutils;

import java.util.List;

import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController.UserAnswer;
import com.j256.ormlite.field.DatabaseField;

public class LocalQuizHistory {
	@DatabaseField(index=true)
	String	withUid; // game with uid
	@DatabaseField
	int quizResult; // win loose , tie , server error
	@DatabaseField
	String quizId;
	@DatabaseField
	int xpGain ;
	@DatabaseField
	String questionIds;//json array list of string
	@DatabaseField
	public
	String userAnswers1;//json of user answers
	@DatabaseField
	public
	String userAnswers2;//json of user answers
	@DatabaseField
	double timestamp;//json of user answers
	
	public LocalQuizHistory(String quizId, int quizResult, double xpGain ,String uid, String userAnswers1Json, String userAnswers2Json) {
		withUid = uid;
		this.quizId = quizId;
		this.quizResult = quizResult;
		this.xpGain = (int) xpGain;
		this.userAnswers1 = userAnswers1Json;
		this.userAnswers1 = userAnswers2Json;
	}
	
	public LocalQuizHistory(){
		
	}
		
}
