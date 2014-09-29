
package com.amcolabs.quizapp.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;

public class PieChartView extends PieChart{

    public PieChartView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    
    public PieChartView(Context context,AttributeSet attr) {
		super(context,attr);
		// TODO Auto-generated constructor stub
	}
    
    public PieChartView(Context context,AttributeSet attr,int id) {
		super(context,attr,id);
		// TODO Auto-generated constructor stub
	}
    
    public void initialize(Context ctxt)  {

        Typeface tf = Typeface.createFromAsset(ctxt.getAssets(), "fonts/OpenSans-Regular.ttf");

        this.setValueTypeface(tf);
        this.setCenterTextTypeface(Typeface.createFromAsset(ctxt.getAssets(), "fonts/OpenSans-Light.ttf"));

        this.setHoleRadius(60f);

        this.setDescription("");

        this.setDrawYValues(true);
        this.setDrawCenterText(true);

        this.setDrawHoleEnabled(true);
        
        this.setRotationAngle(0);

        // draws the corresponding description value into the slice
        this.setDrawXValues(true);

        // enable rotation of the chart by touch
        this.setRotationEnabled(true);
        
        // display percentage values
        this.setUsePercentValues(true);
        // this.setUnit(" €");
        // this.setDrawUnitsInChart(true);
        this.animateXY(1500, 1500);
//        this.spin(2000, 0, 360);

        Legend l = this.getLegend();
        l.setTextSize(6f);
//        l.setPosition(LegendPosition.BELOW_CHART_CENTER);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(5f);
        l.setPosition(LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        
        PieChart mChart = this;
		mChart.setHoleRadius(60f);

        mChart.setDescription("");

        mChart.setDrawYValues(true);
        mChart.setDrawCenterText(true);

        mChart.setDrawHoleEnabled(true);
        
        mChart.setRotationAngle(0);

        // draws the corresponding description value into the slice
        mChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        
        // display percentage values
        mChart.setUsePercentValues(true);
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

//        mChart.setTouchEnabled(false);
                
        mChart.animateXY(1500, 1500);
//        mChart.spin(2000, 0, 360);
        
        mChart.highlightValues(null);
    }
    
    @Override
    protected void drawHighlights() {
    	// TODO Auto-generated method stub
    	super.drawHighlights();
    }

}
