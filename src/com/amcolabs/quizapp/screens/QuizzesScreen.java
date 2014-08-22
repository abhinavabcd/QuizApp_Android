package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

import android.widget.LinearLayout;
import android.widget.ListView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.adapters.QuizItemListAdapter;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;

public class QuizzesScreen extends Screen {

	private ArrayList<Quiz> quizzes;


	public QuizzesScreen(AppController controller) {
		super(controller);
	}
	
	public void addQuizzesToList(ArrayList<Quiz> quizzes , DataInputListener<Quiz> clickListener){
		this.quizzes = quizzes;
		
		QuizItemListAdapter quizAdaptor = new QuizItemListAdapter(getApp(),0,quizzes, clickListener);
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, null);
		((ListView) lView.findViewById(R.id.listView)).setAdapter(quizAdaptor);
		addView(lView);
	}
}
