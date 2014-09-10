package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.databaseutils.Quiz;
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

public class UserProfileScreen extends Screen {
	public TextView wonTextView;
	public TextView lostTextView;
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
		userProfile = (ScrollView) LayoutInflater.from(controller.getContext()).inflate(R.layout.user_profile, null);
		userName = (GothamTextView) userProfile.findViewById(R.id.user_card_name);
		userImage = (ImageView)userProfile.findViewById(R.id.user_card_small_pic);
		userStatusMessage = (GothamTextView) userProfile.findViewById(R.id.user_status_msg);
		userMoreInfo = (GothamTextView) userProfile.findViewById(R.id.user_more_info);
		wonTextView = (TextView) userProfile.findViewById(R.id.win_count);
		lostTextView = (TextView) userProfile.findViewById(R.id.lose_count);
		mPieChart = (PieChartView) userProfile.findViewById(R.id.pie_chart);
		mPieChart = (PieChartView) userProfile.findViewById(R.id.bar_chart);
        setSampleData(controller.getContext());

		addView(userProfile);
	}
	
	public void showUser(User user){
		userName.setText(user.name);
		Picasso.with(getApp().getContext()).load(user.pictureUrl).into(userImage);
		userStatusMessage.setText(user.status);
		userMoreInfo.setText(user.place);
		
		drawUserCharts(user);
	}
	
	public void drawUserCharts(User user){
		List<Category> categories = getApp().getDataBaseHelper().getAllCategories();
		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
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
            yVals1.add(new Entry(totalXP, i));
            yVals2.add(new BarEntry((float)getApp().getGameUtils().getLevelFromXp(totalXP),i));
        }
		
		drawUserActivityDistributionChart(xVals,yVals1);
		drawCategoryWiseLevelsChart(xVals,yVals2);
	}
	
	public void drawCategoryWiseLevelsChart(ArrayList<String> xVals,ArrayList<BarEntry> yVals){
	        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
	        BarDataSet set;

			set = new BarDataSet(yVals, "Level");
			set.setColor(this.getApp().getConfig().getAThemeColor());
			dataSets.add(set);
			BarData data = new BarData(xVals, dataSets);
			data.setGroupSpace(110f);

	        mBarChart.setData(data);
	        mBarChart.setDescription("Your Current Level in each Category");
	        mBarChart.invalidate();
	}
	
	public void drawUserActivityDistributionChart(ArrayList<String> xVals,ArrayList<Entry> yVals){
	
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
	
	// TODO : below method must be remove at the end
	public void setSampleData(Context ctxt){
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
        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);

        // set a text for the chart center
        mPieChart.setCenterText("Total Value\n" + (int) mPieChart.getYValueSum() + "\n(all slices)");
        mPieChart.invalidate();
	 }
}
