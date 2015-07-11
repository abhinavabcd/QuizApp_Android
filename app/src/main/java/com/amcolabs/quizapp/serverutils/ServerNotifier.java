package com.amcolabs.quizapp.serverutils;

import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;

/*
 * Author : Abhinav
 */
public interface ServerNotifier {
  public void onServerResponse(MessageType messageType , ServerResponse response);
}
