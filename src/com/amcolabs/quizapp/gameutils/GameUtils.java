package com.amcolabs.quizapp.gameutils;

import com.amcolabs.quizapp.QuizApp;

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
	public double getLevelFromXp(double newPoints){
		return 1+(double) ((-19.0d*LEVEL_ONE_POINTS + Math.sqrt(19.0d*19*LEVEL_ONE_POINTS*LEVEL_ONE_POINTS + 4*(20*newPoints)*(LEVEL_ONE_POINTS)))/(2*LEVEL_ONE_POINTS*1.0d));
	}
	
	public double getPointsFromLevel(double level){
		level--;
		return LEVEL_ONE_POINTS*level+ (level*(level-1)/10.0d)*(LEVEL_ONE_POINTS/2);
	}
}
