package com.quizapp.tollywood.adapters;

import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.quizapp.tollywood.QuizApp;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.User;
import com.quizapp.tollywood.databaseutils.OfflineChallenge;
import com.quizapp.tollywood.databaseutils.OfflineChallenge.ChallengeData;
import com.quizapp.tollywood.databaseutils.Quiz;
import com.quizapp.tollywood.datalisteners.DataInputListener;
import com.quizapp.tollywood.uiutils.UiUtils.UiText;
import com.quizapp.tollywood.widgets.GothamTextView;

class OfflineChallengeViewHolder{
	
	 ImageView uidImage1;
	 GothamTextView itemName;
	 GothamTextView shortDesc;
	 ImageView uidImage2;
  	 public OfflineChallenge offlineChallenge;
	public GothamTextView shortDesc1;
}

public  class OfflineChallengesAdapter extends ArrayAdapter<OfflineChallenge>{
		QuizApp quizApp;
		private DataInputListener<OfflineChallenge> clickListener;
		public OfflineChallengesAdapter(QuizApp quizApp, int resource,List<OfflineChallenge> objects, DataInputListener<OfflineChallenge> clickListener) {
			super(quizApp.getActivity(), resource, objects);
			this.quizApp = quizApp;
			this.clickListener = clickListener;
		}  
		HashMap<String , Quiz> cachedQuizById = new HashMap<String, Quiz>();

		@Override 
 		public View getView(int position, View convertView, ViewGroup parent) {
			OfflineChallengeViewHolder holder;
			OfflineChallenge item = getItem(position);
			if(convertView==null){
				
				View baseLayout = convertView = quizApp.getActivity().getLayoutInflater().inflate(R.layout.challenge_list_item, null);
				holder = new OfflineChallengeViewHolder();
				holder.uidImage1 = (ImageView) baseLayout.findViewById(R.id.uid_image_1);
				holder.itemName = (GothamTextView) baseLayout.findViewById(R.id.item_name);
				holder.shortDesc = (GothamTextView) baseLayout.findViewById(R.id.short_desc);
				holder.shortDesc1 = (GothamTextView) baseLayout.findViewById(R.id.short_desc_1);
				holder.uidImage2 = (ImageView) baseLayout.findViewById(R.id.uid_image_2);
				
				convertView.setTag(holder);
				if(clickListener!=null)
					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							clickListener.onData(((OfflineChallengeViewHolder)v.getTag()).offlineChallenge);
						}
					});
			}
			else{
				holder = (OfflineChallengeViewHolder) convertView.getTag();
			}
			holder.offlineChallenge = item;
			User info = holder.offlineChallenge.getFromUser(quizApp);
			Quiz q = null;
			ChallengeData c =item.getChallengeData(quizApp);
			String quizId = c.quizId;
			if(cachedQuizById.containsKey(quizId)){
			 q = cachedQuizById.get(quizId);
			}
			else{
			 q = quizApp.getDataBaseHelper().getQuizById(quizId);
			 cachedQuizById.put(quizId, q);
			}
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.uidImage1, info.pictureUrl,true); 
			holder.itemName.setText(UiText.USER_NAME.getValue(info.getName())); 
			String temp = UiText.IN_QUIZ.getValue(q.name);
			holder.shortDesc1.setText(temp);
			holder.shortDesc1.setTextColor(quizApp.getConfig().getUniqueThemeColor(temp));
			holder.shortDesc.setText(UiText.QUIZ_WITH_SCORE.getValue(c.userAnswers.get(c.userAnswers.size()-1).whatUserGot)); 
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.uidImage2, quizApp.getUser().pictureUrl,true); 
//			holder.additionalText.setText("a");
			return convertView;
		}
};
