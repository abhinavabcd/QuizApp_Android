package com.amcolabs.quizapp.databaseutils;

import android.view.View;

public class UserFeed {
	public String feedId;
	public int type;
	public String message;
	
	public static enum UserFeedType{
		USER_WON(0),
		USER_TOOK_PART(1),
		USER_ADDED_FRIEND(2),
		USER_WON_BADGES(3);
		
		int value;
		private UserFeedType(int v) {
			value =v;
		}
	};

	public UserFeed() {
		
	}
	
	public View getLayout(){
		return null;
	}
}
