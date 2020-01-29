/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.reservation.RequestType;
@XmlRootElement(name = "RequestTypePacket")
public class RequestTypePacket extends PostPacket
{

	private RequestType requestType;

	public RequestType getRequestType()
	{
		return requestType;
	}

	public void setRequestType(RequestType requestType)
	{
		this.requestType = requestType;
	}

	@Override
	public String toString()
	{
		return "RequestTypePacket [requestType=" + requestType + "]";
	}

}
