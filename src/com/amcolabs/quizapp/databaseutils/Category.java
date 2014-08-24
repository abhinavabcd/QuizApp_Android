package com.amcolabs.quizapp.databaseutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.ImageView;

import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.fileandcommonutils.FileHelper;
import com.j256.ormlite.field.DatabaseField;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
	public int type;
	@DatabaseField
	public double modifiedTimestamp;

	Category(){
		// needed by ormlite
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
