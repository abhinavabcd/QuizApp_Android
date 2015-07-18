package com.quizapp.tollywood.datalisteners;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.quizapp.tollywood.serverutils.ServerResponse.MessageType;

public interface IDataInputListener<T> extends OnClickListener,OnLongClickListener{ 
	public String onData(T s); // returns error message , if null then everything is alright
	public String onData(T s, MessageType code);
	String onData(T s, int a);

	
	//onclick method too
}

