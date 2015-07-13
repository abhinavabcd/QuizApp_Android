package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController.UserAnswer;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.LocalQuizHistory;
import com.amcolabs.quizapp.databaseutils.Question;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.gameutils.GameUtils;
import com.amcolabs.quizapp.uiutils.CircleTransform;
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
	
	private RelativeLayout questionHintViewWrapper;
	private TextView hintTextView;
	private GothamButtonView hintButtonView;
	
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
//	private WakeLock wklock;
	private boolean isLoadingForBitmaps;

	
	
	public QuestionScreen(AppController controller) {
		super(controller);
		this.pQuizController = (ProgressiveQuizController)controller;
		initUi(true);
	}
	
	public QuestionScreen(AppController controller, boolean onlyForBitmapExtraction) {
		super(controller);
		initUi(false);
	}
	
	
	private void initUi(boolean liveGame){
		LayoutInflater tmp = getApp().getActivity().getLayoutInflater();
		fullQuestionLayout = (LinearLayout) tmp.inflate(R.layout.quiz_full_question, this, false);
		headerViewWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.quizHeader);
		preQuestionView = (LinearLayout)fullQuestionLayout.findViewById(R.id.question_pre_text);
		questionAndOptionsViewWrapper = (LinearLayout) fullQuestionLayout.findViewById(R.id.question_options_wrapper);
		questionAndOptionsViewWrapper.setVisibility(View.INVISIBLE);
		questionViewWrapper = (LinearLayout) questionAndOptionsViewWrapper.findViewById(R.id.quizQuestion);
		questionHintViewWrapper = (RelativeLayout) questionAndOptionsViewWrapper.findViewById(R.id.hint_wrapper);
		optionsViewWrapper = (LinearLayout) questionAndOptionsViewWrapper.findViewById(R.id.quizOptions);
		questionTextView = (TextView) questionViewWrapper.findViewById(R.id.questionText);
		questionImageView = (ImageView) questionViewWrapper.findViewById(R.id.questionImage);
		
