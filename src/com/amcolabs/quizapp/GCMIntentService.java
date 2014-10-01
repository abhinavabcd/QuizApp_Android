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

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(Config.GCM_APP_ID);
    }

    @Override
    protected void onRegistered(final Context context, final String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
  //      displayMessage(parentContext, getString(R.string.gcm_registered,
  //             registrationId)); 
        ServerCalls.setUserGCMKey(context, registrationId, new DataInputListener<Boolean>(){
        	public String onData(Boolean b){
        		if(b){
        			UserDeviceManager.setPreference(context , "registrationKey", registrationId);
        		}
        		return null;
        	}
        });
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
//        displayMessage(parentContext, getString(R.string.gcm_unregistered));
 //       ServerCalls.unsetUserGCMKey(context, registrationId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
//    	Toast.makeText(context, "Received GCM Message!", Toast.LENGTH_LONG).show();
 //       Log.i(TAG, "Received message. Extras: " + intent.getExtras());     
//
	    Intent intent2 = new Intent(Config.RECIEVER_NEW_GCM_NOTIFICATION);
	    intent2.putExtras(intent.getExtras());
        sendOrderedBroadcast(intent2, null);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
//        displayMessage(parentContext, UiText.CLICK_TO_NEW_NOTIFICATIONS.getValue());
        // notifies user
        generateNotification(context, UiText.CLICK_TO_NEW_NOTIFICATIONS.getValue());
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
//        displayMessage(parentContext, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
 //       displayMessage(parentContext, getString(R.string.gcm_recoverable_error,
 //               errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
    	UiUtils.generateNotification(context, message);
    }
    
}
