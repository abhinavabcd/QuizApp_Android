package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.widgets.CustomProgressBar;

public class QuestionScreen extends Screen {

	private String question;
	private ArrayList<String> options;
	private Bitmap image;
	
	private LinearLayout headerViewWrapper;
	private LinearLayout questionViewWrapper;
	private LinearLayout optionsViewWrapper;
	
	private ArrayList<TextView> userNameViews;
	private ArrayList<ImageView> userImageViews;
	private ArrayList<CustomProgressBar> userProgressViews;
	private ArrayList<TextView> userScoreViews;
	
	private TextView questionTextView;
	private ImageView questionImageView;
	private ArrayList<Button> questionOptionsViews;
	
	private LinearLayout fullQuestionLayout;
	
	public QuestionScreen(AppController controller) {
		super(controller);
		LayoutInflater tmp = getApp().getLayoutInflater();
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
		questionImageView = (ImageView) questionViewWrapper.findViewById(R.id.questionImage);
		
		questionOptionsViews = new ArrayList<Button>();
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionA));
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionB));
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionC));
		questionOptionsViews.add((Button) optionsViewWrapper.findViewById(R.id.optionD));
		
		ArrayList<String> uNames = new ArrayList<String>();
		uNames.add("user1"); uNames.add("user2");
		loadUserInfo(uNames);
		addView(fullQuestionLayout);
	}
	
	public void addUserViews(RelativeLayout user){
		userNameViews.add((TextView)user.findViewById(R.id.userName));
		userImageViews.add((ImageView)user.findViewById(R.id.userImageSmall));
		userProgressViews.add((CustomProgressBar)user.findViewById(R.id.userProgress));
		userScoreViews.add((TextView)user.findViewById(R.id.userPointsEarned));
	}

	public void loadUserInfo(ArrayList<String> uNames) {
		for(int i=0;i<userNameViews.size();i++){
			userNameViews.get(i).setText(uNames.get(i));
			userImageViews.get(i).setImageResource(R.drawable.small_logo);
			userProgressViews.get(i).setProgress(0);
			userScoreViews.get(i).setText("+0XP");
		}
	}

	public void loadQuestion(String ques, ArrayList<String> opt, Bitmap img){
		question = ques;
		options = opt;
		image = img;
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
		if (image==null){
			questionImageView.setVisibility(View.GONE);
//			img.setImageBitmap(null);
			noImageFlag = true;
		}
		else{
			questionImageView.setVisibility(View.VISIBLE);
			questionImageView.setImageBitmap(image);
		}
		if (options == null || options.size()<4)
			throw new IllegalAccessError();
		for(int i=0;i<4;i++){
			if (options.get(i).length()>50){
				longOptionFlag = true;
				break;
			}
		}
		if (longOptionFlag && noImageFlag){
			optionsViewWrapper.setOrientation(LinearLayout.VERTICAL);
		}
		else{
			optionsViewWrapper.setOrientation(LinearLayout.HORIZONTAL);
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
