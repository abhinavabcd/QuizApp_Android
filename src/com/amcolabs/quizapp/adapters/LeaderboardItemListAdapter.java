package com.amcolabs.quizapp.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.widgets.GothamTextView;

class LeaderBoardItemViewHolder{
	ImageView imageView ;
	GothamTextView userName;
	GothamTextView userStatus;
	GothamTextView additionalText;
	User item;
	public FrameLayout additionalContainer;
	public TextView rankText;
}

public class LeaderboardItemListAdapter extends ArrayAdapter<User> {
	QuizApp quizApp;
	private DataInputListener<User> clickListener;
	private List<User> userList;
	private HashMap<String ,Integer[]> userScoreBoard;

	public LeaderboardItemListAdapter(QuizApp quizApp, int resource,
			List<User> objects, HashMap<String,Integer[]> userScoreBoard, DataInputListener<User> onClickListener) {
		super(quizApp.getActivity(), resource, objects);
		this.userList = new ArrayList<User>(objects);
		this.quizApp = quizApp;
		this.userScoreBoard =  userScoreBoard;
		this.clickListener = onClickListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LeaderBoardItemViewHolder holder;
		User user = getItem(position);
		Integer[] temp = userScoreBoard.get(user.uid);
		String score = temp.length>0 ? temp[0]+" xp" : null;
		String rank = temp.length>1 ? temp[1]+"" :null;
		if (convertView == null) {
			convertView = quizApp.getActivity().getLayoutInflater()
					.inflate(R.layout.list_item_layout, null);
			holder = new LeaderBoardItemViewHolder();
			holder.rankText = (GothamTextView) convertView.findViewById(R.id.sNoText);
			//remove bg
			convertView.findViewById(R.id.item_wrapper_2).setBackgroundResource(0);
			holder.imageView = (ImageView)convertView.findViewById(R.id.imageView1);
			holder.userName = (GothamTextView) convertView.findViewById(R.id.category_item_name);
			holder.userStatus = (GothamTextView) convertView.findViewById(R.id.category_short_name);
			holder.additionalContainer = (FrameLayout)convertView.findViewById(R.id.additional_container);
			holder.additionalText = (GothamTextView) holder.additionalContainer.findViewById(R.id.additional_text);
			UiUtils uiUtils = quizApp.getUiUtils();
//			holder.levelIndicator = new CircularCounter(quizApp.getContext(), uiUtils.getInSp(10), Color.parseColor("#000000"), uiUtils.getInSp(7), UiText.LEVEL.getValue(), 
//														 uiUtils.getInDp(5), 1, uiUtils.getInDp(3), uiUtils.getInDp(3), 0, 0, 0, 0, uiUtils.getInDp(40),0);
					/*
					 * meter:range="10"
			        meter:textSize="20sp"
			        meter:textColor="#ffffff"
			        meter:metricSize="10sp"
			        meter:metricText="sec"
					 */
//			holder.additionalText = (GothamTextView) convertView.findViewById(R.id.additional_text);
			if(clickListener!=null){
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (clickListener != null)
							clickListener.onData(((LeaderBoardItemViewHolder) v
									.getTag()).item);
					}
				});
			}
			convertView.setTag(holder);
		} else {
			holder = (LeaderBoardItemViewHolder) convertView.getTag();
		}
		holder.item = user;//on every reset
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.imageView, user.pictureUrl,true);
		holder.userName.setText(user.name);
//		if(user.status==null || user.status.trim().equalsIgnoreCase("")){
//			holder.userStatus.setVisibility(View.GONE);
//		}
//		else{
//			holder.shortCategoryDescription.setVisibility(View.VISIBLE);
//		}
		holder.userStatus.setText(user.status);
		holder.additionalText.setText(score);		
		holder.rankText.setText(rank);
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

			for (int i = 0; i < userList.size(); i++) {
				if (userList.get(i).toString().toLowerCase().contains(filterString)) {
					nlist.add(userList.get(i));
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

}
