package com.amcolabs.quizapp.serverutils;

import de.tavendo.autobahn.WebSocketConnection;

public class ServerWebSocketConnection extends WebSocketConnection{
	public String serverId;
	public String addr;
	ServerWebSocketConnection(String serverId, String addr){
		super();
		this.serverId  = serverId;
		this.addr  = addr;
		
	}
}
