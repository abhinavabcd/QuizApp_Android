package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.squareup.picasso.Picasso;

public class CategoryScreen extends Screen {
	ArrayList<Category> categories = new ArrayList<Category>();
	ArrayAdapter<Category> categoryAdaptor;
	public CategoryScreen(AppController appManager) {
		super(appManager);
		
	}
	
	public void fillCategories(){
		for(int i=0;i<10;i++){
			categories.add(Category.createDummy());
		}
		categoryAdaptor = new CategoryListAdapter(getApp(),0,categories);
		LinearLayout lView = (LinearLayout) getApp().getActivity().getLayoutInflater().inflate(R.layout.block_list_view, null);
		((ListView) lView.findViewById(R.id.listView)).setAdapter(categoryAdaptor);
		addView(lView);
	}
	
	
	public void showUserFavourites(){
		
	}
	
	public void showQuizAppActivity(){
		
	}

	static class CategoryViewHolder{
		ImageView imageView ;
		GothamTextView categoryName;
		GothamTextView shortCategoryDescription;
		GothamTextView additionalText;
	}

	static class CategoryListAdapter extends ArrayAdapter<Category>{
		QuizApp quizApp ;
		public CategoryListAdapter(QuizApp quizApp, int resource,List<Category> objects) {
			super(quizApp.getActivity(), resource, objects);
			this.quizApp = quizApp;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CategoryViewHolder holder;
			Category category = getItem(position);
			if(convertView==null){
				convertView = quizApp.getActivity().getLayoutInflater().inflate(R.layout.list_item_layout, null);
				holder = new CategoryViewHolder();
				holder.imageView = (ImageView)convertView.findViewById(R.id.imageView1);
				holder.categoryName = (GothamTextView) convertView.findViewById(R.id.category_item_name);
				holder.shortCategoryDescription = (GothamTextView) convertView.findViewById(R.id.category_short_name);
				holder.additionalText = (GothamTextView) convertView.findViewById(R.id.additional_text);
				convertView.setTag(holder);
			}
			else{
				holder = (CategoryViewHolder) convertView.getTag();
			}
			
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.imageView, getItem(position).assetPath);
			holder.categoryName.setText(category.description);
			holder.shortCategoryDescription.setText(category.shortDescription);
			holder.additionalText.setText("a");
			return convertView;
		}
	};
}
