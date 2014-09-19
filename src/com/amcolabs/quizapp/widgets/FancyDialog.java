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

	private View fancyDialogView;
	public FancyDialog(Context ctxt,int theme){
		super(ctxt, theme);
		fancyDialogView = getLayoutInflater().inflate(R.layout.fancy_dialog, null);
	}
	public FancyDialog(Context ctxt){
		super(ctxt);
		fancyDialogView = getLayoutInflater().inflate(R.layout.fancy_dialog, null);
	}
	public FancyDialog(Context ctxt,boolean cancelable,OnCancelListener listener){
		super(ctxt, cancelable,listener);
		fancyDialogView = getLayoutInflater().inflate(R.layout.fancy_dialog, null);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(fancyDialogView);
		
		ImageView closeButton = (ImageView)fancyDialogView.findViewById(R.id.closeButton);
		// if button is clicked, close the custom dialog
		closeButton.setOnClickListener(this);
	}
	
	public void setContent(int layoutResID){
		View layout = getLayoutInflater().inflate(layoutResID, null);
		((LinearLayout)fancyDialogView.findViewById(R.id.contentWrapper)).addView(layout);
	}
	
	public void setContent(View view) {
		((LinearLayout)fancyDialogView.findViewById(R.id.contentWrapper)).addView(view);
	}
	
	public void setContent(View view, LayoutParams params) {
		LinearLayout layout = (LinearLayout)fancyDialogView.findViewById(R.id.contentWrapper);
		layout.addView(view);
		layout.setLayoutParams(params);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		((GothamTextView)fancyDialogView.findViewById(R.id.titleText)).setText(title);
	}
	
	@Override
	public void setTitle(int titleId) {
		((GothamTextView)fancyDialogView.findViewById(R.id.titleText)).setText(titleId);
	}
	
	@Override
	public void onClick(View v) {
		this.cancel();
	}
	
	public void showTitle(){
		fancyDialogView.findViewById(R.id.headerWrapper).setVisibility(View.VISIBLE);
	}
	
	public void hideTitle(){
		fancyDialogView.findViewById(R.id.headerWrapper).setVisibility(View.GONE);
	}
	
	public void showAlertButtons(){
		fancyDialogView.findViewById(R.id.footerWrapper).setVisibility(View.VISIBLE);
	}
	
	public void hideAlertButtons(){
		fancyDialogView.findViewById(R.id.footerWrapper).setVisibility(View.GONE);
	}
	
	public void setPositiveButtonListener(android.view.View.OnClickListener l){
		fancyDialogView.findViewById(R.id.positiveButton).setOnClickListener(l);
	}
	
	public void setNegativeButtonListener(android.view.View.OnClickListener l){
		fancyDialogView.findViewById(R.id.negativeButton).setOnClickListener(l);
	}
}
