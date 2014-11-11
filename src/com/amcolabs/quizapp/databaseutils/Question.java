package com.amcolabs.quizapp.databaseutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	private String[] cachedOptions = null;
	public String[] getMCQOptions(){
		if(cachedOptions!=null) return cachedOptions;
		options = options.trim();
			if(!options.startsWith("['") && !options.startsWith("[\"")){
				cachedOptions = this.options.split("\n");
				if(cachedOptions.length>1){
					return cachedOptions;
				}
				cachedOptions = this.options.split(",");
				for(int i=0;i<cachedOptions.length;i++){
					cachedOptions[i] = cachedOptions[i].trim();
				}
				return cachedOptions;
			}
			else
				return (cachedOptions = new Gson().fromJson(options, new TypeToken<String[]>(){}.getType()));
//		catch(JsonParseException ex){
//			return new String[]{};
//		}
	}
	
	
	//[[3]] => 3RD OPTION IS THE RIGHT ANSWER
	final static Pattern answerPattern  = Pattern.compile("^\\[\\[(\\d*)\\]\\]"); 
	public boolean isCorrectAnwer(String ans , int index){
		if(getQuestionType()==QuestionType.MCQ){
			Matcher m = answerPattern.matcher(this.answer);
			if((m.find() && index ==Integer.parseInt(m.group(1))-1)){
				return true;
			}
			if(answer.trim().equalsIgnoreCase(ans.trim())){
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
		return getAnswer();
	}
	public String getWrongRandomAnswer(Random rand) {
		if(getQuestionType()==QuestionType.MCQ){
			String[] mcqOptions = getMCQOptions();
			while(true){
				int index = rand.nextInt(mcqOptions.length);
				String a = mcqOptions[index];
				if(!isCorrectAnwer(a, index)){
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
		Matcher m = answerPattern.matcher(this.answer);
		if((m.find())){
			return getMCQOptions()[Integer.parseInt(m.group(1))-1];
		}
		return answer.trim();
	}
	public void setAnswer(String answer) {
		this.answer = answer.trim();
	}
	public int getAnswerIndex() {
		String answer = getAnswer();
		String[] options = getMCQOptions();
		for(int i=0; i<options.length;i++){
			if(options[i].equalsIgnoreCase(answer)){
				return i;
			}
		}
		this.cachedOptions[0] = answer;
		return 0;
	}
}
