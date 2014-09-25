package com.amcolabs.quizapp.screens;

import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
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
	
	public void showFriendsList(String titleText ,List<User> users , DataInputListener<User> onFriendSelectedListener, boolean searchOnServer){
		final SelectFriendsListAdapter friendsAdaptor = new SelectFriendsListAdapter(getApp(),0, users, onFriendSelectedListener,searchOnServer);
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, this, false);
		lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
		EditText searchText = (EditText) lView.findViewById(R.id.search_text);
		searchText.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		        friendsAdaptor.getFilter().filter(cs);  
		    }
		     
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
		            int arg3) {
		    }

			@Override
			public void afterTextChanged(Editable s) {
			}
	
		});

		GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
		titleView.setText(titleText);
		ListView listView = (ListView) lView.findViewById(R.id.listView);
		listView.setAdapter(friendsAdaptor);
	//	addListenersToQuizListItem(listView);
	//	setListViewHeightBasedOnChildren(listView);
		
		FrameLayout viewMore = (FrameLayout) lView.findViewById(R.id.view_all_wrapper);
			viewMore.setVisibility(View.GONE);
		addToScrollView(lView);
	}
	
	@Override
	public boolean showMenu() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void beforeRemove() {
		// TODO Auto-generated method stub
		super.beforeRemove();
	}

	@Override
	public boolean showOnBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
