package com.amcolabs.quizapp.databaseutils;

import java.util.HashMap;

import com.amcolabs.quizapp.serverutils.ServerResponse;
import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;
import com.j256.ormlite.field.DatabaseField;

public class Category {

	@DatabaseField(id=true, index = true)
    String categoryId;
	@DatabaseField
    String shortDescription;
	@DatabaseField
	String description;
	@DatabaseField
	String quizList; 
	@DatabaseField
	int categoryType;
	@DatabaseField
	double modifiedTimestamp;
    
	public static enum CategoryType{
		SPECIAL(0);
		
		private int value;
		
		private CategoryType(int value){
			this.setValue(value);
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

	};
	
	private static HashMap<Integer , CategoryType> categoryTypeMap = null;//new HashMap<Integer , MessageType>();;
		
	public CategoryType getCategoryType(){
		return this.getCategoryType(this.categoryType);
	}
	public  CategoryType getCategoryType(int value){
		if(categoryTypeMap==null){
			categoryTypeMap = new HashMap<Integer, CategoryType>();
			for(CategoryType s : CategoryType.values()){
				categoryTypeMap.put(s.getValue(), s);
			}
		}
		return categoryTypeMap.containsKey(value) ? categoryTypeMap.get(value):null;
	}
}
