package com.amcolabs.quizapp.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.amcolabs.quizapp.R;

public class ImageViewFiltered extends ImageView{
	
		private int USER_PHOTO_WIDTH = 200;
		private int USER_PHOTO_HEIGHT = 200;
		
		private Bitmap mImage = null;
		private Bitmap mMask = null;  // png mask with transparency
		private int mPosX = 0;
		private int mPosY = 0;

		private final Paint maskPaint;
		private final Paint imagePaint;
		
		public ImageViewFiltered(Context context, AttributeSet attr, int progressbarstylehorizontal) {
			super(context,attr,android.R.attr.progressBarStyleHorizontal);
			maskPaint = new Paint();
			imagePaint = new Paint();
			init(context);
		}

		public ImageViewFiltered(Context context, AttributeSet attr){
			super(context,attr,android.R.attr.progressBarStyleHorizontal);
			maskPaint = new Paint();
			imagePaint = new Paint();
			init(context);
		}
		
		public ImageViewFiltered(Context context){
			super(context,null,android.R.attr.progressBarStyleHorizontal);
			maskPaint = new Paint();
			imagePaint = new Paint();
			init(context);
		}
		
		public void init(Context context){
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			int num=0;
			switch (metrics.densityDpi){
				case DisplayMetrics.DENSITY_LOW:
					num = 3;
					break;
				case DisplayMetrics.DENSITY_MEDIUM:
					num = 4;
					break;
				case DisplayMetrics.DENSITY_HIGH:
					num = 6;
					break;
				case DisplayMetrics.DENSITY_XHIGH:
					num = 8;
					break;
				case DisplayMetrics.DENSITY_XXHIGH:
					num = 12;
					break;
				default:
					num = 6;
			}
//			USER_PHOTO_WIDTH = 100*num;
//			USER_PHOTO_HEIGHT = 100*num;
			
//			mMask = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_mask);
//			mImage = getMaskImage(USER_PHOTO_WIDTH/2,USER_PHOTO_HEIGHT/2,Color.RED);//(new BitmapDrawable(context.getResources())).getBitmap();
			mImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.kajal);
			USER_PHOTO_WIDTH = mImage.getWidth();
			USER_PHOTO_HEIGHT = mImage.getHeight();
			mMask = getMaskImage(USER_PHOTO_WIDTH,USER_PHOTO_HEIGHT,Color.BLUE);
			
		    maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
		    imagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		    
//			mMask = convertToAlphaMask(BitmapFactory.decodeResource(context.getResources(), R.drawable.img_mask));
//			Shader targetShader = createShader(mImage);
//			maskPaint.setShader(targetShader);
		}
		
		@Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	        // Get image matrix values and place them in an array
	        float[] f = new float[9];
	        getImageMatrix().getValues(f);

	        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
	        final float scaleX = f[Matrix.MSCALE_X];
	        final float scaleY = f[Matrix.MSCALE_Y];

	        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
	        final Drawable d = getDrawable();
	        final int origW = d==null?USER_PHOTO_WIDTH:d.getIntrinsicWidth();
	        final int origH = d==null?USER_PHOTO_HEIGHT:d.getIntrinsicHeight();

	        // Calculate the actual dimensions
	        final int actW = Math.round(origW * scaleX);
	        final int actH = Math.round(origH * scaleY);
//	        setMeasuredDimension(measuredWidth,measuredHeight);
	        Log.e("DBG", "["+origW+","+origH+"] -> ["+actW+","+actH+"] & scales: x="+scaleX+" y="+scaleY);
	    }
		
		private static Bitmap convertToAlphaMask(Bitmap b) {
		    Bitmap a = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ALPHA_8);
		    Canvas c = new Canvas(a);
		    c.drawBitmap(b, 0.0f, 0.0f, null);
		    return a;
		}
		
		private static Shader createShader(Bitmap b) {
		    return new BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		}
		
		public Bitmap getMaskImage(int width,int height,int color){
			String text = "";
			
//	        int width = USER_PHOTO_WIDTH;
//	        int height = USER_PHOTO_HEIGHT;
	        int radius = width > height ? height/2 : width/2;
	        int center_x = width/2;
	        int center_y = height/2;

	        // prepare a paint
	        Paint mPaint = new Paint();
	        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	        
	        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
	        Canvas canvas = new Canvas(image);
	        mPaint.setColor(Color.argb(100, 255, 255, 0));
//	        canvas.drawPaint(mPaint);
	        canvas.drawRect(center_x - radius/3, center_y - radius/3, center_x + radius/3, center_y + radius/3, mPaint);
		    return image;
		}
		
		@Override
		protected synchronized void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.save();      
		    canvas.drawBitmap(mImage, mPosX, mPosY, imagePaint);
		    canvas.drawBitmap(mMask, 0, 0, maskPaint);
		    canvas.restore();
		}
	}