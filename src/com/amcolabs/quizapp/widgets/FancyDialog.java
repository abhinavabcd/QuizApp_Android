package com.amcolabs.quizapp.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.R;

public class FancyDialog extends Dialog implements android.view.View.OnClickListener{

	public FancyDialog(Context ctxt,int theme){
		super(ctxt, theme);
	}
	public FancyDialog(Context ctxt){
		super(ctxt);
	}
	public FancyDialog(Context ctxt,boolean cancelable,OnCancelListener listener){
		super(ctxt, cancelable,listener);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.fancy_dialog);
		
		ImageView closeButton = (ImageView) findViewById(R.id.closeButton);
		// if button is clicked, close the custom dialog
		closeButton.setOnClickListener(this);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		setContentView(R.layout.fancy_dialog);
		View layout = getLayoutInflater().inflate(layoutResID, null);
		((LinearLayout)findViewById(R.id.mainLayout)).addView(layout);
	}
	
	@Override
	public void setContentView(View view) {
		setContentView(R.layout.fancy_dialog);
		((LinearLayout)findViewById(R.id.mainLayout)).addView(view);
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		setContentView(R.layout.fancy_dialog);
		LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
		layout.addView(view);
		layout.setLayoutParams(params);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		((GothamTextView)findViewById(R.id.titleText)).setText(title);
	}
	
	@Override
	public void setTitle(int titleId) {
		((GothamTextView)findViewById(R.id.titleText)).setText(titleId);
	}
	
	@Override
	public void onClick(View v) {
		this.cancel();
	}
}
