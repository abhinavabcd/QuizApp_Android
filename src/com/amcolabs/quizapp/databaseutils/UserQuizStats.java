package com.amcolabs.quizapp.databaseutils;

import com.j256.ormlite.field.DatabaseField;

/**
 * Intended to store Win Streaks and other quiz info. In Short, Summary.
 * @author Vinay
 */
public class UserQuizStats {
	
	@DatabaseField(id=true ,index=true, unique=true)
	String quizId;
	@DatabaseField
	int streak;
	@DatabaseField
	int win;
	@DatabaseField
	int lose;
	@DatabaseField
	int tie;
	@DatabaseField
	int totalCount;
	@DatabaseField
	double modifiedTimestamp;
	
	public UserQuizStats(){
		
	}

	public UserQuizStats(String qId,int result,double tstamp){
		quizId = qId;
		totalCount = 1;
		modifiedTimestamp = tstamp;
		if(result<0){
			win = 0; lose = 1; tie = 0; streak = 0;
		}
		else if(result==0){
			win = 0; lose = 0; tie = 1; streak = 0;
		}
		else if(result>0){
			win = 1; lose = 0; tie = 0; streak = 1;
		}
		else{
			win = 0; lose = 0; tie = 0; streak = 0;
		}
	}

	public String getQuizId() {
		return quizId;
	}

	public void setQuizId(String quizId) {
		this.quizId = quizId;
	}

	public int getStreak() {
		return streak;
	}

	public void setStreak(int streak) {
		this.streak = streak;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getLose() {
		return lose;
	}

	public void setLose(int lose) {
		this.lose = lose;
	}

	public int getTie() {
		return tie;
	}

	public void setTie(int tie) {
		this.tie = tie;
	}

	public double getModifiedTimeStamp() {
		return modifiedTimestamp;
	}

	public void setModifiedTimeStamp(double timeStamp) {
		this.modifiedTimestamp = timeStamp;
	}
	
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
