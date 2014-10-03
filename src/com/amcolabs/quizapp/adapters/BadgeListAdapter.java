package com.amcolabs.quizapp.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.widgets.GothamTextView;

public class BadgeListAdapter extends ArrayAdapter<Badge>{
	class ViewHolder{
		ImageView badgeImage;
		GothamTextView badgeName;
	}

	private QuizApp quizApp;

    public BadgeListAdapter(QuizApp quizApp, int resource,
            ArrayList<Badge> badges) {
        super(quizApp.getContext(), resource, badges);   
        this.quizApp = quizApp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
    	if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.badge_icon_view, parent, false);
            holder = new ViewHolder();
            holder.badgeName = (GothamTextView) convertView.findViewById(R.id.badgeName);
            holder.badgeImage = (ImageView) convertView.findViewById(R.id.badgeImage);
            
            convertView.setTag(holder);
    	}
    	else{
    		holder = (ViewHolder) convertView.getTag();
    	}
    	Badge currentBadge = getItem(position);
    	quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.badgeImage, currentBadge.getAssetPath(), true);
    	if(quizApp.getUser().badges!=null && quizApp.getUser().badges.contains(currentBadge.getBadgeId())){
    		holder.badgeImage.setAlpha(1.0f);
    	}
    	else{
    		holder.badgeImage.setAlpha(0.5f);
    	}
    	holder.badgeName.setText(currentBadge.getName());
    	holder.badgeName.setTextColor(quizApp.getConfig().getUniqueThemeColor(currentBadge.getName()));
        return convertView;
    }
}
