/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.reasons.ReasonType;
@XmlRootElement(name = "ReasonTypePacket")
public class ReasonTypePacket extends PostPacket
{

	private ReasonType reasonType;

	public ReasonType getReasonType()
	{
		return reasonType;
	}

	public void setReasonType(ReasonType reasonType)
	{
		this.reasonType = reasonType;
	}

	@Override
	public String toString()
	{
		return "ReasonTypePacket [reasonType=" + reasonType + "]";
	}

}
