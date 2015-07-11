package com.amcolabs.quizapp.loginutils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.datalisteners.DataInputListener2;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Gender;
import com.google.android.gms.plus.model.people.PersonBuffer;


/**
 * 
 * @author abhinav2
 *
 */
public class GoogleLoginHelper implements ConnectionCallbacks, OnConnectionFailedListener {
    private static final int RC_SIGN_USER_PROFILE = 101;
    private QuizApp quizApp;
	private GoogleApiClient mGoogleApiClient;
	/* Is there a ConnectionResult resolution in progress? */
	private boolean mIsResolving = false;
	private static final int RC_SIGN_IN = 0;

	/* Should we automatically resolve ConnectionResults when possible? */
	private boolean mShouldResolve = false;

	private DataInputListener<User> listener;
	protected static final String ACTIVITIES_LOGIN = "http://schemas.google.com/AddActivity";

	public GoogleLoginHelper(QuizApp quizApp) {
		this.quizApp = quizApp;
        mGoogleApiClient = new GoogleApiClient.Builder(quizApp.getMainActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
	}
	
	public void doLogin(DataInputListener<User> loginListener){
		this.listener = loginListener;

		mShouldResolve = true;

		quizApp.getMainActivity().setActivityResultListener(new DataInputListener2<Integer, Integer, Intent, Void>() {
            public void onData(Integer requestCode, Integer responseCode, Intent intent) {
                onActivityResult(requestCode, responseCode, intent);
            }

            ;
        });

		mGoogleApiClient.connect();

	}

	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) { // you get this on the activity , propagate to quizApp 
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != Activity.RESULT_OK) {
				mShouldResolve = false;
			}
			mIsResolving  = false;
			mGoogleApiClient.connect();
		}
	}

	private void getUserProfileInformation(){
    	final User user = new User();
        Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        Plus.PeopleApi.load(mGoogleApiClient, "me").setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(final People.LoadPeopleResult loadPeopleResult) {
                quizApp.addUiBlock("Fetching Profile.");
                if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                    try {
                        int count = personBuffer.getCount();
                        Person person = personBuffer.get(0);
                        try {
                            String birthday = person.getBirthday();
                            if (birthday != null)
                                user.birthday = (new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(birthday).getTime()) / 1000;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Person.Cover cover = person.getCover();
                        if (cover != null) {
                            Person.Cover.CoverPhoto coverPhoto = cover.getCoverPhoto();
                            if (coverPhoto != null) {
                                String coverPhotoURL = coverPhoto.getUrl();
                                if (coverPhotoURL != null) {
                                    user.coverUrl = coverPhotoURL;
                                }
                            }
                        }

                        user.uid = person.getId();
                        user.setName(person.getDisplayName());
                        if ((person.hasImage()) && (person.getImage().hasUrl())) {
                            user.pictureUrl = person.getImage().getUrl().replace("?sz=50", "?sz=200");
                        }
                        user.gender = person.getGender() == Gender.MALE ? "male" : "female";
                        user.emailId = Plus.AccountApi.getAccountName(mGoogleApiClient);

                        getAllFriendsList(null, user);
                    } catch (Exception ex) {
                        listener.onData(null);
                    } finally {
                        personBuffer.close();
                    }

                } else {
                    Log.e("GPLUS_HELPER", "Error requesting people data: " + loadPeopleResult.getStatus());
                }
                quizApp.removeUiBlock();
            }
        });
	}


	public void getTokenAndUser(final User user , final DataInputListener<User> listener){
		new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                quizApp.addUiBlock(UiText.CONNECTING.getValue());
                AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String token;
                        try {

                            token = GoogleAuthUtil
                                    .getToken(
                                            quizApp.getContext().getApplicationContext(),
                                            user.emailId,
                                            "oauth2:"//+"server"
                                                    //+":client_id:" + Config.GOOGLE_PLUS_SERVER_CLIENT_ID
                                                    //+":api_scope:"
                                                    + Scopes.PLUS_LOGIN
                                                    + " " + Scopes.PROFILE + " " + Scopes.PLUS_ME);
                        } catch (UserRecoverableAuthException e) {
                            // Recover

                            quizApp.getMainActivity().startActivityForResult(e.getIntent(), GoogleLoginHelper.RC_SIGN_USER_PROFILE);
                            e.printStackTrace();
                            token = null;
                        } catch (GoogleAuthException authEx) {
                            authEx.printStackTrace();
                            authEx.getMessage();
                            token = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                            token = null;
                        } finally {
                        }
                        return token;
                    }

                    @Override
                    protected void onPostExecute(String token) {
                        quizApp.removeUiBlock();
                        if (token != null) {
                            user.googlePlus = token;
                            listener.onData(user);
                        } else {
                            listener.onData(null);
                        }
                    }
                };
                task.execute();
            }
        }, 0);
	}


	public void getAllFriendsList(String token , final User user){
		quizApp.addUiBlock(UiText.CHECKING_FOR_FRIENDS.getValue());
        Plus.PeopleApi.loadVisible(mGoogleApiClient, token).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(final People.LoadPeopleResult loadPeopleResult) {
                if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                    try {
                        for (Person person : personBuffer) {
                            user.gPlusFriendUids.add(person.getId());
                        }
                        if (loadPeopleResult.getNextPageToken() != null) {
                            getAllFriendsList(loadPeopleResult.getNextPageToken(), user);
                        } else {
                            //read all friends list
                            user.gPlusFriends = quizApp.getConfig().getGson().toJson(user.gPlusFriends);
                            getTokenAndUser(user, listener);
                        }
                    } finally {
                        personBuffer.close();
                    }
                } else {
                    Log.e("GOOGLE LOGIN HELPER", "Error requesting people data: " + loadPeopleResult.getStatus());
                }
                quizApp.removeUiBlock();
            }
        });

		
	}

    @Override
    public void onConnected(Bundle bundle) {
        mShouldResolve = true;
        getUserProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    mIsResolving = true;
                    connectionResult.startResolutionForResult(quizApp.getMainActivity(), RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(Config.QUIZAPP_ERR_LOG_TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.GPLUS_ERRROR.getValue(), null,  UiText.OK.getValue() , null);
            }
        } else {
            // Show the signed-out UI
            quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.SIGNEDOUT.getValue(),null,  UiText.OK.getValue() , null);

        }
    }

}

