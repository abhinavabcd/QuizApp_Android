package com.quizapp.tollywood.screens;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.quizapp.tollywood.AppController;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.Screen;
import com.quizapp.tollywood.adapters.ChatListAdapter;
import com.quizapp.tollywood.appcontrollers.ProfileAndChatController;
import com.quizapp.tollywood.uiutils.UiUtils.UiText;
import com.quizapp.tollywood.widgets.GothamTextView;

public class UserChatListScreen extends Screen{

	private ProfileAndChatController pController;
	public GothamTextView debugMessageView;
	FrameLayout chatListWrapper = null;
	public UserChatListScreen(AppController controller) {
		super(controller);
		this.pController = (ProfileAndChatController)controller;
		chatListWrapper = (FrameLayout)getApp().getActivity().getLayoutInflater().inflate(R.layout.chat_list_layout, null);
		chatListWrapper.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		debugMessageView = (GothamTextView)chatListWrapper.findViewById(R.id.debugMessage);
		
	}
	
	public void showChatList(final ChatListAdapter chatListAdapter){
					LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, null);
					lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
					final EditText searchText = (EditText) lView.findViewById(R.id.search_text);
					GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
					GothamTextView viewMore = (GothamTextView) lView.findViewById(R.id.view_more);
					viewMore.setVisibility(View.GONE);
					
					titleView.setText(UiText.PREVIOUS_CHATS_USERS.getValue());
					searchText.addTextChangedListener(new TextWatcher() {
					    @Override
					    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
					        chatListAdapter.getFilter().filter(searchText.getText());
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
					chatListWrapper.addView(lView, 0);
					addToScrollView(chatListWrapper);
	}
	@Override
	public boolean showMenu() {
		// TODO Auto-generated method stub
		return true;
	}

	public ScreenType getScreenType(){
		return ScreenType.CHAT_LIST_SCREEN;
	}
}
