package com.amcolabs.quizapp.screens;

import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.adapters.QuizItemListAdapter;
import com.amcolabs.quizapp.adapters.SelectFriendsListAdapter;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.widgets.GothamTextView;

public class SelectFriendsScreen extends Screen {

	public SelectFriendsScreen(AppController controller) {
		super(controller);
	}
	
	public void showFriendsList(String titleText ,List<User> users , DataInputListener<User> onFriendSelectedListener){
		SelectFriendsListAdapter quizAdaptor = new SelectFriendsListAdapter(getApp(),0, users, onFriendSelectedListener);
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, this, false);
		lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
		EditText searchText = (EditText) lView.findViewById(R.id.search_text);
		searchText.setVisibility(View.GONE);
		GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
		titleView.setText(titleText);
		ListView listView = (ListView) lView.findViewById(R.id.listView);
		listView.setAdapter(quizAdaptor);
	//	addListenersToQuizListItem(listView);
	//	setListViewHeightBasedOnChildren(listView);
		
		FrameLayout viewMore = (FrameLayout) lView.findViewById(R.id.view_all_wrapper);
			viewMore.setVisibility(View.GONE);
		addToScrollView(lView);
	}
}
