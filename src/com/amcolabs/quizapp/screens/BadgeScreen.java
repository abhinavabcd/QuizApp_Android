package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.GridView;

import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.adapters.BadgeListAdapter;
import com.amcolabs.quizapp.databaseutils.Badge;

public class BadgeScreen extends Screen implements OnItemClickListener{
	
	GridView gridView;
	ArrayList<Badge> badges;
	BadgeScreenController controller;
	
	public BadgeScreen(BadgeScreenController controller){
		super(controller);
		this.controller = controller;
		badges = new ArrayList<Badge>(getApp().getDataBaseHelper().getAllBadges());
        LayoutInflater inflater = getApp().getActivity().getLayoutInflater();
		View tmp = inflater.inflate(R.layout.badges_grid, null);
        gridView = (GridView) tmp.findViewById(R.id.gridView);
        gridView.setAdapter( new BadgeListAdapter(getApp(), 0, badges));
        gridView.setOnItemClickListener(this);
        tmp.setBackgroundColor(getApp().getConfig().getAThemeColor());
//        tmp.setBackgroundColor(getApp().getConfig().getAThemeColor());
//        gridView.setBackgroundColor(getApp().getConfig().getAThemeColor());
        addView(tmp);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		controller.onBadgeClick(badges.get(position));
	}
	
	
}
