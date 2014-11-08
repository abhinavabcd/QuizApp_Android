package com.amcolabs.quizapp.appcontrollers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.view.View;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.adapters.ChatListAdapter;
import com.amcolabs.quizapp.adapters.GameEventsListItemAdaptor;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.ChatList;
import com.amcolabs.quizapp.databaseutils.GameEvents;
import com.amcolabs.quizapp.databaseutils.LocalQuizHistory;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.databaseutils.UserInboxMessage;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.datalisteners.DataInputListener2;
import com.amcolabs.quizapp.notificationutils.NotificationReciever;
import com.amcolabs.quizapp.notificationutils.NotificationReciever.NotificationPayload;
import com.amcolabs.quizapp.notificationutils.NotificationReciever.NotificationType;
import com.amcolabs.quizapp.screens.ChatScreen;
import com.amcolabs.quizapp.screens.SelectFriendsScreen;
import com.amcolabs.quizapp.screens.UserChatListScreen;
import com.amcolabs.quizapp.screens.UserProfileScreen;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
/**
 * 
 * @author abhinav2
 * takes cares of chatlist , and conversation screen 
 */
public class ProfileAndChatController extends AppController {

	private DataInputListener<NotificationPayload> gcmListener;
	protected DataInputListener<NotificationPayload> oldMessageListener;

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
	
	public void showProfileScreen(final User user){
		clearScreen();
		final UserProfileScreen profileScreen = new UserProfileScreen(this);
		profileScreen.showUser(user);
		if(user.uid.equalsIgnoreCase(quizApp.getUser().uid)){
			
			final List<GameEvents> events = quizApp.getDataBaseHelper().getAllGameEvents(-1);
			quizApp.getDataBaseHelper().getAllUsersByUid(GameEventsListItemAdaptor.getAllUids(events), new DataInputListener<Boolean>(){
				@Override
				public String onData(Boolean s) {
					profileScreen.addEventsListView(events);
					return super.onData(s);
				}
			});
		}
		else{
				final List<LocalQuizHistory> history = quizApp.getDataBaseHelper().getQuizHistoryByUid(user.uid);
				profileScreen.showHistoryWithUser(user , history);
		}
 
		insertScreen(profileScreen);
	}
	
	public void showQuizLocalHistory(final List<LocalQuizHistory> history){
		clearScreen();
		List<String> uidList = new ArrayList<String>();
		for(LocalQuizHistory h : history){
			uidList.add(h.getWithUid());
		}
		quizApp.getDataBaseHelper().getAllUsersByUid(uidList, new DataInputListener<Boolean>(){
			@Override
			public String onData(Boolean s) {
				UserProfileScreen profileScreen = new UserProfileScreen(ProfileAndChatController.this);
				profileScreen.showHistory(history, false);
				insertScreen(profileScreen);
				return super.onData(s);
			}
		});
	}
	
	public void addPrevMessagesToChatScreen(final ChatScreen chatScreen , int toIndex){
		quizApp.getServerCalls().getMessages(chatScreen.otherUser , toIndex , new DataInputListener<List<UserInboxMessage>>(){
			public String onData(List<UserInboxMessage> userMessages) {
				for(UserInboxMessage message : userMessages)
					chatScreen.addMessage(quizApp.getUser().uid.equalsIgnoreCase(message.fromUid), message.timestamp , message.message);
				return null;
			}
		});
	}
	
	public void loadChatScreen(final User user2 , int toIndex){
		if(toIndex==0){
			return;
		}
		clearScreen();
		quizApp.getServerCalls().getMessages(user2 , toIndex , new DataInputListener<List<UserInboxMessage>>(){
			public String onData(List<UserInboxMessage> userMessages) {
				final ChatScreen chatScreen = new ChatScreen(ProfileAndChatController.this, user2);
				oldMessageListener = NotificationReciever.getListener(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE);
				gcmListener = new DataInputListener<NotificationPayload>(){
					public String onData(NotificationPayload payload) {
						if(payload.fromUser.equalsIgnoreCase(user2.uid)){
							 String messageText = payload.textMessage;
							chatScreen.addMessage(false, Config.getCurrentTimeStamp() ,messageText);
							quizApp.getDataBaseHelper().updateChatList(new ChatList(user2.uid,messageText , Config.getCurrentTimeStamp(), 0));
						}
						chatScreen.scrollToBottom();
						return null;
					}
				};//new gcm message from server
				NotificationReciever.setListener(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE, gcmListener);
				if(userMessages.size()==0){
					chatScreen.setDebugMessage(UiText.NO_RECENT_MESSAGES.getValue());
				}
				Collections.sort(userMessages, new Comparator<UserInboxMessage>(){
					@Override
					public int compare(UserInboxMessage lhs, UserInboxMessage rhs) {
						return (int) (lhs.timestamp - rhs.timestamp);
					}
				});
				for(UserInboxMessage message : userMessages){
					chatScreen.addMessage(quizApp.getUser().uid.equalsIgnoreCase(message.fromUid), message.timestamp , message.message);
				}
				chatScreen.scrollToBottom();
				insertScreen(chatScreen);
				return null;
			};
		});
	}
	
