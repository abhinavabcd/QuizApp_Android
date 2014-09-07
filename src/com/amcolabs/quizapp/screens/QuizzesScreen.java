package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.adapters.QuizItemListAdapter;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.widgets.GothamTextView;

public class QuizzesScreen extends Screen {

	private List<Quiz> quizzes;


	public QuizzesScreen(AppController controller) {
		super(controller);
	}
	
	public void addQuizzesToList(String title ,List<Quiz> quizzes , DataInputListener<Quiz> clickListener){
		this.quizzes = quizzes;
		final QuizItemListAdapter quizAdaptor = new QuizItemListAdapter(getApp(),0,quizzes, clickListener);
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, null);
		EditText searchText = (EditText) lView.findViewById(R.id.search_text);
		GothamTextView titleView = (GothamTextView) lView.findViewById(R.id.title_text_view);
		titleView.setText(title);
		searchText.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		        quizAdaptor.getFilter().filter(cs);  
		    }
		     
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
		            int arg3) {
		    }

			@Override
			public void afterTextChanged(Editable s) {
			}
	
		});
		((ListView) lView.findViewById(R.id.listView)).setAdapter(quizAdaptor);
		addView(lView);
	}
	@Override
	public boolean showMenu() {
		// TODO Auto-generated method stub
		return true;
	}
}
