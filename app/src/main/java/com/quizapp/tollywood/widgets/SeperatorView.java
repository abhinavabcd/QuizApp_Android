package com.quizapp.tollywood.widgets;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.quizapp.tollywood.R;

public class SeperatorView extends View{

	private final Paint paint = new Paint();
	private String lineLength;
	private String thickness;
	private int color;
	public SeperatorView(Context context) {
		super(context);
//		paint.setColor(Color.BLACK);
		color = 0;
		color = context.getResources().getColor(R.color.barDarkColor);
	}
	
	public SeperatorView(Context context,AttributeSet attrs) {
		super(context,attrs);
//		paint.setColor(Color.parseColor("#e7e7e7"));
//		paint.setColor(context.getResources().getColor(R.color.immutable_bg));
//		for(int i=0;i<attrs.getAttributeCount();i++){
//			String name = attrs.getAttributeName(i);
//			if (name.equalsIgnoreCase("layout_width")){
//				lineLength = attrs.getAttributeValue(i);
//			}
//			else if(name.equalsIgnoreCase("layout_height")){
//				thickness = attrs.getAttributeValue(i);
//			}
//		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		canvas.drawPaint(paint);
		canvas.drawColor(this.color);
//		canvas.drawRect(new Rect, paint);
//	    canvas.drawLine(10, 100, lineLength, 100, paint);
	}
}
