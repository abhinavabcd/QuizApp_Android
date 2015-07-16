package com.quizapp.tollywood.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quizapp.tollywood.AppController;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.Screen;
import com.quizapp.tollywood.User;
import com.quizapp.tollywood.adapters.LeaderboardItemListAdapter;
import com.quizapp.tollywood.datalisteners.DataInputListener;
import com.quizapp.tollywood.uiutils.UiUtils.UiText;
import com.quizapp.tollywood.widgets.ExpandableHeightListView;
import com.quizapp.tollywood.widgets.GothamTextView;

public class LeaderBoardScreen extends Screen {

	private String quizId=null;

	public LeaderBoardScreen(AppController controller) {
		super(controller);
	}
	
	public void addLeaderBoards(final HashMap<String , Integer[]> uidRankMap, String titleText){
		List<User> users = new ArrayList<User>();
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, null);;
		lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
		TextView title = (TextView) lView.findViewById(R.id.title_text_view);
		title.setText(titleText);
		GothamTextView debugMessage = (GothamTextView)lView.findViewById(R.id.debugMessage);
		lView.findViewById(R.id.search_text).setVisibility(View.GONE);
		FrameLayout viewMore = (FrameLayout) lView.findViewById(R.id.view_all_wrapper);
		viewMore.setVisibility(View.GONE);
		ExpandableHeightListView listView = ((ExpandableHeightListView) lView.findViewById(R.id.listView));
		((LayoutParams)listView.getLayoutParams()).setMargins(5, 0, 5, 00);
		listView.setPadding(0, 0, 0, 20);
		ColorDrawable sage = new ColorDrawable(this.getResources().getColor(R.color.translucent_black));
		listView.setDivider(sage);
		listView.setDividerHeight(1);
	
		if(uidRankMap.size()==0){ 
			debugMessage.setText(UiText.NO_RANKINGS_AVAILABLE.getValue());
			debugMessage.setVisibility(View.VISIBLE);
		}else{
			for(String uid: uidRankMap.keySet()){
				users.add(getApp().cachedUsers.get(uid));
			}
			Collections.sort(users, new Comparator<User>() {
				@Override
				public int compare(User lhs, User rhs) {
					return uidRankMap.get(lhs.uid)[0]-uidRankMap.get(rhs.uid)[0] ;
				}
			});
			LeaderboardItemListAdapter leaderBoardAdaptor = new LeaderboardItemListAdapter(getApp(),0, users, uidRankMap ,new DataInputListener<User>(){
				@Override
				public String onData(User s) {
					return null;
				}
			});
			listView.setAdapter(leaderBoardAdaptor);
		}
		addToScrollView(lView);
		listView.setExpanded(true);
	}
	
	@Override
	public boolean showMenu() {
		// TODO Auto-generated method stub
		return true;
	}

	public ScreenType getScreenType(){
		return ScreenType.LEADERBOARD_SCREEN.withData(quizId);
	}

	public void setQuizId(String quizId) {
		this.quizId = quizId;
	}
}
