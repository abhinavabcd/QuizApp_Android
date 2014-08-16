package com.amcolabs.quizapp.databaseutils;

import com.j256.ormlite.field.DatabaseField;

public class Quiz {
	@DatabaseField(index=true, unique=true)
    String quizId;
	@DatabaseField
	String quizType;
	@DatabaseField
    String name;
	@DatabaseField
    String tags; //comma seperated list of tags
	@DatabaseField
    int nQuestions;
	@DatabaseField
    int nPeople;
	@DatabaseField
    double modifiedTimestamp;
}
