package com.quizapp.tollywood.uiutils;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.quizapp.tollywood.datalisteners.DataInputListener;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class LoadAndSave implements Target {

	private View imgView;
	private boolean downloadToAssets;
	private File saveImageFile;
	private String assetPath;
	private DataInputListener<Boolean> completedLoadingImage;
	private boolean setAsBg = false;
	public LoadAndSave(View imgView, File file,  String assetPath , boolean downloadToAssets, DataInputListener<Boolean> completedLoadingImage, boolean isBg) {
		this.imgView =imgView;
		this.downloadToAssets = downloadToAssets;
		this.saveImageFile = file;
		this.assetPath = assetPath;
		this.completedLoadingImage = completedLoadingImage;
		this.setAsBg = isBg;
	}
	public LoadAndSave(View imgView, File file,  String assetPath , boolean downloadToAssets, DataInputListener<Boolean> completedLoadingImage) {
		this(imgView , file , assetPath, downloadToAssets, completedLoadingImage, false);
	}

	@Override
	public void onBitmapFailed(Drawable arg0) {
		if(completedLoadingImage!=null)
			completedLoadingImage.onData(false);
	}

	@Override
	public void onBitmapLoaded(final Bitmap bitmap, LoadedFrom arg1) {
		         if(downloadToAssets){
			        	new Thread(new Runnable() {
			                @Override
			                public void run() {               
			                    try
			                    {
			                        saveImageFile.createNewFile();
			                        FileOutputStream ostream = new FileOutputStream(saveImageFile);
			                        bitmap.compress(assetPath.endsWith(".png")?CompressFormat.PNG:CompressFormat.JPEG, 75, ostream);
			                        ostream.close();
			                    }
			                    catch (Exception e)
			                    {
			                        e.printStackTrace();
			                    }
			 
			                }
			            }).start();
	            }
		        if(setAsBg){
		        	UiUtils.setBg(imgView , new BitmapDrawable(imgView.getResources(), bitmap));
		        }
		        else // should be imageView only
		        	((ImageView) imgView).setImageBitmap(bitmap);
	            if(completedLoadingImage!=null)
	            	completedLoadingImage.onData(true);
	}

	@Override
	public void onPrepareLoad(Drawable arg0) {
		// TODO Auto-generated method stub

	}

}
