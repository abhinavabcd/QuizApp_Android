package com.amcolabs.quizapp.screens;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.AppManager;
import com.amcolabs.quizapp.Screen;

public class QuestionScreen extends Screen {

	private String question;
	private ArrayList<String> options;
	private Bitmap image;
	
	private TableLayout mainTableView;
	
	public QuestionScreen(AppController controller) {
		super(controller);
	}

	public void loadQuestion(){
		
	}
}
