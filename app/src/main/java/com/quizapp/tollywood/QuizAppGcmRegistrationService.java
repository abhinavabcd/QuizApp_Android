package com.quizapp.tollywood;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.quizapp.tollywood.configuration.Config;
import com.quizapp.tollywood.datalisteners.DataInputListener;
import com.quizapp.tollywood.serverutils.ServerCalls;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by rama on 04/06/15.
 */
public class QuizAppGcmRegistrationService extends IntentService {

    private static final String TAG = "quizAppRegIntentService";
    private static final String[] TOPICS = {"global"};

    public QuizAppGcmRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                final String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]

                if(!UserDeviceManager.getPreference(getApplicationContext(), Config.GCM_SAVED, "").equalsIgnoreCase(token)) {
                    ServerCalls.setUserGCMKey(getApplicationContext(), token, new DataInputListener<Boolean>() {
                        public String onData(Boolean b) {
                            if (b) {
                                UserDeviceManager.setPreference(getApplicationContext(), Config.GCM_SAVED, token);
                            }
                            return null;
                        }
                    });
                }

                // Subscribe to topic channels
                subscribeTopics(token);
            }
        } catch (IOException e) {
            UserDeviceManager.setPreference(getApplicationContext(), Config.GCM_SAVED, "false");
        }
        catch (Exception e) {
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
// [END subscribe_topics]
}