package com.amcolabs.quizapp.databaseutils;

import com.j256.ormlite.field.DatabaseField;

public class ChatList {
	
	public static final int SEEN = 1;
	public static final int UNSEEN = 0;
	
	public ChatList(String uid2, String message, double currentTimeStamp, int hasSeen) {
		uid = uid2;
		recentMessage = message;
		timestamp  = currentTimeStamp;
		unseenMessagesFlag  = hasSeen;
	}
	public ChatList(){
	}
	
	@DatabaseField(id=true, index=true, unique=true)
    public String uid;
	@DatabaseField
	public String recentMessage;
	@DatabaseField
	public double timestamp;
	@DatabaseField
	public int unseenMessagesFlag;
	
}
