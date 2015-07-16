package com.quizapp.tollywood.notificationutils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.quizapp.tollywood.NotificationReciever;
import com.quizapp.tollywood.configuration.Config;


public class AndroidNotification {
	
	public static void cancelNotification(Context context){
		cancelNotification(context , Config.NOTIFICATION_ID);
	}
	public static void cancelNotification(Context context, int notificationId){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    PendingIntent displayIntent = PendingIntent.getBroadcast(context, notificationId, new Intent(context,NotificationReciever.class), PendingIntent.FLAG_NO_CREATE);
		if(displayIntent != null) {
			   alarmManager.cancel(displayIntent);
			   displayIntent.cancel();  
		}
	} 
	
	public static void setNotificationAfter(Context context , long remainingMillis , Bundle bundle,int notificationId){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    Intent intent = new Intent(context,NotificationReciever.class);
	    if(bundle!=null)
	    	intent.putExtras(bundle);
	    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);
	    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + remainingMillis, alarmIntent);
	}
	
	public static void setNotification(final Context context, long timeInMillis,Bundle bundle,int notificationId){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    Intent intent = new Intent(context,NotificationReciever.class);//Config.COOL_DOWN_BROADCAST_RECIEVE);
	    if(bundle!=null)
	    	intent.putExtras(bundle);
	    PendingIntent alarmIntent = PendingIntent.getBroadcast(context,notificationId, intent, 0);
	    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeInMillis, alarmIntent);
	}
}
