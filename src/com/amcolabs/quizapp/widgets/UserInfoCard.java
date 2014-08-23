package com.amcolabs.quizapp.widgets;


import android.widget.LinearLayout;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;

public class UserInfoCard extends LinearLayout {

	public UserInfoCard(final QuizApp quizApp, String bgAssetPath) {
		super(quizApp.getContext());
		LinearLayout mainView = (LinearLayout) quizApp.getActivity().getLayoutInflater().inflate(R.layout.user_info_card, null);
		addView(mainView);
		quizApp.getUiUtils().loadImageAsBg(quizApp.getContext(), this, bgAssetPath);
	}
}
