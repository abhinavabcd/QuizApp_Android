package com.amcolabs.quizapp.serverutils;

import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;

/*
 * Author : Abhinav
 */
public interface ServerNotifier {
  public void onServerResponse(MessageType statusCode , ServerResponse response);
}
