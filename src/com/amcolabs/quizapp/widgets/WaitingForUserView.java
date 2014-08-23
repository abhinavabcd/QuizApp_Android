package com.amcolabs.quizapp.widgets;

import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;

public class WaitingForUserView extends LinearLayout {

	public WaitingForUserView(QuizApp quizApp) {
		super(quizApp.getContext());
		LinearLayout view = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.waiting_screen_2, null);
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,0.5f));
		setGravity(Gravity.CENTER);
		addView(view);	
		startAnimating();

	}

	private void startAnimating() {
		// TODO Auto-generated method stub
		
	}
}