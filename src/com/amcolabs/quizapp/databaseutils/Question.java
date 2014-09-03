package com.amcolabs.quizapp.databaseutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.j256.ormlite.field.DatabaseField;



public class Question {

	@DatabaseField
    public String questionId;
	@DatabaseField
    public int questionType;
	@DatabaseField
    public String questionDescription; // question description in json , contains image links
	@DatabaseField
    public List<String> pictures; // comma seperated paths
	@DatabaseField
    public String options; //json
	@DatabaseField
    public String answer;
	@DatabaseField
    public String hint;
	@DatabaseField
    public String explanation;
	@DatabaseField
    public double time; // time in seconds
	@DatabaseField
    public int xp;
	
	public List<String> getAssetPaths(){
		return pictures==null?new ArrayList<String>():pictures;
	}
	
	public static enum QuestionType{
		MCQ(0),
		MCQ_MULTIPLE(1),
		FILL_IN_THE_BLANKS(2),
		MATCH_THE_FOLLOWNG(3);
		
		private int value;
		
		private QuestionType(int value){
			this.setValue(value);
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

	};
	
	private static HashMap<Integer , QuestionType> questionTypeMap = null;//new HashMap<Integer , MessageType>();;
		
	public QuestionType getQuestionType(){
		return this.getQuestionType(this.questionType);
	}
	public  QuestionType getQuestionType(int value){
		if(questionTypeMap==null){ 
			questionTypeMap = new HashMap<Integer, QuestionType>(); 
			for(QuestionType s : QuestionType.values()){
				questionTypeMap.put(s.getValue(), s);
			}
		}
		return questionTypeMap.containsKey(value) ? questionTypeMap.get(value):null;
	}
	public String[] getMCQOptions(){
		return this.options.split(",");
	}
	public boolean isCorrectAnwer(String answer){
		if(getQuestionType()==QuestionType.MCQ){
			if(this.answer.trim().equalsIgnoreCase(answer.trim())){
				return true;
			}
		}
		return false;
	}
	public int getTime() {
		if(getQuestionType()==QuestionType.MCQ){
				return (int) (time==0?10d:time);
		}
		return 10;
	}
	public String getCorrectAnswer() {
		// TODO Auto-generated method stub
		return answer;
	}
	public String getWrongRandomAnswer(Random rand) {
		if(getQuestionType()==QuestionType.MCQ){
			String[] mcqOptions = getMCQOptions();
			while(true){
				String a = mcqOptions[rand.nextInt(mcqOptions.length)];
				if(!isCorrectAnwer(a)){
					return a;
				}
			}
		}
		return null;
	}

	
}
