package com.quizapp.tollywood.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.quizapp.tollywood.ABTemplating;
import com.quizapp.tollywood.ABTemplating.ABView;
import com.quizapp.tollywood.AppController;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.Screen;
import com.quizapp.tollywood.User;
import com.quizapp.tollywood.appcontrollers.ProfileAndChatController;
import com.quizapp.tollywood.appcontrollers.ProgressiveQuizController;
import com.quizapp.tollywood.appcontrollers.ProgressiveQuizController.QuizMode;
import com.quizapp.tollywood.appcontrollers.ProgressiveQuizController.UserAnswer;
import com.quizapp.tollywood.appcontrollers.UserMainPageController;
import com.quizapp.tollywood.configuration.Config;
import com.quizapp.tollywood.databaseutils.Quiz;
import com.quizapp.tollywood.gameutils.GameUtils;
import com.quizapp.tollywood.uiutils.UiUtils;
import com.quizapp.tollywood.uiutils.UiUtils.UiText;
import com.quizapp.tollywood.widgets.BarChartViewMultiDataset;
import com.quizapp.tollywood.widgets.FlowLayout;
import com.quizapp.tollywood.widgets.GothamButtonView;
import com.quizapp.tollywood.widgets.GothamTextView;
import com.quizapp.tollywood.widgets.PieChartView;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

public class WinOrLoseScreen extends Screen{
	
	public ScrollView quizResult;
	private BarChartViewMultiDataset mChart;
	public ChatScreen chatScreen;
	private HashMap<String , userViewHolder> userViews;
	
	private GothamTextView quizPoints;
	private GothamTextView quizWinPoints;
	private GothamTextView quizLevelupPoints;
	private GothamTextView quizTotalPoints;
	private LinearLayout buttonsWrapper;
	private GothamButtonView rematchButton;
	private GothamButtonView leaderBoardsButton;
	private GothamButtonView addFriendButton;
	private GothamButtonView viewProfileButton;
	private GothamTextView quizResultMessage;
	
	private List<User> currentUsers;
	private HashMap<String, List<UserAnswer>> userAnswersStack;
	private ProgressiveQuizController progressiveQuizController;
	private QuizMode quizMode;
	private AsyncTask bgTask;
	
	public static class userViewHolder{
		GothamTextView userNameView;
		GothamTextView userStatusMessageView;
		GothamTextView userMoreInfoView;
		ImageView userImageView;
		PieChartView userPieChartView;
		public FlowLayout badgesView;
	}
	
