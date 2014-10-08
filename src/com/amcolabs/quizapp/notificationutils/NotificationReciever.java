package com.amcolabs.quizapp.notificationutils;

import java.util.HashMap;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amcolabs.quizapp.UserDeviceManager;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.google.gson.Gson;


public class NotificationReciever extends BroadcastReceiver{
				
			public static class NotificationPayload{
				public String fromUser;
				public String fromUserName;
				public String quizPoolWaitId;   
				public String serverId;
				public String quizId;
				public String quizName;
				public String textMessage;
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
			public NotificationPayload getNotificationPayload(Bundle bundle){
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
				
				String messageToDisplay = extras.getString(Config.NOTIFICATION_KEY_TEXT_MESSAGE);
				boolean abortThisRequest = false;
				boolean generateNotification = true;
				if(extras!=null){
					switch(type){
						case NOTIFICATION_GCM_INBOX_MESSAGE:
							//type.setPayload(extras);
							checkAndCallListener(type , getNotificationPayload(extras));
							break;
						case DONT_KNOW:
							break;
						case NOTIFICATION_GCM_CHALLENGE_NOTIFICATION:
							checkAndCallListener(type , getNotificationPayload(extras));
							break;
						case NOTIFICATION_GCM_GENERAL_FROM_SERVER:
							break;
						case NOTIFICATION_GCM_OFFLINE_CHALLENGE_NOTIFICATION:
							checkAndCallListener(type , getNotificationPayload(extras));
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
				if(generateNotification)
					UiUtils.generateNotification(context,messageToDisplay,extras);
		 	}
			
			private void checkAndCallListener(NotificationType type , NotificationPayload notificationPayload) {
				if(UserDeviceManager.isRunning()){
					if(listeners.containsKey(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE)){
						listeners.get(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE).onData(notificationPayload);
					}
				}
			}

			static HashMap<NotificationType, DataInputListener<NotificationPayload>> listeners = new HashMap<NotificationReciever.NotificationType, DataInputListener<NotificationPayload>>();
			
			public static void setListener(NotificationType type , DataInputListener<NotificationPayload> gcmListener){
				listeners.put(type, gcmListener);
			}
			
			public static void destroyAllListeners(){
				listeners.clear();
			}


			public static void removeListener(
					NotificationType notificationGcmInboxMessage,
					DataInputListener<NotificationPayload> gcmListener) {
				listeners.remove(notificationGcmInboxMessage);
								
			}
	}