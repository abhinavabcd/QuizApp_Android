package com.amcolabs.quizapp.databaseutils;

import com.google.gson.annotations.SerializedName;

public class UserInboxMessage {
	
	@SerializedName("fromuid_touid_index")
	public String uid1uid2index;
    public String fromUid;
    public String toUid;
    public String message;
    public double timestamp;
//    public String fromUidLastLoginIndex;
//    public String toUidLastLoginIndex; // uid2_LOGININDEX
}
