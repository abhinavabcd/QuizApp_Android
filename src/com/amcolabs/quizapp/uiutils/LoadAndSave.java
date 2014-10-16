package com.amcolabs.quizapp.uiutils;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class LoadAndSave implements Target {

	private ImageView imgView;
	private boolean downloadToAssets;
	private File saveImageFile;
	private String assetPath;
	private DataInputListener<Boolean> completedLoadingImage;

	public LoadAndSave(ImageView imgView, File file,  String assetPath , boolean downloadToAssets, DataInputListener<Boolean> completedLoadingImage) {
		this.imgView =imgView;
		this.downloadToAssets = downloadToAssets;
		this.saveImageFile = file;
		this.assetPath = assetPath;
		this.completedLoadingImage = completedLoadingImage;
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
	            imgView.setImageBitmap(bitmap);
	            if(completedLoadingImage!=null)
	            	completedLoadingImage.onData(true);
	}

	@Override
	public void onPrepareLoad(Drawable arg0) {
		// TODO Auto-generated method stub

	}

}
