package com.amcolabs.quizapp.adapters;

import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProfileAndChatController;
import com.amcolabs.quizapp.databaseutils.ChatList;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.datalisteners.DataInputListener2;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.GothamTextView;

public class UserChatListScreen extends Screen{

	private ProfileAndChatController pController;

	public UserChatListScreen(AppController controller) {
		super(controller);
		this.pController = (ProfileAndChatController)controller;
		
	}
	
	public void showChatList(final ChatListAdapter chatListAdapter){
					LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, null);
					EditText searchText = (EditText) lView.findViewById(R.id.search_text);
					GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
					titleView.setText(UiText.PREVIOUS_CHATS_USERS.getValue());
					searchText.addTextChangedListener(new TextWatcher() {
					    @Override
					    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
					        chatListAdapter.getFilter().filter(cs);  
					    }
					     
					    @Override
					    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					            int arg3) {
					    }
			
						@Override
						public void afterTextChanged(Editable s) {
						}
				
					});
					((ListView) lView.findViewById(R.id.listView)).setAdapter(chatListAdapter);
					addToScrollView(lView);
	}
}
