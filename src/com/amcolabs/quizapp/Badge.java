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
	String assetPath;
	@DatabaseField
	String smallAssetPath;
	@DatabaseField
	int type;
	@DatabaseField		
	double modifiedTimestamp;
	@DatabaseField
	boolean isUserAwarded;
	
	public Badge() {
		// TODO Auto-generated constructor stub
	}

}
