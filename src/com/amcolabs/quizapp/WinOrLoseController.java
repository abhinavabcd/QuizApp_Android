package com.amcolabs.quizapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController.UserAnswer;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.screens.WinOrLoseScreen;


public class WinOrLoseController extends AppController {

	public WinOrLoseController(QuizApp quizApp) {
		super(quizApp);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	public void loadResultScreen(Quiz quiz, ArrayList<User> currentUsers, HashMap<String, List<UserAnswer>> userAnswersStack) {
		// TODO Auto-generated method stub
		ArrayList<String> winnersList = whoWon(userAnswersStack);
		int quizResult = 0;
		if(winnersList.contains(this.quizApp.getUser().uid)){
			if(winnersList.size()==1){
				quizResult = 1;
			}
//			else{ // default value
//				// Tie
//			}
		}
		else{
			quizResult = -1;
		}
		WinOrLoseScreen resultScreen = new WinOrLoseScreen(this,currentUsers);
		double cPoints = quizApp.getUser().getPoints(quiz);
		List<UserAnswer> uAns = userAnswersStack.get(quizApp.getUser().uid);
		double newPoints = cPoints+uAns.get(uAns.size()-1).whatUserGot+(quizResult>0?Config.QUIZ_WIN_BONUS:0);
		resultScreen.showResult(userAnswersStack,quizResult,didUserLevelUp(cPoints,newPoints));
		showScreen(resultScreen);
	}

	private ArrayList<String> whoWon(HashMap<String, List<UserAnswer>> userAnswersStack){
		List<UserAnswer> uAns;
		ArrayList<String> winnersList = new ArrayList<String>();
		Set<String> allUsers = userAnswersStack.keySet();
		Iterator<String> itr = allUsers.iterator();
		String uid;
		int maxScore=0;
		while(itr.hasNext()){
			uid = itr.next();
			uAns = userAnswersStack.get(uid);
			if(maxScore<uAns.get(uAns.size()-1).whatUserGot){
				maxScore = uAns.get(uAns.size()-1).whatUserGot;
				winnersList.clear();
				winnersList.add(uid);
			}
			else if(maxScore==uAns.get(uAns.size()-1).whatUserGot){
				winnersList.add(uid);
			}
		}
		return winnersList;
	}
	
	public boolean didUserLevelUp(double oldPoints,double newPoints){
		if (Math.floor(quizApp.getGameUtils().getLevelFromXp(oldPoints))!=
				Math.floor(quizApp.getGameUtils().getLevelFromXp(newPoints))){
			return true;
		}
		return false;
	}
	
	public void evaluateBadges(){
		// TODO: evaluate all badge conditions and show if unlocked
	}
}
