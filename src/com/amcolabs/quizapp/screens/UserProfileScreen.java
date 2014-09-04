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
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.amcolabs.quizapp.widgets.PieChartView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.squareup.picasso.Picasso;

public class UserProfileScreen extends Screen {
	public TextView wonTextView;
	public TextView lostTextView;
	public ScrollView userProfile;
	private PieChartView mChart;
	private GothamTextView userName;
	private ImageView userImage;
	private GothamTextView userStatusMessage;
	private GothamTextView userMoreInfo;
	private AppController controller;
	
	public UserProfileScreen(AppController controllr) {
		super(controllr);
		controller = controllr;
		userProfile = (ScrollView) LayoutInflater.from(controller.getContext()).inflate(R.layout.user_profile, null);
		userName = (GothamTextView) userProfile.findViewById(R.id.user_card_name);
		userImage = (ImageView)userProfile.findViewById(R.id.user_card_small_pic);
		userStatusMessage = (GothamTextView) userProfile.findViewById(R.id.user_status_msg);
		userMoreInfo = (GothamTextView) userProfile.findViewById(R.id.user_more_info);
		wonTextView = (TextView) userProfile.findViewById(R.id.win_count);
		lostTextView = (TextView) userProfile.findViewById(R.id.lose_count);
		mChart = (PieChartView) userProfile.findViewById(R.id.pie_chart);
        setSampleData(controller.getContext());

		addView(userProfile);
	}
	
	public void showUser(User user){
		userName.setText(user.name);
		Picasso.with(getApp().getContext()).load(user.pictureUrl).into(userImage);
		userStatusMessage.setText(user.status);
		userMoreInfo.setText(user.place);
		
		drawChart(user);
	}
	
	public void drawChart(User user){
		List<Category> categories = controller.quizApp.getDataBaseHelper().getCategories(5);
		
		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		ArrayList<String> xVals = new ArrayList<String>();
		int sz = categories.size();
		float scale = 4;
		
		for(int i=0;i<sz;i++){
			xVals.add(categories.get(i).shortDescription);
		}
		for (int i = 0; i < sz + 1; i++) {
			List<Quiz> qList = categories.get(i).getQuizzes(controller.quizApp);
			float totalXP = 0;
			for(int j=0;j<qList.size();j++){
				totalXP = totalXP + (float)user.getPoints(qList.get(i));
			}
            yVals1.add(new Entry((float) (Math.random() * scale) + scale / 5, i));
        }
		PieDataSet set1 = new PieDataSet(yVals1, "Quiz Stats");
		set1.setSliceSpace(3f);
        set1.setColors(ColorTemplate.createColors(controller.getContext().getApplicationContext(),
                ColorTemplate.VORDIPLOM_COLORS));
        PieData data = new PieData(xVals, set1);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        // set a text for the chart center
        mChart.setCenterText("Total Value\n" + (int) mChart.getYValueSum() + "\n(all slices)");
        mChart.invalidate();
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
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        // set a text for the chart center
        mChart.setCenterText("Total Value\n" + (int) mChart.getYValueSum() + "\n(all slices)");
        mChart.invalidate();
	 }
}
