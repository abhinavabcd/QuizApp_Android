package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ScrollView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.widgets.BarChartViewMultiDataset;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

public class WinOrLoseScreen extends Screen{
	
	public ScrollView quizResult;
	private BarChartViewMultiDataset mChart;
	
	public WinOrLoseScreen(AppController controller) {
		super(controller);
		quizResult = (ScrollView) LayoutInflater.from(controller.getContext()).inflate(R.layout.win_lose_screen, null);
		mChart = (BarChartViewMultiDataset) quizResult.findViewById(R.id.bar_chart);
        setSampleData();

		addView(quizResult);
	}
	
	public void showResult(User currenUser , User user2 , boolean hasWon){
	  // Show whether user has won or not
	  // rematch button , addFriend button , challenge with points button ,  seeProfile button
	// for these buttons , will use the same layout we used for category view ,list_item_layout.xml
	  // and load the profileViewLayout of both users in block , one after the other
	  //  will have place for chat block there itself users can live chat there itself
	}

	public void showAnimationOfCurrentGamePoints(int[] questionPoints, int[] questionBasedBonus , int winBonus){
	         // just current gain , and all sum of them gained in small boxes
	}
	
	// TODO: Below method must be removed at the end
	public void setSampleData(){
		int columns = 4;
		int barWidth = 4;
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < columns; i++) {
            xVals.add((i+1990) + "");
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();

        for (int i = 0; i < columns; i++) {
            float val = (float) (Math.random() * barWidth) + 3;
            yVals1.add(new BarEntry(val, i));
        }

        for (int i = 0; i < columns; i++) {
            float val = (float) (Math.random() * barWidth) + 3;
            yVals2.add(new BarEntry(val, i));
        }

        for (int i = 0; i < columns; i++) {
            float val = (float) (Math.random() * barWidth) + 3;
            yVals3.add(new BarEntry(val, i));
        }

        // create 3 datasets with different types
        BarDataSet set1 = new BarDataSet(yVals1, "User1");
//        set1.setColors(ColorTemplate.createColors(getApplicationContext(), ColorTemplate.FRESH_COLORS));
        set1.setColor(Color.rgb(104, 241, 175));
        BarDataSet set2 = new BarDataSet(yVals2, "User2");
        set2.setColor(Color.rgb(164, 228, 251));
//        BarDataSet set3 = new BarDataSet(yVals3, "Company C");
//        set3.setColor(Color.rgb(242, 247, 158));
        
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
//        dataSets.add(set3);

        BarData data = new BarData(xVals, dataSets);
        
        // add space between the dataset groups in percent of bar-width
        data.setGroupSpace(110f);

        mChart.setData(data);
        mChart.invalidate();
	}
}
