package com.amcolabs.quizapp;

import java.util.ArrayList;
import java.util.HashMap;

import com.amcolabs.quizapp.databaseutils.Quiz;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

public class User {
	public String uid;
	public String password;
	public String name;
	public String deviceId;
	public String emailId;
	public String pictureUrl;
	public String coverUrl;	
	public String gender;
	public double birthday;
	public String place;
	public boolean isActivated =false;
	public double createdAt;
	public String country; 
	public String status;
	public String googlePlus;
	public String facebook;
	public String solvedId;
	public ArrayList<Integer> badges;
	public HashMap<String,Integer> stats;
	public HashMap<String, Integer[]>winsLosses;

	public String getFacebookAuthToken(){
		return facebook;
	}
	public String getGooglePlusAuthToken(){
		return googlePlus;
	}

	public static User from(Person person) {
		// TODO Auto-generated method stub
		return null;
	}
	public static User getDummyUser(QuizApp quizApp) {
		return quizApp.getConfig().getGson().fromJson("{\"emailId\": \"ramasolipuram@gmail.com\", \"uid\": \"110040773460941325994\", \"isActivated\": true, \"googlePlus\": \"ya29.aACYqyIWDi39LksAAADNOtMCHgeTwAr1HzPWinCQtAq_6cjPmrtbqpwHnfwnK9GJDm4Df6I5_Bgwm8j_H7_m0czGX90AfjVtfPyvSbAp86y5y_DgUWffbXg_9RoF4g\", \"pictureUrl\": \"https://lh3.googleusercontent.com/-TyulralhJFw/AAAAAAAAAAI/AAAAAAAAA9o/8KyUnpS-j_Y/photo.jpg?sz=200\", \"deviceId\": \"31e7d9178c3ca41f\", \"winsLosses\": {}, \"stats\": {}, \"name\": \"Rama Reddy\", \"gender\": \"female\", \"birthday\": 0, \"newDeviceId\": \"31e7d9178c3ca41f\", \"badges\": [], \"activationKey\": \"\"}", User.class);
	}
	public int getLevel(Quiz quiz) {
		return 0;
	}
	
	public boolean isBotUser(){
		return uid.startsWith("0");
	}
	
	
}

