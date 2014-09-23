package com.amcolabs.quizapp.databaseutils;

import com.j256.ormlite.field.DatabaseField;

public class OfflineChallenge {
	
	
	@DatabaseField(id=true , index=true, unique=true)
	String challengeId;	
	@DatabaseField
	String fromUid_userChallengeIndex;
	@DatabaseField
	String toUid_userChallengeIndex;
	@DatabaseField
	int challengeTye;
	@DatabaseField
	String challengeData;
	@DatabaseField
	String challengeData2;
	@DatabaseField
	String wonUid;
	
}
