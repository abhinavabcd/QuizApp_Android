package com.amcolabs.quizapp.fileandcommonutils;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes;

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

    public static ArrayList<ArrayList<String>> getCombinations(ArrayList<String> arr, int len, int startPosition, ArrayList<String> result,ArrayList<ArrayList<String>> combinations){
        if (len == 0){
            combinations.add(result);
            return combinations;
        }       
        for (int i = startPosition; i <= arr.size()-len; i++){
            result.set(result.size() - len, arr.get(i));
            combinations = getCombinations(arr, len-1, i+1, result,combinations);
        }
        return combinations;
    }
    
    public static void main(String[] args) {
    	System.out.println("check");
    	ArrayList<String> alist = new ArrayList<String>();
    	alist.add("A");
    	alist.add("B");
    	alist.add("C");
    	alist.add("D");
    	alist.add("E");
    	alist.add("F");
    	ArrayList<ArrayList<String>> comb = getCombinations(alist, 3, 0, new ArrayList<String>(), new ArrayList<ArrayList<String>>());
    	for(int i=0;i<comb.size();i++){
    		for(int j=0;j<comb.get(i).size();j++){
    			System.out.print(comb.get(i).get(j));
    		}
    		System.out.println();
    	}
    }
}
