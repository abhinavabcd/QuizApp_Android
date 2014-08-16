package com.amcolabs.quizapp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TableLayout;

public class Screen extends TableLayout {
	protected AppController appManager = null;
	protected boolean isInViewPort = true;
	public Screen(AppController appManager) {
		super(appManager.getContext());
		this.appManager = appManager;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(isInViewPort)
			super.onDraw(canvas);
	}

}
