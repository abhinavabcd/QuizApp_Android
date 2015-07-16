package com.quizapp.tollywood.adapters;

import java.util.ArrayList;
import java.util.List;

import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.quizapp.tollywood.QuizApp;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.User;
import com.quizapp.tollywood.configuration.Config;
import com.quizapp.tollywood.databaseutils.Badge;
import com.quizapp.tollywood.databaseutils.Feed.FeedType;
import com.quizapp.tollywood.databaseutils.GameEvents;
import com.quizapp.tollywood.databaseutils.Quiz;
import com.quizapp.tollywood.uiutils.UiUtils.UiText;
import com.quizapp.tollywood.widgets.GothamTextView;

public class GameEventsListItemAdaptor extends ArrayAdapter<GameEvents> {

	public static class GameEventViewHolder{
		public ImageView titleImage;
		public GothamTextView data;
		public GothamTextView timestampText;
	}
	
	private QuizApp quizApp;
//	private boolean dataInitialized = false;

	public GameEventsListItemAdaptor(QuizApp quizApp , int resource,
			 List<GameEvents> events) {
		super(quizApp.getContext(), resource, events);
		this.quizApp = quizApp;
	}
	
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return FeedType.values().length+1;
	}
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return this.getItem(position).getEventType().getValue();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		if(!dataInitialized){
//			return new View(quizApp.getContext());
//		}
		GameEvents evt = this.getItem(position);
		GameEventViewHolder viewHolder = null;
		if(convertView==null){
			viewHolder = new GameEventViewHolder();
			convertView = createLayout(evt, quizApp, viewHolder);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (GameEventViewHolder) convertView.getTag();
		}
		
		setDataIntoView(evt, quizApp, viewHolder);
		return convertView;
	}

	private void setDataIntoView(GameEvents evt, QuizApp quizApp,GameEventViewHolder viewHolder) {
		User user = null;
		Quiz quiz = null;
		switch(evt.getEventType()){
		case LEVEL_UP:
			quizApp.getUiUtils().setTextViewHTML(viewHolder.data, UiText.YOU_LEVELED_UP.getValue(quizApp.getDataBaseHelper().getQuizById(evt.getMessage()).name),null);
			viewHolder.timestampText.setText(DateUtils.getRelativeTimeSpanString((long)evt.timestamp*1000, (long)Config.getCurrentTimeStamp()*1000, DateUtils.FORMAT_ABBREV_RELATIVE));
			break;
		case LOST_QUIZ:
			user = quizApp.cachedUsers.get(evt.getMessage2());
			quiz = quizApp.getDataBaseHelper().getQuizById(evt.getMessage());
			quizApp.getUiUtils().setTextViewHTML(viewHolder.data,UiText.YOU_LOST_TO_USER.getValue(
							user.uid,
							user.getName(),
							quiz.quizId,
							quiz.name,
							evt.getMessage3("0")
					),null);
			viewHolder.timestampText.setText(DateUtils.getRelativeTimeSpanString((long)evt.timestamp*1000, (long)Config.getCurrentTimeStamp()*1000, DateUtils.FORMAT_ABBREV_RELATIVE));
			break;
		case SERVER_ERROR_QUIZ:
			user = quizApp.cachedUsers.get(evt.getMessage2());
			quiz = quizApp.getDataBaseHelper().getQuizById(evt.getMessage());
			quizApp.getUiUtils().setTextViewHTML(viewHolder.data, UiText.THERE_WAS_SERVER_ERROR.getValue(
							user.uid,
							user.getName(),
							quiz.quizId,
							quiz.name
					),null);
			viewHolder.timestampText.setText(DateUtils.getRelativeTimeSpanString((long)evt.timestamp*1000, (long)Config.getCurrentTimeStamp()*1000, DateUtils.FORMAT_ABBREV_RELATIVE));
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
			ImageView img = viewHolder.titleImage;
			img.setVisibility(View.VISIBLE);
			Badge badge = quizApp.getDataBaseHelper().getBadgeById(evt.getMessage());
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), img, badge.getAssetPath(), true , quizApp.getUiUtils().dp2px(50) , quizApp.getUiUtils().dp2px(50) , null);
			quizApp.getUiUtils().setTextViewHTML(viewHolder.data, UiText.YOU_UNLOCKED_BADGE.getValue(
							badge.getBadgeId(),
							badge.getName()
					),null);

			viewHolder.timestampText.setText(DateUtils.getRelativeTimeSpanString((long)evt.timestamp*1000, (long)Config.getCurrentTimeStamp()*1000, DateUtils.FORMAT_ABBREV_RELATIVE));
			break;
		case USER_JOINED:
			break;
		case WON_QUIZ:
			user = quizApp.cachedUsers.get(evt.getMessage2());
			quiz = quizApp.getDataBaseHelper().getQuizById(evt.getMessage());
			quizApp.getUiUtils().setTextViewHTML(viewHolder.data,UiText.YOU_DEFEATED_USER.getValue(
							user.uid,
							user.getName(),
							quiz.quizId,
							quiz.name,
							evt.getMessage3("0")
				),null);
			viewHolder.timestampText.setText(DateUtils.getRelativeTimeSpanString((long)evt.timestamp*1000, (long)Config.getCurrentTimeStamp()*1000, DateUtils.FORMAT_ABBREV_RELATIVE));
			break;
		default:
			break;
		}
		
	}

	private View createLayout(GameEvents evt, QuizApp quizApp, GameEventViewHolder viewHolder) {
		LinearLayout genericTextView = null;
		GothamTextView textView = null;
		switch(evt.getEventType()){ 
			case LEVEL_UP:
				genericTextView = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.generic_event_view, null); 
				viewHolder.data = textView = (GothamTextView)genericTextView.findViewById(R.id.data);
				viewHolder.timestampText = (GothamTextView)genericTextView.findViewById(R.id.time_text);
				return genericTextView;
			case LOST_QUIZ:
				genericTextView = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.generic_event_view, null); 
				viewHolder.data = textView = (GothamTextView)genericTextView.findViewById(R.id.data); 
				viewHolder.timestampText = (GothamTextView)genericTextView.findViewById(R.id.time_text);
				return genericTextView;
			case SERVER_ERROR_QUIZ:
				genericTextView = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.generic_event_view, null); 
				viewHolder.data = textView = (GothamTextView)genericTextView.findViewById(R.id.data); 
				viewHolder.timestampText = (GothamTextView)genericTextView.findViewById(R.id.time_text);
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
				viewHolder.data = textView = (GothamTextView)genericTextView.findViewById(R.id.data); 
				ImageView img = viewHolder.titleImage = (ImageView)genericTextView.findViewById(R.id.image);
				viewHolder.timestampText = (GothamTextView)genericTextView.findViewById(R.id.time_text);
				return genericTextView;
			case USER_JOINED:
				break;
			case WON_QUIZ:
				genericTextView = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.generic_event_view, null); 
				viewHolder.data = textView = (GothamTextView)genericTextView.findViewById(R.id.data); 
				viewHolder.timestampText = (GothamTextView)genericTextView.findViewById(R.id.time_text);
				return genericTextView;
			default:
				break;
		}
		return null;
	}


	public static List<String> getAllUids(List<GameEvents> events) {
		List<String> uids = new ArrayList<String>();
		for(GameEvents evt: events){
			switch(evt.getEventType()){
			case LEVEL_UP:
				break;
			case LOST_QUIZ:
				uids.add(evt.getMessage2());//uid
				break;
			case SERVER_ERROR_QUIZ:
				uids.add(evt.getMessage2());//uid
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
				uids.add(evt.getMessage2());
				break;
			default:
				break;
			}	
		}
		return uids;
	}
}
