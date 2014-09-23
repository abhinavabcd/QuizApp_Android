package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.widgets.GothamTextView;

public class BadgeScreen extends Screen implements OnItemClickListener{
	
	GridView gridView;
	ArrayList<Badge> badges;
	BadgeScreenController controller;
	
	public BadgeScreen(BadgeScreenController contrlr){
		super(contrlr);
		controller = contrlr;
		badges = new ArrayList<Badge>(getApp().getDataBaseHelper().getAllBadges());
        LayoutInflater inflater = getApp().getActivity().getLayoutInflater();
		View tmp = inflater.inflate(R.layout.badges_grid, null);
        gridView = (GridView) tmp.findViewById(R.id.gridView);
        gridView.setAdapter( new MyArrayAdapter(this.getContext(), R.layout.badge_small, badges));
        gridView.setOnItemClickListener(this);
        tmp.setBackgroundColor(Color.BLACK);
//        tmp.setBackgroundColor(getApp().getConfig().getAThemeColor());
//        gridView.setBackgroundColor(getApp().getConfig().getAThemeColor());
        addView(tmp);
	}
	
	private class MyArrayAdapter extends ArrayAdapter<Badge>{
		class ViewHolder{
			ImageView badgeImage;
			GothamTextView badgeName;
		}

        public MyArrayAdapter(Context context, int resource,
                ArrayList<Badge> badges) {
            super(context, resource, badges);   
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ViewHolder holder;
        	if(convertView==null){
	            LayoutInflater inflater = (LayoutInflater) getContext()
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.badge_small, parent, false);
	            holder = new ViewHolder();
	            holder.badgeName = (GothamTextView) convertView.findViewById(R.id.badgeName);
	            holder.badgeImage = (ImageView) convertView.findViewById(R.id.badgeImage);
	            convertView.setTag(holder);
        	}
        	else{
        		holder = (ViewHolder) convertView.getTag();
        	}
        	Badge currentBadge = getItem(position);
        	getApp().getUiUtils().loadImageIntoView(getApp().getContext(), holder.badgeImage, currentBadge.getAssetPath(), true);
        	if(getApp().getUser().badges!=null && getApp().getUser().badges.contains(currentBadge.getBadgeId())){
        		holder.badgeImage.setAlpha(1.0f);
        	}
        	else{
        		holder.badgeImage.setAlpha(0.5f);
        	}
        	holder.badgeName.setText(currentBadge.getName());
            return convertView;
        }
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		controller.onBadgeClick(badges.get(position));
	}
}
