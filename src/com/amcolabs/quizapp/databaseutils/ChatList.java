package com.amcolabs.quizapp.databaseutils;

import com.j256.ormlite.field.DatabaseField;

public class ChatList {
	
	public ChatList(String uid2, String message, double currentTimeStamp, int hasSeen) {
		uid = uid2;
		recentMessage = message;
		timestamp  = currentTimeStamp;
		unseenMessagesFlag  = hasSeen;
	}
	public ChatList(){
	}
	@DatabaseField(index=true, unique=true)
    public String uid;
	@DatabaseField
	public String recentMessage;
	@DatabaseField
	public double timestamp;
	@DatabaseField
	public int unseenMessagesFlag;
	
}
