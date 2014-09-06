package com.amcolabs.quizapp.appcontrollers;

import java.util.List;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.databaseutils.UserInboxMessage;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.notificationutils.NotificationReciever;
import com.amcolabs.quizapp.notificationutils.NotificationReciever.NotificationType;
import com.amcolabs.quizapp.screens.ChatScreen;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;

public class ProfileAndChatController extends AppController {

	private ChatScreen chatScreen;
	private Object gcmListener;

	public ProfileAndChatController(QuizApp quizApp) {
		super(quizApp);
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public boolean onBackPressed() {
		if(gcmListener!=null){
			NotificationReciever.removeListener(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE);
		}
		return super.onBackPressed();
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
					chatScreen.setDebugMessage(UiText.FETCHING_MESSAGES.getValue());
					insertScreen(chatScreen);
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
}
