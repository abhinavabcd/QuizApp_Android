package com.amcolabs.quizapp.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class GothamButtonView extends GothamTextView{

	private Object tag2;

	public GothamButtonView(Context context) {
		super(context);
//		init();
	}

	public void setTag2(Object tag2){
		this.tag2 = tag2;
	}
	public GothamButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		init();
	}

	public GothamButtonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		init();
	}

	public Object getTag2() {
		return tag2;
	}
	

}
