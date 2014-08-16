package com.amcolabs.quizapp.datalisteners;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;

public interface IDataInputListener<T> extends OnClickListener,OnLongClickListener{ 
	public String onData(T s); // returns error message , if null then everything is alright
	public String onData(T s, MessageType code);

	public String onData2(T s1,T s2);
	public String onData2(T s1,int s2);
	
	public String onData3(T s1,T s2,T s3);	
	public String onData3(T s1,int s2, int s3);
	public String onData3(T s1,int s2, String s3);
	Object onDataReturnObj(T s);
	String onDataObject(Object s);

	
	//onclick method too
}

