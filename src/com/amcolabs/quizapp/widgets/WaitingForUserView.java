package com.amcolabs.quizapp.widgets;

import android.widget.LinearLayout;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;

public class WaitingForUserView extends LinearLayout {

	public WaitingForUserView(QuizApp quizApp) {
		super(quizApp.getContext());
		LinearLayout view = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.waiting_screen_2, null);
		addView(view);	
		startAnimating();

	}

	private void startAnimating() {
		// TODO Auto-generated method stub
		
	}
}