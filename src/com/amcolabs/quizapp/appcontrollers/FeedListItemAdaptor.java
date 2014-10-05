package com.amcolabs.quizapp.appcontrollers;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.databaseutils.Feed;
import com.amcolabs.quizapp.databaseutils.Feed.FeedType;
import com.amcolabs.quizapp.widgets.GothamTextView;

public class FeedListItemAdaptor extends ArrayAdapter<Feed> {

	public static class FeedViewHolder{
		public GothamTextView titleName;
		public GothamTextView textContent1;
		public GothamTextView textContent2;
		public ImageView titleImage;
	}
	
	private QuizApp quizApp;

	public FeedListItemAdaptor(QuizApp quizApp , int resource,
			 List<Feed> objects) {
		super(quizApp.getContext(), resource, objects);
		this.quizApp = quizApp;
	}
	
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return FeedType.values().length;
	}
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return this.getItem(position).getUserFeedType().ordinal();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Feed feed = this.getItem(position);
		FeedViewHolder feedHolder = null;
		if(convertView==null){
			feedHolder = new FeedViewHolder();
			convertView = feed.createLayout(quizApp, feedHolder);
		}
		else{
			feedHolder = (FeedViewHolder) convertView.getTag();
		}
		
		feed.setDataIntoView(quizApp, feedHolder);
		return convertView;
	}

}
