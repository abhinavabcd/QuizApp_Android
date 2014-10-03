package com.amcolabs.quizapp.gameutils;

import java.util.List;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController.UserAnswer;
import com.amcolabs.quizapp.databaseutils.OfflineChallenge;

/**
 * 
 * @author abhinav2
 *
 */
public class GameUtils {
	QuizApp quizApp;
	public GameUtils(QuizApp quizApp) {
		this.quizApp = quizApp;
	}
	
	public static int LEVEL_ONE_POINTS = 300;
	public static double getLevelFromXp(double newPoints){
		return 1+(double) ((-19.0d*LEVEL_ONE_POINTS + Math.sqrt(19.0d*19*LEVEL_ONE_POINTS*LEVEL_ONE_POINTS + 4*(20*newPoints)*(LEVEL_ONE_POINTS)))/(2*LEVEL_ONE_POINTS*1.0d));
	}
	
	public static double getPointsFromLevel(double level){
		level--;
		return LEVEL_ONE_POINTS*level+ (level*(level-1)/10.0d)*(LEVEL_ONE_POINTS/2);
	}

	public static<T> T getLastElement(List<T> objects){
		return  objects.get(objects.size()-1);
	}
	public static boolean didUserLevelUp(double oldPoints,double newPoints){
		if (Math.floor(GameUtils.getLevelFromXp(oldPoints))!=
				Math.floor(GameUtils.getLevelFromXp(newPoints))){
			return true;
		}
		return false;
	}
	
	public static void hasUserWonChallenge(OfflineChallenge offlineChallenge){
		
	}
}