	public void sendMessage(final User user2, final String string) {
		quizApp.getServerCalls().sendChatMessage(user2, string, new DataInputListener<Boolean>(){
			@Override
			public String onData(Boolean s) {
				if(s){
					quizApp.getDataBaseHelper().updateChatList(new ChatList(user2.uid,string , Config.getCurrentTimeStamp(), 0));
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
		if(oldMessageListener!=null){
			NotificationReciever.setListener(NotificationType.NOTIFICATION_GCM_INBOX_MESSAGE , oldMessageListener);
		}
	}
	

	public void showChatScreen() {
		clearScreen();
		final List<ChatList> chatList = quizApp.getDataBaseHelper().getAllChatList();
		HashMap<String , Boolean > uidHash = new HashMap<String, Boolean>();
		for(ChatList item : chatList){
			uidHash.put(item.uid , true);
		}
		for(String uid : quizApp.getUser().getSubscribedTo()){
			if(!uidHash.containsKey(uid)){
				uidHash.put(uid, true);
				chatList.add(new ChatList(uid, "", 0 , 0));
			}
		}
		List<String> uidsList = new ArrayList<String>(uidHash.keySet());
		
		quizApp.getDataBaseHelper().getAllUsersByUid(uidsList, new DataInputListener<Boolean>(){ // should run on ui thread
			@Override
			public String onData(Boolean s) {
				
					final ChatListAdapter chatListAdapter = new ChatListAdapter(quizApp,0,chatList, 
					new DataInputListener2<ChatList, User , Void, Void>(){
						@Override
						public void onData(ChatList s , User u , Void v1 , Void v2) {
							ProfileAndChatController.this.loadChatScreen(u, -1);
							s.unseenMessagesFlag = 0;
							quizApp.getDataBaseHelper().updateChatList(s);//remove unseen messages as you are viewing now
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
		quizApp.getDataBaseHelper().getAllUsersByUid(new ArrayList<String>(quizApp.getUser().getSubscribedTo()), new DataInputListener<Boolean>(){
			@Override
			public String onData(Boolean s) {
				SelectFriendsScreen friendsScreen = new SelectFriendsScreen(ProfileAndChatController.this);
				ArrayList<User> users = new ArrayList<User>();
				for(String uid: quizApp.getUser().getSubscribedTo()){
					if(quizApp.cachedUsers.containsKey(uid)){//if exists in db
						users.add(quizApp.cachedUsers.get(uid));
					}
				} 
				friendsScreen.showFriendsList(UiText.YOUR_FRIENDS.getValue(), users ,new DataInputListener<User>(){
					@Override
					public String onData(final User  user) {
						quizApp.getStaticPopupDialogBoxes().showUserSelectedMenu(user, new DataInputListener<Integer>(){
							public String onData(Integer s) {
								switch(s){
								case 1:
									quizApp.getServerCalls().getUserByUid(user.uid, new DataInputListener<User>(){
										public String onData(User s) {
											showProfileScreen(s); // show full profile only
											return null;
										};
									});
									break;
								case 2:
									loadChatScreen(user, -1);
									break;
								case 3:
									((UserMainPageController)quizApp.loadAppController(UserMainPageController.class)).showAllUserQuizzes(UiText.SELECT_TO_CHALLENGE_USER.getValue(user.getName()), new DataInputListener<Quiz>(){
										public String onData(Quiz quiz) {
											((ProgressiveQuizController)quizApp.loadAppController(ProgressiveQuizController.class)).startNewChallenge(user, quiz);
											return null;
										};
									});
									break;
								}
								return null;
							};
						});
						return null;
					}
				}, true , true);
				insertScreen(friendsScreen);

				return null;
			}
		});	
		

	}
}
