/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.context;

public class SessionDetails
{

	private String sessionId;

	private String encryptionKey;

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getEncryptionKey()
	{
		return encryptionKey;
	}

	public void setEncryptionKey(String encyptionKey)
	{
		this.encryptionKey = encyptionKey;
	}

}
