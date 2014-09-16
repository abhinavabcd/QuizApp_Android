package com.amcolabs.quizapp;

import com.j256.ormlite.field.DatabaseField;

public class Badge {
	
	@DatabaseField(id=true , index=true, unique=true)
    int id;
	@DatabaseField
	String name;
	@DatabaseField
	String description;
	@DatabaseField
	private
	String condition;
	@DatabaseField
	String assetPath;
	@DatabaseField
	String smallAssetPath;
	@DatabaseField
	int type;
	@DatabaseField		
	double modifiedTimestamp;
	@DatabaseField
	boolean isAwarded;
	
	public Badge() {
		// TODO Auto-generated constructor stub
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
