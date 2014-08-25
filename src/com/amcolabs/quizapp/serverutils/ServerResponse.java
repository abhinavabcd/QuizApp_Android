package com.amcolabs.quizapp.serverutils;

import java.util.HashMap;

import android.app.Activity;

/*
 * Author : Abhinav
 */

public class ServerResponse {
	public int messageType;
	public String payload; 
	public String payload1;
	public String payload2;
	public String payload3;
	public String payload4;
	public String payload5;
	
	
	public long responseTime;
	
		
	// below code to enumize the response codes
	public enum MessageType{
				////    =(.*)$ replace with (\1),  eclipse replace
				DUMMY(-1),
				NOT_ACTIVATED (104),
				ACTIVATED (105),
				NOT_AUTHORIZED (106),
				OK (200),
				OK_AUTH(202),
				USER_EXISTS(203),
				USER_NOT_EXISTS ( 204),
				USER_SAVED ( 205),
				OK_IMMUTABLE ( 206),
				OK_FEED ( 207),
				OK_INIT ( 208),
				FAILED(300),
				DUPLICATE_USER ( 301),
				CAN_UPGRADE(212),
				ALLOWED(211),
				CAN_UPGRADE_RECHARGE ( 213),
				REG_SAVED ( 214),
				OK_USER_INFO (215),
				OK_NAME(216),
				NO_NAME_FOUND ( 217),
				RATING_OK ( 220),

				OK_DETAILS ( 501),
				NOT_FOUND(404   ),
				OK_QUESTIONS ( 502),
				OK_QUESTION ( 503),
				OK_SERVER_DETAILS ( 504),
				OK_QUIZZES_CATEGORIES ( 505),
				FACEBOOK_USER_SAVED ( 506),
				GPLUS_USER_SAVED ( 507),

//				################################# dict values/commands for payload type definition
				USER_ANSWERED_QUESTION ( 1),
				GET_NEXT_QUESTION ( 2),
				STARTING_QUESTIONS ( 3),
				ANNOUNCING_WINNER ( 4),
				USER_DISCONNECTED ( 5),
				NEXT_QUESTION (6),
				START_QUESTIONS ( 7),
				STATUS_WHAT_USER_GOT ( 8);


				
		private int value;
		
		private MessageType(int value){
			this.setValue(value);
			//messageTypeMap.put(value, this);
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

	};
	
	private static HashMap<Integer , MessageType> messageTypeMap = null;//new HashMap<Integer , MessageType>();;
	
	
	
	public MessageType getMessageType(){
		return this.getMessageType(this.messageType);
	}
	
	
	public MessageType getMessageType(int value){
		if(messageTypeMap==null){
			messageTypeMap = new HashMap<Integer, ServerResponse.MessageType>();
			for(MessageType s : MessageType.values()){
				messageTypeMap.put(s.getValue(), s);
			}
		}
		return messageTypeMap.containsKey(value) ? messageTypeMap.get(value):MessageType.DUMMY;
	}

	
	@Deprecated
	public MessageType getStatusCode(){
		return getMessageType();
	}
	
	@Deprecated
	public MessageType getStatusCode(int value){
		return getMessageType(value);
	}
	
	public long getResponseTime() {
		return responseTime;
	}
	
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}
}



//127.0.0.1:8084/func?task=saveUser&deviceId=abcdefgh&email=abhinavabcd@gmail.com
//http://127.0.0.1:8084/func?task=getAuthKey&deviceId=abcdefgh&email=abhinavabcd@gmail.com
//{"payload": "YWJjZGVmZ2h8YWJoaW5hdmFiY2RAZ21haWwuY29t|1393389556|37287ef4a1261b927e8a98d639035d81f0e7eb2c", "statusCode": 1}

//http://127.0.0.1:8084/func?task=saveImmutable&message=hello+world&properties={%22toUser%22:%22check%22,%22unlock_time%22:%220%22}&encodedKey=YWJjZGVmZ2h8YWJoaW5hdmFiY2RAZ21haWwuY29t|1393389556|37287ef4a1261b927e8a98d639035d81f0e7eb2c
//{"payload": "", "statusCode": 0}
//http://127.0.0.1:8084/func?task=getAllImmutables&encodedKey=YWJjZGVmZ2h8YWJoaW5hdmFiY2RAZ21haWwuY29t|1393389556|37287ef4a1261b927e8a98d639035d81f0e7eb2c
//{"payload": "[{\"timeStamp\": {\"$date\": 1393454946772}, \"user\": {\"$oid\": \"530d6fa0e6ea3d61a8cd5bd8\"}, \"messageId\": \"5IGKLANZ\", \"_id\": {\"$oid\": \"530e220ae6ea3d4292100317\"}, \"properties\": {\"locked\": true, \"unlock_time\": \"0\", \"toUser\": \"check\"}, \"messageContent\": \"hello world\"}]", "statusCode": 200}

