package com.amcolabs.quizapp.databaseutils;

import com.j256.ormlite.field.DatabaseField;

/**
 * Intended to store Win Streaks and other quiz info. In Short, Summary.
 * @author Vinay
 */
public class QuizHistory {
	
	@DatabaseField(id=true , index=true, unique=true)
    int id;
	@DatabaseField
	String quizId;
	@DatabaseField
	String categoryId;
	@DatabaseField
	String streak;
	@DatabaseField
	int win;
	@DatabaseField
	int lose;
	@DatabaseField
	int tie;
	@DatabaseField
	double timeStamp;
	
	public QuizHistory(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQuizId() {
		return quizId;
	}

	public void setQuizId(String quizId) {
		this.quizId = quizId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getStreak() {
		return streak;
	}

	public void setStreak(String streak) {
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

	public double getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(double timeStamp) {
		this.timeStamp = timeStamp;
	}
}
