package com.quizapp.tollywood.adapters;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;

import com.quizapp.tollywood.QuizApp;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.User;
import com.quizapp.tollywood.databaseutils.ChatList;
import com.quizapp.tollywood.datalisteners.DataInputListener2;
import com.quizapp.tollywood.widgets.GothamTextView;


class UserChatViewHolder{
	ImageView imageView ;
	GothamTextView userName;
	GothamTextView lastMessageText;
	GothamTextView additionalText;
	User user;
	String uid;
	public ChatList chatListItem;
}


public class ChatListAdapter  extends ArrayAdapter<ChatList>{
	QuizApp quizApp ;
	private DataInputListener2<ChatList, User, Void, Void> clickListener;
	
	public ChatListAdapter(QuizApp quizApp, int resource,List<ChatList> objects, DataInputListener2<ChatList,User, Void , Void> clickListener) {
		super(quizApp.getActivity(), resource, objects);
		this.quizApp = quizApp;
		this.clickListener = clickListener;

		for(ChatList chatListItem: objects){
			User user = quizApp.cachedUsers.containsKey(chatListItem.uid)?quizApp.cachedUsers.get(chatListItem.uid):null;
			if(user!=null)
				chatListItem.name = user.getName();
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UserChatViewHolder holder;
		ChatList chatListItem = getItem(position);
		if(convertView==null){
			convertView = quizApp.getActivity().getLayoutInflater().inflate(R.layout.list_item_layout, null);
			holder = new UserChatViewHolder();
			holder.imageView = (ImageView)convertView.findViewById(R.id.imageView1);
			holder.userName = (GothamTextView) convertView.findViewById(R.id.category_item_name);
			holder.lastMessageText = (GothamTextView) convertView.findViewById(R.id.category_short_name);
			holder.additionalText = (GothamTextView) convertView.findViewById(R.id.additional_text);
			convertView.setTag(holder);
			if(clickListener!=null)
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ChatList temp = ((UserChatViewHolder)v.getTag()).chatListItem;
						User user = quizApp.cachedUsers.get(temp.uid);
						clickListener.onData( temp, user , null,null);
					}
				});
		}
		else{
			holder = (UserChatViewHolder) convertView.getTag();
		}
		holder.chatListItem = chatListItem;
		User user = quizApp.cachedUsers.containsKey(chatListItem.uid)?quizApp.cachedUsers.get(chatListItem.uid):null;
		if(user==null)
			return null;
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.imageView, user.pictureUrl,true);
		holder.userName.setText(user==null?"Loading..":user.getName());
		holder.lastMessageText.setText(chatListItem.recentMessage);
		if(chatListItem.unseenMessagesFlag>0){
			holder.additionalText.setVisibility(View.VISIBLE);
			holder.additionalText.setText(chatListItem.unseenMessagesFlag+"");
		}
		else{
			holder.additionalText.setVisibility(View.GONE);
		}
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
			final ArrayList<ChatList> nlist = new ArrayList<ChatList>();

			for (int i = 0; i < ChatListAdapter.this.getCount(); i++) {
				if (getItem(i).toString().toLowerCase().contains(filterString)) {
					nlist.add(getItem(i));
				}
			}
			results.values = nlist;
			results.count = nlist.size();
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			clear();
			addAll((ArrayList<ChatList>) results.values);
			notifyDataSetChanged();
		}
	};

}

