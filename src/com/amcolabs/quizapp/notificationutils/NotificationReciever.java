package com.amcolabs.quizapp.notificationutils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amcolabs.quizapp.UserDeviceManager;
import com.amcolabs.quizapp.configuration.Config;



public class NotificationReciever extends BroadcastReceiver{
	
			public static enum NotificationType{

				DONT_KNOW,
				NOTIFICATION_USER_CHALLENGE,
				NOTIFICATION_NEW_BADGE,				
				NOTIFICATION_GCM_GENERAL_FROM_SERVER,
				
			}
			
			public static NotificationType getNotificationTypeFromInt(int x){
				NotificationType[] values = NotificationType.values();
				if(x >0 && x < values.length)
					return values[x];
				return NotificationType.DONT_KNOW;
			}
			
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle extras = intent.getExtras();
				int temp = 0;
				try{
					temp = Integer.parseInt(extras.getString(Config.NOTIFICATION_KEY_MESSAGE_TYPE, "-1"));
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
					}					
				}
				if(abortThisRequest)
					abortBroadcast();
				if(generateNotification)
					UiUtils.generateNotification(context,messageToDisplay,extras);
		 	}
	}