package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Question;
import com.amcolabs.quizapp.widgets.CustomProgressBar;
import com.amcolabs.quizapp.widgets.ImageViewFiltered;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class QuestionScreen extends Screen {

	private String question;
	private ArrayList<String> options;
	private String imagePath;
	private ProgressiveQuizController controller;
	private LinearLayout headerViewWrapper;
	private LinearLayout questionViewWrapper;
	private LinearLayout optionsViewWrapper;
	
	private ArrayList<TextView> userNameViews;
	private ArrayList<ImageView> userImageViews;
	private ArrayList<CustomProgressBar> userProgressViews;
	private ArrayList<TextView> userScoreViews;
	
	private TextView questionTextView;
	private ImageViewFiltered questionImageView;
	private ArrayList<Button> questionOptionsViews;
	
	private LinearLayout fullQuestionLayout;
	
	public QuestionScreen(AppController controller) {
		super(controller);
		this.controller = (ProgressiveQuizController)controller;
		LayoutInflater tmp = getApp().getActivity().getLayoutInflater();
		fullQuestionLayout = (LinearLayout) tmp.inflate(R.layout.quiz_full_question, null);
		headerViewWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.quizHeader);
		questionViewWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.quizQuestion);
		optionsViewWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.quizOptions);
		
		userNameViews = new ArrayList<TextView>();
		userImageViews = new ArrayList<ImageView>();
		userProgressViews = new ArrayList<CustomProgressBar>();
		userScoreViews = new ArrayList<TextView>();
		
		addUserViews((RelativeLayout) headerViewWrapper.findViewById(R.id.user1));
		addUserViews((RelativeLayout) headerViewWrapper.findViewById(R.id.user2));
		
		
		questionTextView = (TextView) questionViewWrapper.findViewById(R.id.questionText);
		questionImageView = (ImageViewFiltered) questionViewWrapper.findViewById(R.id.questionImage);
		

		questionOptionsViews = new ArrayList<Button>();
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionA));
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionB));
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionC));
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionD));
		
		addView(fullQuestionLayout);
	}
	
	public void addUserViews(RelativeLayout user){
		userNameViews.add((TextView)user.findViewById(R.id.userName));
		userImageViews.add((ImageView)user.findViewById(R.id.userImageSmall));
		userProgressViews.add((CustomProgressBar)user.findViewById(R.id.userProgress));
		userScoreViews.add((TextView)user.findViewById(R.id.userPointsEarned));
	}

	public void loadUserInfo(ArrayList<User> uNames) {
		for(int i=0;i<userNameViews.size();i++){
			userNameViews.get(i).setText(uNames.get(i).name);
			userImageViews.get(i).setImageResource(R.drawable.small_logo);
			userProgressViews.get(i).setProgress(0);
			userScoreViews.get(i).setText("+0XP");
		}
	}

	public void loadQuestion(Question ques){
		question = ques.questionDescription;
		options = new ArrayList<String>(Arrays.asList(ques.getMCQOptions()));
		imagePath = ques.pictures.split(",")[0];
	}
	
	public void loadImage(String assetPath,final ImageViewFiltered imgView){
		Picasso.with(getApp().getContext()).load(Config.CDN_IMAGES_PATH+assetPath).into(new Target() {
	        @Override
	        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
	            imgView.setImageBitmap(bitmap);
	        }

			@Override
			public void onBitmapFailed(Drawable arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPrepareLoad(Drawable arg0) {
				// TODO Auto-generated method stub
			}
	    });
	}
	
	// Not needed as of now as we are creating new screen each time
	public void resetScreenForNewQuiz(){
		headerViewWrapper.findViewById(R.id.user1);
	}
	
	public void showNextQuestion(){
		animateQuestionChange();
	}

	private void animateQuestionChange() {
		// TODO Animation
		questionTextView.setText(question);
		boolean noImageFlag = false;
		boolean longOptionFlag = false;
		if (imagePath==null){
			noImageFlag = true;
		}
		else{
			noImageFlag = false;
		}
		if (options == null || options.size()<4)
			throw new IllegalAccessError();
		for(int i=0;i<4;i++){
			if (options.get(i).length()>50){
				longOptionFlag = true;
				break;
			}
		}
		// TODO: should use longoptionflag to change layout
		if (noImageFlag){ //longOptionFlag ||
			optionsViewWrapper.setOrientation(LinearLayout.VERTICAL);
			questionImageView.setVisibility(View.GONE);
		}
		else{
			optionsViewWrapper.setOrientation(LinearLayout.HORIZONTAL);
			questionImageView.setVisibility(View.VISIBLE);
			loadImage(imagePath,questionImageView);
		}
		for(int i=0;i<4;i++){
			Button opt = questionOptionsViews.get(i);
			opt.setText(options.get(0));
			// to reset options
			opt.setTextColor(Color.BLACK);
			opt.setBackgroundResource(R.drawable.quiz_option_background);
		}
	}

	public void onOptionSelected(int id,boolean isCorrect){
		Button opt = questionOptionsViews.get(id);
		if (opt!=null){
			opt.setBackgroundResource(R.drawable.quiz_option_background_checked);
			if(isCorrect){
				opt.setTextColor(Color.GREEN);
			}
			else{
				opt.setTextColor(Color.RED);
			}
		}
	}	
}
