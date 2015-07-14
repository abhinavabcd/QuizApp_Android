package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.adapters.SelectFriendsListAdapter;
import com.amcolabs.quizapp.appcontrollers.UserMainPageController;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.google.android.gms.common.SignInButton;

public class SelectFriendsScreen extends Screen {

	public SelectFriendsScreen(AppController controller) {
		super(controller);
	}
	
	SelectFriendsListAdapter friendsAdapter;
	private SelectFriendsListAdapter fbFriendsAdapter;
	private SelectFriendsListAdapter gPlusFriendsAdapter;
	private SignInButton gPlusButton;
	private GothamTextView debugMessage;
	private Button fbButton;
	private List<User> users;
	private LinearLayout debugMessageWrapper;
	private ListView listView;
	private ListView fbFriendsView;
	private ListView gPlusFriendsView;
	public boolean doNotShowOnBackPress = false;
	private TabWidget tabs;
	private TabHost tabsHost;
	private int t_Black;
	private int t_Blacker;
	
	public void showFriendsList(String titleText ,List<User> users , DataInputListener<User> onFriendSelectedListener, boolean searchOnServer, boolean enableSocial){
		this.users = users;
		friendsAdapter = new SelectFriendsListAdapter(getApp(),0, users, onFriendSelectedListener,searchOnServer);
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.friends_social, this, false);		
		lView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
				
		lView.setBackgroundColor(getApp().getConfig().getAThemeColor());
		EditText searchText = (EditText) lView.findViewById(R.id.search_text);
		searchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				friendsAdapter.getFilter().filter(cs);
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
		titleView.setTextSize(12);
		titleView.setText(titleText);
		
		listView = (ListView) lView.findViewById(R.id.listView);
		fbFriendsView = (ListView)lView.findViewById(R.id.fb_friends_list);
		gPlusFriendsView = (ListView)lView.findViewById(R.id.gplus_friends_list);
		debugMessageWrapper = (LinearLayout)lView.findViewById(R.id.debug_message_wrapper);
		debugMessage = (GothamTextView)lView.findViewById(R.id.debugMessage);
		gPlusButton = (SignInButton)lView.findViewById(R.id.google_plus_button);
		fbButton = (Button)lView.findViewById(R.id.facebook_button);
		tabs = (TabWidget)lView.findViewById(android.R.id.tabs);
		tabsHost = (TabHost)lView.findViewById(android.R.id.tabhost);
		
