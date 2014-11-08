package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.adapters.CategoryItemListAdapter;
import com.amcolabs.quizapp.adapters.FeedListItemAdaptor;
import com.amcolabs.quizapp.adapters.OfflineChallengesAdapter;
import com.amcolabs.quizapp.adapters.QuizItemListAdapter;
import com.amcolabs.quizapp.appcontrollers.ProfileAndChatController;
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
	List<ArrayAdapter> listViewsAdaptors = new ArrayList<ArrayAdapter>();
	
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
		if(totalXp!=null) // prolly screen is used for other purposes
			totalXp.setText(getApp().getUser().getTotalPoints()+"xp");
		
		for(int i=0;i<listViewsAdaptors.size();i++){
			listViewsAdaptors.get(i).notifyDataSetChanged();;
		}
	}
	
	
	double challengesClickTime = 0;
	private OfflineChallengesAdapter offlineChallengeAdaptor;
	private List<OfflineChallenge> offlineChallenges;
	private List<Quiz> quizzes;
	private TextView totalXp;
	
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
		
		listViewsAdaptors.add(offlineChallengeAdaptor);
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
		GothamTextView debugMessage = (GothamTextView) lView.findViewById(R.id.debugMessage);
		if(feeds.size()==0){
			debugMessage.setVisibility(View.VISIBLE);
			debugMessage.setText(UiText.NO_FEED_AVAILABLE.getValue());
		}
		searchText.setVisibility(View.GONE);//hide search
		GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
		titleView.setText(title);
		ListView listView = (ListView) lView.findViewById(R.id.listView);
		listView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.translucent_black)));
		listView.setDividerHeight(1);

		LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lParams.setMargins(5, 0, 0, 5);
		listView.setLayoutParams(lParams);
		listView.setAdapter(feedAdapter);
		
		listViewsAdaptors.add(feedAdapter);
	//	addListenersToQuizListItem(listView);
		
		
		

		UiUtils.setListViewHeightBasedOnChildren(listView);
		
		FrameLayout viewMore = (FrameLayout) lView.findViewById(R.id.view_all_wrapper);
		viewMore.setVisibility(View.GONE);
		((LayoutParams)lView.getLayoutParams()).weight = 1.0f;
		addToScrollView(lView);
		addShortProfileStats();
	}
	
	
	
	private void addShortProfileStats() {
		 LinearLayout wrapUserStrip;
		 GothamTextView userName;
		 ImageButton viewProfileButton;
		LinearLayout baseLayout = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.user_strip, null);
		baseLayout.setBackgroundColor(getResources().getColor(R.color.black));
		wrapUserStrip = (LinearLayout) baseLayout.findViewById(R.id.wrap_user_strip);
		userName = (GothamTextView) baseLayout.findViewById(R.id.user_name);
		final GothamTextView userStatus = (GothamTextView) baseLayout.findViewById(R.id.status_msg);
		totalXp = (GothamTextView) baseLayout.findViewById(R.id.totalXp);
		viewProfileButton = (ImageButton) baseLayout.findViewById(R.id.view_profile_button);
		
		userName.setText(getApp().getUser().getName());
		totalXp.setText(getApp().getUser().getTotalPoints()+"xp");
		userStatus.setText(getApp().getUser().getStatus());
		OnClickListener statusMessageClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				getApp().getStaticPopupDialogBoxes().promptInput(UiText.SET_STATUS.getValue(), 30 , getApp().getUser().getStatus() , new DataInputListener<String>(){
					@Override
					public String onData(String s) {
						userStatus.setText(s);
						userMainController.updateUserStatus(s);
						return super.onData(s);
					}
				});
			}
		};
		
		userStatus.setOnClickListener(statusMessageClick);
		
		OnClickListener profileClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProfileAndChatController c = (ProfileAndChatController) getApp().loadAppController(ProfileAndChatController.class);
				c.showProfileScreen(getApp().getUser());
			}
		};
		totalXp.setOnClickListener(profileClickListener);
		viewProfileButton.setOnClickListener(profileClickListener);
		userName.setOnClickListener(profileClickListener);
		addToScrollView(baseLayout);
	}


	private DataInputListener<Quiz> quizClickListener = new DataInputListener<Quiz>(){
		@Override
		public String onData(final Quiz quiz) {
			getApp().getStaticPopupDialogBoxes().showQuizSelectMenu(quiz, new DataInputListener<Integer>(){ 
				@Override
				public String onData(Integer s) {
					switch(s){
						case 1: 
							userMainController.onQuizPlaySelected(quiz);
							break;
						case 2:
							userMainController.showQuizHistory(quiz);
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
	};
	
	public DataInputListener<Quiz> getQuizClickListener() {
		return quizClickListener;
	}

	public void setQuizClickListener(DataInputListener<Quiz> quizClickListener) {
		this.quizClickListener = quizClickListener;
	}

	public void addUserQuizzesView(List<Quiz> quizzes, boolean showViewMore , String text) {
		final QuizItemListAdapter quizAdaptor = new QuizItemListAdapter(getApp(),0,quizzes, quizClickListener);
		quizAdaptorList.add(quizAdaptor);
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, this, false);
		lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
		EditText searchText = (EditText) lView.findViewById(R.id.search_text);
		searchText.setVisibility(View.GONE);
		GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
		titleView.setText(text);
		ListView listView = (ListView) lView.findViewById(R.id.listView);
		listView.setAdapter(quizAdaptor);
		
		listViewsAdaptors.add(quizAdaptor);
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
	
	
	
	public void addQuizzesToListFullView(String title ,List<Quiz> quizzes){
		this.quizzes = quizzes;
		final QuizItemListAdapter quizAdaptor = new QuizItemListAdapter(getApp(),0,quizzes, quizClickListener);
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, this, false);
		EditText searchText = (EditText) lView.findViewById(R.id.search_text);
		GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
		titleView.setText(title);
		searchText.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		        quizAdaptor.getFilter().filter(cs);  
		    }
		     
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
		            int arg3) {
		    }

			@Override
			public void afterTextChanged(Editable s) {
			}
	
		});
		((ListView) lView.findViewById(R.id.listView)).setAdapter(quizAdaptor);
		FrameLayout viewMore = (FrameLayout) lView.findViewById(R.id.view_all_wrapper);
		viewMore.setVisibility(View.GONE);
		addView(lView);
	}

	@Override
	public boolean showMenu() {
		// TODO Auto-generated method stub
		return true;
	}
}
