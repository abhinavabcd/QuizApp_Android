package com.amcolabs.quizapp.adapters;

import java.util.ArrayList;
import java.util.List;

import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.LocalQuizHistory;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.GothamTextView;

public class QuizHistoryListAdapter extends ArrayAdapter<LocalQuizHistory> {

	public static class QuizHistoryHolder{
		public GothamTextView data;
		public ImageView titleImage;
		public GothamTextView timeText;
	}
	
	private QuizApp quizApp;

	public QuizHistoryListAdapter(QuizApp quizApp , int resource,
			 List<LocalQuizHistory> historyItems) {
			super(quizApp.getContext(), resource, historyItems);
			this.quizApp = quizApp;
	}
	
	public static List<String> getAllUids(List<LocalQuizHistory> historyItems){
		List<String> uids = new ArrayList<String>();
		for(LocalQuizHistory history: historyItems){
			switch(history.getQuizResult()){
			case Quiz.LOOSE:
				break;
			case Quiz.SERVER_ERR:
				uids.add(history.getWithUid());//uid
				break;
			case Quiz.TIE:
				uids.add(history.getWithUid());//uid
				break;
			case Quiz.WON:
				uids.add(history.getWithUid());//uid
				break;
			}	
		}
		return uids;
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 4; // win loose tie server error
	}
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return this.getItem(position).getQuizResult();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LocalQuizHistory historyItem = this.getItem(position);
		QuizHistoryHolder viewHolder = null;
		if(convertView==null){
			viewHolder = new QuizHistoryHolder();
			convertView = createLayout(historyItem, quizApp, viewHolder);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (QuizHistoryHolder) convertView.getTag();
		}
		
		setDataIntoView(historyItem, quizApp, viewHolder);
		return convertView;
	}

	private void setDataIntoView(LocalQuizHistory history, QuizApp quizApp,QuizHistoryHolder viewHolder) {
		String text = "";
		User user = quizApp.cachedUsers.get(history.getWithUid());
		Quiz quiz = quizApp.getDataBaseHelper().getQuizById(history.quizId);
		
		switch(history.getQuizResult()){
		case Quiz.LOOSE:
			 text = UiText.YOU_LOST_TO_USER.getValue(user.uid, user.getName(), quiz.quizId, quiz.name, history.xpGain);
			break;
		case Quiz.SERVER_ERR:
			 text = UiText.THERE_WAS_SERVER_ERROR.getValue(user.uid, user.getName(), quiz.quizId, quiz.name);
			break;
		case Quiz.TIE:
			 text = UiText.THE_QUIZ_WAS_TIE.getValue(user.uid, user.getName(), quiz.quizId, quiz.name, history.xpGain);
			break;
		case Quiz.WON:
			 text = UiText.YOU_DEFEATED_USER.getValue(user.uid, user.getName(), quiz.quizId, quiz.name, history.xpGain);
			break;
		}
		quizApp.getUiUtils().setTextViewHTML(viewHolder.data,text,null);
		viewHolder.timeText.setText(DateUtils.getRelativeTimeSpanString((long)history.timestamp*1000, (long)Config.getCurrentTimeStamp()*1000, DateUtils.FORMAT_ABBREV_RELATIVE));
	}

	private View createLayout(LocalQuizHistory history, QuizApp quizApp, QuizHistoryHolder viewHolder) {
		LinearLayout genericTextView = null;
		switch(history.getQuizResult()){
		case Quiz.LOOSE:
		case Quiz.SERVER_ERR:
		case Quiz.TIE:
		case Quiz.WON:
			genericTextView = (LinearLayout)quizApp.getActivity().getLayoutInflater().inflate(R.layout.generic_event_view, null); 
			viewHolder.data = (GothamTextView)genericTextView.findViewById(R.id.data);
			viewHolder.titleImage = (ImageView)genericTextView.findViewById(R.id.image);
			viewHolder.timeText = (GothamTextView)genericTextView.findViewById(R.id.time_text);
			break;
		}
		return genericTextView;
	}
}
