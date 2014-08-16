package com.amcolabs.quizapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;

public class Screen extends TableLayout {
	protected AppController appManager = null;
	public Screen(AppController appManager) {
		super(appManager.getContext());
		this.appManager = appManager;
	}

}
