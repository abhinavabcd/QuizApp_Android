package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
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
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.gameutils.GameUtils;
import com.amcolabs.quizapp.widgets.CircularCounter;
import com.amcolabs.quizapp.widgets.CustomProgressBar;
import com.amcolabs.quizapp.widgets.GothamButtonView;

class UserProgressViewHolder{

	public TextView userNameView;
	public ImageView userImageView;
	public CustomProgressBar userProgressView;
	public TextView userScoreView;
}

public class QuestionScreen extends Screen implements View.OnClickListener, AnimationListener{

	private ProgressiveQuizController pQuizController;
	private LinearLayout headerViewWrapper;
	private LinearLayout questionViewWrapper;
	private LinearLayout optionsViewWrapper;
	
	private TextView questionTextView;
	private ImageView questionImageView;
	private ArrayList<GothamButtonView> questionOptionsViews;
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
	private DataInputListener<Boolean> onQuestionTimeEnd;
	private MediaPlayer possitiveButtonSounds;
	private MediaPlayer negetiveButtonSounds;
	private WakeLock wklock;

	
	
	public QuestionScreen(AppController controller) {
		super(controller);
		
		this.pQuizController = (ProgressiveQuizController)controller;
		LayoutInflater tmp = getApp().getActivity().getLayoutInflater();
		fullQuestionLayout = (LinearLayout) tmp.inflate(R.layout.quiz_full_question, this, false);
		headerViewWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.quizHeader);
		preQuestionView = (LinearLayout)fullQuestionLayout.findViewById(R.id.question_pre_text);
		questionAndOptionsViewWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.question_options_wrapper);
		questionAndOptionsViewWrapper.setVisibility(View.INVISIBLE);
		questionViewWrapper = (LinearLayout) questionAndOptionsViewWrapper.findViewById(R.id.quizQuestion);
		optionsViewWrapper = (LinearLayout) questionAndOptionsViewWrapper.findViewById(R.id.quizOptions);
		setTimerView((CircularCounter) headerViewWrapper.findViewById(R.id.timerView));
		
		
		questionTextView = (TextView) questionViewWrapper.findViewById(R.id.questionText);
		questionImageView = (ImageView) questionViewWrapper.findViewById(R.id.questionImage);
		

		questionOptionsViews = new ArrayList<GothamButtonView>();
		questionOptionsViews.add((GothamButtonView) optionsViewWrapper.findViewById(R.id.optionA));
		questionOptionsViews.add((GothamButtonView) optionsViewWrapper.findViewById(R.id.optionB));
		questionOptionsViews.add((GothamButtonView) optionsViewWrapper.findViewById(R.id.optionC));
		questionOptionsViews.add((GothamButtonView) optionsViewWrapper.findViewById(R.id.optionD));
		for(GothamButtonView optionView: questionOptionsViews){
			optionView.setOnClickListener(this);
		}
		
		animFadeOut = AnimationUtils.loadAnimation(getApp().getContext(), R.anim.fade_out_animation);
		animFadeOut.setAnimationListener(this);

		animTextScale = AnimationUtils.loadAnimation(getApp().getContext(), R.anim.xp_points_animation);
		preQuestionText1 = (TextView) preQuestionView.findViewById(R.id.textView1);
		preQuestionText2 = (TextView) preQuestionView.findViewById(R.id.textView2);
		preQuestionText3 = (TextView) preQuestionView.findViewById(R.id.textView3);
		onQuestionTimeEnd = new DataInputListener<Boolean>(){
			public String onData(Boolean s) {
				isOptionSelected = true;
				return pQuizController.onNoAnswer(currentQuestion);
			};
		};
		timerView.setTimerEndListener(onQuestionTimeEnd);
		
        possitiveButtonSounds = MediaPlayer.create(getApp().getActivity(),R.raw.tap_correct);
        negetiveButtonSounds = MediaPlayer.create(getApp().getActivity(),R.raw.tap_wrong);
        getApp().changeMusic(R.raw.quiz_play);
        
        PowerManager pManager = (PowerManager) getApp().getContext().getSystemService(Context.POWER_SERVICE);
		wklock = pManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE,"quizapp");
		wklock.acquire();
        
        fullQuestionLayout.setKeepScreenOn(true);
        
		addView(fullQuestionLayout);
	}
	
	public void showUserInfo(ArrayList<User> uNames,int maxScore) {
		int index = 0;

		for(User user : uNames){
				if(user.uid.equalsIgnoreCase(getApp().getUser().uid)){
					uNames.remove(user);
					uNames.add(0,user);
					break;
				}
		}
		
		for(RelativeLayout userView : Arrays.asList((RelativeLayout) headerViewWrapper.findViewById(R.id.user1), (RelativeLayout) headerViewWrapper.findViewById(R.id.user2))){
			User user = uNames.get(index++);
			UserProgressViewHolder userProgressView = new UserProgressViewHolder();
			
			userProgressView.userNameView = ((TextView)userView.findViewById(R.id.userName));
			userProgressView.userImageView =  (ImageView)userView.findViewById(R.id.userImageSmall);
			
			userProgressView.userProgressView = (CustomProgressBar)userView.findViewById(R.id.userProgress);
			userProgressView.userScoreView = (TextView)userView.findViewById(R.id.userPointsEarned);
			userViews.put(user.uid, userProgressView);
			
			userProgressView.userNameView.setText(user.getName());
			getApp().getUiUtils().loadImageIntoView(getApp().getContext(), userProgressView.userImageView, user.pictureUrl, false);

			userProgressView.userProgressView.setProgress(0);
			userProgressView.userProgressView.setMax(maxScore);

			userProgressView.userProgressView.setBackgroundResource(R.drawable.fat_progress_bar);//(getApp().getConfig().getAThemeColor());
			userProgressView.userProgressView.getProgressDrawable().setColorFilter(getApp().getConfig().getAThemeColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
			
			userProgressView.userScoreView.setText( (!pQuizController.isChallengeMode() || user.uid.equalsIgnoreCase(getApp().getUser().uid))?"+0 Xp":"?");
		}
	}
	
	public void animateXpPoints(String uid,  int xpPoints){
		userViews.get(uid).userScoreView.setText(xpPoints+" xp");
		animTextScale.reset();
//		TODO: uncomment below line to animate
//		userViews.get(uid).userScoreView.startAnimation(animTextScale);
		
//		userViews.get(uid).userScoreView.refreshDrawableState();
//		userViews.get(uid).userScoreView.invalidate();
	}
	
	public void animateProgressView(final String uid,final int newProgress){
		final CustomProgressBar pbar = userViews.get(uid).userProgressView;
		if(pbar==null)
			return;
		pbar.showAnimatedIncrement(newProgress);
	}
	
	private boolean isOptionSelected = true;
	protected Question currentQuestion;
	private int currentQuestionIndex;
	
	private void showQuestion(final Question ques){
		preQuestionView.setVisibility(View.INVISIBLE);
		questionAndOptionsViewWrapper.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getTimerView().startTimer(ques.getTime(), true);
				isOptionSelected = false;
			}
		}, Config.TIMER_SLIGHT_DELAY_START);
		
	}
	
	private void loadQuestion(final Question ques){
		boolean isImageAvailable = false;
//		preQuestionView.setVisibility(View.INVISIBLE);
//		questionAndOptionsViewWrapper.setVisibility(View.VISIBLE);
		questionTextView.setText(ques.questionDescription);
		
		// TODO: should use longoptionflag to change layout
		if (ques.getPictures().size()==0){ //longOptionFlag ||
			questionImageView.setVisibility(View.GONE);
			optionsViewWrapper.setOrientation(LinearLayout.VERTICAL);
			optionsViewWrapper.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0,1.6f));			
		} 
		else{
			questionImageView.setVisibility(View.VISIBLE);
			optionsViewWrapper.setOrientation(LinearLayout.HORIZONTAL);
			optionsViewWrapper.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0,0.4f));			
			isImageAvailable = getApp().getUiUtils().loadImageIntoView(getApp().getContext(), questionImageView, ques.getPictures().get(0), false);
			if(!isImageAvailable){
				// TODO: Show could not load image or quit quiz
			}
		}
		questionAndOptionsViewWrapper.invalidate();
		String[] mcqOptions = ques.getMCQOptions();
		int tmpIndex = ques.getAnswerIndex();
		if (tmpIndex>3){
			tmpIndex = (int)currentQuestionIndex%4;
			mcqOptions[tmpIndex] = ques.getAnswer();
		}
		
		for(int i=0;i<questionOptionsViews.size();i++){
			GothamButtonView opt = questionOptionsViews.get(i);
			// TODO : get below hard coded values from config
			if (mcqOptions[i].length()>40)
				opt.setTextSize(13);
			else
				opt.setTextSize(15);
			if(GameUtils.isUrl(mcqOptions[i])){
				getApp().getUiUtils().loadImageAsBg(opt , mcqOptions[i],false);
			}
			else{
				opt.setText(mcqOptions[i]);//lets see
			}
			opt.setTag(mcqOptions[i]);
			opt.setTag2(i);
			opt.setTextColor(Color.BLACK);
		}
		getTimerView().resetTimer();//reset timer
	}

	public void animateQuestionChange(String titleInfo1, String titleInfo2, Question ques , int questionIndex) {
		animateQuestionChange(titleInfo1, titleInfo2, null, ques , questionIndex);
	}

	public void animateQuestionChange(String titleInfo1, String titleInfo2, String info3 ,Question ques, int questionIndex){
		currentQuestion = ques;
		questionAndOptionsViewWrapper.setVisibility(View.INVISIBLE);
		getTimerView().resetValues();
		preQuestionText1.setText(titleInfo1);
		preQuestionText2.setText(titleInfo2);
		preQuestionText3.setText(info3);
		animFadeOut.reset();
		preQuestionView.startAnimation(animFadeOut);
		currentQuestionIndex = questionIndex;
	}

	public void highlightCorrectAnswer(){
		for(GothamButtonView b:questionOptionsViews){
			if(currentQuestion.isCorrectAnwer((String)b.getTag(), (Integer)b.getTag2()) )
				b.setTextColor(Color.GREEN); 
		}
	}
	public void highlightOtherUsersOption(String uid , String option){
		for(GothamButtonView b:questionOptionsViews){
			if(((String)b.getTag()).equalsIgnoreCase(option)){ 
				if(!currentQuestion.isCorrectAnwer((String)b.getTag(), (Integer)b.getTag2()))
					b.setTextColor(Color.RED);					
			}
		}
	}
	
	@Override
	public void onClick(View optionView) {
		if(isOptionSelected) return;
		isOptionSelected = true;
		GothamButtonView b = (GothamButtonView)optionView;
		Boolean isAnwer = currentQuestion.isCorrectAnwer((String)b.getTag(), (Integer)b.getTag2());
		if(!isAnwer){
			b.setTextColor(Color.RED);
			negetiveButtonSounds.start();
			highlightCorrectAnswer();
		}
		else{
			possitiveButtonSounds.start();
			b.setTextColor(Color.GREEN);
		}
		pQuizController.onOptionSelected(isAnwer,(String) b.getText() , currentQuestion);
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
	
		.setThirdWidth(getResources().getDimension(R.dimen.timer_third_width))
		.setThirdColor(getApp().getConfig().getAThemeColor())
		
		.setBackgroundColor(-14606047);

		timerView.resetTimer(10);
	}
	
	@Override
	public boolean showOnBackPressed() {
		return false;
	}


	@Override
	public void onAnimationStart(Animation animation) {
		if(animation==animFadeOut){
			loadQuestion(currentQuestion);//but do not show
		}
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
	@Override
	public void beforeRemove() {
		timerView.cleanUp();
		super.beforeRemove();
	}
	
	@Override
	public void onRemovedFromScreen() {
		getApp().changeMusic(R.raw.app_music);		
		wklock.release();
		super.onRemovedFromScreen();
	}
	
	@Override
	public boolean doNotDistrub() {
		// TODO Auto-generated method stub
		return true;
	}
}
