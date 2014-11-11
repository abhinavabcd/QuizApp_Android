package com.amcolabs.quizapp.adapters;

import java.util.ArrayList;
import java.util.List;

import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProfileAndChatController;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.databaseutils.Feed;
import com.amcolabs.quizapp.databaseutils.Feed.FeedType;
import com.amcolabs.quizapp.databaseutils.OfflineChallenge;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.gameutils.GameUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.FlowLayout;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.google.gson.reflect.TypeToken;

public class FeedListItemAdaptor extends ArrayAdapter<Feed> {

	public static class FeedViewHolder{
		public GothamTextView titleName;
		public GothamTextView textContent1;
		public GothamTextView textContent2;
		public ImageView titleImage;
		public FlowLayout imagesLayout;
		public GothamTextView timestampText;
		public User user;
	}
	
	private static class ViewProfileListener implements OnClickListener{
		
		FeedViewHolder viewHolder = null;
		private QuizApp quizApp;
		public ViewProfileListener(QuizApp quizApp , FeedViewHolder holder){
			this.viewHolder = holder;
			this.quizApp = quizApp;
		}
		@Override
		public void onClick(View v) {
			ProfileAndChatController.showProfileScreen(quizApp, viewHolder.user);
		}
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
			convertView = createLayout(feed, quizApp, feedHolder);
		}
		else{
			feedHolder = (FeedViewHolder) convertView.getTag();
		}
		
