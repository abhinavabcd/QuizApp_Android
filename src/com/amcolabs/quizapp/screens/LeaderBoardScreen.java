package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.adapters.CategoryItemListAdapter;
import com.amcolabs.quizapp.adapters.LeaderboardItemListAdapter;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;

public class LeaderBoardScreen extends Screen {

	public LeaderBoardScreen(AppController controller) {
		super(controller);
	}
	
	public void addLeaderBoards(HashMap<String , Integer[]> uidRankMap, String titleText){
		List<User> users = new ArrayList<User>();
		for(String uid: uidRankMap.keySet()){
			users.add(getApp().cachedUsers.get(uid));
		}
		LeaderboardItemListAdapter categoryAdaptor = new LeaderboardItemListAdapter(getApp(),0, users, uidRankMap ,new DataInputListener<User>(){
			@Override
			public String onData(User s) {
				return null;
			}
		});
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, null);
		lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
		TextView title = (TextView) lView.findViewById(R.id.title_text_view);
		title.setText(titleText);
		lView.findViewById(R.id.search_text).setVisibility(View.GONE);
		FrameLayout viewMore = (FrameLayout) lView.findViewById(R.id.view_all_wrapper);
		viewMore.setVisibility(View.GONE);
		((ListView) lView.findViewById(R.id.listView)).setAdapter(categoryAdaptor);
		addToScrollView(UiUtils.setListViewHeightBasedOnChildren((ListView)lView.findViewById(R.id.listView)));
	}
}
