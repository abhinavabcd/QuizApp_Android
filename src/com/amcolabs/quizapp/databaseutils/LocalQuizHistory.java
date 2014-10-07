package com.amcolabs.quizapp.databaseutils;

import com.j256.ormlite.field.DatabaseField;

public class LocalQuizHistory {
	
	@DatabaseField(index=true)
	String	withUid; // game with uid
	@DatabaseField
	int quizResult; // win loose , tie , server error
	@DatabaseField
	String quizId;
	@DatabaseField
	String questionIds;//json array list of string
	@DatabaseField
	String userAnswers1;//json of user answers
	@DatabaseField
	String userAnswers2;//json of user answers
}
