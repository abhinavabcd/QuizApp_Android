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
	public float getLevelFromXp(double newPoints){
		return 1+(float) ((-19.0f*LEVEL_ONE_POINTS + Math.sqrt(19*19*LEVEL_ONE_POINTS*LEVEL_ONE_POINTS + 4*(20*newPoints)*(LEVEL_ONE_POINTS)))/(2*LEVEL_ONE_POINTS*1.0f));
	}
	
	public double getPointsFromLevel(double level){
		level--;
		return LEVEL_ONE_POINTS*level+ level*(level-1)/10*LEVEL_ONE_POINTS/2;
	}
}
