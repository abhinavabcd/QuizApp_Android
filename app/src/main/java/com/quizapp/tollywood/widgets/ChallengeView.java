package com.quizapp.tollywood.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quizapp.tollywood.QuizApp;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.User;
import com.quizapp.tollywood.datalisteners.DataInputListener;

public class ChallengeView extends LinearLayout implements OnClickListener, IViewType{

	private DataInputListener<Integer> clickListener;
	private TextView textView;

	public ChallengeView(Context context) {
		super(context);
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
		quizApp.getUiUtils().loadImageAsBg(this, bgAssetPath , false);

		
		QuizAppMenuItem menu1 = (QuizAppMenuItem) mainView.findViewById(R.id.start_offline_challenge);
		QuizAppMenuItem menu2 = (QuizAppMenuItem) mainView.findViewById(R.id.exit_challenge_button);
		menu1.setId(R.id.start_offline_challenge);
		menu2.setId(R.id.exit_challenge_button);
		
		menu1.setOnClickListener(this);
		menu2.setOnClickListener(this);
		this.addView(mainView);

		quizApp.getUiUtils().blickAnimation(textView);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.start_offline_challenge:
				clickListener.onData(1);
				break;
			case R.id.exit_challenge_button:
				clickListener.onData(2);
				break;
		}
	}

	@Override
	public ViewType getViewType() {
		return ViewType.CHALLENGE_VIEW;
	}
	
	public void cleanUp(){
		textView.clearAnimation();
	}
}
