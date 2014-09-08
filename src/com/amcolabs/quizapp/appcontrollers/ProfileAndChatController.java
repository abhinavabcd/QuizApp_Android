package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.adapters.ChatListAdapter;
import com.amcolabs.quizapp.adapters.UserChatListScreen;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.ChatList;
import com.amcolabs.quizapp.databaseutils.UserInboxMessage;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.datalisteners.DataInputListener2;
import com.amcolabs.quizapp.notificationutils.NotificationReciever;
import com.amcolabs.quizapp.notificationutils.NotificationReciever.NotificationType;
import com.amcolabs.quizapp.screens.ChatScreen;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;

public class ProfileAndChatController extends AppController {

	private ChatScreen chatScreen;
	private DataInputListener<Bundle> gcmListener;
	private List<ChatList> chatList;

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
								chatScreen.addMessage(false, -1 , extras.getString(Config.KEY_GCM_TEXT_MESSAGE));
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
					chatScreen.addMessage(quizApp.getUser().uid.equalsIgnoreCase(message.fromUid), message.timestamp , message.message);
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
	

	public void showChatScreen() {
		clearScreen();
		chatList = quizApp.getDataBaseHelper().getAllChatList();
		List<String> uidsList = new ArrayList<String>();
		for(ChatList item : chatList){
			uidsList.add(item.uid);
		}
		
		quizApp.getDataBaseHelper().getAllUsersByUid(uidsList, new DataInputListener<Boolean>(){ // should run on ui thread
			@Override
			public String onData(Boolean s) {
				
				
				
					final ChatListAdapter chatListAdapter = new ChatListAdapter(quizApp,0,chatList, 
					new DataInputListener2<ChatList, User , Void, Void>(){
						@Override
						public void onData(ChatList s , User u , Void v1 , Void v2) {
							ProfileAndChatController.this.loadChatScreen(u, -1, true);
							return;
						}
					});
					UserChatListScreen chatListScreen = new UserChatListScreen(ProfileAndChatController.this);
					insertScreen(chatListScreen);
					chatListScreen.showChatList(chatListAdapter);

		return null;
		}
		});


	}
}
