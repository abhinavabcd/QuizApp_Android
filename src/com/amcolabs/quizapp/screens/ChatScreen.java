package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.Random;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProfileAndChatController;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController;
import com.amcolabs.quizapp.chat.ChatViewAdapter;
import com.amcolabs.quizapp.chat.Message;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.squareup.picasso.Picasso;

/**
 * ChatScreen is a screen which show chat log and enables chatting
 * 
 * @author Vinay Ainavolu
 *
 */

public class ChatScreen extends Screen {

	ArrayList<Message> messages;
	ChatViewAdapter adapter;
	ListView chatView;
	EditText text;
	private GothamTextView user1Name;
	private GothamTextView user1Status;
	private GothamTextView user2Name;
	private GothamTextView user2Status;
	private ImageView user1Image;
	private ImageView user2Image;
	private ProfileAndChatController pController;
	private Button sendButton;
	private User otherUser;

	private GothamTextView debugTextView;
	static Random rand = new Random();	
	static String sender;
	
	public ChatScreen(AppController controller , User user2) {
		super(controller);
		this.otherUser = user2;
		this.pController = (ProfileAndChatController) controller;
		View chatLayout = LayoutInflater.from(controller.getContext()).inflate(R.layout.chat_main, null);
		
		text = (EditText) chatLayout.findViewById(R.id.text);

		debugTextView =(GothamTextView) chatLayout.findViewById(R.id.empty);
		
		sendButton = (Button) chatLayout.findViewById(R.id.send_button);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
 				pController.sendMessage(otherUser, text.getText().toString());
			}
		});
		
		
		user1Name = (GothamTextView)chatLayout.findViewById(R.id.user_name);
		user1Status = (GothamTextView)chatLayout.findViewById(R.id.user_status_msg);
		user1Image = (ImageView)chatLayout.findViewById(R.id.user1);
		
		user2Name = (GothamTextView)chatLayout.findViewById(R.id.user_name_2);
		user2Status = (GothamTextView)chatLayout.findViewById(R.id.user_status_msg_2);
		user2Image = (ImageView)chatLayout.findViewById(R.id.user2);
		
		
		
//		this.setTitle(sender);
		messages = new ArrayList<Message>();


		adapter = new ChatViewAdapter(controller.getContext(), messages);
		chatView = (ListView) chatLayout.findViewById(R.id.chat_list_view);
		chatView.setAdapter(adapter);
		chatView.setEmptyView(chatLayout.findViewById(R.id.empty));
//		setListAdapter(adapter);
		addView(chatLayout);
		showUsers(user2, getApp().getUser());
		
	}
	
	public void showUsers(User user2 , User user){
		user1Status.setText(user.status);
		user1Name.setText(user.name);
		if(user.pictureUrl!=null){
			Picasso.with(getApp().getContext()).load(user.pictureUrl).into(user1Image);
		}
		user2Status.setText(user2.status);
		user2Name.setText(user2.name);
		if(user2.pictureUrl!=null){
			Picasso.with(getApp().getContext()).load(user2.pictureUrl).into(user2Image);
		}
	}
	
	@Override
	public void beforeRemove() {
		pController.removeChatListeners();
		super.beforeRemove();
	}
	
	public void addMessage(boolean isCurrentUser , String textData){
		addNewMessage(new Message(textData, -1, isCurrentUser));
	}
		
	void addNewMessage(Message m){
		messages.add(m);
		adapter.notifyDataSetChanged();
	}

	public void setDebugMessage(String value) {
		debugTextView.setText(value);
	}
}