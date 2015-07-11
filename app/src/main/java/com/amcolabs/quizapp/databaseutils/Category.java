package com.amcolabs.quizapp.databaseutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.amcolabs.quizapp.QuizApp;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;

public class Category {

	@DatabaseField(id=true, index = true)
    public String categoryId;
	@DatabaseField
    public  String shortDescription;
	@DatabaseField
	public String description;
	@DatabaseField
	public String quizList; 
	@DatabaseField
	public String assetPath;  //could be cdn or a direct url
	@DatabaseField
	public String bgAssetPath = null;  //could be cdn or a direct url
	@DatabaseField
	public String titleAssetPath=null;  //could be cdn or a direct url
	
	@DatabaseField
	public int type;
	@DatabaseField
	public double modifiedTimestamp;

	Category(){
		// needed by ormlite
	}
	
	public List<Quiz> getQuizzes(QuizApp quizApp){
		List<Quiz> allQuiz = new ArrayList<Quiz>();
		List<String> quizIds = quizApp.getConfig().getGson().fromJson(quizList, new TypeToken<List<String>>(){}.getType());
		for(String quizId : quizIds){
			Quiz a = quizApp.getDataBaseHelper().getQuizById(quizId);
			if(a!=null)
				allQuiz.add(a);
		}
		return allQuiz;
	}
	
	public Category(String catId,String shortDesc, String desc,String qList,String aPath,int cType,double mTimeStamp){
		this.categoryId = catId;
		this.shortDescription = shortDesc;
		this.description = desc;
		this.quizList = qList;
		this.assetPath = aPath;
		this.type = cType;
		this.modifiedTimestamp = mTimeStamp;
	}
	
	//TODO: remove this 
	public static Category createDummy(){
		Category c = new Category();
		c.categoryId = "abcd";
		c.shortDescription = "Time to go on";
		c.description = "Do something";
		c.quizList = "1,2,3,4,5";
		c.type = 2;
		c.modifiedTimestamp = 0;
		
		return c;
	}
	
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
		return this.getCategoryType(this.type);
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
