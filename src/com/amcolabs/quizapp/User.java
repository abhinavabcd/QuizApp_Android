package com.amcolabs.quizapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.j256.ormlite.field.DatabaseField;

public class User {
	@DatabaseField(id=true, index=true, unique=true)
    public String uid;
	@DatabaseField
	public String name;
	public String deviceId;
	public String emailId;
	@DatabaseField
	public String pictureUrl;
	@DatabaseField
	public String coverUrl;	
	@DatabaseField
	public String gender;
	@DatabaseField
	public double birthday;
	@DatabaseField
	public String place;
	public boolean isActivated =false;
	public double createdAt;
	public String country; 
	@DatabaseField
	private String status;
	public String googlePlus;
	public String facebook;
	public ArrayList<String> badges;
	public HashMap<String,Integer> stats;
	public HashMap<String, Integer[]> winsLosses;
	private int userType = 0;
	@DatabaseField
	private String jsonDump;
	
	private List<String> subscribers;//uids
	private List<String> subscribedTo;
	
	
	
	
	public User(){
	}
	
	public String getFacebookAuthToken(){
		return facebook;
	}
	public String getGooglePlusAuthToken(){
		return googlePlus;
	}

	public static User getDummyUser(QuizApp quizApp) {
		return quizApp.getConfig().getGson().fromJson("{\"emailId\": \"ramasolipuram@gmail.com\", \"uid\": \"110040773460941325994\", \"isActivated\": true, \"googlePlus\": \"ya29.aACYqyIWDi39LksAAADNOtMCHgeTwAr1HzPWinCQtAq_6cjPmrtbqpwHnfwnK9GJDm4Df6I5_Bgwm8j_H7_m0czGX90AfjVtfPyvSbAp86y5y_DgUWffbXg_9RoF4g\", \"pictureUrl\": \"https://lh3.googleusercontent.com/-TyulralhJFw/AAAAAAAAAAI/AAAAAAAAA9o/8KyUnpS-j_Y/photo.jpg?sz=200\", \"deviceId\": \"31e7d9178c3ca41f\", \"winsLosses\": {'2':[5,2,1],'1':[2,6,3]}, \"stats\": {}, \"name\": \"Rama Reddy\", \"gender\": \"female\", \"birthday\": 0, \"newDeviceId\": \"31e7d9178c3ca41f\", \"badges\": [], \"activationKey\": \"\"}", User.class);
	}
	
	public int getLevel(Quiz quiz) {
		return 0;
	}
	
	public double getPoints(Quiz quiz){
		return 0;
	}
	
	public boolean isBotUser(){
		return uid.startsWith("0");
	}
	
	public void save(QuizApp quizApp){
		this.jsonDump = quizApp.getConfig().getGson().toJson(this);
		quizApp.getDataBaseHelper().saveUser(this);
	}
	
	public int[] getTotalWinsLosses(){
		int[] totalWinsLosses = new int[3];
		Iterator<String> itr = this.winsLosses.keySet().iterator();
		String quizId = null;
		Integer[] tmpWinsLosses;
		while(itr.hasNext()){
			quizId = itr.next();
			tmpWinsLosses = this.winsLosses.get(quizId);
			for(int i=0;i<3;i++){
				totalWinsLosses[i] = totalWinsLosses[i]+tmpWinsLosses[i];
			}
		}
		return totalWinsLosses;
	}
	
	public int[] getWinsLossesSum(List<String> quizList){
		int[] totalWinsLosses = new int[3];
		Iterator<String> itr = quizList.iterator();
		String quizId = null;
		Integer[] tmpWinsLosses;
		if(winsLosses!=null){
			return new int[]{0,0,0};
		}
		while(itr.hasNext()){
			quizId = itr.next();
			if(!this.winsLosses.containsKey(quizId)){
				continue;
			}
			tmpWinsLosses = this.winsLosses.get(quizId);
			if(tmpWinsLosses!=null){
				for(int i=0;i<3;i++){
					totalWinsLosses[i] = totalWinsLosses[i]+tmpWinsLosses[i];
				}
			}
		}
		return totalWinsLosses;
	}

	public HashMap<String, Integer> getStats() {
		// TODO Auto-generated method stub
		if(stats==null){
			stats = new HashMap<String, Integer>();
		}
		return stats;
	}

	public Integer[] getWinsLosses(String quizId) {
		if(winsLosses==null){
			winsLosses = new HashMap<String, Integer[]>();
			winsLosses.put(quizId, new Integer[]{0,0,0});
		}
		else if(!winsLosses.containsKey(quizId)){
			winsLosses.put(quizId, new Integer[]{0,0,0});
		}
		return winsLosses.get(quizId);
	}

	public List<String> getSubscribers() {
		return subscribers;
	}

	public List<String> getSubscribedTo() {
		return subscribedTo;
	}

	public String getStatus(float level) {
		if(status==null){
			if(level<10)
				return UiText.BEGINNER.getValue();
			if(level<20)
				return UiText.RUNNER.getValue();
			if(level<40)
				return UiText.GO_GETTER.getValue();
			if(level<60)
				return UiText.TREND_SETTER.getValue();
		}
		return status;
	}
	public String getStatus() {
		if(status==null){
			return UiText.BEGINNER.getValue();
		}
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

}

