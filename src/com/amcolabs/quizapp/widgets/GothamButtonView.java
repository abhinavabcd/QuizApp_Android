package com.amcolabs.quizapp.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class GothamButtonView extends Button {

	public GothamButtonView(Context context) {
		super(context);
		init();
	}

	public GothamButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GothamButtonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	

    private void init() {
    	if(!isInEditMode()){
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/gotham-medium.ttf");
            setTypeface(tf);
    	}
    }
}