		if(enableSocial){
			// add adapters
			fbFriendsAdapter = new SelectFriendsListAdapter(getApp(),0, getFbUsers(users), onFriendSelectedListener,false);
			gPlusFriendsAdapter = new SelectFriendsListAdapter(getApp(),0, getGPlusUsers(users), onFriendSelectedListener,false);
			
			fbFriendsView.setAdapter(fbFriendsAdapter);
			gPlusFriendsView.setAdapter(gPlusFriendsAdapter);			
			// add tabs here
			tabsHost.setup();
//			tabs.setDividerDrawable(null);
			TabSpec spec;
			// all tab
			spec = tabsHost.newTabSpec(UiText.ALL.getValue())
					 .setIndicator("", getResources().getDrawable(R.drawable.smaller_logo))
					 .setContent(R.id.listView);
			tabsHost.addTab(spec);
			// fb tab
			spec = tabsHost.newTabSpec(UiText.FB.getValue())
					 .setIndicator("", getResources().getDrawable(R.drawable.fb))
					 .setContent(R.id.fb_friends_list);
			tabsHost.addTab(spec);
			// gplus tab
			spec = tabsHost.newTabSpec(UiText.GPLUS.getValue())
					 .setIndicator("", getResources().getDrawable(R.drawable.gplus))
					 .setContent(R.id.gplus_friends_list);
			tabsHost.addTab(spec);
			
			t_Black = getResources().getColor(R.color.translucent_black);
			t_Blacker =  getResources().getColor(R.color.darker_translucent_black);
			for(int i=0;i<tabsHost.getTabWidget().getChildCount();i++){
	              tabsHost.getTabWidget().getChildAt(i).setBackgroundColor(t_Black);
  			}
			OnTabChangeListener tabChangeListner = new OnTabChangeListener() {
				@Override
				public void onTabChanged(String tabId) {
					if(tabId.equalsIgnoreCase(UiText.GPLUS.getValue())){
						refreshToGooglePlusFriends();
					}
					else if(tabId.equalsIgnoreCase(UiText.FB.getValue())){
						refreshFbFriends();
					}
					else{
						refreshAllFriends();
					}
					 for(int i=0;i<tabsHost.getTabWidget().getChildCount();i++){
				              tabsHost.getTabWidget().getChildAt(i).setBackgroundColor(t_Black);
			         }
			         tabsHost.getTabWidget().getChildAt(tabsHost.getCurrentTab()).setBackgroundColor(t_Blacker);
				}
			};
			tabsHost.setOnTabChangedListener(tabChangeListner);
			
			tabChangeListner.onTabChanged(UiText.ALL.getValue());
			gPlusButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					UserMainPageController userMainPageController = (UserMainPageController) getApp().loadAppController(UserMainPageController.class);
					userMainPageController.setLoginListener(new DataInputListener<User>(){
						@Override
						public String onData(User user) {
							getApp().setUser(user);
							refreshToGooglePlusFriends();
							return null;
						}
					});
					userMainPageController.doGplusLogin();
				}
			});
			fbButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					UserMainPageController userMainPageController = (UserMainPageController) getApp().loadAppController(UserMainPageController.class);
					userMainPageController.setLoginListener(new DataInputListener<User>(){
						@Override
						public String onData(User user) {
							getApp().setUser(user);
							refreshFbFriends();
							return null;
						}
					});
					userMainPageController.doFbLogin();
				}
			});
		}
		else{
			debugMessageWrapper.setVisibility(View.GONE);
		}
		listView.setAdapter(friendsAdapter);
		refreshAllFriends();
		if(!enableSocial){
			tabs.setVisibility(View.GONE);
		}
		
		if(friendsAdapter.getCount()==0){
			debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			debugMessage.setText(UiText.NO_FRIENDS_SEARCH_AND_SUBSCRIBE.getValue());
		}
	//	addListenersToQuizListItem(listView);
	//	setListViewHeightBasedOnChildren(listView);
		
		addToScrollView(lView);
	}
	
	private List<User> getGPlusUsers(List<User> users) {
		// TODO Auto-generated method stub
		if(getApp().getUser().gPlusUid==null || getApp().getUser().gPlusUid.equalsIgnoreCase("")){
			return new ArrayList<User>();
		}
		ArrayList<User> friendsConnectedToGoogle = new ArrayList<User>();
		for(User user :users){
			if(user.gPlusUid!=null){
				friendsConnectedToGoogle.add(user);
			}
		}
		return friendsConnectedToGoogle;
	}

	private List<User> getFbUsers(List<User> users2) {
		if(getApp().getUser().fbUid==null || getApp().getUser().fbUid.equalsIgnoreCase("")){
			return new ArrayList<User>();
		}

		ArrayList<User> friendsConnectedToFb = new ArrayList<User>();
		for(User user :users){
			if(user.fbUid!=null){
				friendsConnectedToFb.add(user);
			}
		}
		return friendsConnectedToFb;
	}

	public void refreshToGooglePlusFriends(){
		if(getApp().getUser().gPlusUid==null || getApp().getUser().gPlusUid.trim().equalsIgnoreCase("")){
			debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			debugMessage.setText(UiText.CONENCT_WITH_GOOGLE.getValue());
			fbButton.setVisibility(View.GONE);
			gPlusButton.setVisibility(View.VISIBLE);
			return;
		}
		else{
			debugMessageWrapper.setVisibility(View.GONE);
		}
		// TODO : show a popup
		if(gPlusFriendsAdapter.getCount()==0){
			debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			getApp().getUiUtils().setTextViewHTML(debugMessage, UiText.INVITE_YOUR_FRIENDS.getValue(), new DataInputListener<String>(){
				@Override
				public String onData(String s) {
					getApp().onMenuClick(QuizApp.MENU_SHARE_WITH_FRIENDS);
					return null;
				}
			});
			gPlusButton.setVisibility(View.GONE);
			fbButton.setVisibility(View.GONE);
		}		
	}
	
	public void refreshAllFriends(){
//		gPlusTab.setChecked(false);
//		fbTab.setChecked(false);
//		allTab.setChecked(true);
		if(friendsAdapter.getCount()==0){
			debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			debugMessage.setText(UiText.NO_FRIENDS_SEARCH_AND_SUBSCRIBE.getValue());
			fbButton.setVisibility(View.GONE);
			gPlusButton.setVisibility(View.GONE);
			return;
		}
		else{
			debugMessageWrapper.setVisibility(View.GONE);
		}
	}
	public void refreshFbFriends(){
		if(getApp().getUser().fbUid==null || getApp().getUser().fbUid.trim().equalsIgnoreCase("")){
			debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			debugMessage.setText(UiText.CONENCT_WITH_FACEBOOK.getValue());
			fbButton.setVisibility(View.VISIBLE);
			gPlusButton.setVisibility(View.GONE);
			return;
		}
		else{
			debugMessageWrapper.setVisibility(View.GONE);
		}
		// TODO :add popup to invite for the first time
		if(fbFriendsAdapter.getCount()==0){
			debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			getApp().getUiUtils().setTextViewHTML(debugMessage, UiText.INVITE_YOUR_FB_FRIENDS.getValue(), new DataInputListener<String>(){
				@Override
				public String onData(String s) {
					getApp().onMenuClick(QuizApp.MENU_SHARE_WITH_FRIENDS);
					return null;
				}
			});
			gPlusButton.setVisibility(View.GONE);
			fbButton.setVisibility(View.GONE);
		}				

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
		return !doNotShowOnBackPress;
	}

	public ScreenType getScreenType(){
		return ScreenType.SELECT_FRIENDS_SCREEN;
	}
	
}
