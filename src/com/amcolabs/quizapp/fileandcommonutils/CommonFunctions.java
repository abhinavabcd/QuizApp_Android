package com.amcolabs.quizapp.fileandcommonutils;

import java.util.Date;

import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonFunctions {

	public static String ifEmptyReturnNull(String str){
		if(str==null || str.trim().isEmpty()){
			return null;
		}
		return str.trim();
	}	
	
	public static long getCurrentTimeInSeconds() {
		  return new Date().getTime()/1000;
	}
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);
	
	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
	
	public static boolean isNetworkOn(Activity currentActivity){
		ConnectivityManager conMgr = (ConnectivityManager) currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isAvailable()) {
		    return true;
		} else {
			StaticPopupDialogBoxes.alertPrompt(currentActivity, "You are offline!", null);
		    return false;
		} 
	}
}
