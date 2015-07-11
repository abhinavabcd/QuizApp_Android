package com.amcolabs.quizapp.widgets;

import java.util.Timer;
import java.util.TimerTask;

import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.configuration.Config;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class CustomProgressBar extends ProgressBar{

	private Paint pt; 
	public CustomProgressBar(Context context, AttributeSet attr, int progressbarstylehorizontal) {
		super(context,attr,android.R.attr.progressBarStyleHorizontal);
		pt = new Paint();
		pt.setColor(Color.GREEN);
//		this.setProgressDrawable(context.getResources().getDrawable(R.drawable.fat_progress_bar));
//
//		this.setMinimumHeight(300);
//		this.setSecondaryProgress(80);
//		this.setProgress(50);
//		this.setMinimumWidth(100);
//		this.setIndeterminate(false);
	}

	public CustomProgressBar(Context context, AttributeSet attr){
		super(context,attr,android.R.attr.progressBarStyleHorizontal);
	}
	
	public CustomProgressBar(Context context){
		super(context,null,android.R.attr.progressBarStyleHorizontal);
	}
	
	
	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);
	}
	
	public void showAnimatedIncrement(final int pos){
		
		this.setSecondaryProgress(pos);
		Timer timer = (new Timer());
		timer.schedule(new TimerTask() {
					@Override
					public void run() {
						int cpos = CustomProgressBar.this.getProgress();
						if(cpos<=100 && cpos<pos){
							cpos = cpos+1;
							CustomProgressBar.this.setProgress(cpos);
						}
						else{
							this.cancel();
						}
					}
		}, 100,Config.PROGRESS_BAR_POINTS_ANIMATION_STEP_TIME);		//TODO: vinay change this to config variables ? 

//		Thread bg = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				int cpos = CustomProgressBar.this.getProgress();
//				while(cpos<=100 && cpos<pos){
//					cpos = cpos+1;
//					CustomProgressBar.this.setProgress(cpos);
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//		bg.run();
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
//		canvas.drawText("Hello world", 0, 0, pt);
		super.onDraw(canvas);
	}
}
