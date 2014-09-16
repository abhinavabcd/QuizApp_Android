package com.amcolabs.quizapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.amcolabs.quizapp.appcontrollers.ProfileAndChatController;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController.UserAnswer;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.screens.WinOrLoseScreen;
import com.amcolabs.quizapp.serverutils.ServerWebSocketConnection;


public class WinOrLoseController extends AppController {
	private WinOrLoseScreen quizResultScreen;

	private ServerWebSocketConnection serverSocket;

	public WinOrLoseController(QuizApp quizApp) {
		super(quizApp);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Main method to load result screen after quiz
	 * @param quiz Current quiz user has played
	 * @param currentUsers Current list of users who played quiz
	 * @param userAnswersStack Current user's answers in hashmap mapped with uid's
	 */
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
		if (quizResultScreen==null){
			quizResultScreen = new WinOrLoseScreen(this,currentUsers);
		}
		double cPoints = quizApp.getUser().getPoints(quiz);
		List<UserAnswer> uAns = userAnswersStack.get(quizApp.getUser().uid);
		double newPoints = cPoints+uAns.get(uAns.size()-1).whatUserGot+(quizResult>0?Config.QUIZ_WIN_BONUS:0);
		quizResultScreen.showResult(userAnswersStack,quizResult,didUserLevelUp(cPoints,newPoints));
		showScreen(quizResultScreen);
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
		List<Badge> badges = quizApp.getDataBaseHelper().getAllUnAwardedBadges();
		Iterator<Badge> itr = badges.iterator();
		Badge curBadge = null;
		String[] ors;
		String[] ands;
		String[] cond;
		boolean state = true;
		while(itr.hasNext()){
			curBadge = itr.next();
			String condition = curBadge.condition;
			ors = condition.split("||");
			for(int i=0;i<ors.length;i++){
				ands = ors[i].split("&&");
				for(int j=0;j<ands.length;j++){
					cond = ands[j].split(":");
					if(cond.length==2)
						state = state && badgeConditionEvaluator(cond[0], cond[1]);
				}
				if(state){
					// condition satisfied else continue
				}
			}
		}
	}
	
	public boolean badgeConditionEvaluator(String cond,String value){
		// To be implemented
		if(cond.equalsIgnoreCase("quiz")){
			
		}
		else if(cond.equalsIgnoreCase("category")){
			
		}
		else if(cond.equalsIgnoreCase("streak")){
			
		}
		else if(cond.equalsIgnoreCase("quizCount")){
			
		}
		else if(cond.equalsIgnoreCase("level")){
			
		}
		return true;
	}

	public void loadProfile(User user) {
		ProfileAndChatController profileAndChat = (ProfileAndChatController) quizApp.loadAppController(ProfileAndChatController.class);
//		profileAndChat.loadChatScreen(user, -1, true);
		profileAndChat.showProfileScreen(user);
	}
}
