package com.amcolabs.quizapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;

public class Screen extends LinearLayout {
	protected AppController controller = null;
	protected boolean isInViewPort = true;
	public Screen(AppController controller) {
		super(controller.getContext());
		this.controller = controller;
		this.setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,1));
	}
	
	public QuizApp getApp(){
		return controller.quizApp;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		if(isInViewPort)
			super.onDraw(canvas);
	}
	
	protected void onReInit(){
		
	}
	
	public void beforeRemove(){
		
	}
	
	public boolean showOnBackPressed(){
		return true;
	}

}
