package com.amcolabs.quizapp.databaseutils;

import java.util.HashMap;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.FeedListItemAdaptor.FeedViewHolder;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.gameutils.GameUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.j256.ormlite.field.DatabaseField;

public class Feed {
	
	@DatabaseField
	public String fromUid;
	@DatabaseField
	private int type;
	@DatabaseField
	public String message;
	@DatabaseField
	public String message2;
	@DatabaseField
	public double timestamp;
	

	
	private static HashMap<Integer , FeedType> feedTypeMap = null;//new HashMap<Integer , FeedType>();
	
	public FeedType getUserFeedType(){
		return this.getUserFeedType(this.type);
	}
	
	
	public FeedType getUserFeedType(int value){
		if(feedTypeMap==null){
			feedTypeMap = new HashMap<Integer, FeedType>();
			for(FeedType s : FeedType.values()){
				feedTypeMap.put(s.getValue(), s);
			}
		}
		return feedTypeMap.containsKey(value) ? feedTypeMap.get(value):FeedType.FEED_GENERAL;
	}
	
	
	public static enum FeedType{
		FEED_GENERAL(0),
		FEED_USER_WON(1),
		FEED_USER_TOOK_PART(2),
		FEED_USER_ADDED_FRIEND(3),
		FEED_USER_WON_BADGES(4),
		FEED_CHALLENGE(5);
		
		int value;
		private FeedType(int v) {
			value =v;
		}
		private int getValue() {
			return this.value;
		}
	};

	public Feed() {
		
	}
	
	public View createLayout(QuizApp quizApp, FeedViewHolder feedHolder){
		 ImageView titleImage;
		 GothamTextView titleName;
		 GothamTextView textContent1;
		 GothamTextView textContent2;
		 switch(getUserFeedType()){
		 	case FEED_CHALLENGE:
		 		LinearLayout baseLayout = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.feed_generic_view, null);
				feedHolder.titleImage = (ImageView) baseLayout.findViewById(R.id.title_image);
				feedHolder.titleName = (GothamTextView) baseLayout.findViewById(R.id.title_name);
				feedHolder.textContent1 = (GothamTextView) baseLayout.findViewById(R.id.text_content_1);
				feedHolder.textContent2 = (GothamTextView) baseLayout.findViewById(R.id.text_content_2);
				baseLayout.setTag(feedHolder);
				return baseLayout;
			default:
				break;
		 }
		 return null;
	}
 

	public void setDataIntoView(final QuizApp quizApp ,FeedViewHolder feedHolder) {
		 switch(getUserFeedType()){
			 case FEED_CHALLENGE:
				User user = quizApp.cachedUsers.get(fromUid);
				quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), feedHolder.titleImage, user.pictureUrl, false);
				feedHolder.titleName.setText(user.name);
				OfflineChallenge offlineChallenge = quizApp.getDataBaseHelper().getOfflineChallengeByChallengeId(message);
				int user1Points = GameUtils.getLastElement(offlineChallenge.getChallengeData(quizApp).userAnswers).whatUserGot;
				int user2Points = GameUtils.getLastElement(offlineChallenge.getChallengeData(quizApp).userAnswers).whatUserGot;
				String winText = user1Points==user2Points?UiText.TIE_QUIZ_MESAGE.getValue(): (user1Points>user2Points?UiText.WON_QUIZ_MESSAGE.getValue():UiText.LOST_QUIZ_MESAGE.getValue());
				quizApp.getUiUtils().setTextViewHTML(feedHolder.textContent1, UiText.YOU_WON_LOOSE_CHALLENGE_FEED.getValue(winText, message), new DataInputListener<String>(){
					@Override
					public String onData(String s) {
						if(s.startsWith("offlineChallengeId")){
							OfflineChallenge offlineChallenge = quizApp.getDataBaseHelper().getOfflineChallengeByChallengeId(s.split("/")[1]);
							quizApp.getStaticPopupDialogBoxes().showChallengeWinDialog(offlineChallenge);
						}
						return super.onData(s);
					}
				});
				break;
		 }
	}
}
