package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.adapters.CategoryItemListAdapter;
import com.amcolabs.quizapp.adapters.OfflineChallengesAdapter;
import com.amcolabs.quizapp.adapters.QuizItemListAdapter;
import com.amcolabs.quizapp.appcontrollers.FeedListItemAdaptor;
import com.amcolabs.quizapp.appcontrollers.UserMainPageController;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.databaseutils.Feed;
import com.amcolabs.quizapp.databaseutils.OfflineChallenge;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.GothamTextView;

public class HomeScreen extends Screen { 
	List<Category> categories = new ArrayList<Category>();
	List<QuizItemListAdapter> quizAdaptorList = new ArrayList<QuizItemListAdapter>();
	List<ListView> listViews = new ArrayList<ListView>();
	
	private UserMainPageController userMainController;
	public HomeScreen(AppController appController) {
		super(appController);
		userMainController = (UserMainPageController)appController;
	} 

	public void addCategoriesView(List<Category> categories, boolean showViewMore) {
		this.categories = categories;
		
		CategoryItemListAdapter categoryAdaptor = new CategoryItemListAdapter(getApp(),0,categories,new DataInputListener<Category>(){
			@Override
			public String onData(Category s) {
				if(isRapidReClick()) return null;
				userMainController.onCategorySelected(s);
				return null;
			}
		});
		
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view,this,false);
		lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
		TextView title = (TextView) lView.findViewById(R.id.title_text_view);
		title.setText(UiText.CATEGORIES.getValue());
		lView.findViewById(R.id.search_text).setVisibility(View.GONE);
		FrameLayout viewMore = (FrameLayout) lView.findViewById(R.id.view_all_wrapper);
		if(!showViewMore){
			viewMore.setVisibility(View.GONE);
		}
		else{
			viewMore.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) { 
					userMainController.showAllCategories();
				}
			});
		}
		((ListView) lView.findViewById(R.id.listView)).setAdapter(categoryAdaptor);
		addToScrollView(lView);
	}

	
