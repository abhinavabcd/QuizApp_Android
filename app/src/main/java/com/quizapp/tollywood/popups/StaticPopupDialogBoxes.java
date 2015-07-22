package com.quizapp.tollywood.popups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quizapp.tollywood.QuizApp;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.User;
import com.quizapp.tollywood.NotificationReciever.NotificationPayload;
import com.quizapp.tollywood.appcontrollers.ProgressiveQuizController;
import com.quizapp.tollywood.configuration.Config;
import com.quizapp.tollywood.databaseutils.Badge;
import com.quizapp.tollywood.databaseutils.OfflineChallenge;
import com.quizapp.tollywood.databaseutils.Quiz;
import com.quizapp.tollywood.datalisteners.DataInputListener;
import com.quizapp.tollywood.gameutils.GameUtils;
import com.quizapp.tollywood.uiutils.CircleTransform;
import com.quizapp.tollywood.uiutils.UiUtils;
import com.quizapp.tollywood.uiutils.UiUtils.UiText;
import com.quizapp.tollywood.widgets.GothamButtonView;
import com.quizapp.tollywood.widgets.GothamTextView;
import com.quizapp.tollywood.widgets.QuizAppMenuItem;
import com.squareup.picasso.Picasso;


public class StaticPopupDialogBoxes {
	QuizApp quizApp ;
	public StaticPopupDialogBoxes(QuizApp quizApp){
		this.quizApp = quizApp;
	}
	public static ArrayList<Dialog> openDialogs= new ArrayList<Dialog>();
	private static class TrackedDialog extends Dialog{

		public TrackedDialog(Context context) {
			
			super(context);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void show() {
//			openDialogs.add(this);
			super.show();
		}
		@Override
		public void dismiss() {
//			openDialogs.remove(this);
			super.dismiss();
		}
		
	}
	
	public void showChallengeWinDialog(final OfflineChallenge offlineChallenge ){
		 final Dialog d = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme); 
		 final LinearLayout challengeWinLooseDialog = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.challenge_win_popup, null);

		 final GothamTextView winLooseText;
		 final GothamTextView challengeWithUser;
		 final GothamTextView quizDesc;
		 
		 LinearLayout points1;
		 final GothamTextView points1QuizPoints;
		 GothamTextView points1QuizPointsText;
		 final GothamTextView points1QuizWinPoints;
		 GothamTextView points1QuizWinPointsText;
		 final GothamTextView points1QuizLevelupPoints;
		 GothamTextView points1QuizLevelupPointsText;
		 final GothamTextView points1QuizTotalPoints;
		 GothamTextView points1QuizTotalPointsText;
		 LinearLayout points2;
		 final GothamTextView points2QuizPoints;
		 GothamTextView points2QuizPointsText;
		 final GothamTextView points2QuizWinPoints;
		 GothamTextView points2QuizWinPointsText;
		 final GothamTextView points2QuizLevelupPoints;
		 GothamTextView points2QuizLevelupPointsText;
		 final GothamTextView points2QuizTotalPoints;
		 GothamTextView points2QuizTotalPointsText;

		winLooseText = (GothamTextView) challengeWinLooseDialog.findViewById(R.id.win_loose_text);
		challengeWithUser = (GothamTextView) challengeWinLooseDialog.findViewById(R.id.challenge_with_user);
		quizDesc = (GothamTextView) challengeWinLooseDialog.findViewById(R.id.quiz_desc);
		