//		hintButtonView = (GothamButtonView) questionHintViewWrapper.findViewById(R.id.hint_button);
//		hintTextView = (TextView) questionHintViewWrapper.findViewById(R.id.hint_text_view);
//		hintButtonView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				v.setVisibility(View.GONE); // Hide button and show hint
//				hintTextView.setVisibility(View.VISIBLE);
//			}
//		});

		questionOptionsViews = new ArrayList<GothamButtonView>();
		questionOptionsViews.add((GothamButtonView) optionsViewWrapper.findViewById(R.id.optionA));
		questionOptionsViews.add((GothamButtonView) optionsViewWrapper.findViewById(R.id.optionB));
		questionOptionsViews.add((GothamButtonView) optionsViewWrapper.findViewById(R.id.optionC));
		questionOptionsViews.add((GothamButtonView) optionsViewWrapper.findViewById(R.id.optionD));
		for(GothamButtonView optionView: questionOptionsViews){
			optionView.setOnClickListener(this);
		}
		
		preQuestionText1 = (TextView) preQuestionView.findViewById(R.id.textView1);
		preQuestionText2 = (TextView) preQuestionView.findViewById(R.id.textView2);
		preQuestionText3 = (TextView) preQuestionView.findViewById(R.id.textView3);

		
		
		setTimerView((CircularCounter) headerViewWrapper.findViewById(R.id.timerView));

		if(liveGame){
			animFadeOut = AnimationUtils.loadAnimation(getApp().getContext(), R.anim.fade_out_animation);
			animFadeOut.setAnimationListener(this);

			animTextScale = AnimationUtils.loadAnimation(getApp().getContext(), R.anim.xp_points_animation);
			onQuestionTimeEnd = new DataInputListener<Boolean>(){
				public String onData(Boolean s) {
					synchronized (isOptionSelected) {
						if (!isOptionSelected) {
							isOptionSelected = true;
							pQuizController.onNoAnswer(currentQuestion);
						}
					}
					return  null;
				}
			};
			timerView.setTimerEndListener(onQuestionTimeEnd);
			
	        possitiveButtonSounds = MediaPlayer.create(getApp().getActivity(),R.raw.tap_correct);
	        negetiveButtonSounds = MediaPlayer.create(getApp().getActivity(),R.raw.tap_wrong);
	        getApp().changeMusic(R.raw.quiz_play);
//        PowerManager pManager = (PowerManager) getApp().getContext().getSystemService(Context.POWER_SERVICE);
//		wklock = pManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE,"quizapp");
//		wklock.acquire();
//        
	        fullQuestionLayout.setKeepScreenOn(true);
		}

		addView(fullQuestionLayout);

	}
	
	public static List<Bitmap> getBitmapOfQuestions(AppController controller ,List<Question> questions , String uid , ArrayList<User> users, int maxScore , List<UserAnswer> answers1  , List<UserAnswer> answers2){
		// load question
		// load the users bar , 
		// load the timers for each question
		// show highlights of answers 
		// for each get the bitmaps
		// add Ab view horozontal scroll
		//		// on click on the bitmap , open a dialog , with little lesser height and the close button with the image on it ? 
		List<Bitmap> ret = new ArrayList<Bitmap>();
		int questionIndex = 0;
		QuestionScreen questionScreen = new QuestionScreen(controller, true);
		questionScreen.isLoadingForBitmaps  = true;
		for(Question question : questions){
			questionScreen.showUserInfo(users,maxScore); //load user info
			questionScreen.loadQuestion(question, questionIndex++);
			questionScreen.questionAndOptionsViewWrapper.setVisibility(View.VISIBLE);//show it
			((FrameLayout.LayoutParams)questionScreen.questionAndOptionsViewWrapper.getLayoutParams()).width = 700;
			//TODO: Currently only for two users only
			UserAnswer userAnswer1  = null;
			for(UserAnswer a : answers1){
				if(a.questionId.equalsIgnoreCase(question.questionId)){
					userAnswer1 = a;
					break;
				}
			}
			
			UserAnswer userAnswer2  = null;
			for(UserAnswer a : answers2){
				if(a.questionId.equalsIgnoreCase(question.questionId)){
					userAnswer2 = a;
					break;
				}
			}
			questionScreen.getTimerView().setValues(userAnswer1.elapsedTime, userAnswer2.elapsedTime, 0);
			 
			questionScreen.highlightCorrectAnswer();
			questionScreen.highlightOtherUsersOption(userAnswer1.uid, userAnswer1.userAnswer);
			questionScreen.highlightOtherUsersOption(userAnswer2.uid, userAnswer2.userAnswer);
			
			questionScreen.userViews.get(userAnswer1.uid).userProgressView.setProgress(userAnswer1.whatUserGot);
			questionScreen.userViews.get(userAnswer2.uid).userProgressView.setProgress(userAnswer2.whatUserGot);
			
			questionScreen.userViews.get(userAnswer1.uid).userScoreView.setText(userAnswer1.whatUserGot+" xp");
			questionScreen.userViews.get(userAnswer2.uid).userScoreView.setText(userAnswer2.whatUserGot+" xp");

			questionScreen.getTimerView().attachToWindow(true);
			Bitmap viewBitmap = getScreenViewBitmap(questionScreen);
			if(viewBitmap!=null)
				ret.add(viewBitmap);
			questionScreen.getTimerView().dettachToWindow(true);
		}
			
		return ret;
	}
	
	public static Bitmap getScreenViewBitmap(final View v) {
	    v.setDrawingCacheEnabled(true);

	    v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
	            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	    v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

	    v.buildDrawingCache(true);
	    Bitmap g = v.getDrawingCache();
	    Bitmap b;
	    try{
		    b = Bitmap.createScaledBitmap(g, g.getWidth()/2,g.getHeight()/2, false);
	    }
	    catch(Exception e){
	    	b=null;
	    }
	    v.setDrawingCacheEnabled(false); // clear drawing cache

	    return b;
	}
	
	
	public static List<Bitmap> getBitmapOfQuestions(AppController controller ,LocalQuizHistory quizHistory){
		return getBitmapOfQuestions(
					controller,
					quizHistory.getQuestions(),
					controller.quizApp.getUser().uid,
					quizHistory.getUsers(),
					quizHistory.maxScore,
					quizHistory.getUserAnswers1(controller.quizApp),
					quizHistory.getUserAnswers2(controller.quizApp)
				);
	}
	
	
	
	public void showUserInfo(List<User> users,int maxScore) {
		int index = 0;

		for(User user : users){
				if(user.uid.equalsIgnoreCase(getApp().getUser().uid)){
					users.remove(user);
					users.add(0,user);
					break;
				}
		}
		
		for(RelativeLayout userView : Arrays.asList((RelativeLayout) headerViewWrapper.findViewById(R.id.user1), (RelativeLayout) headerViewWrapper.findViewById(R.id.user2))){
			User user = users.get(index++);
			UserProgressViewHolder userProgressView = new UserProgressViewHolder();
			
			userProgressView.userNameView = ((TextView)userView.findViewById(R.id.userName));
			userProgressView.userImageView =  (ImageView)userView.findViewById(R.id.userImageSmall);
			
			userProgressView.userProgressView = (CustomProgressBar)userView.findViewById(R.id.userProgress);
			userProgressView.userScoreView = (TextView)userView.findViewById(R.id.userPointsEarned);
			userViews.put(user.uid, userProgressView);
			
			userProgressView.userNameView.setText(user.getName());
			if(!isLoadingForBitmaps){
				getApp().getUiUtils().loadImageIntoView(getApp().getContext(), userProgressView.userImageView, user.pictureUrl, false, new CircleTransform());
			}
			else
				getApp().getUiUtils().loadImageIntoViewDoInBackground(getApp().getContext(), userProgressView.userImageView, user.pictureUrl, false);
				
			userProgressView.userProgressView.setProgress(0);
			userProgressView.userProgressView.setMax(maxScore);

			userProgressView.userProgressView.setBackgroundResource(R.drawable.fat_progress_bar);//(getApp().getConfig().getAThemeColor());
			userProgressView.userProgressView.getProgressDrawable().setColorFilter(getApp().getConfig().getAThemeColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
			
			userProgressView.userScoreView.setText( (!(pQuizController!=null && pQuizController.isChallengeMode()) || user.uid.equalsIgnoreCase(getApp().getUser().uid))?"+0 Xp":"?");
		}
	}
	public void animateXpPoints(String uid,  String xpPointsStr){
		userViews.get(uid).userScoreView.setText(xpPointsStr);
		animTextScale.reset();
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
	
	private Boolean isOptionSelected = true;
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
	
	private void loadQuestion(final Question ques, int questionIndex){
		boolean isImageAvailable = false;
		if(currentQuestion!=ques)// unnecessary check , but just in case , when you want to set current question differently
			currentQuestion = ques;
		if(questionIndex!=currentQuestionIndex)
			currentQuestionIndex = questionIndex;
		
//		preQuestionView.setVisibility(View.INVISIBLE);
//		questionAndOptionsViewWrapper.setVisibility(View.VISIBLE);
//		questionTextView.setText(ques.questionDescription);
		getApp().getUiUtils().setTextViewHTML(questionTextView, ques.questionDescription, null);
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
			if(!isLoadingForBitmaps){
				isImageAvailable = getApp().getUiUtils().loadImageIntoView(getApp().getContext(), questionImageView, ques.getPictures().get(0), false).getResult()!=null;
			}
			else
				isImageAvailable = getApp().getUiUtils().loadImageIntoViewDoInBackground(getApp().getContext(), questionImageView, ques.getPictures().get(0), false);
				
			if(!isImageAvailable){
				// TODO: Show could not load image or quit quiz
			}
		}
		
//		hintButtonView.setVisibility(View.VISIBLE); // Hide button and show hint
//		hintTextView.setVisibility(View.GONE);
//		if (ques.hint!=null && !ques.hint.equalsIgnoreCase("")){
//			hintTextView.setText(ques.hint);
//		}
		
		List<String> mcqOptions = ques.getMCQOptions();
		int tmpIndex = ques.getAnswerIndex();
		if (tmpIndex>3){
			tmpIndex = (int)currentQuestionIndex%4;
			mcqOptions.set(tmpIndex , ques.getAnswer());
		}
		for(int i=0;i<questionOptionsViews.size();i++){
			GothamButtonView opt = questionOptionsViews.get(i);
			// TODO : get below hard coded values from config
			if (mcqOptions.get(i).length()>40)
				opt.setTextSize(13);
			else
				opt.setTextSize(15);
			if(GameUtils.isUrl(mcqOptions.get(i))){
				getApp().getUiUtils().loadImageAsBg(opt , mcqOptions.get(i),false);
			}
			else{
				opt.setText(mcqOptions.get(i));//lets see
			}
			opt.setTag(mcqOptions.get(i));
			opt.setTag2(i);
			opt.setTextColor(Color.BLACK);
		}
		questionAndOptionsViewWrapper.invalidate();
		getTimerView().resetTimer((float) ques.time);//reset timer
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
			if(currentQuestion.isCorrectAnwer((String)b.getTag()) )
				b.setTextColor(Color.GREEN); 
		}
	}
	public void highlightOtherUsersOption(String uid , String option){
		for(GothamButtonView b:questionOptionsViews){
			if(((String)b.getTag()).equalsIgnoreCase(option)){ 
				if(!currentQuestion.isCorrectAnwer((String)b.getTag()))
					b.setTextColor(Color.RED);					
			}
		}
	}
	
	@Override
	public void onClick(View optionView) {
		synchronized (isOptionSelected) {
			if (isOptionSelected) return;
			isOptionSelected = true;
			GothamButtonView b = (GothamButtonView) optionView;
			Boolean isAnwer = currentQuestion.isCorrectAnwer((String) b.getTag());
			if (!isAnwer) {
				b.setTextColor(Color.RED);
				negetiveButtonSounds.start();
				highlightCorrectAnswer();
			} else {
				possitiveButtonSounds.start();
				b.setTextColor(Color.GREEN);
			}
			pQuizController.onOptionSelected(isAnwer, (String) b.getText(), currentQuestion);
		}
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
			loadQuestion(currentQuestion, currentQuestionIndex);//but do not show
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
//		wklock.release();
		super.onRemovedFromScreen();
	}
	
	@Override
	public boolean doNotDistrub() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean removeOnBackPressed(){
		return pQuizController.isRemoveScreenOnBackPressed();
	}
}
