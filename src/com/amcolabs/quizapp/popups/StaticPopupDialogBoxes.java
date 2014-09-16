package com.amcolabs.quizapp.popups;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.UserDeviceManager;
import com.amcolabs.quizapp.datalisteners.DataInputListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;



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

}
