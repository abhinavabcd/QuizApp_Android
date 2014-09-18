package com.amcolabs.quizapp.databaseutils;

import com.j256.ormlite.field.DatabaseField;

public class Quiz {
	
	@DatabaseField(id=true , index=true, unique=true)
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
	@DatabaseField
	public double userXp;
	
	Quiz(){
		// needed by ormlite
	}
	
	public Quiz(String qId,String qType,String qName,String sDesc,String aPath,String qTags,int nQues,int nCount,double mTimeStamp){
		this.quizId = qId;
		this.quizType = qType;
		this.name = qName;
		this.shortDescription = sDesc;
		this.assetPath = aPath;
		this.tags = qTags;
		this.nQuestions = nQues;
		this.nPeople = nCount;
		this.modifiedTimestamp = mTimeStamp;
	}
	
	public static Quiz createDummy() {
		
		Quiz c = new Quiz();
		c.quizId = "abcd";
		c.shortDescription = "Time to go on";
		c.name = "Do something";
		c.modifiedTimestamp = 0;
		return c;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + " "+shortDescription;
	}
	
}
