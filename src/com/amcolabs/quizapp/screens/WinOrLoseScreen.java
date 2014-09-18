package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController.UserAnswer;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.BarChartViewMultiDataset;
import com.amcolabs.quizapp.widgets.FlowLayout;
import com.amcolabs.quizapp.widgets.GothamButtonView;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.amcolabs.quizapp.widgets.PieChartView;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.squareup.picasso.Picasso;

public class WinOrLoseScreen extends Screen{
	
	public ScrollView quizResult;
	private BarChartViewMultiDataset mChart;
	public ChatScreen chatScreen;
	private HashMap<String , userViewHolder> userViews;
	
	private GothamTextView quizPoints;
	private GothamTextView quizWinPoints;
	private GothamTextView quizLevelupPoints;
	private GothamTextView quizTotalPoints;
	
	private GothamButtonView rematchButton;
	private GothamButtonView challengeButton;
	private GothamButtonView addFriendButton;
	private GothamButtonView viewProfileButton;
	private GothamTextView quizResultMessage;
	
	private ArrayList<User> currentUsers;
	private HashMap<String, List<UserAnswer>> userAnswersStack;
	
	public static class userViewHolder{
		GothamTextView userNameView;
		GothamTextView userStatusMessageView;
		GothamTextView userMoreInfoView;
		ImageView userImageView;
		PieChartView userPieChartView;
		public FlowLayout badgesView;
	}
	
	public WinOrLoseScreen(AppController controller,ArrayList<User> curUsers) {
		super(controller);
		currentUsers = curUsers;
		quizResult = (ScrollView) LayoutInflater.from(controller.getContext()).inflate(R.layout.win_lose_screen,this, false);
		LinearLayout usersPieChartViews = (LinearLayout) quizResult.findViewById(R.id.users);
		LinearLayout usersShortView = (LinearLayout) quizResult.findViewById(R.id.users_short_view);
		
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
			drawUserActivityDistributionChart(curUsers.get(i),uView.userPieChartView);
//			setSampleData(this.getContext(),uView.userPieChartView);
		}
		

		
		
		quizResultMessage = (GothamTextView) quizResult.findViewById(R.id.quizResultMessage);
		mChart = (BarChartViewMultiDataset) quizResult.findViewById(R.id.bar_chart);
        
        quizPoints = (GothamTextView)quizResult.findViewById(R.id.quizPoints);
        quizWinPoints = (GothamTextView)quizResult.findViewById(R.id.quizWinPoints);
        quizLevelupPoints = (GothamTextView)quizResult.findViewById(R.id.quizLevelupPoints);
        quizTotalPoints = (GothamTextView)quizResult.findViewById(R.id.quizTotalPoints);
        
