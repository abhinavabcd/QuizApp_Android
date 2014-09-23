package com.amcolabs.quizapp.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class ChallengeView extends LinearLayout implements OnClickListener, Target , IViewType{

	private DataInputListener<Integer> clickListener;
	private TextView textView;

	public ChallengeView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ChallengeView(QuizApp quizApp, User otherUser, String bgAssetPath, DataInputListener<Integer> dataInputListener) {
		super(quizApp.getContext());
		this.clickListener = dataInputListener;
		LinearLayout mainView = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.challenge_view, this, false);
		textView = (TextView) mainView.findViewById(R.id.textView1);
		mainView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,0,1f));
		this.setGravity(Gravity.CENTER);
		
		if(bgAssetPath==null){
			bgAssetPath = (otherUser.coverUrl!=null && !otherUser.coverUrl.trim().equalsIgnoreCase(""))?otherUser.coverUrl:quizApp.getConfig().getRandomImageBg();
		}
		quizApp.getUiUtils().loadImageAsBg(quizApp.getContext(), this, bgAssetPath);

		QuizAppMenuItem menu1 = new QuizAppMenuItem(quizApp.getContext(), R.id.button1, quizApp.getConfig().themeColors[2], 0, UiText.START_CHALLENGE.getValue());
		menu1.setPadding(0, 0 , quizApp.getUiUtils().dp2px(20),0);
		mainView.addView(menu1);
		
		mainView.addView(new QuizAppMenuItem(quizApp.getContext(), R.id.button2, quizApp.getConfig().themeColors[2], 0, UiText.START_CHALLENGE.getValue()));
		this.addView(mainView);
		quizApp.getUiUtils().blickAnimation(textView);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.button1:
				clickListener.onData(1);
				break;
			case R.id.button2:
				clickListener.onData(2);
				break;
		}
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
		return ViewType.CHALLENGE_VIEW;
	}
	
	public void cleanUp(){
		textView.clearAnimation();
	}
}
