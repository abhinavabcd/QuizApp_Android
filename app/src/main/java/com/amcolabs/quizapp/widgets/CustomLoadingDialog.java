package com.amcolabs.quizapp.widgets;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.amcolabs.quizapp.R;

/**
 * Created by abhinav on 7/12/15.
 */
public class CustomLoadingDialog extends ProgressDialog {
    private GothamTextView messageTextView;
    private CharSequence message;
    public CustomLoadingDialog(Context context, CharSequence text) {
        super(context);
        setIndeterminate(true);
        setCancelable(false);
        message = text;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog_ui);
        messageTextView = (GothamTextView)findViewById(R.id.progress_bar_text);
        if(message!=null)
            messageTextView.setText(message);
    }

    @Override
    public void setMessage(CharSequence message) {
        if(messageTextView!=null)
            messageTextView.setText(message);
    }
}