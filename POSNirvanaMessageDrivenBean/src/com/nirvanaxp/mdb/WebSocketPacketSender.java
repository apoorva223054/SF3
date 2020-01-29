/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.mdb;

import java.net.URI;
import java.net.URISyntaxException;

import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.mdb.WebSocketClient.MessageHandler;
import com.nirvanaxp.server.util.NirvanaLogger;

class WebSocketPacketSender
{

	private final static NirvanaLogger logger = new NirvanaLogger(WebSocketPacketSender.class.getName());

	private static class SingletonHolder
	{
		private static final WebSocketPacketSender INSTANCE = new WebSocketPacketSender();
	}

	static WebSocketPacketSender getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private WebSocketClient mClient;

	private String wsurl1;
	private String wsurl2;
	private String wsurl;

	private WebSocketPacketSender()
	{
		try
		{
			String socketVersion = "/POSNirvanaSocketV6/";
			wsurl1 = ConfigFileReader.getWebSocketURLPart1FromFile();
			wsurl2 = ConfigFileReader.getWebSocketURLPart2FromFile();
			wsurl = wsurl1 + socketVersion + wsurl2;

			startWebSocketClient();
		}
		catch (Exception e)
		{
			logger.severe(e, "Error when intializing socket from MDB");
		}
	}

	private void startWebSocketClient()
	{
		try
		{
			// open websocket
			mClient = new WebSocketClient(new URI(wsurl));

			// add listener
			mClient.addMessageHandler(new MessageHandler()
			{
				public void handleMessage(String message)
				{
					logger.info("MDB websocket message handler got message:", message);
				}
			});

		}
		catch (URISyntaxException ex)
		{
			logger.severe(ex, "URISyntaxException exception while starting MDB websocket");
		}
	}

	synchronized void sendToSocketForBroadcast(String packet)
	{
		try
		{
			if (mClient == null || mClient.userSession == null)
			{
				startWebSocketClient();
			}

			logger.info("Socket is to send packet at url:", wsurl, ";Packet:" + packet);
			mClient.userSession.getBasicRemote().sendText(packet);

		}
		catch (Exception e)
		{
			logger.severe(e, "Error when sending to socket for broadcast: ", e.getMessage());
		}

	}

	void shutDownSocket()
	{
		try
		{
			if (mClient != null && mClient.userSession!=null)
			{
				mClient.userSession.close();
				mClient = null;
			}
		}
		catch (Exception e)
		{
			logger.severe(e, "Error when shutting down MDB socket");
		}
	}

}
