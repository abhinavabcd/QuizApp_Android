package com.amcolabs.quizapp.widgets;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class UserInfoCard extends LinearLayout implements Target,IViewType{

	public UserInfoCard(final QuizApp quizApp, String bgAssetPath, User user) {
		super(quizApp.getContext());
		LinearLayout mainView = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.user_info_card, null);
		mainView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,0,1f));
		setGravity(Gravity.CENTER);
		if(bgAssetPath==null){
			bgAssetPath = (user.coverUrl!=null && !user.coverUrl.trim().equalsIgnoreCase(""))?user.coverUrl:getRandomImageBg();
		}
		quizApp.getUiUtils().loadImageAsBg(quizApp.getContext(), this, bgAssetPath);
		
		
		GothamTextView name = (GothamTextView)mainView.findViewById(R.id.user_card_name);
		ImageView imgView = (ImageView) mainView.findViewById(R.id.user_card_small_pic);
		GothamTextView statusMsg = (GothamTextView)mainView.findViewById(R.id.user_status_msg);
		statusMsg.setText(user.status);
		
		
		name.setText(user.name);
		if(user.pictureUrl!=null){
			Picasso.with(quizApp.getContext()).load(user.pictureUrl).into(imgView);
		}
		
		addView(mainView);
	}

	public String getRandomImageBg(){
		return "images/bg_2.jpg";
	}
	
	@Override
	public void onBitmapFailed(Drawable arg0) {
		
	}

	@Override
	public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
		this.setBackgroundDrawable(new BitmapDrawable(bitmap));	
	}

	@Override
	public void onPrepareLoad(Drawable arg0) {
		
	}

	@Override
	public ViewType getViewType() {
		// TODO Auto-generated method stub
		return ViewType.USER_INFO_CARD;
	}
}
