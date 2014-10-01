package com.amcolabs.quizapp.databaseutils;

import java.util.HashMap;

import android.view.View;

import com.amcolabs.quizapp.QuizApp;
import com.j256.ormlite.field.DatabaseField;

public class Feed {
	
	@DatabaseField
	public String fromUid;
	@DatabaseField
	private int type;
	@DatabaseField
	public String message;

	
	private static HashMap<Integer , UserFeedType> feedTypeMap = null;//new HashMap<Integer , UserFeedType>();;
	
	
	
	public UserFeedType getUserFeedType(){
		return this.getUserFeedType(this.type);
	}
	
	
	public UserFeedType getUserFeedType(int value){
		if(feedTypeMap==null){
			feedTypeMap = new HashMap<Integer, UserFeedType>();
			for(UserFeedType s : UserFeedType.values()){
				feedTypeMap.put(s.getValue(), s);
			}
		}
		return feedTypeMap.containsKey(value) ? feedTypeMap.get(value):UserFeedType.FEED_GENERAL;
	}
	
	
	public static enum UserFeedType{
		FEED_GENERAL(0),
		FEED_USER_WON(1),
		FEED_USER_TOOK_PART(2),
		FEED_USER_ADDED_FRIEND(3),
		FEED_USER_WON_BADGES(4),
		FEED_CHALLENGE_WON(5),
		FEED_CHALLENGE_LOOSE(6);
		
		int value;
		private UserFeedType(int v) {
			value =v;
		}
		private int getValue() {
			return this.value;
		}
	};

	public Feed() {
		
	}
	
	public View getLayout(QuizApp quizApp){
		return null;
	}
}
