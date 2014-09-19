package com.amcolabs.quizapp.popups;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.UserDeviceManager;
import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.widgets.FancyDialog;
import com.amcolabs.quizapp.widgets.GothamButtonView;
import com.amcolabs.quizapp.widgets.GothamTextView;



class AlertMessage extends DialogFragment {
	private String message;
	private DataInputListener<Boolean> listener;

	public  AlertMessage(String message , DataInputListener<Boolean> listener){
		this.message  = message;
		this.listener = listener;
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return super.onCreateView(inflater, container, savedInstanceState);//inflater.inflate(R.layout.purchase_items, container, false);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.message)
               .setPositiveButton("okay", new android.content.DialogInterface.OnClickListener(){
   				@Override
   				public void onClick(DialogInterface dialog, int which) {
   					if (listener!=null)listener.onData(true);
   				}
        });      
        Dialog dialog = builder.create();//super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}


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
	
	public static void alertPrompt(FragmentManager A, String message , DataInputListener<Boolean> listener){
		if (!UserDeviceManager.isRunning()) return;
		(new AlertMessage(message, listener)).show(A, "");
	}
	
	public static Dialog alertPrompt(Activity A, String message , final DataInputListener<Boolean> listener){
		if (!UserDeviceManager.isRunning()) return null;
        AlertDialog.Builder builder = new AlertDialog.Builder(A);
        builder.setMessage(message)
               .setPositiveButton("OK", new android.content.DialogInterface.OnClickListener(){
   				@Override
   				public void onClick(DialogInterface dialog, int which) {
   					if (listener!=null)listener.onData(true);
   				}
        });      
        Dialog dialog = builder.create();//super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(!A.isFinishing()){
        	dialog.setCanceledOnTouchOutside(false);
//    		openDialogs.add(dialog);
			dialog.show();
        }
        else{
        	dialog.dismiss();
        }
        return dialog;
	}
	
	public void showQuizSelectMenu(final DataInputListener<Integer> menuListener){
		final Dialog d = new Dialog(quizApp.getContext(),R.style.CustomDialogTheme); 
		LinearLayout dialogLayout = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.quiz_menu, null);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getId()==R.id.icon1){
					menuListener.onData(1);
				}
				else if(v.getId()==R.id.icon2){
					menuListener.onData(2);
				}
				else if(v.getId()==R.id.icon3){
					menuListener.onData(3);
				}
				else if(v.getId()==R.id.icon4){
					menuListener.onData(4);
				}
				d.dismiss();
			}
		};
		dialogLayout.setOnClickListener(listener);
		dialogLayout.findViewById(R.id.icon1).setOnClickListener(listener);
		dialogLayout.findViewById(R.id.icon2).setOnClickListener(listener);
		dialogLayout.findViewById(R.id.icon3).setOnClickListener(listener);
		dialogLayout.findViewById(R.id.icon4).setOnClickListener(listener);
		d.setContentView(dialogLayout);
		d.show();
	}
	
	class YesNoDialog extends Dialog{

		private DataInputListener<Boolean> acceptListener;
		public YesNoDialog(Context context , int resId, DataInputListener<Boolean> acceptListener) {
			super(context , resId);
			this.acceptListener = acceptListener;
		}
		public void dismiss() {
			acceptListener.onData(false);
			super.dismiss();
		}
		public void dismissQuietly() {
			super.dismiss();
		}
	}
	
	YesNoDialog yesNoPopup = null; 
	public void yesOrNo(String text, String possitiveText , String negetiveText , final DataInputListener<Boolean> acceptListener) {
		yesNoPopup = new YesNoDialog(quizApp.getContext(),R.style.CustomDialogTheme, acceptListener);
		LinearLayout dialogLayout = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.full_screen_dialog, null);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getId()==R.id.button1){
					acceptListener.onData(true);
				}
				else if(v.getId()==R.id.button2){
					acceptListener.onData(false);
				}
				yesNoPopup.dismiss();
			}
		};
		((TextView) dialogLayout.findViewById(R.id.textView1)).setText(text);
		GothamButtonView button1 = (GothamButtonView) dialogLayout.findViewById(R.id.button1);
		button1.setOnClickListener(listener);
		button1.setText(possitiveText);
		
		GothamButtonView button2 = (GothamButtonView) dialogLayout.findViewById(R.id.button2);
		button2.setOnClickListener(listener);
		button2.setText(negetiveText);
		yesNoPopup.setContentView(dialogLayout);
		yesNoPopup.show();
	}

	public void removeRematchRequestScreen() {
		if(yesNoPopup!=null)
			yesNoPopup.dismissQuietly();
		yesNoPopup= null;
	}
	
	public void showUnlockedBadge(Badge badge) {
		FancyDialog dialog = new FancyDialog(quizApp.getContext());
		dialog.setTitle(UiUtils.UiText.NEW_BADGE_UNLOCKED_MESSAGE.getValue());
		RelativeLayout badgeLayout = (RelativeLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.badge_small, null);
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), (ImageView)badgeLayout.findViewById(R.id.badgeImage),badge.getAssetPath(), true);
		((GothamTextView)badgeLayout.findViewById(R.id.badgeName)).setText(badge.getName());
		dialog.setContent(badgeLayout);
		dialog.showTitle();
		dialog.hideAlertButtons();
		dialog.show();
	}
}