		ImageView closeButton = (ImageView)challengeWinLooseDialog.findViewById(R.id.close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss();
			}
		});
		points1 = (LinearLayout) challengeWinLooseDialog.findViewById(R.id.points_1);
		points1QuizPoints = (GothamTextView) points1.findViewById(R.id.quizPoints);
		(points1QuizPointsText = (GothamTextView) points1.findViewById(R.id.quizPointsText)).setTextColor(Color.WHITE);
		points1QuizWinPoints = (GothamTextView) points1.findViewById(R.id.quizWinPoints);
		(points1QuizWinPointsText = (GothamTextView) points1.findViewById(R.id.quizWinPointsText)).setTextColor(Color.WHITE);;
		points1QuizLevelupPoints = (GothamTextView) points1.findViewById(R.id.quizLevelupPoints);
		(points1QuizLevelupPointsText = (GothamTextView) points1.findViewById(R.id.quizLevelupPointsText)).setTextColor(Color.WHITE);;
		points1QuizTotalPoints = (GothamTextView) points1.findViewById(R.id.quizTotalPoints);
		(points1QuizTotalPointsText = (GothamTextView) points1.findViewById(R.id.quizTotalPointsText)).setTextColor(Color.WHITE);;


		points2 = (LinearLayout) challengeWinLooseDialog.findViewById(R.id.points_2);
		points2QuizPoints = (GothamTextView) points2.findViewById(R.id.quizPoints);
		(points2QuizPointsText = (GothamTextView) points2.findViewById(R.id.quizPointsText)).setTextColor(Color.WHITE);;
		points2QuizWinPoints = (GothamTextView) points2.findViewById(R.id.quizWinPoints);
		(points2QuizWinPointsText = (GothamTextView) points2.findViewById(R.id.quizWinPointsText)).setTextColor(Color.WHITE);;
		points2QuizLevelupPoints = (GothamTextView) points2.findViewById(R.id.quizLevelupPoints);
		(points2QuizLevelupPointsText = (GothamTextView) points2.findViewById(R.id.quizLevelupPointsText)).setTextColor(Color.WHITE);;
		points2QuizTotalPoints = (GothamTextView) points2.findViewById(R.id.quizTotalPoints);
		(points2QuizTotalPointsText = (GothamTextView) points2.findViewById(R.id.quizTotalPointsText)).setTextColor(Color.WHITE);;


		quizApp.getDataBaseHelper().getAllUsersByUid(Arrays.asList(offlineChallenge.getToUserUid()), new DataInputListener<Boolean>() {
					@Override
					public String onData(Boolean s) {
						challengeWithUser.setText(quizApp.cachedUsers.get(offlineChallenge.getToUserUid()).getName());
						challengeWithUser.setTextColor(quizApp.getConfig().getUniqueThemeColor(challengeWithUser.getText().toString()));
						return super.onData(s);
					}
				}
		);
		quizDesc.setText(quizApp.getDataBaseHelper().getQuizById(offlineChallenge.getChallengeData(quizApp).quizId).name);
		quizDesc.setTextColor(quizApp.getConfig().getUniqueThemeColor(quizDesc.getText().toString()));


		int user1Points = 0;
		int user2Points = 0;
		try{
			user1Points = GameUtils.getLastElement(offlineChallenge.getChallengeData(quizApp).userAnswers).whatUserGot;
			user2Points  = GameUtils.getLastElement(offlineChallenge.getChallengeData2(quizApp).userAnswers).whatUserGot;
		}
		catch (Exception e){

		}

		int user1Bonus = (int) (user1Points>user2Points?Config.QUIZ_WIN_BONUS:0);
		if(user1Points==user2Points){
			winLooseText.setText(UiText.TIE_QUIZ_MESAGE.getValue());
		}
		else{
			winLooseText.setText(user1Points>user2Points ? UiText.WON_QUIZ_MESSAGE.getValue(): UiText.LOST_QUIZ_MESAGE.getValue());
			winLooseText.setTextColor(quizApp.getConfig().getUniqueThemeColor(winLooseText.getText().toString()));
		}
		
		int user1CurrentPoints = quizApp.getUser().getStats().get(offlineChallenge.getChallengeData(quizApp).quizId);
		int user1LevelUpBonus = 0;//(int) (GameUtils.didUserLevelUp(user1CurrentPoints , user1CurrentPoints+ user1Bonus+user1Points)?Config.QUIZ_LEVEL_UP_BONUS:0);
		int user1TotalPoints = user1Points+user1Bonus+user1LevelUpBonus;
		points1QuizLevelupPoints.setText("-"); 
		points1QuizPoints.setText(user1Points+"");
		points1QuizWinPoints.setText( user1Bonus+"");
		points1QuizTotalPoints.setText(user1TotalPoints+"");
		
		int user2Bonus = (int) (user2Points>user1Points?Config.QUIZ_WIN_BONUS:0);


		int user2TotalPoints = user2Points+user2Bonus;
		
		points2QuizPoints.setText(user2Points+"");
		points2QuizWinPoints.setText( user2Bonus+"");
		points2QuizLevelupPoints.setText("-"); 
		points2QuizTotalPoints.setText(user2TotalPoints+"");
		
		
		d.setContentView(challengeWinLooseDialog);
		d.show();
	}
	
	
	
	Dialog quizMenuDialog = null;
	DataInputListener<Integer> onQuizItemSelectedListener = null; 
	public void showQuizSelectMenu(Quiz quiz, DataInputListener<Integer> menuListener){
		onQuizItemSelectedListener = menuListener;
		if(quizMenuDialog==null){
			quizMenuDialog = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme); 
			quizMenuDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					onQuizItemSelectedListener = null; //free it 
				}
			});
			LinearLayout dialogLayout = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.quiz_menu, null);
			dialogLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					quizMenuDialog.dismiss();
				}
			});
			quizMenuDialog.setContentView(dialogLayout);
			LinearLayout menuContainer = (LinearLayout)dialogLayout.findViewById(R.id.menu_items_container);

			OnClickListener listener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(onQuizItemSelectedListener!=null){
							onQuizItemSelectedListener.onData(v.getId());
					}
					quizMenuDialog.dismiss();
				}
			};
			menuContainer.addView(getMenuItem(UiText.PLAY_QUIZ.getValue(), 1, listener , Gravity.CENTER));
			menuContainer.addView(getMenuItem(UiText.VIEW_HISTORY.getValue(), 2, listener , Gravity.CENTER));
			menuContainer.addView(getMenuItem(UiText.CHALLENGE.getValue(), 3, listener, Gravity.CENTER));
			menuContainer.addView(getMenuItem(UiText.SCORE_BOARDS.getValue(), 4, listener, Gravity.CENTER));
		}
		else if(quizMenuDialog.isShowing()){
			return;
		}
		((GothamTextView)quizMenuDialog.findViewById(R.id.descText)).setText(quiz.name); 
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), (ImageView) quizMenuDialog.findViewById(R.id.quiz_icon) , quiz.assetPath , true, new CircleTransform());
		quizMenuDialog.show();
	}

	public void showUserSelectedMenu(User user, final DataInputListener<Integer> onUserSelectedListener){
		final Dialog userMenu = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme); 
		LinearLayout dialogLayout = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.quiz_menu, null);
		dialogLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userMenu.dismiss();
			}
		});
		userMenu.setContentView(dialogLayout);
		LinearLayout menuContainer = (LinearLayout)dialogLayout.findViewById(R.id.menu_items_container);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onUserSelectedListener.onData(v.getId());
				userMenu.dismiss(); 
			} 
		};
		menuContainer.addView(getMenuItem(UiText.VIEW_PROFILE.getValue(), 1, listener , Gravity.CENTER));
		menuContainer.addView(getMenuItem(UiText.START_CONVERSATION.getValue(""), 2, listener , Gravity.CENTER));
		menuContainer.addView(getMenuItem(UiText.CHALLENGE.getValue(), 3, listener , Gravity.CENTER));
		menuContainer.addView(getMenuItem(user.isFriend(quizApp) ? UiText.UNSUBSCRIBE.getValue() : UiText.SUBSCRIBE.getValue(), 4, listener , Gravity.CENTER));
		((GothamTextView)dialogLayout.findViewById(R.id.descText)).setText(user.getName());
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), (ImageView) userMenu.findViewById(R.id.quiz_icon) , user.pictureUrl , true, new CircleTransform());
		userMenu.show();
	}

	
	public static class YesNoDialog extends Dialog{

		private DataInputListener<Boolean> acceptListener;
		public YesNoDialog(Context context , int resId, DataInputListener<Boolean> acceptListener) {
			super(context , resId);
			this.acceptListener = acceptListener;
		}
		public void dismiss() {
			if(acceptListener!=null)
				acceptListener.onData(false);
			super.dismiss();
		}
		public void dismissQuietly() {
			super.dismiss();
		}
	}
	
	public YesNoDialog yesOrNo(String text, String possitiveText , String negetiveText , final DataInputListener<Boolean> acceptListener) {
		final YesNoDialog yesNoPopup = new YesNoDialog(quizApp.getContext(),R.style.CustomDialogTheme, acceptListener);
		LinearLayout dialogLayout = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.full_screen_dialog, null);
		((TextView) dialogLayout.findViewById(R.id.textView1)).setText(text);
		QuizAppMenuItem button1 = (QuizAppMenuItem) dialogLayout.findViewById(R.id.button1);
		if(possitiveText!=null){
			button1.setVisibility(View.VISIBLE);
			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(acceptListener!=null)
						acceptListener.onData(true);	
					yesNoPopup.dismissQuietly();
					}
				
			});
			button1.setText(possitiveText);
		}
		else{
			button1.setVisibility(View.GONE);
		}
		QuizAppMenuItem button2 = (QuizAppMenuItem) dialogLayout.findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(acceptListener!=null)
					acceptListener.onData(false);		
				yesNoPopup.dismissQuietly();
				}
		});
		button2.setText(negetiveText);
		yesNoPopup.setContentView(dialogLayout);
		yesNoPopup.show();
		return yesNoPopup;
	}

	
	public void showUnlockedBadge(String badgeId, boolean addDetail){
		showUnlockedBadge(quizApp.getDataBaseHelper().getBadgeById(badgeId), addDetail);
	}
	public void showUnlockedBadge(Badge badge, boolean addDetail){
		showUnlockedBadge(badge, addDetail, null);
	}
	public void showUnlockedBadge(Badge badge, boolean addDetail , String titleText) {
		final Dialog d = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme); 
		LinearLayout badgeLayout = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.badge_detail_view, null);
		ImageView closeButton = (ImageView)badgeLayout.findViewById(R.id.close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss();
			}
		});

		if(titleText==null){
			((GothamTextView) badgeLayout.findViewById(R.id.title)).setVisibility(View.GONE);
		}
		else
			((GothamTextView) badgeLayout.findViewById(R.id.title)).setText(UiUtils.UiText.NEW_BADGE_UNLOCKED_MESSAGE.getValue());
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), (ImageView)badgeLayout.findViewById(R.id.badgeImage),badge.getAssetPath(), true);
		((GothamTextView)badgeLayout.findViewById(R.id.badgeName)).setText(badge.getName());
		if(addDetail)
			((GothamTextView)badgeLayout.findViewById(R.id.badgeDescription)).setText(badge.getDescription());
		else{
			((GothamTextView)badgeLayout.findViewById(R.id.badgeDescription)).setVisibility(View.GONE);
		}
		d.setContentView(badgeLayout);
		d.show();
	}

	public void challengeRequestedPopup(User user, Quiz quiz , final NotificationPayload payload , final DataInputListener<Boolean> listener) {
		final Dialog d = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme); 
		 LinearLayout mainWrapper;
		 ImageView titleImage;
		 GothamTextView userWantsChalengeText;
		 GothamTextView quizName;
		 QuizAppMenuItem startChallenge;
		 QuizAppMenuItem closeButton;
		 
		LinearLayout baseLayout = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.user_wants_a_game, null);

		titleImage = (ImageView) baseLayout.findViewById(R.id.title_image);
		userWantsChalengeText = (GothamTextView) baseLayout.findViewById(R.id.user_wants_chalenge_text);
		quizName = (GothamTextView) baseLayout.findViewById(R.id.quiz_name);
		startChallenge = (QuizAppMenuItem) baseLayout.findViewById(R.id.start_challenge);
		closeButton = (QuizAppMenuItem) baseLayout.findViewById(R.id.close_button);
		
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), titleImage, user.pictureUrl, false, new CircleTransform());
		quizName.setText(quiz.name);
		userWantsChalengeText.setText(UiText.USER_WANTS_A_GAME.getValue(user.getName()));
		startChallenge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) ;
				listener.onData(true);
				((ProgressiveQuizController) quizApp.loadAppController(ProgressiveQuizController.class)).startChallengedLiveGame(payload.serverId, payload.quizPoolWaitId, payload.quizId);
				;
				d.dismiss();
			}
		});
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onData(false);
				d.dismiss();
			}
		}) ;
		d.setContentView(baseLayout);
		d.show();
		
	}

	public void showCopyRightActivity(){
		final Dialog dialog = new Dialog(quizApp.getContext(), R.style.CustomDialogTheme);
		TextView textView = new TextView(quizApp.getContext());
		textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		textView.setVerticalScrollBarEnabled(true);
		textView.setText(quizApp.getContext().getResources().getString(R.string.copyrightText));
		textView.setTextSize(12);
		textView.setTextColor(Color.WHITE);
		dialog.setContentView(textView);
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.cancel();
			}
		});
		dialog.setCancelable(true);
		dialog.show();
	}


	Dialog  menuDialog = null;
	public void showMenu(final HashMap<Integer, UiText> map) {
		if(menuDialog==null){
			menuDialog = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme); 
			RelativeLayout dialogLayout = (RelativeLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.menu_items, null);
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(map.containsKey(v.getId()))
						quizApp.onMenuClick(v.getId());
					menuDialog.dismiss();
				}
			};
			LinearLayout container = (LinearLayout)dialogLayout.findViewById(R.id.menu_items_container);
			for(int id : map.keySet()){
	
				container.addView(getMenuItem(map.get(id).getValue().toUpperCase(Locale.getDefault()) , id, listener , Gravity.LEFT));
			}

			//add music on/off
			final ImageView musicButton = (ImageView) dialogLayout.findViewById(R.id.music_icon);
			musicButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					quizApp.toggleMusic();
					showMusicStateIcon(musicButton);
				}
			});
			showMusicStateIcon(musicButton);

			container.setOnClickListener(listener);
			
			dialogLayout.setOnClickListener(listener);
			menuDialog.setContentView(dialogLayout);
		}	
		if(menuDialog.isShowing()){
			return;
		}
		menuDialog.show();
	}

	private void showMusicStateIcon(final ImageView musicButton){
		int musicState = quizApp.getUserDeviceManager().getPreference(Config.PREF_MUSIC_STATE, 1);
		if(musicState==1) {
			Picasso.with(quizApp.getContext()).load(R.drawable.sound_on).into(musicButton);
		}
		else{
			Picasso.with(quizApp.getContext()).load(R.drawable.sound_off).into(musicButton);
		}

	}
	private View getMenuItem(String text , int id , OnClickListener listener , int gravity) {

		GothamTextView t = new GothamTextView(quizApp.getActivity());
		t.setText(text);
		t.setId(id);
		t.setTextSize(25);
		//t.setAllCaps(true);
		t.setPadding(10, 20, 10, 10);
		t.setGravity(gravity);
		t.setTextColor(quizApp.getConfig().getUniqueThemeColor(t.getText().toString()));
		t.setOnClickListener(listener);
		return t;
	}

	public void promptInput(String title, int charLimit, String prevStatus, final DataInputListener<String> dataInputListener) {
		final Dialog prompt = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme3); 
		 ImageView closeButton;
		 GothamTextView titleView;
		 final EditText messageContent;
		 GothamButtonView okButton;
		 LinearLayout baseLayout = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.input_prompt, null);
		 
		closeButton = (ImageView) baseLayout.findViewById(R.id.close_button);
		titleView = (GothamTextView) baseLayout.findViewById(R.id.title);
		titleView.setText(title);
		messageContent = (EditText) baseLayout.findViewById(R.id.messageContent);
		messageContent.setText(prevStatus);
		okButton = (GothamButtonView) baseLayout.findViewById(R.id.ok_button);

		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				prompt.dismiss();
			}
		});
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dataInputListener.onData(messageContent.getText().toString());
				prompt.dismiss();
			}
		});		
		prompt.setContentView(baseLayout);
		prompt.show();
	}
}

