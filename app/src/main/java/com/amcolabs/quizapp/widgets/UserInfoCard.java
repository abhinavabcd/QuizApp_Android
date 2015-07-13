package com.amcolabs.quizapp.widgets;


import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.uiutils.CircleTransform;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class UserInfoCard extends LinearLayout implements IViewType{
	private LinearLayout moreInfoWrapper;
	private User user;
	private GothamTextView debugTextView;

	public UserInfoCard(final QuizApp quizApp, String bgAssetPath, User user) {
		this(quizApp, bgAssetPath, user, false, false, Gravity.CENTER);
		this.setUser(user);
	}

	public UserInfoCard(final QuizApp quizApp, String bgAssetPath, User user, boolean left,boolean smaller , int gravity) {
		super(quizApp.getContext());
		LinearLayout mainView = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.user_info_card, this, false);
		mainView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
		setGravity(gravity);
		if(bgAssetPath==null){
			bgAssetPath = (user.coverUrl!=null && !user.coverUrl.trim().equalsIgnoreCase(""))?user.coverUrl:quizApp.getConfig().getRandomImageBg();
		}
		quizApp.getUiUtils().loadImageAsBg(this, bgAssetPath, false);
		
		GothamTextView name = (GothamTextView)mainView.findViewById(R.id.user_card_name);
		ImageView imgView = (ImageView) mainView.findViewById(R.id.user_card_small_pic);
		GothamTextView statusMsg = (GothamTextView)mainView.findViewById(R.id.user_status_msg);
		statusMsg.setText(user.getStatus());
		moreInfoWrapper = (LinearLayout)mainView.findViewById(R.id.level_more_info);
		debugTextView = (GothamTextView)mainView.findViewById(R.id.user_more_info);
		name.setText(user.getName());
		if(user.pictureUrl!=null){
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), imgView, user.pictureUrl, false, new CircleTransform());
		}
		
		FlowLayout badgesLayout = (FlowLayout)mainView.findViewById(R.id.userbadges);
		
		ArrayList<String>badgeIds = user.getBadges();
		for(String badgeId : badgeIds){
			Badge badge = quizApp.getDataBaseHelper().getBadgeById(badgeId);
			ImageView temp = new ImageView(quizApp.getContext());
			temp.setPadding(0, 0, 10, 0);
			badgesLayout.addView(temp);
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), temp, badge.getAssetPath(), true , quizApp.getUiUtils().dp2px(30),quizApp.getUiUtils().dp2px(30), null);
			temp.setTag(badgeId);
			temp.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					quizApp.getStaticPopupDialogBoxes().showUnlockedBadge((String)v.getTag(), true);
				}
			});
		}

		
		addView(mainView);
	}
	
	public void addLevelIndicator(QuizApp quizApp , float xpPoints){
		UiUtils uiUtils = quizApp.getUiUtils();
		float currentLevelProgress = (float)quizApp.getGameUtils().getLevelFromXp((int)xpPoints);
		CircularCounter levelIndicator = new CircularCounter(quizApp.getContext(), uiUtils.getInSp(10), Color.parseColor("#FFFFFF"), uiUtils.getInSp(7), UiText.LEVEL.getValue(), 
				 uiUtils.getInDp(5), 1, uiUtils.getInDp(3), uiUtils.getInDp(3), 0, 0, 0, 0, uiUtils.getInDp(40),0);		
		levelIndicator.setValues(currentLevelProgress - (int)currentLevelProgress, 1, 0);
		levelIndicator.setCurrentValue((int)currentLevelProgress);		
		moreInfoWrapper.addView(levelIndicator);
	}

	@Override
	public ViewType getViewType() {
		// TODO Auto-generated method stub
		return ViewType.USER_INFO_CARD;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setDebugText(String text) {
		debugTextView.setText(text);
	}
}
