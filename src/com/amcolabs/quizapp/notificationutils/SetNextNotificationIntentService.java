package com.amcolabs.quizapp.notificationutils;

import android.app.IntentService;
import android.content.Intent;

import com.appsandlabs.immutableandroid.uiUtils.UiUtils;

public class SetNextNotificationIntentService extends IntentService {
    public SetNextNotificationIntentService() {
        super("setnextnotificationservice");
    }
 
    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        UiUtils.setNextUnlockedNotification(this);
    }
}
 