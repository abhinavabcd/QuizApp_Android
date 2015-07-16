package com.quizapp.tollywood.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quizapp.tollywood.QuizApp;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.datalisteners.DataInputListener;
import com.quizapp.tollywood.uiutils.UiUtils.UiText;
import com.squareup.picasso.Picasso;

public class WaitingForUserView extends LinearLayout {
	
	
	ArrayList<ImageView>imageViews = new ArrayList<ImageView>();
	private QuizApp quizApp;
	private Timer timer;
	
	public WaitingForUserView(QuizApp quizApp) {
		super(quizApp.getContext());
		this.quizApp  = quizApp;
		LinearLayout view = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.waiting_screen_2, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,0,1f));
		this.setBackgroundColor(Color.BLACK);
		setGravity(Gravity.CENTER);
		((TextView) view.findViewById(R.id.debug_text_1)).setText(UiText.SEARCHING_FOR_OPPONENT.getValue());
		((TextView) view.findViewById(R.id.debug_text_small)).setText(null);
		addView(view);	
		imageViews.add((ImageView) view.findViewById(R.id.searching_for_opponent_images1));
		imageViews.add((ImageView) view.findViewById(R.id.searching_for_opponent_images2));
		imageViews.add((ImageView) view.findViewById(R.id.searching_for_opponent_images3));
		imageViews.add((ImageView) view.findViewById(R.id.searching_for_opponent_images4));
		startAnimations();
	}
	
	
	
	public void startAnimations(){
		
		timer = quizApp.getUiUtils().setInterval(1000, new DataInputListener<Integer>(){
			Random rand = new Random();
			@Override
			public String onData(Integer s) {
				Picasso.with(quizApp.getContext()).load("file:///android_asset/images/avatars/"+(1+rand.nextInt(6))+".png").into(imageViews.get((s)%imageViews.size()));
				Picasso.with(quizApp.getContext()).load((File)null).into(imageViews.get((s-1)%imageViews.size()));
				
				return super.onData(s);
			}
		});
	}
	
	public void cleanUp(){
		if(timer!=null)
			timer.cancel();
	}
}