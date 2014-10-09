package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.adapters.SelectFriendsListAdapter;
import com.amcolabs.quizapp.appcontrollers.UserMainPageController;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.facebook.widget.LoginButton;
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
	private LoginButton fbButton;
	private List<User> users;
	private LinearLayout debugMessageWrapper;
	private ListView listView;
	private ListView fbFriendsView;
	private ListView gPlusFriendsView;
	private LinearLayout tabsWrapper;
	private ToggleButton gPlusTab;
	private ToggleButton fbTab;
	private ToggleButton allTab;
	private ViewFlipper viewFlipper;
	public boolean doNotShowOnBackPress = false;
	
	public void showFriendsList(String titleText ,List<User> users , DataInputListener<User> onFriendSelectedListener, boolean searchOnServer, boolean enableSocial){
		this.users = users;
		friendsAdapter = new SelectFriendsListAdapter(getApp(),0, users, onFriendSelectedListener,searchOnServer);
		fbFriendsAdapter = new SelectFriendsListAdapter(getApp(),0, users, onFriendSelectedListener,false);
		gPlusFriendsAdapter = new SelectFriendsListAdapter(getApp(),0, users, onFriendSelectedListener,false);
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
		titleView.setText(titleText);
		
		viewFlipper = (ViewFlipper) lView.findViewById(R.id.friends_views);
		listView = (ListView) lView.findViewById(R.id.listView);
		fbFriendsView = (ListView)lView.findViewById(R.id.fb_friends_list);
		gPlusFriendsView = (ListView)lView.findViewById(R.id.gplus_friends_list);
		debugMessageWrapper = (LinearLayout)lView.findViewById(R.id.debug_message_wrapper);
		debugMessage = (GothamTextView)lView.findViewById(R.id.debugMessage);
		gPlusButton = (SignInButton)lView.findViewById(R.id.google_plus_button);
		fbButton = (LoginButton)lView.findViewById(R.id.facebook_button);
		tabsWrapper = (LinearLayout)lView.findViewById(R.id.tabs_wrapper);
		if(enableSocial){
			fbFriendsView.setAdapter(fbFriendsAdapter);
			gPlusFriendsView.setAdapter(gPlusFriendsAdapter);			
			(gPlusTab = (ToggleButton)tabsWrapper.findViewById(R.id.gplus_friends_tab)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateToGooglePlusFriends();
				}
			});
			(fbTab = (ToggleButton)tabsWrapper.findViewById(R.id.fb_friends_tab)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateToFbFriends();
				}
			});
			
			(allTab = (ToggleButton)tabsWrapper.findViewById(R.id.all_friends_tab)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateToAllFriends();
				}
			});			
			gPlusButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					UserMainPageController userMainPageController = (UserMainPageController) getApp().loadAppController(UserMainPageController.class);
					userMainPageController.setLoginListener(new DataInputListener<User>(){
						@Override
						public String onData(User user) {
							getApp().setUser(user);
							navigateToGooglePlusFriends();
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
							navigateToGooglePlusFriends();
							return null;
						}
					});
					userMainPageController.doFbLogin();
				}
			});
			
		}
		else{
			tabsWrapper.setVisibility(View.GONE);
			debugMessageWrapper.setVisibility(View.GONE);
		}
		listView.setAdapter(friendsAdapter);
		if(enableSocial)
			navigateToAllFriends();
		else if(friendsAdapter.getCount()==0){
				debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			debugMessage.setText(UiText.NO_FRIENDS_SEARCH_AND_SUBSCRIBE.getValue());
		}
	//	addListenersToQuizListItem(listView);
	//	setListViewHeightBasedOnChildren(listView);
		
		addToScrollView(lView);
	}
	
	public void navigateToGooglePlusFriends(){
		fbTab.setChecked(false);
		allTab.setChecked(false);
		gPlusTab.setChecked(true);
	//	if(gPlusTab.isChecked()) return;
		viewFlipper.setDisplayedChild(2);
		
		if(getApp().getUser().gPlusUid==null || getApp().getUser().gPlusUid.trim().equalsIgnoreCase("")){
			debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			debugMessage.setText(UiText.CONENCT_WITH_GOOGLE.getValue());
			fbButton.setVisibility(View.GONE);
			gPlusButton.setVisibility(View.VISIBLE);
			return;
		}
		ArrayList<User> friendsConnectedToGoogle = new ArrayList<User>();
		for(User user :users){
			if(user.gPlusUid!=null){
				friendsConnectedToGoogle.add(user);
			}
		}
		gPlusFriendsAdapter.clear();
		gPlusFriendsAdapter.addAll(friendsConnectedToGoogle);
		gPlusFriendsAdapter.notifyDataSetChanged();
		if(gPlusFriendsAdapter.getCount()==0){
			debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			getApp().getUiUtils().setTextViewHTML(debugMessage, UiText.INVITE_YOUR_FRIENDS.getValue(), null);
			gPlusButton.setVisibility(View.GONE);
			fbButton.setVisibility(View.GONE);
		}		
	}
	
	public void navigateToAllFriends(){
		gPlusTab.setChecked(false);
		fbTab.setChecked(false);
		allTab.setChecked(true);
		viewFlipper.setDisplayedChild(0);
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
	public void navigateToFbFriends(){
		gPlusTab.setChecked(false);
		allTab.setChecked(false);
		fbTab.setChecked(true);
		viewFlipper.setDisplayedChild(1);
		if(getApp().getUser().fbUid==null || getApp().getUser().fbUid.trim().equalsIgnoreCase("")){
			debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			debugMessage.setText(UiText.CONENCT_WITH_FACEBOOK.getValue());
			fbButton.setVisibility(View.VISIBLE);
			gPlusButton.setVisibility(View.GONE);
			return;
		}	
		ArrayList<User> friendsConnectedToGoogle = new ArrayList<User>();
		for(User user :users){
			if(user.fbUid!=null){
				friendsConnectedToGoogle.add(user);
			}
		}
		fbFriendsAdapter.clear();
		fbFriendsAdapter.addAll(friendsConnectedToGoogle);
		fbFriendsAdapter.notifyDataSetChanged();
		if(fbFriendsAdapter.getCount()==0){
			debugMessageWrapper.setVisibility(View.VISIBLE);
			debugMessage.setVisibility(View.VISIBLE);
			getApp().getUiUtils().setTextViewHTML(debugMessage, UiText.INVITE_YOUR_FB_FRIENDS.getValue(), null);
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
	
}
