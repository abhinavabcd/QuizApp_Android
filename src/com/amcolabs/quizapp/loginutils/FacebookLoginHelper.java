package com.amcolabs.quizapp.loginutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.datalisteners.DataInputListener2;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class FacebookLoginHelper {
	private QuizApp quizApp;
	private DataInputListener<User> listener;
	private Session session;

	public FacebookLoginHelper(QuizApp quizApp) {
		this.quizApp = quizApp;
	}
	
    Session.StatusCallback statusCallback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	    	if (state.isOpened()) {
		        Log.i("FB HELPER", "Logged in...");
		        
		        getTokenAndUserInfo();
		        
		    } else if (state.isClosed()) {
		        Log.i("FB HELPER", "Logged out...");
		    }
	    }
	};
	
	public void doLogin(DataInputListener<User> listener){
	    session = Session.openActiveSessionFromCache(quizApp.getActivity());  // Create new session by re-opening from cache.
	    if(session!=null && session.isOpened()){
		    session.closeAndClearTokenInformation();   
		    return;
	    }

	    this.listener = listener;
		// fb setup
		// get access token , 
		// get profile information with additional fields
		// get all friends list 
		// call listener 
		//resolve error in betwee
		
		
		
		
	    session = Session.getActiveSession();
	    session.addCallback(statusCallback);   
		quizApp.getMainActivity().setActivityResultListener(new DataInputListener2<Integer, Integer, Intent, Void>(){
			public void onData(Integer requestCode, Integer resultCode, Intent data) {
				 session.onActivityResult(quizApp.getActivity(), requestCode,
				            resultCode, data);
			};
		});
	    
	    
	    if (!session.isOpened() && !session.isClosed()) {
	        session.openForRead(new Session.OpenRequest(quizApp.getActivity())
	            .setPermissions(Arrays.asList("email", "user_friends","user_birthday"))
	            .setCallback(statusCallback));
	    } else {
	        Session.openActiveSession(quizApp.getActivity() ,true, Arrays.asList("email", "user_friends","user_birthday"),statusCallback);
	    }
		
		
		
		
		
	}


	protected void getTokenAndUserInfo() {
		quizApp.addUiBlock("Fetching Profile Info");
        Bundle params = new Bundle();
        params.putString("fields", "cover,name,first_name,last_name,middle_name,email,address,picture,location,gender,birthday,verified,friends");
        new Request(session, "me", params, null, new Callback() {
			@Override
			public void onCompleted(Response response) {
				if(response.getError()!=null){
					listener.onData(null);
				}
				else{
					GraphUser fbUser = response.getGraphObjectAs(GraphUser.class);
					User user = new User();
					user.facebook = session.getAccessToken();
			        user.uid = fbUser.getId();
			        user.setName(fbUser.getName());
			        user.pictureUrl = String.format("https://graph.facebook.com/%s/picture?type=large", fbUser.getId());
			        
			        if(fbUser.getProperty("email") != null){
			        	user.emailId = fbUser.getProperty("email").toString();
			        }
			        if(fbUser.getProperty("gender") != null) {
			            user.gender = fbUser.getProperty("gender").toString();
			        }
	                try {
	                	String birthday= fbUser.getBirthday();
	                	if(birthday!=null)
	                		user.birthday = (new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(birthday).getTime())/1000;
					} catch (ParseException e) {
						e.printStackTrace();
					}
			        if(fbUser.getLocation() != null) {
			            user.place = fbUser.getLocation().getProperty("name").toString();
			        }
			        if(fbUser.getProperty("cover")!=null){
			        	try {
							user.coverUrl = (new JSONObject(fbUser.getProperty("cover").toString())).getString("source");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			        getFriendsList(user);
			        
				}
				quizApp.removeUiBlock();
		   }
		}).executeAsync();

	}


	protected void getFriendsList(final User user) {
		quizApp.addUiBlock("Checking Friends");
		Request.newMyFriendsRequest(session, new GraphUserListCallback() {
			@Override
			public void onCompleted(List<GraphUser> fbUsers, Response response) {
				if(response.getError()!=null){
					listener.onData(null);
				}
				else{
					for(GraphUser fbUser : fbUsers){
						user.fbFriendUids.add(fbUser.getId());
					}
					user.fbFriends  = quizApp.getConfig().getGson().toJson(user.fbFriendUids);
					listener.onData(user);
				}
				quizApp.removeUiBlock();
			}
		}).executeAsync();
        
        
	}

	
}
