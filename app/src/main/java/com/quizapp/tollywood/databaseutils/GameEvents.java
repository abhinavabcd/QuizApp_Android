package com.quizapp.tollywood.databaseutils;

import com.j256.ormlite.field.DatabaseField;

public class GameEvents {
	

	@DatabaseField
	public
	double timestamp;
	@DatabaseField
	int eventType;
	@DatabaseField
	private
	String message;
	@DatabaseField
	private
	String message2;
	@DatabaseField
	private
	String message3;
	
	public GameEvents() {
	}
	
	public static enum EventType{
		SOMETHING_ELSE(-1),
		UNLOCKED_BADGE(0),
		WON_QUIZ(1),
		LOST_QUIZ(2),
		LEVEL_UP(3),
		USER_JOINED(4),
		SHARED_WITH_FB(5),
		SHARED_WITH_GOOGLE(6),
		SERVER_ERROR_QUIZ(7),
		TIE_QUIZ(8);
		
		int value = 0;
		EventType(int i){
			value = i;
		}
		public int getValue() {
			return value;
		}
	}
	
	public EventType getEventType(){
		for(EventType evt : EventType.values()){
			if(eventType==evt.getValue())
				return evt;
		}
		return EventType.SOMETHING_ELSE;
	}
	
	public GameEvents(EventType evtType, String message,	String message2 , String message3){
		eventType = evtType.getValue();
		this.setMessage(message);
		this.setMessage2(message2);
		this.setMessage3(message3);
	}

	public String getMessage2() {
		return message2;
	}

	public void setMessage2(String message2) {
		this.message2 = message2;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage3(String def) {
		return message3==null?def : message3;
	}

	public void setMessage3(String message3) {
		this.message3 = message3;
	}
}
