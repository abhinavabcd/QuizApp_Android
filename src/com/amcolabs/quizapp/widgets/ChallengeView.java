package com.amcolabs.quizapp.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.datalisteners.DataInputListener;

public class ChallengeView extends LinearLayout implements OnClickListener {

	private DataInputListener<Integer> clickListener;

	public ChallengeView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ChallengeView(QuizApp quizApp, User otherUser,
			DataInputListener<Integer> dataInputListener) {
		super(quizApp.getContext());
		this.clickListener = dataInputListener;
		LinearLayout mainView = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.challenge_view, this, false);
		mainView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,0,1f));
		this.setGravity(Gravity.CENTER);
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
}
