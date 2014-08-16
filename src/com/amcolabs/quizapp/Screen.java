package com.amcolabs.quizapp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TableLayout;

public class Screen extends TableLayout {
	protected AppController controller = null;
	protected boolean isInViewPort = true;
	public Screen(AppController controller) {
		super(controller.getContext());
		this.controller = controller;
	}
	
	public QuizApp getApp(){
		return controller.quizApp;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		if(isInViewPort)
			super.onDraw(canvas);
	}

}
