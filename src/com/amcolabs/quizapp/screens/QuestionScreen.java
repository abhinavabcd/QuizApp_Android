package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

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
	
	private LinearLayout headerWrapper;
	private LinearLayout questionWrapper;
	private LinearLayout optionsWrapper;
	
	private TableLayout mainTableView;
	private LinearLayout fullQuestionLayout;
	
	public QuestionScreen(AppController controller) {
		super(controller);
		LayoutInflater tmp = getApp().getActivity().getLayoutInflater();
		fullQuestionLayout = (LinearLayout) tmp.inflate(R.layout.quiz_full_question, null);
		headerWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.quizHeader);
		questionWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.quizQuestion);
		optionsWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.quizOptions);
		
		loadUserInfo();
		addView(fullQuestionLayout);
	}

	public void loadUserInfo() {
		RelativeLayout u1 = (RelativeLayout) headerWrapper.findViewById(R.id.user1);
		((TextView)u1.findViewById(R.id.userName)).setText("user1");
		((ImageView)u1.findViewById(R.id.userImageSmall)).setImageResource(R.drawable.small_logo);
		((CustomProgressBar)u1.findViewById(R.id.userProgress)).setProgress(0);
		((TextView)u1.findViewById(R.id.userPointsEarned)).setText("+0XP");
		
		RelativeLayout u2 = (RelativeLayout) headerWrapper.findViewById(R.id.user2);
		((TextView)u2.findViewById(R.id.userName)).setText("user2");
		((ImageView)u2.findViewById(R.id.userImageSmall)).setImageResource(R.drawable.small_logo);
		((CustomProgressBar)u2.findViewById(R.id.userProgress)).setProgress(0);
		((TextView)u2.findViewById(R.id.userPointsEarned)).setText("+0XP");
	}

	public void loadQuestion(String ques, ArrayList<String> opt, Bitmap img){
		question = ques;
		options = opt;
		image = img;
	}
	
	// Not needed as of now as we are creating new screen each time
	public void resetScreenForNewQuiz(){
		headerWrapper.findViewById(R.id.user1);
	}
	
	public void showNextQuestion(){
		animateQuestionChange();
	}

	private void animateQuestionChange() {
		// TODO Animation
		TextView ques = (TextView) questionWrapper.findViewById(R.id.questionText);
		ques.setText(question);
		ImageView img = (ImageView) questionWrapper.findViewById(R.id.questionImage);
		boolean noImageFlag = false;
		boolean longOptionFlag = false;
		if (image==null){
			img.setVisibility(View.GONE);
//			img.setImageBitmap(null);
			noImageFlag = true;
		}
		else{
			img.setVisibility(View.VISIBLE);
			img.setImageBitmap(image);
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
			optionsWrapper.setOrientation(LinearLayout.VERTICAL);
		}
		else{
			optionsWrapper.setOrientation(LinearLayout.HORIZONTAL);
		}
		
		Button optionA = (Button) optionsWrapper.findViewById(R.id.optionA);
		optionA.setText(options.get(0));
		Button optionB = (Button) optionsWrapper.findViewById(R.id.optionB);
		optionB.setText(options.get(1));
		Button optionC = (Button) optionsWrapper.findViewById(R.id.optionC);
		optionC.setText(options.get(2));
		Button optionD = (Button) optionsWrapper.findViewById(R.id.optionD);
		optionD.setText(options.get(3));
		
		resetOptionsState(optionA,optionB,optionC,optionD);
	}
	
	private void resetOptionsState(Button optionA,Button optionB,Button optionC,Button optionD) {
		optionA.setTextColor(Color.BLACK);
		optionA.setBackgroundResource(R.drawable.quiz_option_background);
		optionB.setTextColor(Color.BLACK);
		optionB.setBackgroundResource(R.drawable.quiz_option_background);
		optionC.setTextColor(Color.BLACK);
		optionC.setBackgroundResource(R.drawable.quiz_option_background);
		optionD.setTextColor(Color.BLACK);
		optionD.setBackgroundResource(R.drawable.quiz_option_background);
	}

	public void onOptionSelected(int id,boolean isCorrect){
		Button opt = null;
		switch(id){
			case 0:
				opt = (Button) optionsWrapper.findViewById(R.id.optionA);			
				break;
			case 1:
				opt = (Button) optionsWrapper.findViewById(R.id.optionB);
				break;
			case 2:
				opt = (Button) optionsWrapper.findViewById(R.id.optionC);
				break;
			case 3:
				opt = (Button) optionsWrapper.findViewById(R.id.optionD);
		}
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
