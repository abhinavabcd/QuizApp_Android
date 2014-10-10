package com.amcolabs.quizapp.popups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.databaseutils.OfflineChallenge;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.gameutils.GameUtils;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.CircularImageView;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.amcolabs.quizapp.widgets.QuizAppMenuItem;





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
	
	public void showChallengeWinDialog(OfflineChallenge offlineChallenge ){
		 final Dialog d = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme); 
		 LinearLayout challengeWinLooseDialog = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.challenge_win_popup, null);

		 GothamTextView winLooseText;
		 GothamTextView challengeWithUser;
		 GothamTextView quizDesc;
		 
		 LinearLayout points1;
		 GothamTextView points1QuizPoints;
		 GothamTextView points1QuizPointsText;
		 GothamTextView points1QuizWinPoints;
		 GothamTextView points1QuizWinPointsText;
		 GothamTextView points1QuizLevelupPoints;
		 GothamTextView points1QuizLevelupPointsText;
		 GothamTextView points1QuizTotalPoints;
		 GothamTextView points1QuizTotalPointsText;
		 LinearLayout points2;
		 GothamTextView points2QuizPoints;
		 GothamTextView points2QuizPointsText;
		 GothamTextView points2QuizWinPoints;
		 GothamTextView points2QuizWinPointsText;
		 GothamTextView points2QuizLevelupPoints;
		 GothamTextView points2QuizLevelupPointsText;
		 GothamTextView points2QuizTotalPoints;
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

		challengeWithUser.setText(offlineChallenge.getFromUser(quizApp).name);
		challengeWithUser.setTextColor(quizApp.getConfig().getUniqueThemeColor(challengeWithUser.getText().toString()));
		
		quizDesc.setText(quizApp.getDataBaseHelper().getQuizById(offlineChallenge.getChallengeData(quizApp).quizId).name);
		quizDesc.setTextColor(quizApp.getConfig().getUniqueThemeColor(quizDesc.getText().toString()));
		
		int user1Points = GameUtils.getLastElement(offlineChallenge.getChallengeData(quizApp).userAnswers).whatUserGot;
		int user2Points = GameUtils.getLastElement(offlineChallenge.getChallengeData2(quizApp).userAnswers).whatUserGot;
		
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
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), (ImageView) quizMenuDialog.findViewById(R.id.quiz_icon) , quiz.assetPath , true);
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
		menuContainer.addView(getMenuItem(UiText.START_CONVERSATION.getValue(), 2, listener , Gravity.CENTER));
		((GothamTextView)dialogLayout.findViewById(R.id.descText)).setText(user.name); 
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), (ImageView) userMenu.findViewById(R.id.quiz_icon) , user.pictureUrl , true);
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
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getId()==R.id.button1){
					if(acceptListener!=null)
						acceptListener.onData(true);
				}
				else if(v.getId()==R.id.button2){
					if(acceptListener!=null)
						acceptListener.onData(false);
				}
				yesNoPopup.dismiss();
			}
		};
		((TextView) dialogLayout.findViewById(R.id.textView1)).setText(text);
		QuizAppMenuItem button1 = (QuizAppMenuItem) dialogLayout.findViewById(R.id.button1);
		if(possitiveText!=null){
			button1.setOnClickListener(listener);
			button1.setText(possitiveText);
		}
		else{
			button1.setVisibility(View.GONE);
		}
		QuizAppMenuItem button2 = (QuizAppMenuItem) dialogLayout.findViewById(R.id.button2);
		button2.setOnClickListener(listener);
		button2.setText(negetiveText);
		yesNoPopup.setContentView(dialogLayout);
		yesNoPopup.show();
		return yesNoPopup;
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

	public void challengeRequestedPopup(User user, Quiz quiz) {
		final Dialog d = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme); 
		 LinearLayout mainWrapper;
		 CircularImageView titleImage;
		 GothamTextView userWantsChalengeText;
		 GothamTextView quizName;
		 QuizAppMenuItem startChallenge;
		 QuizAppMenuItem closeButton;
		 
		LinearLayout baseLayout = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.user_wants_a_game, null);

		titleImage = (CircularImageView) baseLayout.findViewById(R.id.title_image);
		userWantsChalengeText = (GothamTextView) baseLayout.findViewById(R.id.user_wants_chalenge_text);
		quizName = (GothamTextView) baseLayout.findViewById(R.id.quiz_name);
		startChallenge = (QuizAppMenuItem) baseLayout.findViewById(R.id.start_challenge);
		closeButton = (QuizAppMenuItem) baseLayout.findViewById(R.id.close_button);
		
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), titleImage, user.pictureUrl, false);
		quizName.setText(quiz.name);
		userWantsChalengeText.setText(UiText.USER_WANTS_A_GAME.getValue(user.name));
		startChallenge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		d.setContentView(baseLayout);
		d.show();
		
	}
	Dialog  menuDialog = null;
	public void showMenu(final HashMap<Integer, UiText> map) {
		if(menuDialog==null){
			menuDialog = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme); 
			ScrollView dialogLayout = (ScrollView)quizApp.getActivity().getLayoutInflater().inflate(R.layout.menu_items, null);
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
			container.setOnClickListener(listener);
			
			dialogLayout.setOnClickListener(listener);
			menuDialog.setContentView(dialogLayout);
		}	
		if(menuDialog.isShowing()){
			return;
		}
		menuDialog.show();
	}

	private View getMenuItem(String text , int id , OnClickListener listener , int gravity) {

		GothamTextView t = new GothamTextView(quizApp.getActivity());
		t.setText(text);
		t.setId(id);
		t.setTextSize(25);
		//t.setAllCaps(true);
		t.setPadding(10, 10, 10, 10);
		t.setGravity(gravity);
		t.setTextColor(quizApp.getConfig().getUniqueThemeColor(t.getText().toString()));
		t.setOnClickListener(listener);
		return t;
	}
}
