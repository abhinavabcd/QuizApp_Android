package com.amcolabs.quizapp.databaseutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.gameutils.GameUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;



public class Question {

	@DatabaseField
    public String questionId;
	@DatabaseField
    public int questionType;
	@DatabaseField
    public String questionDescription; // question description in json , contains image links
	@DatabaseField
    private List<String> pictures; // comma seperated paths
	@DatabaseField
    private String options; //json
	@DatabaseField
    private String answer;
	@DatabaseField
    public String hint;
	@DatabaseField
    public String explanation;
	@DatabaseField
    public double time; // time in seconds
	@DatabaseField
    public int xp;

	private String correctAnswer;

	public List<String> getAssetPaths(){
		if(getPictures()==null)
			return new ArrayList<String>(); 
		else{
			for(int i=0;i <getPictures().size();i++){
				String url = getPictures().get(i);
				if(url==null || url.trim().isEmpty()){
					getPictures().remove(i);
				}
			}
			for(String option : getMCQOptions()){
				if(GameUtils.isUrl(option)){
					getPictures().add(options);
				}
			}
			return getPictures();
		}
	}


	public int getAnswerIndex() {
		return this.getMCQOptions().indexOf(getAnswer());
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
	
	private List<String> cachedOptions = null;
	public synchronized  List<String> getMCQOptions(){
			if(cachedOptions!=null) return cachedOptions;
			options = options.trim();
			cachedOptions = new ArrayList<>();
			if(!options.startsWith("['") && !options.startsWith("[\"")){
				String[] lines = this.options.split("\n");
				for(String line : lines) {
					for(String option : this.options.split(",")){
						cachedOptions.add(option.trim());
					}
				}
			return cachedOptions;
			}
			else
				return (cachedOptions = new Gson().fromJson(options, new TypeToken<List<String>>(){}.getType()));
	}
	
	
	//[[3]] implies 3RD OPTION IS THE RIGHT ANSWER
	final static Pattern answerPattern  = Pattern.compile("^\\[\\[(\\d*)\\]\\]"); 
	public boolean isCorrectAnwer(String ans){
		if(getQuestionType()==QuestionType.MCQ){
			if(getAnswer().trim().equalsIgnoreCase(ans.trim())){
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

	public String getWrongRandomAnswer(Random rand) {
		if(getQuestionType()==QuestionType.MCQ){
			List<String> mcqOptions = getMCQOptions();
			//check for infifnite loop
			int temp = 0;
			while(true){
				if(temp>mcqOptions.size()){
					return "";
				}
				temp++;
				int index = rand.nextInt(mcqOptions.size());
				String a = mcqOptions.get(index);
				if(!isCorrectAnwer(a)){
					return a;
				}
			}
		}
		return null;
	}
	public List<String> getPictures() {
		return pictures;
	}
	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
	}

	public String getAnswer() {
		if( correctAnswer !=null)
			return correctAnswer;

		Matcher m = answerPattern.matcher(this.answer);
		if((m.find())){
			return correctAnswer = getMCQOptions().get(Integer.parseInt(m.group(1))-1);
		}
		return correctAnswer = answer.trim();
	}


	public String toJson(QuizApp app) {
		return app.getConfig().getGson().toJson(this);
	}
}
