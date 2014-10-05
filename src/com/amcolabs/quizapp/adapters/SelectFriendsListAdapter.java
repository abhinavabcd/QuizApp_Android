package com.amcolabs.quizapp.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabWidget;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.amcolabs.quizapp.widgets.QuizAppMenuItem;

class FriendViewHolder{ 
		ImageView imageView ;
		GothamTextView userName;
		GothamTextView statusDescription;
		User item;
		public FrameLayout additionalcontainer;
		public QuizAppMenuItem addFriendButton;
}

public  class SelectFriendsListAdapter extends ArrayAdapter<User>{
		QuizApp quizApp;
		private DataInputListener<User> clickListener;
		private ArrayList<User> usersList;
		private boolean searchOnserver = false;
		public SelectFriendsListAdapter(QuizApp quizApp, int resource,List<User> objects, DataInputListener<User> clickListener, boolean searchOnServer) {
			super(quizApp.getActivity(), resource, objects);
			this.usersList = new ArrayList<User>(objects);
			this.quizApp = quizApp;
			this.clickListener = clickListener;
			this.searchOnserver  = searchOnServer;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FriendViewHolder holder;
			User user = getItem(position);
			if(convertView==null){
				convertView = quizApp.getActivity().getLayoutInflater().inflate(R.layout.list_item_layout, null);
				holder = new FriendViewHolder();
				holder.imageView = (ImageView)convertView.findViewById(R.id.imageView1);
				holder.userName = (GothamTextView) convertView.findViewById(R.id.category_item_name);
				holder.statusDescription = (GothamTextView) convertView.findViewById(R.id.category_short_name);
				holder.additionalcontainer = (FrameLayout)convertView.findViewById(R.id.additional_container);
				convertView.findViewById(R.id.additional_text).setVisibility(View.GONE);
				holder.addFriendButton = new QuizAppMenuItem(quizApp, 0, 0, UiText.ADD.getValue());
				holder.addFriendButton.setTag(holder);
				holder.addFriendButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View v) {
						quizApp.getServerCalls().subscribeTo(((FriendViewHolder)v.getTag()).item, new DataInputListener<Boolean>(){
							@Override
							public String onData(Boolean s) {
								if(s){
									FriendViewHolder holder = ((FriendViewHolder)v.getTag());
									User user = holder.item;
									quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.ADDED_USER.getValue(user.name), null, UiText.CLOSE.getValue(), null);
									quizApp.getUser().getSubscribedTo().add(user.uid);
									holder.addFriendButton.setVisibility(View.GONE);
									user.isFriend = true;
									usersList.add(user);
								}
								else{
									quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.UNABLE_TO_ADD_USER.getValue(), null, UiText.CLOSE.getValue(), null);
								}
								return super.onData(s);
							}
						});
					}
				});
				holder.additionalcontainer.addView(holder.addFriendButton);	
				convertView.setTag(holder);
				if(clickListener!=null)
					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							clickListener.onData(((FriendViewHolder)v.getTag()).item);
						}
					});
			}
			else{
				holder = (FriendViewHolder) convertView.getTag();
			}
			holder.item = user;
			if(user.isFriend){
				holder.addFriendButton.setVisibility(View.GONE);
			}
			else{
				holder.addFriendButton.setVisibility(View.VISIBLE);
			}
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.imageView, getItem(position).pictureUrl,true);
			holder.userName.setText(user.name);
			holder.statusDescription.setText(user.getStatus());
			return convertView;
		}
		
		private String oldSearchQuery="";		
		private Timer timer=null;
		protected String currentSearchString;
		public Filter getFilter() {
			return mFilter;
		};
		
		
		public void fetchUsers(){
			
		}
		
		Filter mFilter = new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				final ArrayList<User> nlist = new ArrayList<User>();
				FilterResults results = new FilterResults();
				currentSearchString = constraint.toString().toLowerCase();
				if(currentSearchString.length()>2){
					if(timer==null){
						timer = quizApp.getUiUtils().setInterval(1000, new DataInputListener<Integer>(){
							public String onData(Integer s) {
								if(!oldSearchQuery.trim().equalsIgnoreCase(currentSearchString.trim())){
									oldSearchQuery = currentSearchString.trim();
									quizApp.getServerCalls().searchUsersByName(oldSearchQuery, new DataInputListener<List<User>>(){
										public String onData(List<User> users) {
											addAll(users);
											notifyDataSetChanged();
											return null;
										};
									});
								}
								return null;
							};
						});
					}

					for (int i = 0; i < usersList.size(); i++) {
						if (usersList.get(i).toString().toLowerCase().contains(currentSearchString)) {
							nlist.add(usersList.get(i));
						}
					}
					
				}
				else{
					if(timer!=null){
						timer.cancel();
						timer = null;
						oldSearchQuery ="";
					}
				}
				results.values = nlist;
				results.count = nlist.size();
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				clear();
				ArrayList<User> a = (ArrayList<User>) results.values;
				if(a.size()==0){
					a = usersList;
				}
				addAll(a);
				notifyDataSetChanged();
			}
		};

		
		public void cleanUp(){
			if(timer!=null){
				timer.cancel();
				timer =null;
			}
		}
};



