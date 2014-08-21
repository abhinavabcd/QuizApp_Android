package com.amcolabs.quizapp.databaseutils;

import com.j256.ormlite.field.DatabaseField;

public class Quiz {
	@DatabaseField(index=true, unique=true)
    public String quizId;
	@DatabaseField
	public String quizType;
	@DatabaseField
	public String name;
	@DatabaseField
	public String shortDescription;
	@DatabaseField
	public String assetPath;	
	@DatabaseField
	public String tags; //comma seperated list of tags
	@DatabaseField
	public int nQuestions;
	@DatabaseField
	public int nPeople;
	@DatabaseField
	public double modifiedTimestamp;
	public static Quiz createDummy() {
		
		Quiz c = new Quiz();
		c.quizId = "abcd";
		c.shortDescription = "Time to go on";
		c.name = "Do something";
		c.modifiedTimestamp = 0;
		return c;
	}
}
