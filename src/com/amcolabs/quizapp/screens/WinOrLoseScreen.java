package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController.UserAnswer;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.BarChartViewMultiDataset;
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
	
	private Button rematchButton;
	private Button challengeButton;
	private Button addFriendButton;
	private Button viewProfileButton;
	private GothamTextView quizResultMessage;
	
	private ArrayList<User> currentUsers;
	private HashMap<String, List<UserAnswer>> userAnswersStack;
	
	public static class userViewHolder{
		GothamTextView userNameView;
		GothamTextView userStatusMessageView;
		GothamTextView userMoreInfoView;
		ImageView userImageView;
		PieChartView userPieChartView;
	}
	
	public WinOrLoseScreen(AppController controller,ArrayList<User> curUsers) {
		super(controller);
		currentUsers = curUsers;
		quizResult = (ScrollView) LayoutInflater.from(controller.getContext()).inflate(R.layout.win_lose_screen, null);
		LinearLayout users = (LinearLayout) quizResult.findViewById(R.id.users);
		userViews = new HashMap<String, userViewHolder>();
		LinearLayout tmp;
		userViewHolder uView;
		for (int i=0;i<users.getChildCount();i++){
			tmp = (LinearLayout) users.getChildAt(i);
			uView = new userViewHolder();
			
			uView.userNameView = (GothamTextView) tmp.findViewById(R.id.user_card_name);
			uView.userStatusMessageView = (GothamTextView) tmp.findViewById(R.id.user_status_msg);
			uView.userMoreInfoView = (GothamTextView) tmp.findViewById(R.id.user_more_info);
			uView.userImageView = (ImageView) tmp.findViewById(R.id.user_card_small_pic);
			uView.userPieChartView = (PieChartView) tmp.findViewById(R.id.pie_chart);
			setSampleData(this.getContext(),uView.userPieChartView);
			
			userViews.put(curUsers.get(i).uid,uView);
		}
		
		quizResultMessage = (GothamTextView) quizResult.findViewById(R.id.quizResultMessage);
		mChart = (BarChartViewMultiDataset) quizResult.findViewById(R.id.bar_chart);
        
        quizPoints = (GothamTextView)quizResult.findViewById(R.id.quizPoints);
        quizWinPoints = (GothamTextView)quizResult.findViewById(R.id.quizWinPoints);
        quizLevelupPoints = (GothamTextView)quizResult.findViewById(R.id.quizLevelupPoints);
        quizTotalPoints = (GothamTextView)quizResult.findViewById(R.id.quizTotalPoints);
        
        rematchButton = (Button)quizResult.findViewById(R.id.rematchButton);
        challengeButton = (Button)quizResult.findViewById(R.id.challengeButton);
        addFriendButton = (Button)quizResult.findViewById(R.id.addFriendButton);
        viewProfileButton = (Button)quizResult.findViewById(R.id.viewProfileButton);
        
        
        addView(quizResult);
        
//		chatScreen = new ChatScreen(controller);
////		for(int i=0;i<chatScreen.getChildCount();i++){
////			addView(chatScreen.getChildAt(i));
////		}
//		chatScreen.setLayoutParams(new LayoutParams(300, 400));
//		addView(chatScreen);
	}
	
	/**
	 * This is the main function to be invoked to display result of a quiz, right after init method
	 * @param currentUsers List of Users participated in the quiz
	 * @param userAnswersStack HashMap of list of answers mapped with users
	 * @param isWinner has current user who won the quiz
	 */
	public void showResult(HashMap<String, List<UserAnswer>> uAnswersStack,boolean isWinner){
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
			tmp.userMoreInfoView.setText(cUser.country);
			imgView.setTag(cUser);
			imgView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					User user = (User) v.getTag();
					UserProfileScreen uScreen = new UserProfileScreen(controller);
					uScreen.showUser(user);
					controller.showScreen(uScreen);
				}
			});
		}
		if(isWinner){
			quizResultMessage.setText(UiText.WON_QUIZ_MESSAGE.getValue());
		}
		else{
			quizResultMessage.setText(UiText.LOST_QUIZ_MESAGE.getValue());
		}
		boolean levelUp = true;
		
		List<UserAnswer> ans = userAnswersStack.get(getApp().getUser().uid);
		animatePoints(ans.get(ans.size()-1).whatUserGot,isWinner?20:0,levelUp?20:0);
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

	public void showAnimationOfCurrentGamePoints(int[] questionPoints, int[] questionBasedBonus , int winBonus){
	         // just current gain , and all sum of them gained in small boxes
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
				set = new BarDataSet(yVals, "User1");
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
