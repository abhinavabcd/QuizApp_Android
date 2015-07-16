
package com.quizapp.tollywood.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.quizapp.tollywood.uiutils.UiUtils.UiText;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

public class BarChartViewMultiDataset extends BarChart{

    public BarChartViewMultiDataset(Context context) {
		super(context);
		initialize(context);
	}
    
    public BarChartViewMultiDataset(Context context,AttributeSet attr) {
		super(context,attr);
		initialize(context);
	}
    
    public BarChartViewMultiDataset(Context context,AttributeSet attr,int id) {
		super(context,attr,id);
		initialize(context);
	}

    public void initialize(Context ctxt) {
    	
//        this.setOnChartValueSelectedListener(this);
        this.setDescription("");
        this.setNoDataText("");
        this.setNoDataTextDescription(UiText.NO_QUIZ_DATA_AVAILABLE_PLAY_TO_SEE.getValue());
        // disable the drawing of values
        this.setDrawYValues(false);

        // scaling can now only be done on x- and y-axis separately
        this.setPinchZoom(false);

        this.setDrawBarShadow(false);
        
        this.setDrawGridBackground(false);
        this.setDrawHorizontalGrid(false);
        
        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//
//        // define an offset to change the original position of the marker
//        // (optional)
//        mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());

        // set the marker to the chart
//        this.setMarkerView(mv);
        
        Typeface tf = Typeface.createFromAsset(ctxt.getAssets(), "fonts/gotham-medium.ttf");
        
        Legend l = this.getLegend();
        if (l!=null){
	        l.setPosition(LegendPosition.RIGHT_OF_CHART);
	        l.setTypeface(tf);
        }
        
        XLabels xl  = this.getXLabels();
        if (xl!=null){
        	xl.setCenterXLabelText(true);
        	xl.setTypeface(tf);
        }
        
        YLabels yl = this.getYLabels();
        if(yl!=null){
        	yl.setTypeface(tf);
        }
        
        this.setValueTypeface(tf);
    }
    
    @Override
    protected void onAttachedToWindow() {
        Legend l = this.getLegend();
        if(l!=null){
	        l.setTextSize(7f);
	
	        l.setStackSpace(1f);
	 //       l.setPosition(LegendPosition.RIGHT_OF_CHART);
	        l.setXEntrySpace(4f);
	        l.setYEntrySpace(1f);
        }
    	super.onAttachedToWindow();
    }
}
