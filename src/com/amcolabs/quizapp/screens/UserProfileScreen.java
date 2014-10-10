package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.uiutils.UiUtils;
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

public class UserProfileScreen extends Screen {
	public GothamTextView wonTextView;
	public GothamTextView lostTextView;
	public GothamTextView tieTextView;
	public ScrollView userProfile;
	private PieChartView mPieChart;
	private BarChartViewMultiDataset mBarChart;
	private GothamTextView userName;
	private ImageView userImage;
	private GothamTextView userStatusMessage;
	private GothamTextView userMoreInfo;
	private AppController controller;
	
	public UserProfileScreen(AppController cont) {
		super(cont);
		controller = cont;
		userProfile = (ScrollView) LayoutInflater.from(controller.getContext()).inflate(R.layout.user_profile, this, false);
		userName = (GothamTextView) userProfile.findViewById(R.id.user_card_name);
		userImage = (ImageView)userProfile.findViewById(R.id.user_card_small_pic);
		userStatusMessage = (GothamTextView) userProfile.findViewById(R.id.user_status_msg);
		userMoreInfo = (GothamTextView) userProfile.findViewById(R.id.user_more_info);
		wonTextView = (GothamTextView) userProfile.findViewById(R.id.win_count);
		lostTextView = (GothamTextView) userProfile.findViewById(R.id.lose_count);
		tieTextView = (GothamTextView) userProfile.findViewById(R.id.tie_count);
		mPieChart = (PieChartView) userProfile.findViewById(R.id.pie_chart);
		mBarChart = (BarChartViewMultiDataset) userProfile.findViewById(R.id.bar_chart);
 //       setSampleData(controller.getContext());

		addView(userProfile);
	}
	
	public void showUser(User user){
		userName.setText(user.name);
		getApp().getUiUtils().loadImageIntoView(getApp().getContext(), userImage, user.pictureUrl, false);
		userStatusMessage.setText(user.getStatus());
		userMoreInfo.setText(user.place);
		
		drawUserQuizChartsAndUpdateStats(user);
	}
	
	public void drawUserQuizChartsAndUpdateStats(User user){
		int win_count = 0;
		int lose_count = 0;
		int tie_count = 0;
		List<Quiz> quizList = getApp().getDataBaseHelper().getAllQuizzesOrderedByXP();
		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		ArrayList<BarEntry> yValsWins = new ArrayList<BarEntry>();
		ArrayList<BarEntry> yValsLosses = new ArrayList<BarEntry>();
		ArrayList<BarEntry> yValsTies = new ArrayList<BarEntry>();
		ArrayList<String> xVals = new ArrayList<String>();
		int sz = quizList.size();

		int[] winsLosses;
		List<String> qIdList = new ArrayList<String>();
		double userXp = 0;
		int myindex = 0;
		for (int i = 0; i < sz; i++) {
			if (i<Config.PIE_CHART_MAX_FIELDS-1){
				userXp = user.getPoints(quizList.get(i).quizId);
				qIdList.add(quizList.get(i).quizId);
				xVals.add(quizList.get(i).name);
				myindex = i;
			}
			else{
				userXp = userXp + user.getPoints(quizList.get(i).quizId);
				qIdList.add(((Quiz)quizList.get(i)).quizId);
				if(i!=sz-1)
					continue;
				xVals.add(UiUtils.UiText.PIE_CHART_OTHERS_TEXT.getValue());
				myindex = Config.PIE_CHART_MAX_FIELDS-1;
			}
			winsLosses = user.getWinsLossesSum(qIdList);
//            yVals1.add(new Entry((float)getApp().getGameUtils().getLevelFromXp(userXp), myindex));
            yVals1.add(new Entry((float)userXp, myindex));
            // update win lose tie counts here
            win_count = win_count + winsLosses[0];
            lose_count = lose_count + winsLosses[1];
            tie_count = tie_count + winsLosses[2];
            yValsWins.add(new BarEntry(winsLosses[0],myindex));
            yValsLosses.add(new BarEntry(winsLosses[1],myindex));
            yValsTies.add(new BarEntry(winsLosses[2],myindex));
            qIdList.clear();
            userXp = 0;
        }
		
		//update stats
		wonTextView.setText(win_count+" "+UiUtils.UiText.PROFILE_WON_STATS_TEXT.getValue());
		lostTextView.setText(lose_count+" "+UiUtils.UiText.PROFILE_LOST_STATS_TEXT.getValue());
		tieTextView.setText(tie_count+" "+UiUtils.UiText.PROFILE_TIE_STATS_TEXT.getValue());
		
		// TODO: Reduce code redundancy by checking for nonzero values in a method (present in uiutils, conflict for BarEntry and Entry)
		boolean nonZeroFlag = false;
		for(int i=0;i<yVals1.size();i++){
			if(yVals1.get(i).getVal() > 0){
				nonZeroFlag = true;
			}
		}
		if(nonZeroFlag)
			drawUserActivityDistributionChart(xVals,yVals1);
		nonZeroFlag = false;
		for(int i=0;i<yVals1.size();i++){
			if(yValsWins.get(i).getVal() > 0 || yValsLosses.get(i).getVal() > 0 || yValsTies.get(i).getVal() > 0 ){
				nonZeroFlag = true;
			}
		}
		if(nonZeroFlag)
			drawWinLoseTieChart(xVals,yValsWins,yValsLosses,yValsTies);
	}
	
//	public void drawUserCategoryChartsAndUpdateStats(User user){
//		int win_count = 0;
//		int lose_count = 0;
//		int tie_count = 0;
//		List<Category> categories = getApp().getDataBaseHelper().getAllCategories();
//		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
//		ArrayList<BarEntry> yValsWins = new ArrayList<BarEntry>();
//		ArrayList<BarEntry> yValsLosses = new ArrayList<BarEntry>();
//		ArrayList<BarEntry> yValsTies = new ArrayList<BarEntry>();
//		ArrayList<String> xVals = new ArrayList<String>();
//		int sz = categories.size();
//		
//		for(int i=0;i<sz;i++){
//			xVals.add(categories.get(i).shortDescription);
//		}
//		int[] winsLosses;
//		List<String> qIdList = new ArrayList<String>();
//		List<Quiz> qList = null;
//		for (int i = 0; i < sz; i++) {
//			qList = categories.get(i).getQuizzes(getApp());
//			qIdList.clear();
//			float totalXP = 0;
//			for(int j=0;j<qList.size();j++){
//				totalXP = totalXP + (float)user.getPoints(qList.get(j).quizId);
//				qIdList.add(((Quiz)qList.get(i)).quizId);
//			}
//			winsLosses = user.getWinsLossesSum(qIdList);
//            yVals1.add(new Entry((float)getApp().getGameUtils().getLevelFromXp(totalXP), i));
//            // update win lose tie counts here
//            yValsWins.add(new BarEntry(winsLosses[0],i));
//            yValsLosses.add(new BarEntry(winsLosses[1],i));
//            yValsTies.add(new BarEntry(winsLosses[2],i));
//        }
//		
//		//update stats
//		wonTextView.setText(win_count+" "+UiUtils.UiText.PROFILE_WON_STATS_TEXT.getValue());
//		lostTextView.setText(lose_count+" "+UiUtils.UiText.PROFILE_LOST_STATS_TEXT.getValue());
//		tieTextView.setText(tie_count+" "+UiUtils.UiText.PROFILE_TIE_STATS_TEXT.getValue());
//		
//		// TODO: Reduce code redundancy by checking for nonzero values in a method (present in uiutils, conflict for BarEntry and Entry)
//		boolean nonZeroFlag = false;
//		for(int i=0;i<yVals1.size();i++){
//			if(yVals1.get(i).getVal() > 0){
//				nonZeroFlag = true;
//			}
//		}
//		if(nonZeroFlag)
//			drawUserActivityDistributionChart(xVals,yVals1);
//		nonZeroFlag = false;
//		for(int i=0;i<yVals1.size();i++){
//			if(yValsWins.get(i).getVal() > 0 || yValsLosses.get(i).getVal() > 0 || yValsTies.get(i).getVal() > 0 ){
//				nonZeroFlag = true;
//			}
//		}
//		if(nonZeroFlag)
//			drawCategoryWiseLevelsChart(xVals,yValsWins,yValsLosses,yValsTies);
//	}
	
