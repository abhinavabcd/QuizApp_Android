package com.amcolabs.quizapp.uiutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.notificationutils.NotificationReciever;
import com.amcolabs.quizapp.widgets.UserInfoCard;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Picasso.LoadedFrom;


public class UiUtils {
	
	private QuizApp quizApp;
	private Animation animationSlideInLeft;
	private Animation animationSlideOutRight;
	private Animation animationSlideInRight;
	private Animation animationSlideOutLeft;
	
	public UiUtils(QuizApp quizApp){
		this.quizApp = quizApp;
		
       animationSlideInLeft = AnimationUtils.loadAnimation(quizApp.getContext(),
    	         R.anim.slide_in_left);
       animationSlideInRight = AnimationUtils.loadAnimation(quizApp.getContext(),
  	         R.anim.slide_in_right);
  	   animationSlideOutLeft = AnimationUtils.loadAnimation(quizApp.getContext(),
  	         R.anim.slide_out_left);
       animationSlideOutRight = AnimationUtils.loadAnimation(quizApp.getContext(),
    		   R.anim.slide_out_right);
       animationSlideOutLeft.setAnimationListener(quizApp);
       animationSlideOutRight.setAnimationListener(quizApp);
//       animationSlideInLeft.setAnimationListener(quizApp);
//       animationSlideInRight.setAnimationListener(quizApp);
	}
	
	public boolean isOutAnimation(Animation animation){
		if(animation==animationSlideOutLeft || animation==animationSlideOutRight){
			return true;
		}
		return false;
	}
	
	public static enum UiText{
		NO_PREVIOUS_MESSAGES("No Previous Messages"), TEXT_LOADING("loading.."), INVITE_DIALOG_TITLE("Invite your Friends"), SERVER_ERROR("Could not connect."), FETCHING_USER("Fetching User..");
		String value = null;
		UiText(String value){
			this.value = value;
		}
		public String getValue(){
			return value;
		}
		public String getValue(Object...args){
			return String.format(value,args);
		}
	}


	
	
	private static int uiBlockCount  =0;
	private static ProgressDialog preloader = null;
	private static CharSequence preloaderText;
	public  synchronized void addUiBlock(){
		try{
			if(uiBlockCount==0){
				preloaderText = UiText.TEXT_LOADING.getValue();
				preloader = ProgressDialog.show(quizApp.getContext(), "", preloaderText, true);
			}
			uiBlockCount++;
		}
		catch(Exception e){
			uiBlockCount =0 ;
			//older view error
		}
			
	}
	public synchronized void addUiBlock(String text){
		try{
		if(uiBlockCount==0){
			preloaderText = text;
			preloader = ProgressDialog.show(quizApp.getContext(), "", text, true);
		}
		else{
			preloaderText = preloaderText+ ("\n"+text);
			preloader.setMessage(preloaderText);
		}
		uiBlockCount++;
	}
	catch(Exception e){
		uiBlockCount =0 ;
		//older view error
	}

	}
	
