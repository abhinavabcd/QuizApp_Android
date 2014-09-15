package com.amcolabs.quizapp.adapters;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.widgets.GothamTextView;

class FriendViewHolder{ 
		ImageView imageView ;
		GothamTextView userName;
		GothamTextView statusDescription;
		User item;
}

public  class SelectFriendsListAdapter extends ArrayAdapter<User>{
		QuizApp quizApp;
		private DataInputListener<User> clickListener;
		private ArrayList<User> usersList;
		public SelectFriendsListAdapter(QuizApp quizApp, int resource,List<User> objects, DataInputListener<User> clickListener) {
			super(quizApp.getActivity(), resource, objects);
			this.usersList = new ArrayList<User>(objects);
			this.quizApp = quizApp;
			this.clickListener = clickListener;
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
				convertView.findViewById(R.id.additional_text).setVisibility(View.GONE);
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
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.imageView, getItem(position).pictureUrl,true);
			holder.userName.setText(user.name);
			holder.statusDescription.setText(user.status);
			return convertView;
		}
		
		

		public Filter getFilter() {
			return mFilter;
		};
		
		Filter mFilter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				String filterString = constraint.toString().toLowerCase();
			
				FilterResults results = new FilterResults();
				final ArrayList<User> nlist = new ArrayList<User>();
				
				for (int i = 0; i < usersList.size(); i++) {
					if (usersList.get(i).toString().toLowerCase().contains(filterString)) {
						nlist.add(usersList.get(i));
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
				addAll((ArrayList<User>) results.values);
				notifyDataSetChanged();
			}
		};

};



