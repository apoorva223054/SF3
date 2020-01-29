/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.mdb;

import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.nirvanaxp.server.util.NirvanaLogger;

@ClientEndpoint
public class WebSocketClient
{

	private static final NirvanaLogger logger = new NirvanaLogger(WebSocketClient.class.getName());

	Session userSession = null;
	private MessageHandler messageHandler;

	public WebSocketClient(URI endpointURI)
	{
		try
		{
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			logger.info("Connecting to :", endpointURI.toString());
			container.connectToServer(this, endpointURI);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Callback hook for Connection open events.
	 *
	 * @param userSession
	 *            the userSession which is opened.
	 */
	@OnOpen
	public void onOpen(Session userSession)
	{
		logger.info("opening MDB websocket");
		this.userSession = userSession;
	}

	/**
	 * Callback hook for Connection close events.
	 *
	 * @param userSession
	 *            the userSession which is getting closed.
	 * @param reason
	 *            the reason for connection close
	 */
	@OnClose
	public void onClose(Session userSession, CloseReason reason)
	{
		logger.info("closing MDB websocket");
		this.userSession = null;
	}

	/**
	 * Callback hook for Message Events. This method will be invoked when a
	 * client send a message.
	 *
	 * @param message
	 *            The text message
	 */
	@OnMessage
	public void onMessage(String message)
	{
		if (this.messageHandler != null)
		{
			this.messageHandler.handleMessage(message);
		}
	}

	/**
	 * register message handler
	 *
	 * @param message
	 */
	public void addMessageHandler(MessageHandler msgHandler)
	{
		this.messageHandler = msgHandler;
	}

	/**
	 * Send a message.
	 *
	 * @param user
	 * @param message
	 */
	public void sendMessage(String message)
	{
		this.userSession.getAsyncRemote().sendText(message);
	}

	/**
	 * Message handler. TODO - what is the use of this?
	 */
	public static interface MessageHandler
	{

		public void handleMessage(String message);
	}
}
