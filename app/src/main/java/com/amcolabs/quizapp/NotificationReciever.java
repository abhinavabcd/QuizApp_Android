package com.amcolabs.quizapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amcolabs.quizapp.NotificationReciever.NotificationType;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.notificationutils.NotifificationProcessingState;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.google.gson.Gson;


public class NotificationReciever extends BroadcastReceiver{
				
			public static class NotificationPayload{
				public int messageType = -1;
				public String fromUser;
				public String fromUserName;
				public String quizPoolWaitId;   
				public String serverId;
				public String quizId;
				public String quizName;
				public String textMessage;
				public String payload1;
				public String payload2;
				public String payload3;
				public String payload4;
			}
	
			public static enum NotificationType{

				DONT_KNOW(-1),
				NOTIFICATION_USER_CHALLENGE(1),
				NOTIFICATION_NEW_BADGE(2),				
				NOTIFICATION_GCM_GENERAL_FROM_SERVER(3),
				NOTIFICATION_GCM_INBOX_MESSAGE(4),
				NOTIFICATION_SERVER_MESSAGE(5),
				NOTIFICATION_SERVER_COMMAND (6),
				NOTIFICATION_GCM_CHALLENGE_NOTIFICATION (7),
				NOTIFICATION_GCM_OFFLINE_CHALLENGE_NOTIFICATION (8);
				
				public int value;

				private NotificationType(int val) {
					this.value = val;
				}
			}
			public static NotificationPayload getNotificationPayload(Bundle bundle){
					JSONObject json = new JSONObject();
					Set<String> keys = bundle.keySet();
					for (String key : keys) {
					    try {
					        // json.put(key, bundle.get(key)); see edit below
					        json.put(key, bundle.get(key));
					    } catch(JSONException e) {
					        //Handle exception here
					    }
					}
					return new Gson().fromJson(json.toString(),NotificationPayload.class );
			}
			public static NotificationType getNotificationTypeFromInt(int x){
				NotificationType[] values = NotificationType.values();
				if(x >0 && x < values.length)
					for(NotificationType type:values){
						if(type.value == x)
							return type;
					}
				return NotificationType.DONT_KNOW;
			}
			
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle extras = intent.getExtras();
				int temp = 0;
				try{
					String temp2 = extras.getString(Config.NOTIFICATION_KEY_MESSAGE_TYPE);
					temp = temp2==null ? -1 : Integer.parseInt(temp2);
				}
				catch(ClassCastException e){
					temp =0;
				}
				NotificationType type = getNotificationTypeFromInt(Math.max(temp,extras.getInt(Config.NOTIFICATION_KEY_MESSAGE_TYPE, -1)));
				
				String titleText = null;
				String messageToDisplay = UiText.NEW_TEXT_AVAILABLE.getValue();
				boolean abortThisRequest = false;
				boolean generateNotification = true;
				NotificationPayload payload = null;
				if(extras!=null){
					if(!QuizApp.pendingNotifications.containsKey(type)){
						ArrayList<NotificationPayload> temp2 = new ArrayList<NotificationPayload>();
						QuizApp.pendingNotifications.put(type, temp2);
					}
					if(!UserDeviceManager.isRunning() || QuizApp.nState==NotifificationProcessingState.CONTINUE){
						payload = getNotificationPayload(extras);
						QuizApp.pendingNotifications.get(type).add(payload);
					}
					switch(type){
						case NOTIFICATION_GCM_INBOX_MESSAGE:
							//type.setPayload(extras);
							payload = getNotificationPayload(extras);
							if(checkAndCallListener(type , payload)){
								generateNotification = false;
							}
							else{
								titleText = UiText.USER_SENT_YOU_MESSAGE.getValue(payload.fromUserName);
								messageToDisplay = payload.textMessage;
							}
							break;
						case DONT_KNOW:
							break;
						case NOTIFICATION_GCM_CHALLENGE_NOTIFICATION:
							payload = getNotificationPayload(extras);
							if(checkAndCallListener(type , payload)){
								// generateNotification = false;
								generateNotification = false; // listener says ok
							}
							else{
								// not running or not handled 
//                                "fromUserName":self.user.name,
//                                "quizPoolWaitId":self.quizPoolWaitId,   
//                                "serverId":SERVER_ID,
//                                "quizId": quiz.quizId,
//                                "quizName":quiz.name,
//                                "messageType":NOTIFICATION_GCM_CHALLENGE_NOTIFICATION,  
//                                "timeStamp":HelperFunctions.toUtcTimestamp(datetime.datetime.now())
								titleText = UiText.LIVE_CHALLENGE.getValue();
								messageToDisplay = UiText.USER_WAITING_FOR_CHALLENGE.getValue(payload.fromUserName , payload.quizName);
							}
							break;
						case NOTIFICATION_GCM_GENERAL_FROM_SERVER:
							payload = getNotificationPayload(extras);
							messageToDisplay = payload.textMessage;	
							break;
						case NOTIFICATION_GCM_OFFLINE_CHALLENGE_NOTIFICATION:
							payload = getNotificationPayload(extras);
							if(checkAndCallListener(type , getNotificationPayload(extras))){
								generateNotification = false;
							}
							else{
								messageToDisplay = UiText.NEW_OFFLINE_CHALLENGE.getValue(payload.quizName);	
								titleText = UiText.OFFLINE_CHALLENGE_FROM.getValue(payload.fromUserName);
							}
							break;
						case NOTIFICATION_NEW_BADGE:
							break;
						case NOTIFICATION_SERVER_COMMAND:
							break;
						case NOTIFICATION_SERVER_MESSAGE:
							break;
						case NOTIFICATION_USER_CHALLENGE:
							break;
						default:
							break;
					}
				}
				if(abortThisRequest)
					abortBroadcast();
				if(generateNotification && !UserDeviceManager.isRunning()) // inside app all notifications should be handled without notifications
					UiUtils.generateNotification(context, titleText , messageToDisplay, extras);
		 	}
			
			static HashMap<NotificationType, DataInputListener<NotificationPayload>> listeners = new HashMap<NotificationReciever.NotificationType, DataInputListener<NotificationPayload>>();
			
			public static void setListener(NotificationType type , DataInputListener<NotificationPayload> listener){
				listeners.put(type, listener);
			}
			
			public static void destroyAllListeners(){
				listeners.clear();
			}


			public static void removeListener(
					NotificationType notificationGcmInboxMessage,
					DataInputListener<NotificationPayload> gcmListener) {
				listeners.remove(notificationGcmInboxMessage);
			}
			
			public static boolean checkAndCallListener(NotificationType type, NotificationPayload notificationPayload){
				if(UserDeviceManager.isRunning() && QuizApp.nState==NotifificationProcessingState.CONTINUE){
					if(listeners.containsKey(type)){
							listeners.get(type).onData(notificationPayload);
							return true;
					}
				}
				return false; // we wont call listener until state is continue , setting to continue is very important 
			}
			
			public static DataInputListener<NotificationPayload> getListener( NotificationType type) {
				if(UserDeviceManager.isRunning()){
					if(listeners.containsKey(type)){
							return listeners.get(type);
					}
				}
				return null;
			}
	}