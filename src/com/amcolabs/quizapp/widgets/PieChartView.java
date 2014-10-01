
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
		initialize(context);
	}
    
    public PieChartView(Context context,AttributeSet attr) {
		super(context,attr);
		initialize(context);
	}
    
    public PieChartView(Context context,AttributeSet attr,int id) {
		super(context,attr,id);
		initialize(context);
	}
    
    public void initialize(Context ctxt)  {

        Typeface tf = Typeface.createFromAsset(ctxt.getAssets(), "fonts/gotham-medium.ttf");

        this.setValueTypeface(tf);
        this.setCenterTextTypeface(Typeface.createFromAsset(ctxt.getAssets(), "fonts/gothambold1.ttf"));

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


    }
    @Override
    protected void onAttachedToWindow() {
        Legend l = this.getLegend();
        l.setTextSize(6f);
        l.setStackSpace(1f);
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
        l.setXEntrySpace(1f);
        l.setYEntrySpace(5f);
    	super.onAttachedToWindow();
    }
    
    @Override
    protected void drawHighlights() {
    	// TODO Auto-generated method stub
    	super.drawHighlights();
    }

}
