package com.amcolabs.quizapp.appcontrollers;

import java.util.List;

import android.os.Bundle;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.UserInboxMessage;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.notificationutils.NotificationReciever;
import com.amcolabs.quizapp.notificationutils.NotificationReciever.NotificationType;
import com.amcolabs.quizapp.screens.ChatScreen;
import com.amcolabs.quizapp.screens.UserProfileScreen;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;

public class ProfileAndChatController extends AppController {

	private ChatScreen chatScreen;
	private UserProfileScreen profileScreen;
	private DataInputListener<Bundle> gcmListener;

	public ProfileAndChatController(QuizApp quizApp) {
		super(quizApp);
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public boolean onBackPressed() {
		removeChatListeners();
		return super.onBackPressed();
	}
	
	public void showProfileScreen(User user){
		if(profileScreen==null){
			profileScreen = new UserProfileScreen(this);
			profileScreen.showUser(user);
		}
		profileScreen.showUser(user);
	}
	
	public void loadChatScreen(final User user2 , int toIndex , boolean isNewLoad){
		if(toIndex==0){
			return;
		}
		if(isNewLoad){
			clearScreen();
		}
		quizApp.getServerCalls().getMessages(user2 , toIndex , new DataInputListener<List<UserInboxMessage>>(){
			public String onData(List<UserInboxMessage> userMessages) {
				if(chatScreen==null){
					chatScreen = new ChatScreen(ProfileAndChatController.this, user2);
					gcmListener = new DataInputListener<Bundle>(){
						public String onData(Bundle extras) {
							if(extras.getString(Config.KEY_GCM_FROM_USER) == user2.uid){
								chatScreen.addMessage(false, extras.getString(Config.KEY_GCM_TEXT_MESSAGE));
							}
							return null;
						}
					};
					NotificationReciever.setListener(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE, gcmListener);
					insertScreen(chatScreen);
				}
				if(userMessages.size()==0){
					chatScreen.setDebugMessage(UiText.NO_RECENT_MESSAGES.getValue());
				}
				for(UserInboxMessage message : userMessages)
					chatScreen.addMessage(quizApp.getUser().uid==message.fromUid, message.message);
				return null;
			};
		});
	}
	
	public void sendMessage(User user2, String string) {
		quizApp.getServerCalls().sendChatMessage(user2, string);
	}

	public void removeChatListeners() {
		if(gcmListener!=null){
			NotificationReciever.removeListener(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE , gcmListener);
			gcmListener = null;
		}
	}
}
