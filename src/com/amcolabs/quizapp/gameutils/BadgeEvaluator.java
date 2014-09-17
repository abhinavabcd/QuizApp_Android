package com.amcolabs.quizapp.gameutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import android.R.integer;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.databaseutils.Badge;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.databaseutils.Quiz;

public class BadgeEvaluator {

	HashMap<String, Category> mainCategoryList;
	HashMap<String, Quiz> mainQuizList;
	QuizApp quizApp;
	ArrayList<ArrayList<String>> categoryList = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> quizList = new ArrayList<ArrayList<String>>();
	ArrayList<Integer> categoryFullChildState = new ArrayList<Integer>();
	ArrayList<Integer> quizFullChildState = new ArrayList<Integer>();
	boolean allFlag = false;
	
	public BadgeEvaluator(QuizApp qApp){
		quizApp = qApp;
		
		mainCategoryList = new HashMap<String, Category>();
		mainQuizList = new HashMap<String, Quiz>();
		
		List<Category> tmp = quizApp.getDataBaseHelper().getAllCategories();
		for(int i=0;i<tmp.size();i++){
			mainCategoryList.put(tmp.get(i).categoryId, tmp.get(i));
		}
		
		List<Quiz> tmp1 = quizApp.getDataBaseHelper().getAllQuizzes();
		for(int i=0;i<tmp1.size();i++){
			mainQuizList.put(tmp1.get(i).quizId, tmp1.get(i));
		}
	}
	
	/**
	 * Values must be given as limits i.e., anything, greater than or equal to given value, is counted as True 
	 * -- (all is the only keyword allowed instead of values which implies condition must apply for all selected)
	 * values must be followed by %x where x is the minimum count for the condition for given type (category and quiz only)
	 * Full child match - must satisfy for all->0 ; atleast one -> 1 ; no restriction(default value) -> -1 
	 * selected quizList will be List of lists which are bound by OR condition
	 * Eg: (category::%0)&&(level::5|10|15) ; (category::id1|id3&id2%2)
	 * @param cond
	 * @param value
	 * @return
	 */
	public void evaluateBadges(){
		// TODO: evaluate all badge conditions and show if unlocked
		List<Badge> badges = quizApp.getDataBaseHelper().getAllUnAwardedBadges();
		Iterator<Badge> itr = badges.iterator();
		Badge curBadge = null;
		String[] ors;
		ArrayList<String> ands;
		String[] cond;
		boolean state = false;
		boolean andState = true;
		while(itr.hasNext()){
			curBadge = itr.next();
			String condition = curBadge.getCondition();
			ors = condition.split("||");
			for(int i=0;i<ors.length;i++){
				ands = loadQuizList(ors[i]);
				
				for(int j=0;j<ands.size();j++){
					cond = ands.get(j).split("::");
					if(cond.length==2){
						andState = andState && badgeConditionEvaluator(cond[0], cond[1]);
					}
					else{
						System.out.println("Input is not valid, output may not be as expected");
					}
				}
				state = state || andState;
				if(state){
					// condition satisfied else continue
				}
			}
		}
	}
	
	/**
	 * Category and Quiz can be manipulated to give conditions. category
	 * default -1 -> no restrictions(one quiz match with other conditions); 0 -> must match all given entries fully; n -> must match n entries 
	 * @param condition
	 */
	private ArrayList<String> loadQuizList(String condition){
//		for(int i=0;i<ors.length;i++){
//			quizList.add(new ArrayList<Quiz>());
//		}
		String[] ands;
		String[] cond;
		String[] tmp;
		String[] childConditions;
		String[] cList;
		ArrayList<String> tmp1;
		ArrayList<String> tmp2;
		int sz;
		ands = condition.split("&&");
		cond = ands[0].split("::");
		if(cond[0].equalsIgnoreCase("category")){
			if(cond.length>1){
				childConditions = cond[1].split("|");
				for(int i=0;i<childConditions.length;i++){
					cList = childConditions[i].split("%");
					tmp = cList[0].split("&");
					tmp1 = new ArrayList<String>();
					for(int j=0;j<tmp.length;j++){
						tmp1.add(tmp[j]);
					}
					categoryList.add(tmp1);
					if(tmp.length>1){
						categoryFullChildState.add(Integer.valueOf(tmp[1]));
					}
					else{
						categoryFullChildState.add(-1);
					}
				}
			}
			if(ands.length>1){
				cond = ands[1].split("::");
				if(cond[0].equalsIgnoreCase("quiz")){
					if(cond.length>1){
						childConditions = cond[1].split("|");
						for(int i=0;i<childConditions.length;i++){
							cList = childConditions[i].split("%");
							tmp = cList[0].split("&");
							tmp2 = new ArrayList<String>();
							for(int j=0;j<tmp.length;j++){
								tmp2.add(tmp[j]);
							}
							quizList.add(tmp2);
							if(tmp.length>1){
								quizFullChildState.add(Integer.valueOf(tmp[1]));
							}
							else{
								quizFullChildState.add(-1);
							}
						}
					}
					return getRemainingElements(2,ands);
				}
				else{
					return getRemainingElements(1,ands);
				}
			}
			System.out.println("Somethings not right");
			return getRemainingElements(1,ands);
		}
		else if(cond[0].equalsIgnoreCase("quiz")){
			if(cond.length>1){
				childConditions = cond[1].split("|");
				for(int i=0;i<childConditions.length;i++){
					cList = childConditions[i].split("%");
					tmp = cList[0].split("&");
					tmp2 = new ArrayList<String>();
					for(int j=0;j<tmp.length;j++){
						tmp2.add(tmp[j]);
					}
					quizList.add(tmp2);
					if(tmp.length>1){
						quizFullChildState.add(Integer.valueOf(tmp[1]));
					}
					else{
						quizFullChildState.add(-1);
					}
				}
			}
			return getRemainingElements(1,ands);
		}
		else{
			loadAllQuizzes();
			return getRemainingElements(0,ands);
		}

	}
	
