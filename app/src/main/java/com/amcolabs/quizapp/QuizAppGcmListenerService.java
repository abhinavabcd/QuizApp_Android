package com.amcolabs.quizapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amcolabs.quizapp.NotificationReciever;
import com.amcolabs.quizapp.configuration.Config;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by abhinav on 04/06/15.
 */
public class QuizAppGcmListenerService extends GcmListenerService{
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //broadcast to Notification reciever that does the notification management ? is this needed ?
        broadcastToNotificationReceivers(getApplication(), data);
    }

    public static void broadcastToNotificationReceivers(Context context, Bundle data){
        Intent intent2 = new Intent();
        intent2.setAction(Config.GCM_NOTIFICATION_INTENT_ACTION);
        intent2.addCategory(Intent.CATEGORY_DEFAULT);
        intent2.putExtras(data);
        intent2.setClass(context, NotificationReciever
                .class);
        context.sendOrderedBroadcast(intent2, null);
    }
}
