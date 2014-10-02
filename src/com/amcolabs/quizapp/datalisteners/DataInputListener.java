package com.amcolabs.quizapp.datalisteners;

import android.view.View;

import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;


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
	public String onDataObject(Object s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String onData(T s,MessageType code) {
		// TODO Auto-generated method stub
	
		return null;
	}
	@Override
	public Object onDataReturnObj(T s){
		return null;
	}
	@Override
	public String onData2(T s1, T s2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String onData2(T s1, int s2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String onData3(T s1, T s2, T s3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String onData3(T s1, int s2, int s3) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String onData3(T s1, int s2, String s3) {
		// TODO Auto-generated method stub
		return null;
	}

}