//	private void addListenersToQuizListItem2(SwipeMenuListView listView){
//		SwipeMenuCreator creator = new SwipeMenuCreator() {
//			int color1 =getApp().getConfig().getAThemeColor();
//			int color2 = getApp().getConfig().getAThemeColor();
//			@Override
//			public void create(SwipeMenu menu) {
//				// create "open" item
//				SwipeMenuItem openItem = new SwipeMenuItem(
//						getApp().getContext());
//				openItem.setTitle("Play");
//				openItem.setBgColor(color1);
//				menu.addMenuItem(openItem);
//
//				// create "delete" item
//				SwipeMenuItem deleteItem = new SwipeMenuItem(
//						getApp().getContext());
//				// set item background
//				deleteItem.setTitle("Challenge");
//				deleteItem.setBgColor(color2);
//				menu.addMenuItem(deleteItem);
//			}
//		};
//		// set creator
//		listView.setMenuCreator(creator);
//
//		// step 2. listener item click event
//		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//			@Override
//			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
//				switch (index) {
//				case 0:
//					break;
//				case 1:
//					break;
//				}
//			}
//		});
//		
//		// set SwipeListener
//		listView.setOnSwipeListener(new OnSwipeListener() {
//			
//			@Override
//			public void onSwipeStart(int position) {
//				// swipe start
//			}
//			
//			@Override
//			public void onSwipeEnd(int position) {
//				// swipe end
//			}
//		});
//
//	}
	
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		super.refresh();
		if(this.offlineChallengeAdaptor!=null){
			for(int i=0;i<offlineChallengeAdaptor.getCount();i++){
				if(offlineChallengeAdaptor.getItem(i).isCompleted()){
					offlineChallengeAdaptor.remove(offlineChallengeAdaptor.getItem(i));
				}
			}
		}
		for(int i=0;i<listViews.size();i++){
			listViews.get(i).invalidate();
		}
	}
	
	
	double challengesClickTime = 0;
	private OfflineChallengesAdapter offlineChallengeAdaptor;
	private List<OfflineChallenge> offlineChallenges;
	
	public void addOfflineChallengesView(List<OfflineChallenge> offlineChallenges, boolean showViewMore , String text , boolean spanOnFullWidth) {
		this.offlineChallenges = offlineChallenges;
		for(OfflineChallenge offlineChallenge : offlineChallenges){
			if(offlineChallenge.isCompleted()){
				offlineChallenges.remove(offlineChallenge);
			}
		}
		this.offlineChallengeAdaptor = new OfflineChallengesAdapter(getApp(),0,offlineChallenges, new DataInputListener<OfflineChallenge>(){
			@Override
			public String onData(final OfflineChallenge offlineChallenge){
				if(Config.getCurrentNanos()-challengesClickTime < 1000000000.0){ // 10^9 nano sec
					return null;
				}
				challengesClickTime = Config.getCurrentNanos();
				userMainController.startNewOfflineChallenge(offlineChallenge);
				return null; 
			}
		});
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, this, false);
		lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
		EditText searchText = (EditText) lView.findViewById(R.id.search_text);
		searchText.setVisibility(View.GONE);
		GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
		titleView.setText(text);
		ListView listView = (ListView) lView.findViewById(R.id.listView);
		listView.setAdapter(offlineChallengeAdaptor);
		
		listViews.add(listView);
	//	addListenersToQuizListItem(listView);
		
		
		addToScrollView(lView);
		if(spanOnFullWidth)
			UiUtils.setListViewHeightBasedOnChildren(listView);
		
		FrameLayout viewMore = (FrameLayout) lView.findViewById(R.id.view_all_wrapper);
		if(!showViewMore){
			viewMore.setVisibility(View.GONE);
		}
		else{
			viewMore.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					userMainController.showAllOfflineChallenges();
				}
			});
		}
	}
		
	public void addFeedView(List<Feed> feeds, String title){
		final FeedListItemAdaptor feedAdapter = new FeedListItemAdaptor(getApp(), 0, feeds);
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, this, false);
		lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
		EditText searchText = (EditText) lView.findViewById(R.id.search_text);
		searchText.setVisibility(View.GONE);//hide search
		GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
		titleView.setText(title);
		ListView listView = (ListView) lView.findViewById(R.id.listView);
		listView.setAdapter(feedAdapter);
		
		listViews.add(listView);
	//	addListenersToQuizListItem(listView);
		
		
		

		UiUtils.setListViewHeightBasedOnChildren(listView);
		
		FrameLayout viewMore = (FrameLayout) lView.findViewById(R.id.view_all_wrapper);
		viewMore.setVisibility(View.GONE);
		addToScrollView(lView);

	}
	
	
	public void addUserQuizzesView(List<Quiz> quizzes, boolean showViewMore , String text) {
		final QuizItemListAdapter quizAdaptor = new QuizItemListAdapter(getApp(),0,quizzes, new DataInputListener<Quiz>(){
			@Override
			public String onData(final Quiz quiz) {
				getApp().getStaticPopupDialogBoxes().showQuizSelectMenu(new DataInputListener<Integer>(){
					@Override
					public String onData(Integer s) {
						switch(s){
							case 1: 
								userMainController.onQuizPlaySelected(quiz);
								break;
							case 2:
								break;
							case 3://challenge
								userMainController.onStartChallengeQuiz(quiz);
								break;
							case 4://scoreboard
								userMainController.showLeaderBoards(quiz.quizId);
								break;
						}
						return super.onData(s);
					}
				});
				return null;
			}
		});
		quizAdaptorList.add(quizAdaptor);
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, this, false);
		lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
		EditText searchText = (EditText) lView.findViewById(R.id.search_text);
		searchText.setVisibility(View.GONE);
		GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
		titleView.setText(text);
		ListView listView = (ListView) lView.findViewById(R.id.listView);
		listView.setAdapter(quizAdaptor);
		
		listViews.add(listView);
	//	addListenersToQuizListItem(listView);
		
		
		

		UiUtils.setListViewHeightBasedOnChildren(listView);
		
		FrameLayout viewMore = (FrameLayout) lView.findViewById(R.id.view_all_wrapper);
		if(!showViewMore){
			viewMore.setVisibility(View.GONE);
		}
		else{
			viewMore.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					userMainController.showAllUserQuizzes();
				}
			});
		}
		addToScrollView(lView);
	}
	
	@Override
	public boolean showMenu() {
		// TODO Auto-generated method stub
		return true;
	}
}
