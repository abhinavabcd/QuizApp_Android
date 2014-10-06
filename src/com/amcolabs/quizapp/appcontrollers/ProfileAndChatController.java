package com.amcolabs.quizapp.appcontrollers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

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
import com.amcolabs.quizapp.notificationutils.NotificationReciever.NotificationPayload;
import com.amcolabs.quizapp.notificationutils.NotificationReciever.NotificationType;
import com.amcolabs.quizapp.screens.ChatScreen;
import com.amcolabs.quizapp.screens.SelectFriendsScreen;
import com.amcolabs.quizapp.screens.UserProfileScreen;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;

public class ProfileAndChatController extends AppController {

	private ChatScreen chatScreen;
	private UserProfileScreen profileScreen;
	private DataInputListener<NotificationPayload> gcmListener;
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
	
	public void showProfileScreen(User user){
		clearScreen();
		if(profileScreen==null){
			profileScreen = new UserProfileScreen(this);
		}
		profileScreen.showUser(user);
		showScreen(profileScreen);
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
					gcmListener = new DataInputListener<NotificationPayload>(){
						public String onData(NotificationPayload payload) {
							if(payload.fromUser.equalsIgnoreCase(user2.uid)){
								 String messageText = payload.textMessage;
								chatScreen.addMessage(false, -1 ,messageText);
								try {
									quizApp.getDataBaseHelper().getChatListDao().createOrUpdate(new ChatList(user2.uid,messageText , Config.getCurrentTimeStamp(), 0));
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
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
	
	public void sendMessage(final User user2, final String string) {
		quizApp.getServerCalls().sendChatMessage(user2, string, new DataInputListener<Boolean>(){
			@Override
			public String onData(Boolean s) {
				if(s){
					try {
						quizApp.getDataBaseHelper().getChatListDao().createOrUpdate(new ChatList(user2.uid,string , Config.getCurrentTimeStamp(), 0));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return super.onData(s);
			}
		});
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
					if(chatList.size()!=0){
						chatListScreen.debugMessageView.setVisibility(View.GONE);
					}
					insertScreen(chatListScreen);
					chatListScreen.showChatList(chatListAdapter);
					return null;
		}
		});
	}
	
	public void showFriendsList(){
		clearScreen();
		quizApp.getDataBaseHelper().getAllUsersByUid(quizApp.getUser().getSubscribedTo(), new DataInputListener<Boolean>(){
			@Override
			public String onData(Boolean s) {
				SelectFriendsScreen friendsScreen = new SelectFriendsScreen(ProfileAndChatController.this);
				ArrayList<User> users = new ArrayList<User>();
				for(String uid: quizApp.getUser().getSubscribedTo()){
					if(quizApp.cachedUsers.containsKey(uid)){
						users.add(quizApp.cachedUsers.get(uid));
					}
				}
				friendsScreen.showFriendsList(UiText.SELECT_FRIENDS_TO_CHALLENGE.getValue(), users ,new DataInputListener<User>(){
					@Override
					public String onData(User  user) {
						// Fetch Profile or something
						return null;
					}
				}, true , true);
				insertScreen(friendsScreen);

				return null;
			}
		});	
		

	}
	
}
