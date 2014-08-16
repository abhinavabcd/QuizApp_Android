package com.amcolabs.quizapp.helperclasses;

import android.view.View;

import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;


public class DataInputListener<T> implements IDataInputListener<T> {

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
