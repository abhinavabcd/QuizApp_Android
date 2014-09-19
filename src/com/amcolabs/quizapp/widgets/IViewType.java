package com.amcolabs.quizapp.widgets;

public interface IViewType {
	public static enum ViewType{
		USER_INFO_CARD,
		WAITING_FOR_USER_VIEW,
		CHALLENGE_VIEW;
	}
	
	public ViewType getViewType();
}