	public WinOrLoseScreen(AppController controller,List<User> curUsers, List<Bitmap> answerBitmaps) {
		super(controller);

		for(User user : curUsers){
			if(user.uid.equalsIgnoreCase(getApp().getUser().uid)){
				curUsers.remove(user);
				curUsers.add(0,user);
				break;
			}
		}
		progressiveQuizController =  (ProgressiveQuizController) controller;
		currentUsers = curUsers;
		quizResult = (ScrollView) LayoutInflater.from(controller.getContext()).inflate(R.layout.win_lose_screen,this, false);
		LinearLayout usersPieChartViews = (LinearLayout) quizResult.findViewById(R.id.users);
		LinearLayout usersShortView = (LinearLayout) quizResult.findViewById(R.id.users_short_view);
		buttonsWrapper = (LinearLayout) quizResult.findViewById(R.id.buttons_wrapper);
		
		userViews = new HashMap<String, userViewHolder>();
		LinearLayout tmp;
		userViewHolder uView;
		for(User user : curUsers){
			uView = new userViewHolder();
			userViews.put(user.uid,uView);
			if(user.uid.equalsIgnoreCase(getApp().getUser().uid)){
				uView.userNameView = (GothamTextView) usersShortView.findViewById(R.id.user_name);
				uView.userStatusMessageView = (GothamTextView) usersShortView.findViewById(R.id.user_status_msg);
				uView.userImageView = (ImageView) usersShortView.findViewById(R.id.user1);
				uView.badgesView = (FlowLayout)usersShortView.findViewById(R.id.user1badges);
			}
			else{
				uView.userNameView = (GothamTextView) usersShortView.findViewById(R.id.user_name_2);
				uView.userStatusMessageView = (GothamTextView) usersShortView.findViewById(R.id.user_status_msg_2);
				uView.userImageView = (ImageView) usersShortView.findViewById(R.id.user2);
				uView.badgesView = (FlowLayout)usersShortView.findViewById(R.id.user2badges);
			}
		}
		for (int i=0;i<usersPieChartViews.getChildCount();i++){
			tmp = (LinearLayout) usersPieChartViews.getChildAt(i);//generally discouraged
			uView = userViews.get(curUsers.get(i).uid);
			uView.userPieChartView = (PieChartView) tmp.findViewById(R.id.pie_chart);
			drawUserActivityQuizDistributionChart(curUsers.get(i),uView.userPieChartView);
//			setSampleData(this.getContext(),uView.userPieChartView);
		}
		

		
		
		quizResultMessage = (GothamTextView) quizResult.findViewById(R.id.quizResultMessage);
		mChart = (BarChartViewMultiDataset) quizResult.findViewById(R.id.bar_chart);
        
        quizPoints = (GothamTextView)quizResult.findViewById(R.id.quizPoints);
        quizWinPoints = (GothamTextView)quizResult.findViewById(R.id.quizWinPoints);
        quizLevelupPoints = (GothamTextView)quizResult.findViewById(R.id.quizLevelupPoints);
        quizTotalPoints = (GothamTextView)quizResult.findViewById(R.id.quizTotalPoints);
        
        final User user2 = getOtherUser(curUsers); 
        
        rematchButton = (GothamButtonView)quizResult.findViewById(R.id.rematchButton);
        rematchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!progressiveQuizController.requestRematch()){
					rematchButton.setText(UiText.CHALLENGE.getValue());
				}
				else{
					rematchButton.setText(UiText.REQUESTED.getValue());
				}
			}
		});
        leaderBoardsButton = (GothamButtonView)quizResult.findViewById(R.id.challengeButton);
        leaderBoardsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserMainPageController uController = (UserMainPageController) getApp().loadAppController(UserMainPageController.class);
				uController.showLeaderBoards(progressiveQuizController.getQuiz().quizId);
			}
		});
        
        addFriendButton = (GothamButtonView)quizResult.findViewById(R.id.addFriendButton);
		for(String uid : getApp().getUser().getSubscribedTo()){
			if(uid.equalsIgnoreCase(getApp().getUser().uid)){
					addFriendButton.setText(UiText.UNSUBSCRIBE.getValue());
					addFriendButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							progressiveQuizController.removeFriend(user2);
							addFriendButton.setText(UiText.SUBSCRIBE.getValue());
						}
					});
					break;
			}
			else{
		        addFriendButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						progressiveQuizController.addFriend(user2);
					}
				});
			}
		}
			
        viewProfileButton = (GothamButtonView)quizResult.findViewById(R.id.viewProfileButton);
        viewProfileButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressiveQuizController.showProfileScreen(user2);
			}
		});
		GothamButtonView chatButton = (GothamButtonView) quizResult.findViewById(R.id.chat_with_user);
		chatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				((ProfileAndChatController) getApp().loadAppController(ProfileAndChatController.class)).loadChatScreen(user2, -1); //load chat screen
			}
		});

        addAnswerBitmaps(answerBitmaps);
        addView(quizResult);
	}
	
	
	
	public void addAnswerBitmaps(List<Bitmap> answerBitmaps){
        if(answerBitmaps!=null && answerBitmaps.size()>0){
        	ABView[] imageViews = new ABView[answerBitmaps.size()];
	        for(int i=0;i<answerBitmaps.size();i++){
	        	imageViews[i] = new ABView(getContext(),R.style.answer_bitmap_imageview);
	        	getApp().getUiUtils();
	        	ImageView temp = new ImageView(getContext());
	        	LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	        	lp.setMargins(10, 10, 10, 10);
	        	temp.setLayoutParams(lp);
				UiUtils.setBg(temp, getApp().getResources().getDrawable(R.drawable.custom_border));
	        	temp.setImageBitmap(answerBitmaps.get(i));
	        	imageViews[i].addView(temp);
	        }
	        ABTemplating template = new ABTemplating(controller.getContext());
	        ((LinearLayout) quizResult.getChildAt(0)).addView(
	        		template.v(
		        		new ABView(getApp().getContext()).addLabel(UiText.ANSWERS_LIST.getValue()).gty(Gravity.CENTER),
		        		new ABView(getApp().getContext()).underline(),
		        		template.h(ABTemplating.IS_HORIZONTAL_SCROLL,imageViews)
	        		)
			);
        }

	}
	
	private User getOtherUser(List<User> currentUsers) {
		for(User user : currentUsers){
			if(!user.uid.equalsIgnoreCase(getApp().getUser().uid)){
				return user;
			}
		}
		return null;
	}

	/**
	 * This is the main function to be invoked to display result of a quiz, right after init method
	 * @param currentUsers List of Users participated in the quiz
	 * @param userAnswersStack HashMap of list of answers mapped with users
	 * @param matchResult has current user who won the quiz
	 */
	public void showResult(HashMap<String, List<UserAnswer>> uAnswersStack,int matchResult,boolean levelUp, QuizMode quizMode){
	  // Show whether user has won or not
	  // rematch button , addFriend button , challenge with points button ,  seeProfile button
      // for these buttons , will use the same layout we used for offlineChallenge view ,list_item_layout.xml
 	  // and load the profileViewLayout of both users in block , one after the other
	  //  will have place for chat block there itself users can live chat there itself
		userAnswersStack = uAnswersStack;
		this.quizMode = quizMode;
		userViewHolder tmp;
		ImageView imgView;
		User cUser;
		for(int i=0;i<currentUsers.size();i++){
			tmp = userViews.get(currentUsers.get(i).uid);
			cUser = currentUsers.get(i);
			imgView = tmp.userImageView;
			getApp().getUiUtils().loadImageIntoView(getApp().getContext(), imgView, cUser.pictureUrl, false);
			tmp.userNameView.setText(cUser.getName());
			tmp.userStatusMessageView.setText(cUser.getStatus());
			//tmp.userMoreInfoView.setText(cUser.country);
			imgView.setTag(cUser);
			imgView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					User user = (User) v.getTag();
					((ProgressiveQuizController) controller).showProfileScreen(user);
//					UserProfileScreen uScreen = new UserProfileScreen(controller);
//					uScreen.showUser(user);
//					controller.showScreen(uScreen);
				}
			});
		}
		if(matchResult==1){
			quizResultMessage.setText(UiText.WON_QUIZ_MESSAGE.getValue());
		}
		else if(matchResult==-1){
			quizResultMessage.setText(UiText.LOST_QUIZ_MESAGE.getValue());
		}
		else if(matchResult==0){
			quizResultMessage.setText(UiText.TIE_QUIZ_MESAGE.getValue());
		} 
		else if(matchResult==-2){
			quizResultMessage.setText(UiText.SERVER_ERROR_MESSAGE.getValue());
		}
		boolean isChallengeMode = quizMode==QuizMode.CHALLENGE_MODE;
		
		if(quizMode == QuizMode.CHALLENGE_MODE){
			quizResultMessage.setText(UiText.YOU_CHALLENGED.getValue());	
		}
		
		if(quizMode==QuizMode.CHALLENGED_MODE || quizMode==QuizMode.CHALLENGED_MODE){
			buttonsWrapper.setVisibility(View.GONE);
		}
		
		List<UserAnswer> ans = userAnswersStack.get(getApp().getUser().uid);
		// TODO: Assuming only two users - picking other user score as next best (blunder for multi users)
		List<UserAnswer> opponentAns = userAnswersStack.get(((ProgressiveQuizController) controller).getOtherUser().uid);
		int qwPoints = 0;
		int qPoints = 0;

		if(ans!=null && ans.size()>0) {
			UserAnswer lastAnswer = ans.get(ans.size() - 1);
			if (lastAnswer != null)
				qPoints = (int) Math.floor(lastAnswer.whatUserGot);
		}

		if(matchResult>0&&!isChallengeMode){
			UserAnswer lastAnswerOtherUser = opponentAns.get(opponentAns.size() - 1);
			int opponentQPoints = (int)Math.floor(lastAnswerOtherUser.whatUserGot);
			qwPoints = (int)Math.floor(Config.QUIZ_WIN_BONUS+ qPoints-opponentQPoints); // Adding differential score to bonus
		}
		animatePoints(qPoints,qwPoints,(int)Math.floor(levelUp?Config.QUIZ_LEVEL_UP_BONUS:0));
		showResultInChart();
	}

	private void animatePoints(int qPoints, int qwPoints, int luPoints) {
		final Animation anim = AnimationUtils.loadAnimation(getApp().getContext(), R.anim.push_down_in);
		final Animation fanim = AnimationUtils.loadAnimation(getApp().getContext(), R.anim.fadein);
		quizPoints.setText("+"+qPoints);
		if(quizMode!=QuizMode.CHALLENGE_MODE)
			quizWinPoints.setText("+"+qwPoints);
		else{
			quizWinPoints.setText("?");
		}
		quizLevelupPoints.setText("+"+luPoints);
		quizTotalPoints.setText("+"+(qPoints+qwPoints+luPoints));
		quizTotalPoints.setVisibility(View.INVISIBLE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				quizPoints.startAnimation(anim);
				quizWinPoints.startAnimation(anim);
				quizLevelupPoints.startAnimation(anim);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						quizTotalPoints.setVisibility(View.VISIBLE);
						quizTotalPoints.startAnimation(fanim);
					}
				}, 2000);
			}
		}, 1000);
	}
	
