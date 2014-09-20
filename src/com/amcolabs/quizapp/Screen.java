package com.amcolabs.quizapp;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Screen extends LinearLayout {
	protected AppController controller = null;
	protected boolean isInViewPort = true;
	private LinearLayout scrollView = null;
	public Screen(AppController controller) {
		super(controller.getContext());
		this.controller = controller;
		this.setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,1));
	}
	
	
	public void addToScrollView(View view){
		if(scrollView==null){
			ScrollView scrollViewMain = new ScrollView(getApp().getContext());
			scrollViewMain.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
			scrollViewMain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,1));
			scrollViewMain.setFillViewport(true);
			addView(scrollViewMain);
			scrollView = new LinearLayout(getApp().getContext());
//			scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,1));
			scrollView.setOrientation(LinearLayout.VERTICAL);
			scrollViewMain.addView(scrollView);
		}
		scrollView.addView(view);
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
	
	public boolean showMenu(){
		return false;
	}


	public void refresh() {
	}
}
