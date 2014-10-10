package com.amcolabs.quizapp.adapters;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.databaseutils.Feed.FeedType;
import com.amcolabs.quizapp.databaseutils.GameEvents;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.GothamTextView;

public class GameEventsListItemAdaptor extends ArrayAdapter<GameEvents> {

	public static class GameEventViewHolder{
		public GothamTextView titleName;
		public GothamTextView textContent1;
		public GothamTextView textContent2;
		public ImageView titleImage;
	}
	
	private QuizApp quizApp;
	private boolean dataInitialized = false;

	public GameEventsListItemAdaptor(QuizApp quizApp , int resource,
			 List<GameEvents> events) {
		super(quizApp.getContext(), resource, events);
		List<String> uids = new ArrayList<String>();
		for(GameEvents evt: events){
			switch(evt.getEventType()){
			case LEVEL_UP:
				break;
			case LOST_QUIZ:
				uids.add(evt.message2);
				break;
			case SERVER_ERROR_QUIZ:
				uids.add(evt.message2);
				break;
			case SHARED_WITH_FB:
				break;
			case SHARED_WITH_GOOGLE:
				break;
			case SOMETHING_ELSE:
				break;
			case TIE_QUIZ:
				break;
			case UNLOCKED_BADGE:
				break;
			case USER_JOINED:
				break;
			case WON_QUIZ:
				uids.add(evt.message2);
				break;
			default:
				break;
			}	
		}
		quizApp.getDataBaseHelper().getAllUsersByUid(uids, new DataInputListener<Boolean>(){
			@Override
			public String onData(Boolean s) {
				dataInitialized = true;
				return super.onData(s);
			}
		});
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
		return this.getItem(position).getEventType().getValue();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(!dataInitialized){
			return new View(quizApp.getContext());
		}
		GameEvents evt = this.getItem(position);
		GameEventViewHolder viewHolder = null;
		if(convertView==null){
			viewHolder = new GameEventViewHolder();
			convertView = createLayout(evt, quizApp, viewHolder);
		}
		else{
			viewHolder = (GameEventViewHolder) convertView.getTag();
		}
		
		setDataIntoView(evt, quizApp, viewHolder);
		return convertView;
	}

	private void setDataIntoView(GameEvents evt, QuizApp quizApp,GameEventViewHolder viewHolder) {
		switch(evt.getEventType()){
		case LEVEL_UP:
			break;
		case LOST_QUIZ:
			break;
		case SERVER_ERROR_QUIZ:
			break;
		case SHARED_WITH_FB:
			break;
		case SHARED_WITH_GOOGLE:
			break;
		case SOMETHING_ELSE:
			break;
		case TIE_QUIZ:
			break;
		case UNLOCKED_BADGE:
			break;
		case USER_JOINED:
			break;
		case WON_QUIZ:
			break;
		default:
			break;
		}
		
	}

	private View createLayout(GameEvents evt, QuizApp quizApp, GameEventViewHolder viewHolder) {
		LinearLayout genericTextView = null;
		GothamTextView textView = null;
		User user = null;
		Quiz quiz = null;
		switch(evt.getEventType()){ 
			case LEVEL_UP:
				genericTextView = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.generic_event_view, null); 
				textView = (GothamTextView)genericTextView.findViewById(R.id.data);
				quizApp.getUiUtils().setTextViewHTML(textView, UiText.YOU_LEVELED_UP.getValue(quizApp.getDataBaseHelper().getQuizById(evt.message).name),null);
				return genericTextView;
			case LOST_QUIZ:
				genericTextView = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.generic_event_view, null); 
				textView = (GothamTextView)genericTextView.findViewById(R.id.data); 
				user = quizApp.cachedUsers.get(evt.message2);
				quiz = quizApp.getDataBaseHelper().getQuizById(evt.message);
				quizApp.getUiUtils().setTextViewHTML(textView,UiText.YOU_LOST_TO_USER.getValue(
								user.uid,
								user.name,
								quiz.quizId,
								quiz.name,
								"0"
						),null);
				return genericTextView;
			case SERVER_ERROR_QUIZ:
				genericTextView = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.generic_event_view, null); 
				textView = (GothamTextView)genericTextView.findViewById(R.id.data); 
				user = quizApp.cachedUsers.get(evt.message2);
				quiz = quizApp.getDataBaseHelper().getQuizById(evt.message);
				quizApp.getUiUtils().setTextViewHTML(textView, UiText.THERE_WAS_SERVER_ERROR.getValue(
								user.uid,
								user.name,
								quiz.quizId,
								quiz.name
						),null);
				return genericTextView;
			case SHARED_WITH_FB:
				break;
			case SHARED_WITH_GOOGLE:
				break;
			case SOMETHING_ELSE:
				break;
			case TIE_QUIZ:
				break;
			case UNLOCKED_BADGE:
				genericTextView = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.generic_event_view, null); 
				textView = (GothamTextView)genericTextView.findViewById(R.id.data); 
				Badge badge = quizApp.getDataBaseHelper().getBadgeById(evt.message);
				quizApp.getUiUtils().setTextViewHTML(textView, UiText.YOU_UNLOCKED_BADGE.getValue(
								badge.getBadgeId(),
								badge.getName()
						),null);
				return genericTextView;
			case USER_JOINED:
				break;
			case WON_QUIZ:
				genericTextView = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.generic_event_view, null); 
				textView = (GothamTextView)genericTextView.findViewById(R.id.data); 
				user = quizApp.cachedUsers.get(evt.message2);
				quiz = quizApp.getDataBaseHelper().getQuizById(evt.message);
				quizApp.getUiUtils().setTextViewHTML(textView,UiText.YOU_DEFEATED_USER.getValue(
								user.uid,
								user.name,
								quiz.quizId,
								quiz.name,
								"0"
					),null);
				return genericTextView;
			default:
				break;
		}
		return null;
	}
}
