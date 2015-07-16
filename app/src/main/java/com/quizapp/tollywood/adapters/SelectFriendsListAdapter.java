package com.quizapp.tollywood.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.quizapp.tollywood.QuizApp;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.User;
import com.quizapp.tollywood.configuration.Config;
import com.quizapp.tollywood.datalisteners.DataInputListener;
import com.quizapp.tollywood.uiutils.UiUtils.UiText;
import com.quizapp.tollywood.widgets.GothamTextView;
import com.quizapp.tollywood.widgets.QuizAppMenuItem;

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
				holder.additionalcontainer.removeAllViews();

				holder.addFriendButton = new QuizAppMenuItem(quizApp, 0, 0, UiText.ADD.getValue(), Config.themeColors[0]);
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
									quizApp.getStaticPopupDialogBoxes().yesOrNo(UiText.ADDED_USER.getValue(user.getName()), null, UiText.CLOSE.getValue(), null);
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
				LayoutParams temp = ((LayoutParams)holder.addFriendButton.getLayoutParams());
				temp.gravity = Gravity.CENTER;
				temp.width = LayoutParams.MATCH_PARENT;
				temp.height = LayoutParams.MATCH_PARENT;
				
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
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.imageView, getItem(position).pictureUrl,false);
			holder.userName.setText(user.getName());
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
											HashMap<String , User> map = new HashMap<String, User>();
											for(User user : users){
													map.put(user.uid , user);
											}
											if(map.containsKey(quizApp.getUser().uid))
												map.remove(quizApp.getUser().uid);
											for(User u: usersList){
												if(map.containsKey(u.uid))
													map.remove(u.uid);
											}

											addAll(map.values());
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