//	// TODO : below method must be remove at the end
//	public void setSampleData(Context ctxt,PieChartView myChart){
//		int types = 4;
//		float scale = 4;
//		String[] mParties = new String[] {"Quiz1", "Quiz2", "Quiz3", "Quiz4"};
//		
//        float mult = (float) scale;
//
//        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
//        // ArrayList<Entry> yVals2 = new ArrayList<Entry>();
//
//        // IMPORTANT: In a PieChart, no values (Entry) should have the same
//        // xIndex (even if from different DataSets), since no values can be
//        // drawn above each other.
//        for (int i = 0; i < types + 1; i++) {
//            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
//        }
//
//        // for (int i = types / 2; i <
//        // types; i++) {
//        // yVals2.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
//        // }
//
//        ArrayList<String> xVals = new ArrayList<String>();
//
//        for (int i = 0; i < types + 1; i++)
//            xVals.add(mParties[i % mParties.length]);
//
//        PieDataSet set1 = new PieDataSet(yVals1, UiText.QUIZ_STATS.getValue());
//        set1.setSliceSpace(3f);
//        set1.setColors(ColorTemplate.createColors(ctxt.getApplicationContext(),
//                ColorTemplate.VORDIPLOM_COLORS));
//
//        PieData data = new PieData(xVals, set1);
//        myChart.setData(data);
//        myChart.setValueTextSize(8);
//        myChart.setDescriptionTextSize(8);
//
//        // undo all highlights
//        myChart.highlightValues(null);
//        myChart.setCenterTextSize(8);
//
//        // set a text for the chart center
//        myChart.setCenterText((int) myChart.getYValueSum() + "");
//        myChart.invalidate();
//	 }
	
	// very old - while uncommenting use quiz code
