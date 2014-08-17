package com.amcolabs.quizapp;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
	public String uid;
	public String password;
	public String name;
	public String deviceId;
	public String emailId;
	public String pictureUrl;
	public String coverPictureUrl;	
	public String gender;
	public double birthday;
	public String place;
	public boolean isActivated =false;
	public String status;
	public String googlePlus;
	public String facebook;
	public String solvedId;
	public ArrayList<Integer> badgeIds;
	public HashMap<String,Integer> stats;
	public HashMap<String, Integer[]>winsLosses;
	
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
