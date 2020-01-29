package com.nirvanaxp.storeForward;

import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.orders.Publisher;

public class PacketSynchThread extends Thread
{
	private final static NirvanaLogger logger = new NirvanaLogger(PacketSynchThread.class.getName());
	HttpServletRequest httpRequest;
	 
	String json;
	String url;
	String methodType;
	int accountId;
	String locationId;
	Publisher publisher;
	String serverName;
	String schemaName;
	String sessionId;
	
	
	public PacketSynchThread(HttpServletRequest httpRequest, String serverName,String schemaName, String json, String url, String methodType, int accountId, String locationId, Publisher publisher,String sessionId)
	{
		super();
		this.httpRequest = httpRequest;
		 
		this.json = json;
		this.url = url;
		this.methodType = methodType;
		this.accountId = accountId;
		this.locationId = locationId;
		this.publisher = publisher;
		this.schemaName=schemaName;
		this.sessionId = sessionId;
		this.serverName= serverName;
	}


	@Override
	public void run()
	{

		try
		{

			new StoreForwardUtility().insertSyncPacketWithServer(serverName,schemaName,  json, url, methodType, publisher,
					accountId, locationId,sessionId);
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
	
		
	}
	
}
