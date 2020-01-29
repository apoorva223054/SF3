/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;


public class PostPacket
{

	private String merchantId;

	private String clientId;

	private String locationId;
	private String echoString;

	private String schemaName;

	private String sessionId;

	private int idOfSessionUsedByPacket;
	
	private int localServerURL;
	// 1 for local 
	// 2 for global

	public String getMerchantId()
	{
		return merchantId;
	}

	public void setMerchantId(String merchantId)
	{
		this.merchantId = merchantId;
	}

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public String getEchoString()
	{
		return echoString;
	}

	public void setEchoString(String echoString)
	{
		this.echoString = echoString;
	}

	public String getSchemaName()
	{
		return schemaName;
	}

	public void setSchemaName(String schemaName)
	{
		this.schemaName = schemaName;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public int getIdOfSessionUsedByPacket()
	{
		return idOfSessionUsedByPacket;
	}

	public void setIdOfSessionUsedByPacket(int idOfSessionUsedByPacket)
	{
		this.idOfSessionUsedByPacket = idOfSessionUsedByPacket;
	}
	
	

	public int getLocalServerURL()
	{
		return localServerURL;
	}

	public void setLocalServerURL(int localServerURL)
	{
		this.localServerURL = localServerURL;
	}

	@Override
	public String toString()
	{
		return "PostPacket [merchantId=" + merchantId + ", clientId=" + clientId + ", locationId=" + locationId + ", echoString=" + echoString + ", schemaName=" + schemaName + ", sessionId="
				+ sessionId + ", idOfSessionUsedByPacket=" + idOfSessionUsedByPacket + ", localServerURL=" + localServerURL + "]";
	}

}
