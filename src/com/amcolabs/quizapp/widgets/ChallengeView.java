package com.amcolabs.quizapp.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.widgets.IViewType.ViewType;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class ChallengeView extends LinearLayout implements OnClickListener, Target , IViewType{

	private DataInputListener<Integer> clickListener;

	public ChallengeView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ChallengeView(QuizApp quizApp, User otherUser, String bgAssetPath, DataInputListener<Integer> dataInputListener) {
		super(quizApp.getContext());
		this.clickListener = dataInputListener;
		LinearLayout mainView = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.challenge_view, this, false);
		mainView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,0,1f));
		this.setGravity(Gravity.CENTER);
		
		if(bgAssetPath==null){
			bgAssetPath = (otherUser.coverUrl!=null && !otherUser.coverUrl.trim().equalsIgnoreCase(""))?otherUser.coverUrl:quizApp.getConfig().getRandomImageBg();
		}
		quizApp.getUiUtils().loadImageAsBg(quizApp.getContext(), this, bgAssetPath);

		mainView.findViewById(R.id.button1).setOnClickListener(this);
		this.addView(mainView);
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
}
