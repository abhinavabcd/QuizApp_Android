package com.amcolabs.quizapp.widgets;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;

public class WaitingForUserView extends LinearLayout {

	public WaitingForUserView(QuizApp quizApp) {
		super(quizApp.getContext());
		LinearLayout view = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.waiting_screen_2, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,0,1f));
		this.setBackgroundColor(Color.BLACK);
		setGravity(Gravity.CENTER);
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), (ImageView) view.findViewById(R.id.searching_for_opponent_images), "preloader.gif",true);
		((TextView) view.findViewById(R.id.debug_text_1)).setText(UiText.SEARCHING_FOR_OPPONENT.getValue());
		((TextView) view.findViewById(R.id.debug_text_small)).setText(null);
		addView(view);	
		startAnimating();
	}

	private void startAnimating() {
		
	}
}