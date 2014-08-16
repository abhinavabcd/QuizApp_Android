package com.amcolabs.quizapp;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
	String uid;
	String password;
	String name;
	String solvedId;
	ArrayList<Integer> badgeIds;
	HashMap<String,Integer> stats;
	HashMap<String, Integer[]>winsLosses;
	String deviceId;
	String emailId;
	String picture;
	boolean isActivated =false;
	String status;
	String googlePlus;
	String facebook;
	
	public ShortUserInfo getShortUserInfo() {
		return null;
	}	
	
	public String getFacebookAuthToken(){
		return facebook;
	}
	public String getGooglePlusAuthToken(){
		return googlePlus;
	}
}
