package com.quizapp.tollywood.serverutils;

import com.quizapp.tollywood.serverutils.ServerResponse.MessageType;

/*
 * Author : Abhinav
 */
public interface ServerNotifier {
  public void onServerResponse(MessageType messageType , ServerResponse response);
}
