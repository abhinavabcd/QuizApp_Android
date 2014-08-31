package com.amcolabs.quizapp.screens;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;

public class UserProfileScreen extends Screen {
	public TextView wonTextView;
	public TextView lostTextView;
	public ScrollView userProfile;
	public LinearLayout chartView;
	
	private GraphicalView mChart;

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    private XYSeries mCurrentSeries;

    private XYSeriesRenderer mCurrentRenderer;
	
	public UserProfileScreen(AppController controller) {
		super(controller);
		userProfile = (ScrollView) LayoutInflater.from(controller.getContext()).inflate(R.layout.user_profile, null);
		wonTextView = (TextView) userProfile.findViewById(R.id.win_count);
		lostTextView = (TextView) userProfile.findViewById(R.id.lose_count);
		chartView = (LinearLayout) userProfile.findViewById(R.id.chart);
		initChart();
        addSampleData();
        mChart = ChartFactory.getBarChartView(controller.getContext(), mDataset, mRenderer, BarChart.Type.DEFAULT);//CubeLineChartView(controller.getContext(), mDataset, mRenderer, 0.3f);
        mChart.setVisibility(View.VISIBLE);
        chartView.addView(mChart);
        chartView.setVisibility(View.VISIBLE);
		addView(userProfile);
	}
	
	private void initChart() {
        mCurrentSeries = new XYSeries("Sample Data");
        mDataset.addSeries(mCurrentSeries);
        mCurrentRenderer = new XYSeriesRenderer();
        mRenderer.setBarWidth(50f);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.addSeriesRenderer(mCurrentRenderer);
    }

    private void addSampleData() {
        mCurrentSeries.add(1, 2);
        mCurrentSeries.add(2, 3);
        mCurrentSeries.add(3, 2);
        mCurrentSeries.add(4, 5);
        mCurrentSeries.add(5, 4);
    }
    
	// Not using below methods
	public void updateUserData(){
		setUserGraphView();
	}
	
	public void setUserGraphView(){
		
	}
}
