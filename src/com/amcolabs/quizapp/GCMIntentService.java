/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amcolabs.quizapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends IntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(Config.GCM_APP_ID);
    }


    @Override
	public void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
 //               sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                }
                onMessage(getApplicationContext(), intent);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    
    
    protected void onRegistered(final Context context, final String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
  //      displayMessage(parentContext, getString(R.string.gcm_registered,
  //             registrationId)); 

    }

    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
//        displayMessage(parentContext, getString(R.string.gcm_unregistered));
        ServerCalls.unsetUserGCMKey(context, registrationId);
    }

    protected void onMessage(Context context, Intent intent) {
//    	Toast.makeText(context, "Received GCM Message!", Toast.LENGTH_LONG).show();
 //       Log.i(TAG, "Received message. Extras: " + intent.getExtras());     
//
	    Intent intent2 = new Intent(Config.RECIEVER_NEW_GCM_NOTIFICATION);
	    intent2.putExtras(intent.getExtras());
        sendOrderedBroadcast(intent2, null);
    }

    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
//        displayMessage(parentContext, UiText.CLICK_TO_NEW_NOTIFICATIONS.getValue());
        // notifies user
        generateNotification(context, UiText.CLICK_TO_NEW_NOTIFICATIONS.getValue());
    }

    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
//        displayMessage(parentContext, getString(R.string.gcm_error, errorId));
    }


    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
    	UiUtils.generateNotification(context, message);
    }
    
}
