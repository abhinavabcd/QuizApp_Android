package com.amcolabs.quizapp.uiutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amcolabs.quizapp.MainActivity;
import com.amcolabs.quizapp.NotificationReciever;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.OfflineChallenge;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.serverutils.ServerCalls;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.squareup.picasso.Picasso;


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
		QUESTIONS_BONUS("%dx Bonus"),
		QUESTION("Question %s"), 
		LEVEL("Level"), 
		RECENT_QUIZZES("Recently Updated Quizzes"),
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
//		SELECT_FRIENDS_TO_CHALLENGE("Select Friends to Challenge"),
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
		UNEXPECTED_ERROR("Unexpected Error"), YOU_CHALLENGED("You challenged"),
		CANNOT_OFFLINE_CHALLENGE_PRIVATE_USERS("Cannot send an offline challenge private Users"), CLICK_TO_NEW_NOTIFICATIONS("Open the app to sync updates"), 
		USER_NAME("%s"), 
		IN_QUIZ("%s"),
		QUIZ_WITH_SCORE("with %d points"), 
		OFFLINE_CHALLENGES("Offline Challenges"),
		QUIZ_STATS("Quiz Stats"),
		TOTAL_MATCHES_PLAYED("Total Matches Played in each Category"),
		QUIZ_LEVEL_DISTRIBUTION("Quiz Levels Distribution"),
		QUIZ_LEVEL("Quiz Level"), 
		DO_YOU_START_CHALLENGE("Do you want to start the challenge?"), 
		START("Start"), 
		CANCEL("Cancel"), COMPLETED_CHALLENGE("Challenge Completed"), 
		YOU_WON_LOOSE_CHALLENGE_FEED("%s! , here are the <a href='offlineChallengeId/%s'>Challenge Details</a>"),
		USER_FEED("User Feed"),
		FRIEND_USER_STARTED_QUIZAPP("Your Friend %s started using quizApp <a href='userProfile/%s'>view profile</a>"), 
		CONENCT_WITH_GOOGLE("Connect With Google"),
		INVITE_YOUR_FRIENDS("Invite your <a href='googlePlusInvite/friends'>Google Friends</a>"),
		CONENCT_WITH_FACEBOOK("Connect with Facebook"),
		INVITE_YOUR_FB_FRIENDS("Invite Your <a href='facebookInvite/friends'>Facebook Friends</a>"),
		CONNECTING("Connecting to Google"),
		USER_WANTS_A_GAME("%s want to have a game with you"), 
		NO_FEED_AVAILABLE("No Recent Feed"),
		FRIENDS("Friends & Challenge"), NO_FRIENDS_SEARCH_AND_SUBSCRIBE("You have no Friends , Search and Subscribe"),
		NO_RANKINGS_AVAILABLE("No Ranking Data Available"),
		YOU_LEVELED_UP("You have level up'ed in %s"),
		YOU_LOST_TO_USER("You lost to <a href='userProfile/%s'>%s</a> in <a href='quizStats/%s'>%s</a> with %s xp Points"),	
		YOU_DEFEATED_USER("You defeated <a href='userProfile/%s'>%s</a> in <a href='quizStats/%s'>%s</a> with %s xp Points"),
		THE_QUIZ_WAS_TIE("It was a tie with <a href='userProfile/%s'>%s</a> in <a href='quizStats/%s'>%s</a> with %s xp Points"),
		THERE_WAS_SERVER_ERROR("There was a server error while you were playing with <a href='userProfile/%s'>%s</a> in <a href='quizStats/%s'>%s</a>"),
		YOU_UNLOCKED_BADGE("You unlocked a badge <a href='badge/%s'>%s</a>"),
		NO_ACTIVITY_AVAILABLE("No Activity Available"),
		ACTIVITY_LOG("Activity log"),
		PLAY_QUIZ("Play Multiplayer"),
		VIEW_HISTORY("View History"),
		SCORE_BOARDS("ScoreBoards"),
		SELECT_FRIENDS_TO_CHALLENGE_IN("Challenge friends in %s"),
		YOUR_FRIENDS("Your Friends"),
		VIEW_PROFILE("View Profile"),
		START_CONVERSATION("Chat%s"), 
		ALL("All"),
		FB("Facebook"),
		GPLUS("G+"),
		SET_STATUS("Update your status"),
		YOU_VS_USER("You vs %s"),
		LOCAL_QUIZ_HISTORY("Local Quiz History"),
		SELECT_TO_CHALLENGE_USER("Select Quiz to Challenge %s"),
		GPLUS_ERRROR("Error connecting to gplus"),
		FEATURE_COMMING_SOON("Feature Not available at the moment. Will be rolled out in future releases."),
		CHECKING_FOR_FRIENDS("Checking for Friends"),
		NEW_MESSAGE("New message: %s"), NEW_OFFLINE_CHALLENGE("New Offline Challenge in %s"),
		OFFLINE_CHALLENGE_FROM("%s challenge"),
		NEW_TEXT_AVAILABLE("Open to Check for Updates"), USER_WAITING_FOR_CHALLENGE("%s wants a challenge with you in %s "),
		LIVE_CHALLENGE("Live Challenge"), USER_SAYS("%s says:\n %s"),
		HAS_UNLOCKED_A_BADGE("Has Unlocked a Badge"), 
		USER_DOWNLOADING_ASSETS_WAITING("Waiting for other user to intialize"),
		DOWNLOADING_QUESTIONS_AND_ASSETS("Downloading quesitons and assets"),
		LOADING_QUESTIONS("Loading questions and assets"),
		CHECKING_IF_USER_IS_STILL_WAITING("Checking if user is still waiting"),
		IS_LOCKED("locked"), 
		NEW_OFFLINE_CHALLENGE_IN("You have a new OfflineChallenge from %s %s"),
		IN("in %s"), NO_QUIZ_DATA_AVAILABLE_PLAY_TO_SEE("No Quiz data available, play quizzes to see your stats."),
		UNSUBSCRIBE("Unsubscribe"),
		REMOVED_USER("Removed User"),
		SUBSCRIBE("Subscribe"), USER_SENT_YOU_MESSAGE("%s sent you a message"),
		REQUESTED("Requesting Rematch"), PROFILE("My Profile"),
		TOTAL_GAMES_PLAYED("Total Matches Played : %d"),
		ANSWERS_LIST("Your Answers");	
		
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
			if(!preloaderText.toString().endsWith(text)){
				preloaderText = preloaderText+ ("\n"+text);
				preloader.setMessage(preloaderText);
			}
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
	
	
	public Timer setInterval(int millis , final DataInputListener<Integer> listener) {
		// TODO Auto-generated constructor stub
		Timer timer = (new Timer());
		timer.schedule(new TimerTask() {
					int count =0;
					@Override
					public void run() {
						// TODO: NullPointerException after when pressing back button to exit quiz
							FragmentActivity activity = quizApp.getActivity();
							if(activity!=null)
						      (activity).runOnUiThread(new Runnable(){
		
						       @Override
						       public void run() {
						    	   listener.onData(++count);
						       }}
						       );
							else{
								Log.d("ERR","changes");
								this.cancel();
							}
					}
		}, 0, millis);
		return timer;
	}
	public static void generateNotification(Context pContext, String titleText, String message,Bundle b) {
		int notificationId = Config.NOTIFICATION_ID;
    	int type = b!=null ? b.getInt(Config.NOTIFICATION_KEY_MESSAGE_TYPE, -1):-1;
    	if(titleText==null){
    		titleText = pContext.getResources().getString(R.string.app_name);
    	}
    	switch(NotificationReciever.getNotificationTypeFromInt(type)){
				case DONT_KNOW:
					break;
				case NOTIFICATION_GCM_CHALLENGE_NOTIFICATION:
					break;
				case NOTIFICATION_GCM_GENERAL_FROM_SERVER:
					break;
				case NOTIFICATION_GCM_INBOX_MESSAGE:
					break;
				case NOTIFICATION_GCM_OFFLINE_CHALLENGE_NOTIFICATION:
					break;
				case NOTIFICATION_NEW_BADGE:
					break;
				case NOTIFICATION_SERVER_COMMAND:
					break;
				case NOTIFICATION_SERVER_MESSAGE:
					break;
				case NOTIFICATION_USER_CHALLENGE:
					break;
				default:
					break;
    	}
    	
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(pContext)
        		.setSmallIcon(R.drawable.ic_launcher).setContentTitle(titleText)
                        .setContentText(message);
        notificationBuilder.setWhen(System.currentTimeMillis()).setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        Intent resultIntent = new Intent(pContext, MainActivity.class);
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
		generateNotification(pContext, null ,  message, null);
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
	
	public static boolean loadImageIntoView(Context ctx , final ImageView imgView, final String assetPath , final boolean downloadToAssets){
			return loadImageIntoView(ctx , imgView,  assetPath , downloadToAssets , -1 , -1 , null);
	}
	
	public static boolean loadImageIntoView(Context ctx , final ImageView imgView, final String assetPath , final boolean downloadToAssets ,int width , int height , DataInputListener<Boolean> completedLoadingImage){
		if(assetPath==null || assetPath.isEmpty())
			return false;
		try{
			if(assetPath.startsWith("http://") || assetPath.startsWith("https://")){
				if(width> 0 && height>0)
					Picasso.with(ctx).load(assetPath).resize(width , height).into(imgView);
				else
					Picasso.with(ctx).load(assetPath).into(imgView);
			    return true;
			}
			
		    InputStream ims = ctx.getAssets().open("images/"+assetPath); //assets folder 
			if(width>0 && height>0)
				Picasso.with(ctx).load("file:///android_asset/images/"+assetPath).resize(width, height).into(imgView);
			else
				Picasso.with(ctx).load("file:///android_asset/images/"+assetPath).into(imgView);
			return true;
		}
		catch(IOException ex) {//files in SD card
			File file = new File(ctx.getFilesDir().getParentFile().getPath()+"/images/"+assetPath);
			if(file.exists()){
				Picasso.with(ctx).load(file).fit().centerCrop().into(imgView);
			}
			else{
				if(downloadToAssets){//from cdn 
					imgView.setTag(new LoadAndSave(imgView, file, assetPath, downloadToAssets, completedLoadingImage));
					if(width>0 && height>0)
						Picasso.with(ctx).load(ServerCalls.CDN_IMAGES_PATH+assetPath).error(R.drawable.error_image).resize(width , height).into((LoadAndSave)imgView.getTag());
					else{
						Picasso.with(ctx).load(ServerCalls.CDN_IMAGES_PATH+assetPath).error(R.drawable.error_image).into((LoadAndSave)imgView.getTag());
					}
				}
				else{
					Picasso.with(ctx).load(ServerCalls.CDN_IMAGES_PATH+assetPath).error(R.drawable.error_image).into(imgView);//directly
				}
			}
		}		 
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	public void loadImageAsBg(View view , final String assetPath , boolean downloadToAssets){
		if(assetPath==null || assetPath.isEmpty())
			return;
		LoadAndSave loadAndSave = null;
		//to prevent gc
	    view.setTag(loadAndSave = new LoadAndSave(view, null , null, downloadToAssets, null,true));

		try{
		    InputStream ims = quizApp.getActivity().getAssets().open("images/"+assetPath);
		    Picasso.with(quizApp.getContext()).load("file:///android_asset/images/"+assetPath).into((LoadAndSave)view.getTag());
		    return;
		}
		catch(IOException ex) {
			//try from external dir
			final File file = new File(quizApp.getContext().getFilesDir().getParentFile().getPath()+"/images/"+assetPath);
			if(file.exists()){
				Picasso.with(quizApp.getContext()).load(file).into((LoadAndSave)view.getTag());
			}
			else{
				Picasso.with(quizApp.getContext()).load(ServerCalls.CDN_IMAGES_PATH+assetPath).into((LoadAndSave)view.getTag());
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
	
	public static ListView setListViewHeightBasedOnChildren2(ListView myListView) {
	      ListAdapter myListAdapter = myListView.getAdapter();
	        if (myListAdapter == null || myListAdapter.getCount()==0) {
	            //do nothing return null
	            return myListView;
	        }
	        //set listAdapter in loop for getting final size
	        int totalHeight = 0;
	        for (int size = 0; size < myListAdapter.getCount(); size++) {
	            View listItem = myListAdapter.getView(size, null, myListView);
	            listItem.measure(0, 0);
	            totalHeight += listItem.getMeasuredHeight();
	        }
	      //setting listview item in chatListAdapter
	        ViewGroup.LayoutParams params = myListView.getLayoutParams();
	        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
	        myListView.setLayoutParams(params);
	        return myListView;
	}
	
	public void blickAnimation(View view){
		 final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		    animation.setDuration(500); // duration - half a second
		    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		    animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		    animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
		    view.startAnimation(animation);
	}
	
	protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span , final DataInputListener<String> clickListener){
	    int start = strBuilder.getSpanStart(span);
	    int end = strBuilder.getSpanEnd(span);
	    int flags = strBuilder.getSpanFlags(span);
	    ClickableSpan clickable = new ClickableSpan() {
	          public void onClick(View view) {
	        	  if(clickListener!=null)
	        		  clickListener.onData(span.getURL());
	        	  else{
	        		  genericLinkClickListener(span.getURL());
	        	  }
	          }
	    };
	    strBuilder.setSpan(clickable, start, end, flags);
	    strBuilder.removeSpan(span);
	}

	protected void genericLinkClickListener(String url) {
		// TODO Auto-generated method stub
		
	}

	public void setTextViewHTML(TextView text, String html , DataInputListener<String> clickListener){
	    CharSequence sequence = Html.fromHtml(html);
	    	text.setMovementMethod(LinkMovementMethod.getInstance());
	        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
	        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);   
	        for(URLSpan span : urls) {
	            makeLinkClickable(strBuilder, span, clickListener);
	        }
	    text.setText(strBuilder);       
	}
	
	public ValueFormatter getDecimalFormatter(){
		return new ValueFormatter() {
			DecimalFormat df = new DecimalFormat();
			@Override
			public String getFormattedValue(float value) {
				df.setMaximumFractionDigits(0);
				df.setMinimumFractionDigits(0);
				return df.format(value); 
			}
		};
	}
	
	public boolean hasNonZeroValues(ArrayList<BarEntry> yVals){
		for(int k=0;k<yVals.size();k++){
			if(yVals.get(k).getVal() > 0){
				return true;
			}
		}
		return false;
	}
	
	public static Point getScreenDimetions(QuizApp quizApp){
		WindowManager w = quizApp.getActivity().getWindowManager();
		Point point = new Point();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
		    Point size = new Point();
		    w.getDefaultDisplay().getSize(size);
		} else {
		    Display d = w.getDefaultDisplay();
		    point.x = d.getWidth();
		    point.y = d.getHeight();
		}
	    return point;
	}
	
	public void populateViews(LinearLayout linearLayout, View[] views, Context context, View extraView){
	    extraView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	    // kv : May need to replace 'getSherlockActivity()' with 'this' or 'getActivity()'
	    Display display = quizApp.getActivity().getWindowManager().getDefaultDisplay();
	    linearLayout.removeAllViews();
	    int maxWidth = display.getWidth() - extraView.getMeasuredWidth() - 20;

	    linearLayout.setOrientation(LinearLayout.VERTICAL);

	    LinearLayout.LayoutParams params;
	    LinearLayout newLL = new LinearLayout(context);
	    newLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	    newLL.setGravity(Gravity.LEFT);
	    newLL.setOrientation(LinearLayout.HORIZONTAL);

	    int widthSoFar = 0;

	    for (int i = 0; i < views.length; i++)
	    {
	        LinearLayout LL = new LinearLayout(context);
	        LL.setOrientation(LinearLayout.HORIZONTAL);
	        LL.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
	        LL.setLayoutParams(new ListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

	        views[i].measure(0, 0);
	        params = new LinearLayout.LayoutParams(views[i].getMeasuredWidth(), LayoutParams.WRAP_CONTENT);
	        params.setMargins(5, 0, 5, 0);

	        LL.addView(views[i], params);
	        LL.measure(0, 0);
	        widthSoFar += views[i].getMeasuredWidth();
	        if (widthSoFar >= maxWidth)
	        {
	            linearLayout.addView(newLL);

	            newLL = new LinearLayout(context);
	            newLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	            newLL.setOrientation(LinearLayout.HORIZONTAL);
	            newLL.setGravity(Gravity.LEFT);
	            params = new LinearLayout.LayoutParams(LL.getMeasuredWidth(), LL.getMeasuredHeight());
	            newLL.addView(LL, params);
	            widthSoFar = LL.getMeasuredWidth();
	        }
	        else
	        {
	            newLL.addView(LL);
	        }
	    }
	    linearLayout.addView(newLL);
	}

	public void fetchAndShowOfflineChallengePopup(String offlineChallengeId,
			final DataInputListener<OfflineChallenge> dataInputListener) {	
		 
		OfflineChallenge offlineChallenge = quizApp.getDataBaseHelper().getOfflineChallengeByChallengeId(offlineChallengeId);
		if(offlineChallenge!=null && offlineChallenge.isCompleted()){//already fetched , no server call again , no automatic popup
			dataInputListener.onData(offlineChallenge);
			return;
		}

		quizApp.getServerCalls().getOfflineChallenge(offlineChallengeId, new DataInputListener<OfflineChallenge>(){
			@Override 
			public String onData(OfflineChallenge offlineChallenge) {
				if(offlineChallenge!=null){
					offlineChallenge.setCompleted(true);
					//show popup that user has completed and win/lost
					quizApp.getDataBaseHelper().updateOfflineChallenge(offlineChallenge);
					quizApp.getStaticPopupDialogBoxes().showChallengeWinDialog(offlineChallenge);
				}
				dataInputListener.onData(offlineChallenge);
				return null;
			}
		}); 

	}
	
}
