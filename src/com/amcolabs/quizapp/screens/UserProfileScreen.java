package com.amcolabs.quizapp.screens;

import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;

public class UserProfileScreen extends Screen {
	public TextView wonTextView;
	public TextView lostTextView;
	public BarGraphView userGraphView;
	public ScrollView userProfile;
	public UserProfileScreen(AppController controller) {
		super(controller);
		userProfile = (ScrollView) LayoutInflater.from(controller.getContext()).inflate(R.layout.user_profile, null);
		wonTextView = (TextView) userProfile.findViewById(R.id.win_count);
		lostTextView = (TextView) userProfile.findViewById(R.id.lose_count);
		userGraphView = (BarGraphView) userProfile.findViewById(R.id.user_progress_graph);
		if (userGraphView==null){
			userGraphView = new BarGraphView(controller.getContext(), "test graph");
		}
		fillSampleData();
		addView(userProfile);
	}
	
	public void updateUserData(){
		setUserGraphView();
	}
	
	public void setUserGraphView(){
		
	}
	
	public void fillSampleData(){
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
			    new GraphViewData(1, 2.0d)
			    , new GraphViewData(2, 1.5d)
			    , new GraphViewData(3, 2.5d)
			    , new GraphViewData(4, 1.0d)
			});
		userGraphView.addSeries(exampleSeries); 
//		userGraphView.setTitle("XP Progress");
	}
}
