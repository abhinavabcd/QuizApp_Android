package com.amcolabs.quizapp.loginutils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
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
public class GoogleLoginHelper {
	private QuizApp quizApp;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private ConnectionResult mConnectionResult;
	protected boolean mSignInClicked;
	private DataInputListener<User> listener;
	private static final int RC_GOOLE_SIGN_IN = UUID.randomUUID().hashCode() & 0xFFFF;
	private static final int RESULT_OK = 0;

	public GoogleLoginHelper(QuizApp quizApp) {
		this.quizApp = quizApp;
		
	}
	
	public void doLogin(DataInputListener<User> loginListener){
		this.listener = loginListener;
		mSignInClicked = true;
		mGoogleApiClient = new GoogleApiClient.Builder(quizApp.getContext())
		.addConnectionCallbacks(new ConnectionCallbacks() {
			
			@Override
			public void onConnectionSuspended(int arg0) {
				mGoogleApiClient.connect();
			}
			
			@Override
			public void onConnected(Bundle arg0) {
				// get token and profile information
				getTokenAndUser();
			}
		})
		.addOnConnectionFailedListener(new OnConnectionFailedListener() {
			

			@Override
			public void onConnectionFailed(ConnectionResult result) {
				if (!result.hasResolution()) {
					GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), quizApp.getActivity(),
							0).show();
					return;
				}

				if (!mIntentInProgress) {
					// Store the ConnectionResult for later usage
					mConnectionResult = result;

					if (mSignInClicked) {
						// The user has already clicked 'sign-in' so we attempt to
						// resolve all
						// errors until the user is signed in, or they cancel.
						resolveSignInError();
					}
				}

			}
		}).addApi(Plus.API)
		.addScope(Plus.SCOPE_PLUS_LOGIN)
		.addScope(Plus.SCOPE_PLUS_PROFILE)
		.build();
		mGoogleApiClient.connect();

	}
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(quizApp.getActivity(), RC_GOOLE_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}
	
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) { // you get this on the activity , propagate to quizApp 
		if (requestCode == RC_GOOLE_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	public void getTokenAndUser(){
		quizApp.addUiBlock(UiText.CONNECTING.getValue());
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token;
                try {
                	
                	token = GoogleAuthUtil
							.getToken(
									quizApp.getContext(),
		                            Plus.AccountApi.getAccountName(mGoogleApiClient),
									"oauth2:"
											+ Scopes.PLUS_LOGIN
											+ " "+Scopes.PROFILE+" "+Scopes.PLUS_ME);
                } 
                catch (UserRecoverableAuthException e) {
              	     // Recover
                	if(e==null){
                		return null;
                	}
                	quizApp.getMainActivity().setActivityResultListener(new DataInputListener2<Integer, Integer, Intent, Void>(){
                		public void onData(Integer requestCode, Integer responseCode, Intent intent) {
                			onActivityResult(requestCode, responseCode, intent);
                			quizApp.getMainActivity().setActivityResultListener(null);//remove it 
                		};
                	});
                	quizApp.startActivityForResult(e.getIntent(), RC_GOOLE_SIGN_IN);
                    e.printStackTrace();
                    token= null;
             	} catch (GoogleAuthException authEx) {
             		authEx.printStackTrace();
              	     authEx.getMessage();
              	     token = null;
              	} catch (IOException e) {
					e.printStackTrace();
					token=null;
				} 
                finally{
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                quizApp.removeUiBlock();
                if(token != null) {
                	User user = new User();
                	user.googlePlus = token;
                	getUserProfileInformation(user);
                } 
                else {
                	listener.onData(null);
                }
            }
        };
        task.execute();
	}
	private void getUserProfileInformation(final User user){
		quizApp.addUiBlock("Fetching Profile.");
        Plus.PeopleApi.load(mGoogleApiClient, "me").setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(final People.LoadPeopleResult loadPeopleResult) {
                if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                    try {
                        int count = personBuffer.getCount();
                        Person person = personBuffer.get(0);
                        try {
                        	String birthday= person.getBirthday();
                        	if(birthday!=null)
                        		user.birthday = (new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(birthday).getTime())/1000;
						} catch (ParseException e) {
							e.printStackTrace();
						}
                        Person.Cover cover = person.getCover();
                        if (cover != null) {
                            Person.Cover.CoverPhoto coverPhoto = cover.getCoverPhoto();
                            if (coverPhoto != null) {
                                String coverPhotoURL = coverPhoto.getUrl();
                                if(coverPhotoURL != null){
                                    user.coverUrl = coverPhotoURL;
                                }
                            }
                        }
                        
                        user.uid = person.getId();
                        user.name = person.getDisplayName();
                        if ((person.hasImage()) && (person.getImage().hasUrl())) {
                            user.pictureUrl = person.getImage().getUrl().replace("?sz=50", "?sz=200");
                        }
                        user.gender = person.getGender()==Gender.MALE?"male":"female";
                      	user.emailId = Plus.AccountApi.getAccountName(mGoogleApiClient);
                      	
                      	getAllFriendsList(null , user);
                    } 
                    catch(Exception ex){
                    	listener.onData(null);
                    }
                    finally {
                        personBuffer.close();
                    }
                    
                } else {
                    Log.e("Google Plus Login Helper", "Error requesting people data: " + loadPeopleResult.getStatus());
                }
                quizApp.removeUiBlock();
            }
        });
	}
	
	public void getAllFriendsList(String token , final User user){
		quizApp.addUiBlock(UiText.CHECKING_FOR_FRIENDS.getValue());
        Plus.PeopleApi.loadVisible(mGoogleApiClient, token).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(final People.LoadPeopleResult loadPeopleResult) {
                if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                    try {
                        for(Person person : personBuffer){
                            user.gPlusFriendUids.add(person.getId());
                        }
                        if(loadPeopleResult.getNextPageToken() != null) {
                            getAllFriendsList(loadPeopleResult.getNextPageToken(), user);
                        } else {
                        	//read all friends list 
                        	user.gPlusFriends = quizApp.getConfig().getGson().toJson(user.gPlusFriends);
                        	listener.onData(user);
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
	
}

