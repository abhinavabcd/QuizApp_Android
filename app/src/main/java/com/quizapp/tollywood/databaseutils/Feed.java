package com.quizapp.tollywood.databaseutils;

import java.util.HashMap;

import com.quizapp.tollywood.QuizApp;
import com.j256.ormlite.field.DatabaseField;

public class Feed {
	
	@DatabaseField
	public String fromUid;
	@DatabaseField
	private int type;
	@DatabaseField
	public String message;
	@DatabaseField
	public String message2;
	@DatabaseField
	public double timestamp;
	

	
	private static HashMap<Integer , FeedType> feedTypeMap = null;//new HashMap<Integer , FeedType>();
	
	public FeedType getUserFeedType(){
		return this.getUserFeedType(this.type);
	}
	
	
	public FeedType getUserFeedType(int value){
		if(feedTypeMap==null){
			feedTypeMap = new HashMap<Integer, FeedType>();
			for(FeedType s : FeedType.values()){
				feedTypeMap.put(s.getValue(), s);
			}
		}
		return feedTypeMap.containsKey(value) ? feedTypeMap.get(value):FeedType.FEED_GENERAL;
	}
	
	
	public static enum FeedType{
		FEED_GENERAL(0),
		FEED_USER_WON(1),
		FEED_USER_TOOK_PART(2),
		FEED_USER_ADDED_FRIEND(3),
		FEED_USER_WON_BADGES(4),
		FEED_CHALLENGE(5),
		FEED_USED_JOINED(6);
		
		int value;
		private FeedType(int v) {
			value =v;
		}
		private int getValue() {
			return this.value;
		}
	};

	public Feed() {
		
	}
	
	public void onFeedElemClick(final QuizApp quizApp , String s){
		 if(s.startsWith("offlineChallengeId")){
			OfflineChallenge offlineChallenge = quizApp.getDataBaseHelper().getOfflineChallengeByChallengeId(s.split("/")[1]);
			quizApp.getStaticPopupDialogBoxes().showChallengeWinDialog(offlineChallenge);
		 }
		 else if(s.startsWith("userProfile")){
			 String uid = s.split("/")[1];
			 quizApp.fetchAndLoadUserProfile(uid);
		 }
	}

}