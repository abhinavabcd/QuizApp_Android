package com.amcolabs.quizapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;

public class Screen extends TableLayout {
	protected AppManager appManager = null;
	public Screen(Context context , AppManager appManager) {
		super(context);
		this.appManager = appManager;
		// TODO Auto-generated constructor stub
	}

}
