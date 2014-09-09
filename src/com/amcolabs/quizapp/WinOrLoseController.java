package com.amcolabs.quizapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController.UserAnswer;

public class WinOrLoseController extends AppController {

	public WinOrLoseController(QuizApp quizApp) {
		super(quizApp);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	public void loadResultScreen(HashMap<String, List<UserAnswer>> userAnswersStack) {
		// TODO Auto-generated method stub
		ArrayList<String> winnersList = whoWon(userAnswersStack);
		if(winnersList.contains(this.quizApp.getUser().uid)){
			if(winnersList.size()==1){
				// User has won
			}
			else{
				// Tie
			}
		}
		else{
			// User lost
		}
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
	
	public void evaluateBadges(){
		// TODO: evaluate all badge condiitons and show if unlocked
	}
}
