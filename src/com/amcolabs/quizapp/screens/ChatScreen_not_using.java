package com.amcolabs.quizapp.screens;

import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;

public class ChatScreen_not_using extends Screen {
	public ListView chatListView;
	public RelativeLayout chatLayout;
	public ChatScreen_not_using(AppController controller) {
		super(controller);
		chatLayout = (RelativeLayout) LayoutInflater.from(controller.getContext()).inflate(R.layout.chat_main, null);
		chatListView = (ListView) chatLayout.findViewById(R.id.chat_list_view);

		loadOlderChats();
		addView(chatLayout);
	}
	
	public void loadOlderChats(){
		
	}
}
