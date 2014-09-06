package com.amcolabs.quizapp.notificationutils;

import java.security.acl.NotOwnerException;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amcolabs.quizapp.UserDeviceManager;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.notificationutils.NotificationReciever.NotificationType;
import com.amcolabs.quizapp.uiutils.UiUtils;



public class NotificationReciever extends BroadcastReceiver{
	
			public static enum NotificationType{

				DONT_KNOW(-1),
				NOTIFICATION_USER_CHALLENGE(1),
				NOTIFICATION_NEW_BADGE(2),				
				NOTIFICATION_GCM_GENERAL_FROM_SERVER(3),
				NOTIFICATION_GCM_INBOX_MESSAGE(4);
				
				public int value;

				private NotificationType(int val) {
					this.value = val;
				}
				
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
							if(UserDeviceManager.isRunning()){
								if(this.listeners.containsKey(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE)){
									listeners.get(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE).onData(extras);
								}
							}
						default:
							break;
					}
				}
				if(abortThisRequest)
					abortBroadcast();
				if(generateNotification)
					UiUtils.generateNotification(context,messageToDisplay,extras);
		 	}
			
			static HashMap<NotificationType, DataInputListener<Bundle>> listeners = new HashMap<NotificationReciever.NotificationType, DataInputListener<Bundle>>();
			
			public static void setListener(NotificationType type , DataInputListener<Bundle> listener){
				listeners.put(type, listener);
			}
			
			public static void destroyAllListeners(){
				listeners.clear();
			}


			public static void removeListener(
					NotificationType notificationGcmInboxMessage,
					DataInputListener<Bundle> gcmListener) {
				listeners.remove(notificationGcmInboxMessage);
								
			}
	}