	private ArrayList<String> getRemainingElements(int start,String[] array) {
		ArrayList<String> tmp1 = new ArrayList<String>();
		for(int i=start;i<array.length;i++){
			tmp1.add(array[i]);
		}
		return tmp1;
	}
	
	/**
	 * Basic Evaluator for condition
	 * @param cond Eg: Category
	 * @param value Eg: "catId1"|"catId2"&"catId3"
	 * @return
	 */
	public boolean badgeConditionEvaluator(String cond,String value){
		// To be implemented
		boolean state = false;
		boolean andState = true;
		String[] ands = null;
		String[] ors = null;
		String val = null;
		ors = value.split("|");
		for(int i=0;i<ors.length;i++){
			ands = ors[i].split("&");
			for(int j=0;j<ands.length;j++){
				val = ands[j];
				andState = andState && basicConditionEvaluator(cond,val,i);
			}
			state = state || andState;
			if(state){
				return true;
			}
		}
		return true;
	}
	
	private boolean basicConditionEvaluator(String cond,String value,int index){
		boolean state = true;

		if(cond.equalsIgnoreCase("level")){
			if(categoryFullChildState.get(index)<0){
				if(quizFullChildState.get(index)<0){
					for(int i=0;i<quizList.size();i++){
						if(Double.valueOf(value)<=quizApp.getUiUtils().getLevelFromPoints(mainQuizList.get(quizList.get(index).get(i)).userXp))
							return true;
					}
					return false;
				}
				// other cases
			}
		}
		else if(cond.equalsIgnoreCase("streak")){
			
		}
		else if(cond.equalsIgnoreCase("quizCount")){
			
		}
		return state;
	}
	
	private boolean matchQuizListLevel(int index,int count,String value){
		int tmp_count = 0;
		for(int i=0;i<quizList.size();i++){
			if(Double.valueOf(value)<=quizApp.getUiUtils().getLevelFromPoints(mainQuizList.get(quizList.get(index).get(i)).userXp))
				tmp_count++;
			if(tmp_count>=count){
				return true;
			}
		}
		return false;
	}
	
	private boolean matchQuizListLevel(int index,ArrayList<String> quizIds,String value){
		boolean ret = false;
		return ret;
	}
	
	private boolean matchQuizListStreak(int index,int count,String value){
		int tmp_count = 0;
		for(int i=0;i<quizList.size();i++){
			if(Double.valueOf(value)<=quizApp.getUiUtils().getLevelFromPoints(mainQuizList.get(quizList.get(index).get(i)).userXp))
				tmp_count++;
			if(tmp_count>=count){
				return true;
			}
		}
		return false;
	}
	
	private boolean matchQuizListStreak(int index,ArrayList<String> quizIds,String value){
		boolean ret = false;
		return ret;
	}

	private boolean matchQuizListQuizCount(int index,int count,String value){
		int tmp_count = 0;
		for(int i=0;i<quizList.size();i++){
			if(Double.valueOf(value)<=quizApp.getUiUtils().getLevelFromPoints(mainQuizList.get(quizList.get(index).get(i)).userXp))
				tmp_count++;
			if(tmp_count>=count){
				return true;
			}
		}
		return false;
	}
	
	private boolean matchQuizListQuizCount(int index,ArrayList<String> quizIds,String value){
		boolean ret = false;
		return ret;
	}
	
	private void loadAllQuizzes(){
		categoryList.add(new ArrayList<String>(mainCategoryList.keySet()));
		quizList.add(new ArrayList<String>(mainQuizList.keySet()));
		categoryFullChildState.add(-1);
		quizFullChildState.add(-1);
	}
}
