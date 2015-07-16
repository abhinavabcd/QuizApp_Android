package com.quizapp.tollywood;

import android.graphics.Canvas;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;



public class Screen extends LinearLayout {

	public boolean removeOnBackPressed() {
		return true;
	}

	public static enum ScreenType{
		UNKNOWN,
		QUIZZES_SCREEN,
		QUESTIONS_SCREEN,
		CLASH_SCREEN, 
		HOME_SCREEN, 
		WELCOME_SCREEN,
		PROFILE_SCREEN, BADGES_SCREEN, CHAT_SCREEN,
		LEADERBOARD_SCREEN,
		LOGIN_SCREEN, SELECT_FRIENDS_SCREEN, CHAT_LIST_SCREEN, SIGNUP_SCREEN, WIN_LOOSE_SCREEN;

		String data;

		public void setData(String data){
			this.data  = data;
 		}
		public String getData(){
			return data;
		}

		public ScreenType withData(String quizId) {
			this.data = quizId;
			return this;
		}
	}
	
	protected AppController controller = null;
	protected boolean isInViewPort = true;
	private LinearLayout scrollView = null;
	protected ScreenType screenType = ScreenType.UNKNOWN;
	
	private int screenId;
	public int getScreenId(){
		return screenId;
	}
	public Screen(AppController controller) {
		super(controller.getContext());
		this.controller = controller;
		this.controller.incRefCount();
		this.setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,1));
		screenId = controller.quizApp.getConfig().random.nextInt();
	}
	
	
	public void addToScrollView(View view){
		if(scrollView==null){
			ScrollView scrollViewMain = new ScrollView(getApp().getContext());
			scrollViewMain.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
			scrollViewMain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,1));
			scrollViewMain.setFillViewport(true);
			addView(scrollViewMain);
			scrollView = new LinearLayout(getApp().getContext());
//			scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,1));
			scrollView.setOrientation(LinearLayout.VERTICAL);
			scrollView.setVerticalScrollBarEnabled(false);
			scrollView.setHorizontalScrollBarEnabled(false);
			//TODO: remove scroll bars visibility
			scrollViewMain.addView(scrollView);
		}
		scrollView.addView(view);
	}
	
	public QuizApp getApp(){
		return controller.quizApp;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		if(isInViewPort)
			super.onDraw(canvas);
	}
	
	protected void onReInit(){
		
	}
	
	public void beforeRemove(){
		controller.decRefCount();
	}
	
	public boolean shouldAddtoScreenStack(){
		return true;
	}
	public boolean showOnBackPressed(){ //should show on back press
		return true;
	}
	
	public boolean showMenu(){
		return false;
	}


	public void refresh() { //called when added back to screen
	}


	public void onRemovedFromScreen() { // called each time this is disposed from screen view
	}
	
	public boolean  doNotDistrub(){
		return false;
	}
	
	public ScreenType getScreenType(){
		return screenType;
	}
}
