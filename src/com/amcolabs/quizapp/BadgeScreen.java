package com.amcolabs.quizapp;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.squareup.picasso.Picasso;

public class BadgeScreen extends Screen{
	
	GridView gridView;
	
	public BadgeScreen(final AppController controller,ArrayList<Badge> badges){
		super(controller);
		
        LayoutInflater inflater = getApp().getActivity().getLayoutInflater();
		View tmp = inflater.inflate(R.layout.badges_grid, null);
        gridView = (GridView) tmp.findViewById(R.id.gridView);
        gridView.setAdapter( new MyArrayAdapter(this.getContext(), R.layout.badge_small, new ArrayList<Badge>()));
	}
	
	public void evaluateBadgeConditions(){
		
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
        	Picasso.with(getApp().getContext()).load(currentBadge.smallAssetPath).into(holder.badgeImage);
        	holder.badgeName.setText(currentBadge.name);
            return convertView;
        }
    }
}
