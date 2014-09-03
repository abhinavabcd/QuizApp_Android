package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController;
import com.amcolabs.quizapp.databaseutils.Question;
import com.amcolabs.quizapp.widgets.CustomProgressBar;
import com.amcolabs.quizapp.widgets.CircularCounter;
import com.squareup.picasso.Picasso;

class UserProgressViewHolder{

	public TextView userNameView;
	public ImageView userImageView;
	public CustomProgressBar userProgressView;
	public TextView userScoreView;
}

public class QuestionScreen extends Screen implements View.OnClickListener, AnimationListener{

	private ProgressiveQuizController controller;
	private LinearLayout headerViewWrapper;
	private LinearLayout questionViewWrapper;
	private LinearLayout optionsViewWrapper;
	
	private TextView questionTextView;
	private ImageView questionImageView;
	private ArrayList<Button> questionOptionsViews;
	public HashMap<String , UserProgressViewHolder> userViews = new HashMap<String, UserProgressViewHolder>();
	private LinearLayout fullQuestionLayout;
	private Animation animFadeOut;
	private TextView preQuestionText1;
	private TextView preQuestionText2;
	private TextView preQuestionText3;
	private LinearLayout preQuestionView;
	private LinearLayout questionAndOptionsViewWrapper;
	private CircularCounter timerView;
	private Animation animTextScale;
	
	
	public QuestionScreen(AppController controller) {
		super(controller);
		this.controller = (ProgressiveQuizController)controller;
		LayoutInflater tmp = getApp().getActivity().getLayoutInflater();
		fullQuestionLayout = (LinearLayout) tmp.inflate(R.layout.quiz_full_question, null);
		headerViewWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.quizHeader);
		preQuestionView = (LinearLayout)fullQuestionLayout.findViewById(R.id.question_pre_text);
		questionAndOptionsViewWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.question_options_wrapper);
		questionAndOptionsViewWrapper.setVisibility(View.INVISIBLE);
		questionViewWrapper = (LinearLayout) questionAndOptionsViewWrapper.findViewById(R.id.quizQuestion);
		optionsViewWrapper = (LinearLayout) questionAndOptionsViewWrapper.findViewById(R.id.quizOptions);
		setTimerView((CircularCounter) headerViewWrapper.findViewById(R.id.timerView));
		
		
		
		
		questionTextView = (TextView) questionViewWrapper.findViewById(R.id.questionText);
		questionImageView = (ImageView) questionViewWrapper.findViewById(R.id.questionImage);
		

		questionOptionsViews = new ArrayList<Button>();
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionA));
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionB));
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionC));
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionD));
		for(Button optionView: questionOptionsViews){
			optionView.setOnClickListener(this);
		}
		
		animFadeOut = AnimationUtils.loadAnimation(getApp().getContext(), R.anim.fade_out_animation);
		animFadeOut.setAnimationListener(this);

		animTextScale = AnimationUtils.loadAnimation(getApp().getContext(), R.anim.xp_points_animation);
		preQuestionText1 = (TextView) preQuestionView.findViewById(R.id.textView1);
		preQuestionText2 = (TextView) preQuestionView.findViewById(R.id.textView2);
		preQuestionText3 = (TextView) preQuestionView.findViewById(R.id.textView3);
		
		addView(fullQuestionLayout);
	}
	

	public void showUserInfo(ArrayList<User> uNames) {
		int index = 0;
		for(RelativeLayout userView : Arrays.asList((RelativeLayout) headerViewWrapper.findViewById(R.id.user1), (RelativeLayout) headerViewWrapper.findViewById(R.id.user2))){
			User user = uNames.get(index++);
			UserProgressViewHolder userProgressView = new UserProgressViewHolder();
			
			userProgressView.userNameView = ((TextView)userView.findViewById(R.id.userName));
			userProgressView.userImageView =  (ImageView)userView.findViewById(R.id.userImageSmall);
			
			userProgressView.userProgressView = (CustomProgressBar)userView.findViewById(R.id.userProgress);
			userProgressView.userScoreView = (TextView)userView.findViewById(R.id.userPointsEarned);
			userViews.put(user.uid, userProgressView);
			
			userProgressView.userNameView.setText(user.name);
			Picasso.with(getApp().getContext()).load(user.pictureUrl).into(userProgressView.userImageView);
			userProgressView.userProgressView.setProgress(0);
			userProgressView.userScoreView.setText("+0XP");
		}
	}
	
	public void animateXpPoints(String uid,  int xpPoints){
		userViews.get(uid).userScoreView.setText(xpPoints+" xp");
		userViews.get(uid).userScoreView.setAnimation(animTextScale);
		
	}
	
	private boolean isOptionSelected = true;
	protected Question currentQuestion;
	
	private void showQuestion(final Question ques){
		isOptionSelected = false;
		preQuestionView.setVisibility(View.INVISIBLE);
		questionAndOptionsViewWrapper.setVisibility(View.VISIBLE);
		questionTextView.setText(ques.questionDescription);
		// TODO: should use longoptionflag to change layout
		if (ques.getAssetPaths().size()==0){ //longOptionFlag ||
			optionsViewWrapper.setOrientation(LinearLayout.VERTICAL);
			questionImageView.setVisibility(View.GONE);
		} 
		else{
			optionsViewWrapper.setOrientation(LinearLayout.HORIZONTAL);
			questionImageView.setVisibility(View.VISIBLE);
			getApp().getUiUtils().loadImageIntoView(getApp().getContext(), questionImageView, ques.getAssetPaths().get(0), false);
		}
		String[] mcqOptions = ques.getMCQOptions();
		for(int i=0;i<questionOptionsViews.size();i++){
			Button opt = questionOptionsViews.get(i);
			opt.setText(mcqOptions[i]);
			opt.setTag(ques.isCorrectAnwer(mcqOptions[i]));
			opt.setTextColor(Color.BLACK);
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getTimerView().startTimer(ques.getTime());
			}
		}, 500);
	}
	
	public void animateQuestionChange(String titleInfo1, String titleInfo2, Question ques){
		currentQuestion = ques;
		questionAndOptionsViewWrapper.setVisibility(View.INVISIBLE);
		getTimerView().resetTimer();
		preQuestionText1.setText(titleInfo1);
		preQuestionText2.setText(titleInfo2);
		preQuestionText3.setText(null);
		preQuestionView.startAnimation(animFadeOut);
	}

	private void highlightCorrectAnswer(){
		for(Button b:questionOptionsViews){
			if((Boolean) b.getTag())
				b.setTextColor(Color.GREEN);
		}
	}
	
	@Override
	public void onClick(View optionView) {
		if(isOptionSelected) return;
		isOptionSelected = true;
		double timeElapsed = getTimerView().stopPressed(1);
		Button b = (Button)optionView;
		Boolean isAnwer = (Boolean) b.getTag();
		if(!isAnwer){
			b.setTextColor(Color.RED);
			highlightCorrectAnswer();
		}
		else{
			b.setTextColor(Color.GREEN);
		}
		controller.onOptionSelected(isAnwer,(String) b.getText(), timeElapsed , currentQuestion);
	}

	public CircularCounter getTimerView() {
		return timerView;
	}

	public void setTimerView(CircularCounter timerView) {
		this.timerView = timerView;
		timerView.setFirstWidth(getResources().getDimension(R.dimen.timer_first_width))
		.setFirstColor(getApp().getConfig().getAThemeColor())

		.setSecondWidth(getResources().getDimension(R.dimen.timer_second_width))
		.setSecondColor(getApp().getConfig().getAThemeColor())
	
//		.setThirdWidth(getResources().getDimension(R.dimen.third))
//		.setThirdColor(Color.parseColor(colors[2]))
		
		.setBackgroundColor(-14606047);

		timerView.resetTimer(10);
	}
	
	@Override
	public boolean showOnBackPressed() {
		return false;
	}


	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAnimationEnd(Animation animation) {
		if(animation==animFadeOut){
			showQuestion(currentQuestion);
		}
	}


	@Override
	public void onAnimationRepeat(Animation animation) {

	}
}
