package com.amcolabs.quizapp.databaseutils;

import com.amcolabs.quizapp.databaseutils.GameEvents.EventType;
import com.j256.ormlite.field.DatabaseField;

public class GameEvents {
	

	@DatabaseField
	public double timestamp;
	@DatabaseField
	public int eventType;
	@DatabaseField
	public String message;
	@DatabaseField
	public String message2;
	@DatabaseField
	public String message3;
	
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
		this.message = message;
		this.message2 = message2;
		this.message3 = message3;
	}
}
