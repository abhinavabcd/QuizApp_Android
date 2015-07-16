package com.quizapp.tollywood.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.quizapp.tollywood.QuizApp;
import com.quizapp.tollywood.R;
import com.quizapp.tollywood.databaseutils.Badge;
import com.quizapp.tollywood.uiutils.UiUtils.UiText;
import com.quizapp.tollywood.widgets.GothamTextView;

public class BadgeListAdapter extends ArrayAdapter<Badge>{
	class ViewHolder{
		ImageView badgeImage;
		GothamTextView badgeName;
		public GothamTextView badgeDesc;
		public GothamTextView isLockedText;
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
            holder.isLockedText = (GothamTextView)convertView.findViewById(R.id.isLocked);
            holder.badgeDesc = (GothamTextView) convertView.findViewById(R.id.badge_desc);
            convertView.setTag(holder);
    	}
    	else{
    		holder = (ViewHolder) convertView.getTag();
    	}
    	Badge currentBadge = getItem(position);
    	quizApp.getUiUtils().loadImageIntoView(quizApp.getContext(), holder.badgeImage, currentBadge.getAssetPath(), true);
    	if(quizApp.getUser().getBadges()!=null && quizApp.getUser().getBadges().contains(currentBadge.getBadgeId())){
    		holder.badgeImage.setAlpha(1.0f);
    		holder.isLockedText.setText("");
    	}
    	else{
    		holder.badgeImage.setAlpha(0.6f);
    		holder.isLockedText.setText(UiText.IS_LOCKED.getValue());
    	}
    	holder.badgeName.setText(currentBadge.getName());
    	holder.badgeName.setTextColor(quizApp.getConfig().getUniqueThemeColor(currentBadge.getName()));
    	holder.badgeDesc.setText(currentBadge.getDescription());
        return convertView;
    }
}
