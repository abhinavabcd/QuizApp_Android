package com.amcolabs.quizapp.adapters;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.widgets.GothamTextView;

class CategoryViewHolder{
		ImageView imageView ;
		GothamTextView categoryName;
		GothamTextView shortCategoryDescription;
		GothamTextView additionalText;
		Category item;
		public Category category;
}

public  class CategoryItemListAdapter extends ArrayAdapter<Category>{
		QuizApp quizApp;
		private DataInputListener<Category> clickListener;
		public CategoryItemListAdapter(QuizApp quizApp, int resource,List<Category> objects, DataInputListener<Category> clickListener) {
			super(quizApp.getActivity(), resource, objects);
			this.quizApp = quizApp;
			this.clickListener = clickListener;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CategoryViewHolder holder;
			Category category = getItem(position);
			if(convertView==null){
				convertView = quizApp.getActivity().getLayoutInflater().inflate(R.layout.list_item_layout, null);
				holder = new CategoryViewHolder();
				holder.imageView = (ImageView)convertView.findViewById(R.id.imageView1);
				holder.categoryName = (GothamTextView) convertView.findViewById(R.id.category_item_name);
				holder.shortCategoryDescription = (GothamTextView) convertView.findViewById(R.id.category_short_name);
				holder.additionalText = (GothamTextView) convertView.findViewById(R.id.additional_text);
				convertView.setTag(holder);
				if(clickListener!=null)
					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							clickListener.onData(((CategoryViewHolder)v.getTag()).category);
						}
					});
			}
			else{
				holder = (CategoryViewHolder) convertView.getTag();
			}
			holder.category = category;
			quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.imageView, getItem(position).assetPath,true);
			holder.categoryName.setText(category.description);
			holder.shortCategoryDescription.setText(category.shortDescription);
			holder.additionalText.setText("a");
			return convertView;
		}
};
