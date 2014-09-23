package com.amcolabs.quizapp.adapters;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.uiutils.UiUtils;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.amcolabs.quizapp.widgets.CircularCounter;
import com.amcolabs.quizapp.widgets.GothamTextView;

class QuizItemViewHolder{
	ImageView imageView ;
	GothamTextView quizName;
	GothamTextView shortCategoryDescription;
//	GothamTextView additionalText;
	Quiz item;
	public FrameLayout additionalContainer;
	public CircularCounter levelIndicator;
}

public class QuizItemListAdapter extends ArrayAdapter<Quiz> {
	QuizApp quizApp;
	private DataInputListener<Quiz> clickListener;
	private List<Quiz> quizList;

	public QuizItemListAdapter(QuizApp quizApp, int resource,
			List<Quiz> objects, DataInputListener<Quiz> onClickListener) {
		super(quizApp.getActivity(), resource, objects);
		this.quizList = new ArrayList<Quiz>(objects);
		this.quizApp = quizApp;
		this.clickListener = onClickListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		QuizItemViewHolder holder;
		Quiz quiz = getItem(position);
		if (convertView == null) {
			convertView = quizApp.getActivity().getLayoutInflater()
					.inflate(R.layout.list_item_layout, null);
			//remove bg
//			convertView.findViewById(R.id.item_wrapper_2).setBackgroundResource(0);
			holder = new QuizItemViewHolder();
			holder.imageView = (ImageView)convertView.findViewById(R.id.imageView1);
			holder.quizName = (GothamTextView) convertView.findViewById(R.id.category_item_name);
			holder.shortCategoryDescription = (GothamTextView) convertView.findViewById(R.id.category_short_name);
			holder.additionalContainer = (FrameLayout)convertView.findViewById(R.id.additional_container);
			holder.additionalContainer.findViewById(R.id.additional_text).setVisibility(View.GONE);
			UiUtils uiUtils = quizApp.getUiUtils();
			holder.levelIndicator = new CircularCounter(quizApp.getContext(), uiUtils.getInSp(10), Color.parseColor("#000000"), uiUtils.getInSp(7), UiText.LEVEL.getValue(), 
														 uiUtils.getInDp(5), 1, uiUtils.getInDp(3), uiUtils.getInDp(3), 0, 0, 0, 0, uiUtils.getInDp(40),0);
			holder.additionalContainer.addView(holder.levelIndicator);
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
							clickListener.onData(((QuizItemViewHolder) v
									.getTag()).item);
					}
				});
			}
			convertView.setTag(holder);
		} else {
			holder = (QuizItemViewHolder) convertView.getTag();
		}
		holder.item = quiz;//on every reset
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.imageView, quiz.assetPath,true);
		holder.quizName.setText(quiz.name);
		if(quiz.shortDescription==null || quiz.shortDescription.trim().equalsIgnoreCase("")){
//			holder.shortCategoryDescription.setVisibility(View.GONE);
		}
		else{
//			holder.shortCategoryDescription.setVisibility(View.VISIBLE);
		}
		holder.shortCategoryDescription.setText(quiz.shortDescription);
		float currentLevelProgress = (float)quizApp.getGameUtils().getLevelFromXp((int)quiz.userXp);
		
		holder.levelIndicator.setValues(currentLevelProgress - (int)currentLevelProgress, 1, 0);
		holder.levelIndicator.setCurrentValue((int)currentLevelProgress);
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
			final ArrayList<Quiz> nlist = new ArrayList<Quiz>();

			for (int i = 0; i < quizList.size(); i++) {
				if (quizList.get(i).toString().toLowerCase().contains(filterString)) {
					nlist.add(quizList.get(i));
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
			List<Quiz> a = (ArrayList<Quiz>) results.values;
			if(a.size()==0){
				a=quizList;
			}
			addAll(a);
			notifyDataSetChanged();
		}

	};

}
