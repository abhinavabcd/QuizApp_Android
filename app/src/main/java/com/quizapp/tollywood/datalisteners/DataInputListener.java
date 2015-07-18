package com.quizapp.tollywood.datalisteners;

import android.view.View;

import com.quizapp.tollywood.configuration.Config;
import com.quizapp.tollywood.serverutils.ServerResponse.MessageType;


public class DataInputListener<T> implements IDataInputListener<T> {
	
	double lastClick = 0;
	
	public boolean isRapidReClick(){
		if(Config.getCurrentNanos()-lastClick>1000000000){//1 sec
			lastClick = Config.getCurrentNanos();
			return false;
		}
		lastClick = Config.getCurrentNanos();
		return true;
	}
	
	@Override
	public void onClick(View v) {
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String onData(T s) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public String onData(T s,MessageType code) {
		// TODO Auto-generated method stub
	
		return null;
	}
	@Override
	public String onData(T s, int a) {
		return null;
	}

}
