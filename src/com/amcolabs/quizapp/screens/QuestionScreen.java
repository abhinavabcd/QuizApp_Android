package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TableLayout;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.Screen;

public class QuestionScreen extends Screen {

	private String question;
	private ArrayList<String> options;
	private Bitmap image;
	
	
	
	private TableLayout mainTableView;
	
	public QuestionScreen(AppController controller) {
		super(controller);
		Context tmp = controller.getContext();
	}

	public void loadQuestion(String ques, ArrayList<String> opt, Bitmap img){
		question = ques;
		options = opt;
		image = img;
	}
	
	public void resetProgress(){
		
	}
	
	public void showNextQuestion(){
		animateQuestionTextChange();
		animateQuestionOptionsChange();
	}

	private void animateQuestionOptionsChange() {
		// TODO Auto-generated method stub
		
	}

	private void animateQuestionTextChange() {
		// TODO Auto-generated method stub
		
	}
}