        rematchButton = (GothamButtonView)quizResult.findViewById(R.id.rematchButton);
        challengeButton = (GothamButtonView)quizResult.findViewById(R.id.challengeButton);
        addFriendButton = (GothamButtonView)quizResult.findViewById(R.id.addFriendButton);
        viewProfileButton = (GothamButtonView)quizResult.findViewById(R.id.viewProfileButton);
        
        
        addView(quizResult);
	}
	
	/**
	 * This is the main function to be invoked to display result of a quiz, right after init method
	 * @param currentUsers List of Users participated in the quiz
	 * @param userAnswersStack HashMap of list of answers mapped with users
	 * @param matchResult has current user who won the quiz
	 */
	public void showResult(HashMap<String, List<UserAnswer>> uAnswersStack,int matchResult,boolean levelUp){
	  // Show whether user has won or not
	  // rematch button , addFriend button , challenge with points button ,  seeProfile button
	// for these buttons , will use the same layout we used for category view ,list_item_layout.xml
	  // and load the profileViewLayout of both users in block , one after the other
	  //  will have place for chat block there itself users can live chat there itself
		userAnswersStack = uAnswersStack;
		userViewHolder tmp;
		ImageView imgView;
		User cUser;
		for(int i=0;i<currentUsers.size();i++){
			tmp = userViews.get(currentUsers.get(i).uid);
			cUser = currentUsers.get(i);
			imgView = tmp.userImageView;
			Picasso.with(getApp().getContext()).load(cUser.pictureUrl).into(imgView);
			tmp.userNameView.setText(cUser.name);
			tmp.userStatusMessageView.setText(cUser.status);
			//tmp.userMoreInfoView.setText(cUser.country);
			imgView.setTag(cUser);
			imgView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					User user = (User) v.getTag();
					((ProgressiveQuizController) controller).loadProfile(user);
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
		
		
		List<UserAnswer> ans = userAnswersStack.get(getApp().getUser().uid);
		animatePoints((int)Math.floor(ans.get(ans.size()-1).whatUserGot),(int)Math.floor(matchResult>0?Config.QUIZ_WIN_BONUS:0),(int)Math.floor(levelUp?Config.QUIZ_LEVEL_UP_BONUS:0));
		showResultInChart();
	}

	private void animatePoints(int qPoints, int qwPoints, int luPoints) {
		final Animation anim = AnimationUtils.loadAnimation(getApp().getContext(), R.anim.push_down_in);
		final Animation fanim = AnimationUtils.loadAnimation(getApp().getContext(), R.anim.fadein);
		quizPoints.setText("+"+qPoints);
		quizWinPoints.setText("+"+qwPoints);
		quizLevelupPoints.setText("+"+luPoints);
		quizTotalPoints.setText("+"+(qPoints+qwPoints+luPoints));
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				quizPoints.startAnimation(anim);
				quizWinPoints.startAnimation(anim);
				quizLevelupPoints.startAnimation(anim);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						quizTotalPoints.startAnimation(fanim);
					}
				}, 2000);
			}
		}, 1000);
	}
	
	// TODO : below method must be remove at the end
	public void setSampleData(Context ctxt,PieChartView myChart){
		int types = 4;
		float scale = 4;
		String[] mParties = new String[] {"Quiz1", "Quiz2", "Quiz3", "Quiz4"};
		
        float mult = (float) scale;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        // ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 0; i < types + 1; i++) {
            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        }

        // for (int i = types / 2; i <
        // types; i++) {
        // yVals2.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        // }

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < types + 1; i++)
            xVals.add(mParties[i % mParties.length]);

        PieDataSet set1 = new PieDataSet(yVals1, "Quiz Stats");
        set1.setSliceSpace(3f);
        set1.setColors(ColorTemplate.createColors(ctxt.getApplicationContext(),
                ColorTemplate.VORDIPLOM_COLORS));

        PieData data = new PieData(xVals, set1);
        myChart.setData(data);
        myChart.setValueTextSize(8);
        myChart.setDescriptionTextSize(8);

        // undo all highlights
        myChart.highlightValues(null);
        myChart.setCenterTextSize(8);

        // set a text for the chart center
        myChart.setCenterText("Total Value\n" + (int) myChart.getYValueSum() + "\n(all slices)");
        myChart.invalidate();
	 }
	
	public void drawUserActivityDistributionChart(User user,PieChartView mPieChart){
		List<Category> categories = getApp().getDataBaseHelper().getAllCategories();
		ArrayList<Entry> yVals = new ArrayList<Entry>();
		ArrayList<String> xVals = new ArrayList<String>();
		int sz = categories.size();
		
		for(int i=0;i<sz;i++){
			xVals.add(categories.get(i).shortDescription);
		}
		for (int i = 0; i < sz; i++) {
			List<Quiz> qList = categories.get(i).getQuizzes(getApp());
			float totalXP = 0;
			for(int j=0;j<qList.size();j++){
				totalXP = totalXP + (float)user.getPoints(qList.get(j));
			}
            yVals.add(new Entry(totalXP, i));
        }
		
		PieDataSet set = new PieDataSet(yVals, "Quiz Stats");
		set.setSliceSpace(3f);
		set.setColors(Config.themeColors);
//        set1.setColors(ColorTemplate.createColors(controller.getContext().getApplicationContext(),ColorTemplate.VORDIPLOM_COLORS));
        PieData data = new PieData(xVals, set);
        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);

        // set a text for the chart center
        mPieChart.setCenterText("Total \n" + (int) mPieChart.getYValueSum() + "\n(all slices)");
        
        mPieChart.setDescription("Total Matches Played in each Category");
        mPieChart.invalidate();
	}

	/**
	 * To show result of a quiz in bar chart. It assumes non null values any checks must be made beforehand
	 * @param currentUsers List of users participated in the quiz
	 * @param userAnswersStack HashMap of user answers as List of userAnswer objects mapped with uid's of users
	 */
	public void showResultInChart() {
		int columns = userAnswersStack.get(currentUsers.get(0).uid).size(); // to get questions size
		ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < columns; i++) {
            xVals.add("Q"+(i+1));
        }
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        BarDataSet set;
        ArrayList<BarEntry> yVals;
		for(int i=0;i<currentUsers.size();i++){
			if (userAnswersStack.containsKey(currentUsers.get(i).uid)){
				List<UserAnswer> answers = userAnswersStack.get(currentUsers.get(i).uid);
				yVals = new ArrayList<BarEntry>();
				for(int j=0;j<answers.size();j++){
					UserAnswer tmp = answers.get(j);
					yVals.add(new BarEntry((float)tmp.whatUserGot,j));
				}
				set = new BarDataSet(yVals, currentUsers.get(i).name);
				set.setColor(this.getApp().getConfig().getAThemeColor());
				dataSets.add(set);
			}
		}
		BarData data = new BarData(xVals, dataSets);
		data.setGroupSpace(110f);

        mChart.setData(data);
        mChart.invalidate();
	}
}