		setDataIntoView(feed, quizApp, feedHolder);
		return convertView;
	}
	
	
	public View createLayout(Feed feed , QuizApp quizApp, FeedViewHolder feedHolder){
		 ImageView titleImage;
		 GothamTextView titleName;
		 GothamTextView textContent1;
		 GothamTextView textContent2;
		 LinearLayout baseLayout = null;
		 ViewProfileListener showProfile= null;
		 switch(feed.getUserFeedType()){
		 	case FEED_CHALLENGE:
		 	case FEED_USED_JOINED:
		 		baseLayout = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.feed_generic_view, null);
				feedHolder.titleImage = (ImageView) baseLayout.findViewById(R.id.title_image);
				feedHolder.titleName = (GothamTextView) baseLayout.findViewById(R.id.title_name);
				feedHolder.textContent1 = (GothamTextView) baseLayout.findViewById(R.id.text_content_1);
				feedHolder.textContent2 = (GothamTextView) baseLayout.findViewById(R.id.text_content_2);
				feedHolder.timestampText = (GothamTextView)baseLayout.findViewById(R.id.time_text);
				baseLayout.setTag(feedHolder);
				showProfile = new ViewProfileListener(quizApp , feedHolder);
				feedHolder.titleName.setOnClickListener(showProfile);
				feedHolder.titleImage.setOnClickListener(showProfile);
				return baseLayout;
		case FEED_GENERAL:
			break;
		case FEED_USER_ADDED_FRIEND:	
			break;
		case FEED_USER_TOOK_PART:
			break;
		case FEED_USER_WON:
			break;
		case FEED_USER_WON_BADGES:
	 		baseLayout = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.feed_generic_view, null);
			feedHolder.titleImage = (ImageView) baseLayout.findViewById(R.id.title_image);
			feedHolder.titleName = (GothamTextView) baseLayout.findViewById(R.id.title_name);
			feedHolder.textContent1 = (GothamTextView) baseLayout.findViewById(R.id.text_content_1);
			feedHolder.textContent2 = (GothamTextView) baseLayout.findViewById(R.id.text_content_2);
			(baseLayout.findViewById(R.id.badges_wrapper)).setVisibility(View.VISIBLE);
			feedHolder.timestampText = (GothamTextView)baseLayout.findViewById(R.id.time_text);
			feedHolder.imagesLayout = (FlowLayout) baseLayout.findViewById(R.id.userbadges);
			baseLayout.setTag(feedHolder);
			showProfile = new ViewProfileListener(quizApp , feedHolder);
			feedHolder.titleName.setOnClickListener(showProfile);
			feedHolder.titleImage.setOnClickListener(showProfile);
			return baseLayout; 
		default:
			break;				
		 }
		 return new View(quizApp.getContext());
	}
	
	public void setDataIntoView(final Feed feed , final QuizApp quizApp ,FeedViewHolder feedHolder) {
		User user =null; 
		 switch(feed.getUserFeedType()){
			 case FEED_CHALLENGE:
				user = quizApp.cachedUsers.get(feed.fromUid);
				feedHolder.user = user;
				quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), feedHolder.titleImage, user.pictureUrl, false);
				feedHolder.titleName.setText(user.getName());
				OfflineChallenge offlineChallenge = quizApp.getDataBaseHelper().getOfflineChallengeByChallengeId(feed.message);
				int user1Points = GameUtils.getLastElement(offlineChallenge.getChallengeData(quizApp).userAnswers).whatUserGot;
				int user2Points = GameUtils.getLastElement(offlineChallenge.getChallengeData2(quizApp).userAnswers).whatUserGot;
				String winText = user1Points==user2Points?UiText.TIE_QUIZ_MESAGE.getValue(): (user1Points>user2Points?UiText.WON_QUIZ_MESSAGE.getValue():UiText.LOST_QUIZ_MESAGE.getValue());
				quizApp.getUiUtils().setTextViewHTML(feedHolder.textContent1, UiText.YOU_WON_LOOSE_CHALLENGE_FEED.getValue(winText, feed.message), new DataInputListener<String>(){
					@Override
					public String onData(String s) {
						feed.onFeedElemClick(quizApp , s);
						return super.onData(s);
					}
				});
				break;
			 case FEED_USED_JOINED:
				user = quizApp.cachedUsers.get(feed.fromUid);
				feedHolder.user = user;
				quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), feedHolder.titleImage, user.pictureUrl, false);
				feedHolder.titleName.setText(user.getName());
				quizApp.getUiUtils().setTextViewHTML(feedHolder.textContent1, UiText.FRIEND_USER_STARTED_QUIZAPP.getValue(user.getName() , user.uid), new DataInputListener<String>(){
					@Override
					public String onData(String s) {
						feed.onFeedElemClick(quizApp , s);
						return super.onData(s);
					}
				});
				break;
		case FEED_GENERAL:
			break;
		case FEED_USER_ADDED_FRIEND:
			break;
		case FEED_USER_TOOK_PART:
			break;
		case FEED_USER_WON:
			break;
		case FEED_USER_WON_BADGES:
			user = quizApp.cachedUsers.get(feed.fromUid);
			feedHolder.user = user;
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), feedHolder.titleImage, user.pictureUrl, false);
			feedHolder.titleName.setText(user.getName());
			feedHolder.textContent1.setVisibility(View.GONE);
			feedHolder.textContent2.setText(UiText.HAS_UNLOCKED_A_BADGE.getValue());
			ArrayList<String>badgeIds = quizApp.getConfig().getGson().fromJson(feed.message, new TypeToken<ArrayList<String>>(){}.getType());
			feedHolder.imagesLayout.removeAllViews();
			for(String badgeId : badgeIds){
				Badge badge = quizApp.getDataBaseHelper().getBadgeById(badgeId);
				ImageView temp = new ImageView(quizApp.getContext());
				temp.setOnClickListener(getOnClickListener(badgeId));
				feedHolder.imagesLayout.addView(temp);
				quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), temp, badge.getAssetPath(), true , quizApp.getUiUtils().dp2px(25),quizApp.getUiUtils().dp2px(25), null);
				
			}
			break;
		default:
			break;
		 }
		 
		 feedHolder.timestampText.setText(DateUtils.getRelativeTimeSpanString((long)Config.convertToUserTimeStamp(feed.timestamp)*1000, (long)Config.getCurrentTimeStamp()*1000, DateUtils.FORMAT_ABBREV_RELATIVE));
	}


	private OnClickListener getOnClickListener(final String badgeId) {
		// TODO Auto-generated method stub
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				quizApp.getStaticPopupDialogBoxes().showUnlockedBadge(badgeId, true);
			}
		};
	}

	
}
