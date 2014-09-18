package com.amcolabs.quizapp.databaseutils;

import com.j256.ormlite.field.DatabaseField;

public class Badge {
	
	@DatabaseField(id=true , index=true, unique=true)
    int id;
	@DatabaseField
	String name;
	@DatabaseField
	String description;
	@DatabaseField
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAssetPath() {
		return assetPath;
	}

	public void setAssetPath(String assetPath) {
		this.assetPath = assetPath;
	}

	public String getSmallAssetPath() {
		return smallAssetPath;
	}

	public void setSmallAssetPath(String smallAssetPath) {
		this.smallAssetPath = smallAssetPath;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getModifiedTimestamp() {
		return modifiedTimestamp;
	}

	public void setModifiedTimestamp(double modifiedTimestamp) {
		this.modifiedTimestamp = modifiedTimestamp;
	}

	public boolean isAwarded() {
		return isAwarded;
	}

	public void setAwarded(boolean isAwarded) {
		this.isAwarded = isAwarded;
	}
	
	public String getCondition(){
		return this.condition;
	}	

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