	public void drawWinLoseTieChart(ArrayList<String> xVals,ArrayList<BarEntry> yValsWins,ArrayList<BarEntry> yValsLosses,ArrayList<BarEntry> yValsTies){
	        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
	        BarDataSet set;

			set = new BarDataSet(yValsWins, "Won");
			set.setColor(this.getApp().getConfig().getAThemeColor());
			dataSets.add(set);
			set = new BarDataSet(yValsLosses, "Lost");
			set.setColor(this.getApp().getConfig().getAThemeColor());
			dataSets.add(set);
			set = new BarDataSet(yValsTies, "Tie");
			set.setColor(this.getApp().getConfig().getAThemeColor());
			dataSets.add(set);
			
			BarData data = new BarData(xVals, dataSets);
//			data.setGroupSpace(5f);

	        mBarChart.setData(data);
//	        mBarChart.setDescriptionTextSize(6f);
//	        mBarChart.setValueTextSize(5f);
	        mBarChart.setDescription("Quiz Stats");
	        mBarChart.invalidate();
	}
	
	public void drawUserActivityDistributionChart(ArrayList<String> xVals,ArrayList<Entry> yVals){
	
		PieDataSet set = new PieDataSet(yVals, "Quiz Stats");
//		set.setSliceSpace(3f);
		set.setColors(Config.themeColors);
//        set1.setColors(ColorTemplate.createColors(controller.getContext().getApplicationContext(),ColorTemplate.VORDIPLOM_COLORS));
        PieData data = new PieData(xVals, set);
        mPieChart.setValueFormatter(getApp().getUiUtils().getDecimalFormatter());
        mPieChart.setData(data);
        mPieChart.setDescriptionTextSize(6f);
        mPieChart.setValueTextSize(6f);

        // undo all highlights
        mPieChart.highlightValues(null);

        // set a text for the chart center
        mPieChart.setCenterText("Total XP: " + (int) mPieChart.getYValueSum());
        
        mPieChart.setDescription("Total Matches Played");
        mPieChart.invalidate();
	}
	
//	// TODO : below method must be remove at the end
//	public void setSampleData(Context ctxt){
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
//        PieDataSet set1 = new PieDataSet(yVals1, "Quiz Stats");
//        set1.setSliceSpace(3f);
//        set1.setColors(ColorTemplate.createColors(ctxt.getApplicationContext(),
//                ColorTemplate.VORDIPLOM_COLORS));
//
//        PieData data = new PieData(xVals, set1);
//        mPieChart.setData(data);
//
//        // undo all highlights
//        mPieChart.highlightValues(null);
//
//        // set a text for the chart center
//        mPieChart.setCenterText("Total Value\n" + (int) mPieChart.getYValueSum() + "\n(all slices)");
//        mPieChart.invalidate();
//	 }
	
	public void addInfoLocalSummary(){
		
	}
}