	public synchronized boolean removeUiBlock(){
		try{
			uiBlockCount--;
			if(uiBlockCount==0){
				
				preloader.dismiss();
				return true;
			}
			return false;
		}
		catch(Exception e){
			uiBlockCount =0 ;
			//older view error
			return false;
		}

	}
	@SuppressLint("NewApi")
	public static void setBg(View view , Drawable drawable){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
	    	view.setBackground(drawable);
	    } else {
	    	view.setBackgroundDrawable(drawable);
	    }
	}
	
	static class MyTimer extends Timer{
		
	}
	public static Timer setInterval(final Context c ,  int millis , final DataInputListener<Integer> listener) {
		// TODO Auto-generated constructor stub
		Timer timer = (new Timer());
		timer.schedule(new TimerTask() {
					int count =0;
					@Override
					public void run() {
					      ((Activity)c).runOnUiThread(new Runnable(){
	
					       @Override
					       public void run() {
					    	   listener.onData(++count);
					       }}
					       );
					}
		}, 0, millis);
		return timer;
	}
	public static void generateNotification(Context pContext, String message,Bundle b) {
		int notificationId = Config.NOTIFICATION_ID;
    	int type = b!=null ? b.getInt(Config.NOTIFICATION_KEY_MESSAGE_TYPE, -1):-1;
    	switch(NotificationReciever.getNotificationTypeFromInt(type)){
    		//TODO
    	}
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(pContext)
        		.setSmallIcon(R.drawable.ic_launcher).setContentTitle(pContext.getResources().getString(R.string.app_name))
                        .setContentText(message);
        notificationBuilder.setWhen(System.currentTimeMillis()).setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        Intent resultIntent = new Intent(pContext, QuizApp.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if(b!=null)
        	resultIntent.putExtras(b);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(pContext);
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(CalendarView.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(pContext,0,resultIntent ,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, notificationBuilder.build()); //will show a notification and when clicked will open the app.	    
	}
	public static void generateNotification(Context pContext, String message) {
		generateNotification(pContext, message,null);
	}
    
    public static void sendSMS(Context context , String phoneNumber , String text) {  
    	Uri smsUri = Uri.parse("tel:+"+phoneNumber);
    	Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
    	intent.putExtra("sms_body", text);
    	intent.setType("vnd.android-dir/mms-sms");
    	context.startActivity(intent);
    }  
    
    
    public static void shareText(Activity A,String message,String phoneNumber){
    	Intent sendIntent = new Intent();
    	sendIntent.setAction(Intent.ACTION_SEND);
    	sendIntent.putExtra(Intent.EXTRA_TEXT, message);
    	if(phoneNumber!=null){
    		sendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);
    		sendIntent.putExtra("address", phoneNumber);
    	}
    	sendIntent.setType("text/plain");
    	A.startActivity(Intent.createChooser(sendIntent, UiUtils.UiText.INVITE_DIALOG_TITLE.getValue()));
    }
    
	public static String formatRemainingTime(double timeRemainingInMillis){
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;
 
		String ret = "";
		long elapsedDays = (long) (timeRemainingInMillis / daysInMilli);
		timeRemainingInMillis = timeRemainingInMillis % daysInMilli;
		if(elapsedDays>0) ret+=elapsedDays+"days ";

		long elapsedHours = (long) (timeRemainingInMillis / hoursInMilli);
		timeRemainingInMillis = timeRemainingInMillis % hoursInMilli;
		if(elapsedDays>0 ||elapsedHours>0) ret+=elapsedHours+"hours ";

		long elapsedMinutes = (long) (timeRemainingInMillis / minutesInMilli);
		timeRemainingInMillis = timeRemainingInMillis % minutesInMilli;
		if(elapsedDays>0 ||elapsedHours>0 || elapsedMinutes>0) ret+=elapsedMinutes+"min ";
 
		long elapsedSeconds = (long) (timeRemainingInMillis / secondsInMilli);
		if(elapsedDays>0 ||elapsedHours>0 || elapsedMinutes>0 ||elapsedSeconds>0) ret+=elapsedSeconds+"sec";
		 
		
		return 	ret;
	}

	public Animation getAnimationSlideOutRight() {
		return animationSlideOutRight;
	}

	public Animation getAnimationSlideOutLeft() {
		return animationSlideOutLeft;
	}
	
	public Animation getAnimationSlideInLeft() {
		return animationSlideInLeft;
	}
	public Animation getAnimationSlideInRight() {
		return animationSlideInRight;
	}        
	
	
	
	
	public void loadImageIntoView(Context ctx , final ImageView imgView , final String assetPath , boolean downloadToAssets){
		if(assetPath==null || assetPath.isEmpty())
			return;
		try{
		    InputStream ims = ctx.getAssets().open(assetPath);
		    Picasso.with(ctx).load("file:///android_asset/"+assetPath).into(imgView);
		    return;
		}
		catch(IOException ex) {
			final File file = new File(ctx.getFilesDir().getParentFile().getPath()+"/images/"+assetPath);
			if(file.exists()){
				Picasso.with(ctx).load(file).into(imgView);
			}
			else{
				Picasso.with(ctx).load(Config.CDN_IMAGES_PATH+assetPath).into(new Target() {
			        @Override
			        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
			            new Thread(new Runnable() {
			                @Override
			                public void run() {               
			                    try
			                    {
			                        file.createNewFile();
			                        FileOutputStream ostream = new FileOutputStream(file);
			                        bitmap.compress(assetPath.endsWith(".png")?CompressFormat.PNG:CompressFormat.JPEG, 75, ostream);
			                        ostream.close();
			                    }
			                    catch (Exception e)
			                    {
			                        e.printStackTrace();
			                    }
			 
			                }
			            }).start();
			            imgView.setImageBitmap(bitmap);
			        }

					@Override
					public void onBitmapFailed(Drawable arg0) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onPrepareLoad(Drawable arg0) {
						// TODO Auto-generated method stub
					}
			    });
			}
		}		 
		catch (Exception e) {
		}
	}
	
	
	public void loadImageAsBg(Context ctx , final Target target , final String assetPath){
		if(assetPath==null || assetPath.isEmpty())
			return;
		try{
		    InputStream ims = ctx.getAssets().open(assetPath);
		    Picasso.with(ctx).load("file:///android_asset/"+assetPath).into(target);
		    return;
		}
		catch(IOException ex) {
			final File file = new File(ctx.getFilesDir().getParentFile().getPath()+"/images/"+assetPath);
			if(file.exists()){
				Picasso.with(ctx).load(file).into(target);
			}
			else{
				Picasso.with(ctx).load(Config.CDN_IMAGES_PATH+assetPath).into(new Target() {
			        @Override
			        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
			            new Thread(new Runnable() {
			                @Override
			                public void run() {               
			                    try
			                    {
			                        file.createNewFile();
			                        FileOutputStream ostream = new FileOutputStream(file);
			                        bitmap.compress(assetPath.endsWith(".png")?CompressFormat.PNG:CompressFormat.JPEG, 75, ostream);
			                        ostream.close();
			                    }
			                    catch (Exception e)
			                    {
			                        e.printStackTrace();
			                    }
			 
			                }
			            }).start();
			           target.onBitmapLoaded(bitmap, from);
			        }

					@Override
					public void onBitmapFailed(Drawable arg0) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onPrepareLoad(Drawable arg0) {
						// TODO Auto-generated method stub
					}
			    });
			}
		}		 
	}

}
