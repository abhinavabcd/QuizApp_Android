package com.amcolabs.quizapp.uiutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.notificationutils.NotificationReciever;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


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
		NO_PREVIOUS_MESSAGES("No Previous Messages"), 
		TEXT_LOADING("loading.."), 
		INVITE_DIALOG_TITLE("Invite your Friends"), 
		SERVER_ERROR("Could not connect."), 
		FETCHING_USER("Fetching User.."),
		COULD_NOT_CONNECT("Could not fetch Updates"),
		CATEGORIES("categories"),
		USER_FAVOURITES("Quick Play"), NOT_AUTHORIZED("Invalid Login"), SEARCHING_FOR_OPPONENT("Searching for a matching opponent"),
		GET_READY("Get Ready"), 
		FOR_YOUR_FIRST_QUESTION("For your first Question"), 
		QUESTION("Question %s"), 
		LEVEL("Level"), 
		RECENT_QUIZZES("Recent Quizzes"),
		BADGES("Badges"), 
		SHOW_QUIZZES("Quizzes"), 
		SHOW_MESSAGES("Messages"), 
		HOME("Home"), 
		NO_RECENT_MESSAGES("No Recent Conversations available."), 
		PREVIOUS_CHATS_USERS("Previous Conversations"), CHATS("Conversations"),
		FETCHING_MESSAGES("Fetching Messages from Server"),
		WON_QUIZ_MESSAGE("You Won!"),
		LOST_QUIZ_MESAGE("You Lost"), 
		GLOBAL_RANKINGS("Global Rankings"),
		LOCAL_RANKINGS("Local Rankings"),
		TIE_QUIZ_MESAGE("Its A TIE!"), 
		PROFILE_WON_STATS_TEXT("Won"),
		PROFILE_LOST_STATS_TEXT("Lost"),
		PROFILE_TIE_STATS_TEXT("Tie"), 
		USER_WANTS_REMATCH("%s wants a rematch with you"), USER_HAS_DISCONNECTED("%s has disconnected"),
		CHALLENGE("Challenge"),
		EXIT("Exit"),
		YES("Yes"),
		NO("No"), 
		SERVER_ERROR_MESSAGE("An Error"),
		USER_HAS_DECLINED("User declined the request"),
		OK("Ok"),
		NEW_BADGE_UNLOCKED_MESSAGE("New Badge Unlocked!"),
		USER_HAS_LEFT("User left the quiz"), 
		SELECT_FRIENDS_TO_CHALLENGE("Select Friends to Challenge"),
		BEGINNER("Beginner"),
		RUNNER("Runner"),
		GO_GETTER("Go Getter"), TREND_SETTER("Trend Setter"),
		START_CHALLENGE("Start Challenge"),
		NO_FRIENDS_TRY_ADDING("You haven't subscribed to anyone try adding friends."), 
		ADD("add"), 
		CLOSE("close"),
		UNABLE_TO_ADD_USER("Unable to add User."),
		ADDED_USER("successfully subscribed to %s"),
		PIE_CHART_OTHERS_TEXT("Others"),
		UNEXPECTED_ERROR("Unexpected Error"), YOU_CHALLENGED("You challenged");
		
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
	public void setBg(View view , Drawable drawable){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
	    	view.setBackground(drawable);
	    } else {
	    	view.setBackgroundDrawable(drawable);
	    }
	}
	
	
	public Timer setInterval(int millis , final DataInputListener<Integer> listener) {
		// TODO Auto-generated constructor stub
		Timer timer = (new Timer());
		timer.schedule(new TimerTask() {
					int count =0;
					@Override
					public void run() {
						// TODO: NullPointerException after when pressing back button to exit quiz
					      (quizApp.getActivity()).runOnUiThread(new Runnable(){
	
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
	
	public static String formatChatTime(double timestamp){
		String ret = "";
		Calendar today = Calendar.getInstance();
		Calendar dt = Calendar.getInstance();
		dt.setTimeInMillis((long) timestamp);
		if (dt.get(Calendar.DAY_OF_MONTH)==today.get(Calendar.DAY_OF_MONTH)){
			ret = String.valueOf(today.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(today.get(Calendar.MINUTE))+" "+String.valueOf((today.get(Calendar.AM_PM)==Calendar.AM)?"AM":"PM");
		}
		else if(dt.get(Calendar.DAY_OF_MONTH)==today.get(Calendar.DAY_OF_MONTH)){
			ret = "Yesterday ";
			ret = ret + String.valueOf(today.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(today.get(Calendar.MINUTE))+" "+String.valueOf(today.get(Calendar.AM_PM));
		}
		else{
			SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd");
			ret = dtformat.format(dt.getTime());
		}
//		System.currentTimeMillis()-timestamp<
		return ret;
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
	
	
	
	
	public boolean loadImageIntoView(Context ctx , final ImageView imgView, final String assetPath , final boolean downloadToAssets){
		if(assetPath==null || assetPath.isEmpty())
			return false;
		try{
			if(assetPath.startsWith("http://") || assetPath.startsWith("https://")){
				Picasso.with(ctx).load(assetPath).into(imgView);
			    return true;
			}
			
		    InputStream ims = ctx.getAssets().open("images/"+assetPath);
		    Picasso.with(ctx).load("file:///android_asset/images/"+assetPath).into(imgView);
		    return true;
		}
		catch(IOException ex) {
			File file = new File(ctx.getFilesDir().getParentFile().getPath()+"/images/"+assetPath);
			if(file.exists()){
				Picasso.with(ctx).load(file).into(imgView);
			}
			else{
				if(downloadToAssets){
					imgView.setTag(new LoadAndSave(imgView, file, assetPath, downloadToAssets));
					Picasso.with(ctx).load(ServerCalls.CDN_IMAGES_PATH+assetPath).into((LoadAndSave)imgView.getTag());
				}
				else{
					Picasso.with(ctx).load(ServerCalls.CDN_IMAGES_PATH+assetPath).into(imgView);//directly
				}
			}
		}		 
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	public void loadImageAsBg(Context ctx , final Target target , final String assetPath){
		if(assetPath==null || assetPath.isEmpty())
			return;
		try{
		    InputStream ims = ctx.getAssets().open("images/"+assetPath);
		    Picasso.with(ctx).load("file:///android_asset/images/"+assetPath).into(target);
		    return;
		}
		catch(IOException ex) {
			final File file = new File(ctx.getFilesDir().getParentFile().getPath()+"/images/"+assetPath);
			if(file.exists()){
				Picasso.with(ctx).load(file).into(target);
			}
			else{
				Picasso.with(ctx).load(ServerCalls.CDN_IMAGES_PATH+assetPath).into(new Target() {
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
	
//	public double getLevelFromPoints(double points){
//		return points;
////		2+n/3
////		increment: 3 3 4 4 4 5 5 5 6 6 6 7 7 7
////		sigma(3+k/3)
////		3 + k
////		3 6 10 14 18 23 28 33 39 45 51 58 65 72
////		3*n+(n/3)0 0 1 2 3 5 7 9 12 15 18 22 26)   (3*n+(n/3)+ N-1 shit)
////		2+(0 0 1 1 1 3 3 3 6 6 6 ) = 3+9*(1+2+3 ..) 3+9*(n*(n-1))/2 ;; 400+3*(1 2 3) (level-2)*(level-3)/2+(level-2)
////		200 400 700 1000 1300 1800 2300 2800 3600 4400 5200 ..
////		2 4 7 10 13 18 23 18 36 44 52
//	}
	
//	public double getPointsFromLevel(double level){
//		return 100*(2*level + (level*level - level)/6);
//	}
	
	float oneDp = -1;
	public float getInDp(int i) {
		if(oneDp==-1){
			oneDp = quizApp.getResources().getDimension(R.dimen.one_dp);
		}
		return i*oneDp;
	}
	
	float oneSp = -1;
	public float getInSp(int i) {
		if(oneSp==-1){
			oneSp = quizApp.getResources().getDimension(R.dimen.one_sp);
		}
		return i*oneSp;
	}
	public int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				quizApp.getResources().getDisplayMetrics());
	}
	public static ListView setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null)
	        return listView;

	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
	    int totalHeight = 0;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0)
	            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	    return listView;
	}
	
	public void blickAnimation(View view){
		 final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		    animation.setDuration(500); // duration - half a second
		    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		    animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		    animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
		    view.startAnimation(animation);
	}
	
	public DecimalFormat getDecimalFormatter(){
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(0);
		return df; 
	}
}
