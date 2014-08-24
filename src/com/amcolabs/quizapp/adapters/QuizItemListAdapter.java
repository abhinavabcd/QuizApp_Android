package com.amcolabs.quizapp.adapters;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.widgets.GothamTextView;

class QuizItemViewHolder {
	ImageView imageView;
	GothamTextView categoryName;
	GothamTextView shortCategoryDescription;
	GothamTextView additionalText;
	Quiz item;
}

public class QuizItemListAdapter extends ArrayAdapter<Quiz> {
	QuizApp quizApp;
	private DataInputListener<Quiz> clickListener;
	private List<Quiz> quizList;

	public QuizItemListAdapter(QuizApp quizApp, int resource,
			List<Quiz> objects, DataInputListener<Quiz> onClickListener) {
		super(quizApp.getActivity(), resource, objects);
		this.quizList = objects;
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
			holder = new QuizItemViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView1);
			holder.categoryName = (GothamTextView) convertView
					.findViewById(R.id.category_item_name);
			holder.shortCategoryDescription = (GothamTextView) convertView
					.findViewById(R.id.category_short_name);
			holder.additionalText = (GothamTextView) convertView
					.findViewById(R.id.additional_text);
			if (clickListener != null) {
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
		holder.item = quiz;// on every reset
		quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(),
				holder.imageView, getItem(position).assetPath, true);
		holder.categoryName.setText(quiz.name);
		holder.shortCategoryDescription.setText(quiz.shortDescription);
		holder.additionalText.setText("a");
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
			addAll((ArrayList<Quiz>) results.values);
			notifyDataSetChanged();
		}

	};

}
