package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

import android.widget.LinearLayout;
import android.widget.ListView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.adapters.CategoryItemListAdapter;
import com.amcolabs.quizapp.appcontrollers.UserMainController;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.datalisteners.DataInputListener;

public class HomeScreen extends Screen { 
	ArrayList<Category> categories = new ArrayList<Category>();
	private UserMainController userMainController;
	public HomeScreen(AppController appController) {
		super(appController);
		userMainController = (UserMainController)appController;
	} 
	
	public void fillCategories(ArrayList<Category>  categories){
		this.categories = categories;
		CategoryItemListAdapter categoryAdaptor = new CategoryItemListAdapter(getApp(),0,categories,new DataInputListener<Category>(){
			@Override
			public String onData(Category s) {
				userMainController.onCategorySelected(s);
				return super.onData(s);
			}
		});
		
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, null);
			((ListView) lView.findViewById(R.id.listView)).setAdapter(categoryAdaptor);
			addView(lView);
	}
	
	
	public void addUserFavourites(){
		
	}
	
	public void addQuizAppActivity(){
		
	}

	}