//	public void drawUserActivityCategoryDistributionChart(User user,PieChartView mPieChart){
//		List<Category> categories = getApp().getDataBaseHelper().getAllCategories();
//		ArrayList<Entry> yVals = new ArrayList<Entry>();
//		ArrayList<String> xVals = new ArrayList<String>();
//		int sz = categories.size();
//		
//		for(int i=0;i<sz;i++){
//			xVals.add(categories.get(i).shortDescription);
//		}
//		for (int i = 0; i < sz; i++) {
//			List<Quiz> qList = categories.get(i).getQuizzes(getApp());
//			float totalXP = 0;
//			for(int j=0;j<qList.size();j++){
//				totalXP = totalXP + (float)user.getPoints(qList.get(j).quizId);
//			}
//            yVals.add(new Entry(totalXP, i));
//        }
//		
//		PieDataSet set = new PieDataSet(yVals, UiText.QUIZ_STATS.getValue());
//		set.setSliceSpace(3f);
//		set.setColors(Config.themeColors);
////        set1.setColors(ColorTemplate.createColors(controller.getContext().getApplicationContext(),ColorTemplate.VORDIPLOM_COLORS));
//        PieData data = new PieData(xVals, set);
//        mPieChart.setData(data);
//        mPieChart.setDescriptionTextSize(5f);
//        mPieChart.setValueTextSize(5f);    
//        mPieChart.setCenterTextSize(5f);
//
//        // undo all highlights
//        mPieChart.highlightValues(null);
//
//        // set a text for the chart center
//        mPieChart.setCenterText((int) mPieChart.getYValueSum() + "");
//        mPieChart.setDescription(UiText.TOTAL_MATCHES_PLAYED.getValue());
//        mPieChart.invalidate();
//	}
	
	public void drawUserActivityQuizDistributionChart(User user,PieChartView mPieChart){
		List<Quiz> quizList = getApp().getDataBaseHelper().getAllQuizzesOrderedByXP();
		ArrayList<Entry> yVals = new ArrayList<Entry>();
		ArrayList<String> xVals = new ArrayList<String>();
		int sz = quizList.size();
		double userXp = 0;
		double cuserXp = 0;
		double maxuserXP = 0;
		int myindex = 0;
		
		for (int i = 0; i < sz; i++) {
			cuserXp = user.getPoints(quizList.get(i).quizId);
			if (maxuserXP < cuserXp)
				maxuserXP = cuserXp;
			// count is less than limit and values are moderately deviating 
			if (i<Config.PIE_CHART_MAX_FIELDS-1 && cuserXp/maxuserXP>0.01){
				userXp = cuserXp;
				xVals.add(GameUtils.reduceString(quizList.get(i).name));
				myindex = i;
			}
			else{
				// To sum all other types into others
				userXp = userXp + cuserXp;
				if(i!=sz-1)
					continue;
				xVals.add(UiUtils.UiText.PIE_CHART_OTHERS_TEXT.getValue());
				myindex = Config.PIE_CHART_MAX_FIELDS-1;
			}
//			yVals.add(new Entry((float)getApp().getGameUtils().getLevelFromXp(userXp), myindex));
			yVals.add(new Entry((float)userXp, myindex));
			userXp = 0;
		}
		
		PieDataSet set = new PieDataSet(yVals, UiText.QUIZ_LEVEL.getValue());
		set.setSliceSpace(3f);
		set.setColors(Config.themeColors);
//        set1.setColors(ColorTemplate.createColors(controller.getContext().getApplicationContext(),ColorTemplate.VORDIPLOM_COLORS));
        PieData data = new PieData(xVals, set);
        mPieChart.setValueFormatter(getApp().getUiUtils().getDecimalFormatter());
        mPieChart.setData(data);
        mPieChart.setDescriptionTextSize(4f);
        mPieChart.setValueTextSize(4f);

        mPieChart.setCenterTextSize(5f); 

        // undo all highlights
        mPieChart.highlightValues(null);

        // set a text for the chart center
        mPieChart.setCenterText("Total XP: " + (int) mPieChart.getYValueSum());
        
        mPieChart.setDescription(UiText.QUIZ_LEVEL_DISTRIBUTION.getValue());
        mPieChart.invalidate();
	}

	/**
	 * To show result of a quiz in bar chart. It assumes non null values any checks must be made beforehand
	 * @param currentUsers List of users participated in the quiz
	 * @param userAnswersStack HashMap of user answers as List of userAnswer objects mapped with uid's of users
	 */
	public void showResultInChart() {
		List<UserAnswer> userAnswers = userAnswersStack.get(currentUsers.get(0).uid);
		if(userAnswers==null)
			return;
		int columns = userAnswers!=null?userAnswers.size():0; // to get questions size
		ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < columns; i++) {
            xVals.add("Q"+(i+1));
        }
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        BarDataSet set;
        ArrayList<BarEntry> yVals;
        boolean nonZeroFlag = false;
		for(int i=0;i<currentUsers.size();i++){
			if (userAnswersStack.containsKey(currentUsers.get(i).uid)){
				List<UserAnswer> answers = userAnswersStack.get(currentUsers.get(i).uid);
				yVals = new ArrayList<BarEntry>();
				for(int j=0;j<answers.size();j++){
					UserAnswer tmp = answers.get(j);
					yVals.add(new BarEntry((float)tmp.whatUserGot,j));
				}
				nonZeroFlag = getApp().getUiUtils().hasNonZeroValues(yVals);

				set = new BarDataSet(yVals, currentUsers.get(i).getName());
				set.setColor(this.getApp().getConfig().getAThemeColor());
				dataSets.add(set);
			}
		}
		
		BarData data = new BarData(xVals, dataSets);
//		data.setGroupSpace(30f);
		mChart.setValueFormatter(getApp().getUiUtils().getDecimalFormatter());
		if(nonZeroFlag){
			mChart.setData(data);
	        mChart.invalidate();
		}
	}
	@Override
	public boolean showOnBackPressed() {
		return true;
	}

	@Override
	public void beforeRemove() {
		if(bgTask!=null && bgTask.getStatus()==Status.RUNNING)
			bgTask.cancel(true);
		super.beforeRemove();
	}

	public void setBgTask(
			AsyncTask loadAnswerBitmapsInBackground) {
		// TODO Auto-generated method stub
		bgTask = loadAnswerBitmapsInBackground;
	}

	public ScreenType getScreenType(){
		return ScreenType.WIN_LOOSE_SCREEN;
	}
}
